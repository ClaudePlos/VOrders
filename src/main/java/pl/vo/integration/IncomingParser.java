/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.integration;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import org.milyn.SmooksException;
import org.milyn.edi.unedifact.d96a.D96AInterchangeFactory;
import org.milyn.edi.unedifact.d96a.DESADV.Desadv;
import org.milyn.edi.unedifact.d96a.ORDERS.Orders;
import org.milyn.edi.unedifact.d96a.PRICAT.Pricat;
import org.milyn.smooks.edi.unedifact.model.UNEdifactInterchange;
import org.milyn.smooks.edi.unedifact.model.r41.UNEdifactInterchange41;
import org.milyn.smooks.edi.unedifact.model.r41.UNEdifactMessage41;
import pl.vo.company.api.CompanysApi;
import pl.vo.company.model.Company;
import pl.vo.documents.model.Document;
import pl.vo.exceptions.VOWrongDataException;
import pl.vo.exceptions.VoNoResultException;
import pl.vo.integration.edifact.EdifactOrderImport;
import pl.vo.integration.edifact.EdifactPricatImport;
import pl.vo.integration.edifact.EdifactProvider;
import pl.vo.rest.VoRestResponse;

/**
 *
 * Parse incoming messages
 */
@Stateful
@SessionScoped
@LocalBean
public class IncomingParser implements Serializable {

    @EJB
    EdifactOrderImport orderImport;

    @EJB
    EdifactPricatImport pricatImport;

    @EJB
    CompanysApi companyApi;

    public VoRestResponse parseIncomingMessage(String message) throws VOWrongDataException {

        try {
            // identify sender
            VoRestResponse resp = parseEdifactMessage(message);
            return resp;
        } catch (Exception e) {
            throw new VOWrongDataException("Błąd " + e.toString());
        }
        // check 
        // 
    }

    public VoRestResponse parseEdifactMessage(String edifactString) throws VOWrongDataException {
        Company cmpSender = null, cmpRecipent = null;

        VoRestResponse resp = new VoRestResponse();
        Document edifactDoc = new Document();

        D96AInterchangeFactory factory = EdifactProvider.getInterchangeFactory();

        InputStream is = new ByteArrayInputStream(edifactString.getBytes());
        BufferedInputStream bis = new BufferedInputStream(is);
        DataInputStream dis = new DataInputStream(bis);

        UNEdifactInterchange interchange;

        try {
            interchange = factory.fromUNEdifact(bis);
        } catch (SmooksException sme) {
            Throwable caus = sme.getCause();
            throw new VOWrongDataException("Nie udało się przetworzyć pliku edifact:" + caus.getLocalizedMessage(), caus);
        } catch (Exception ioe) {
            throw new VOWrongDataException("Nie udało się przetworzyć pliku edifact:" + ioe.getLocalizedMessage(), ioe);
        }

        if (interchange instanceof UNEdifactInterchange41) {
            UNEdifactInterchange41 interchange41 = (UNEdifactInterchange41) interchange;

            String senderId = interchange41.getInterchangeHeader().getSender().getId();
            String senderCodeQual = interchange41.getInterchangeHeader().getSender().getCodeQualifier();

            String recipent = interchange41.getInterchangeHeader().getRecipient().getId();
// parsuj naglowek - nadawca i odbiorca
            try {
                cmpSender = companyApi.getByNip(senderId);
            } catch (VoNoResultException nre) {
                throw new VOWrongDataException("Błąd - nieznana firma nadawcy o nipie:" + senderId);
            }

            try {
                cmpRecipent = companyApi.getByNip(recipent);
            } catch (VoNoResultException nre) {
                throw new VOWrongDataException("Błąd - nieznana firma odbiorcy o nipie:" + recipent);
            }

            for (UNEdifactMessage41 message : interchange41.getMessages()) {
                Object messageObj = message.getMessage();

                if (messageObj instanceof Orders) {
                    // parsuj zamowienie
                    Orders orders = (Orders) messageObj;
                    try {
                        Document orderDoc = orderImport.parseAndProcess(cmpSender, cmpRecipent, orders);
                        resp.setDocId(orderDoc.getId().toString());
                        resp.setDocNumber( orderDoc.getOwnNumber() );
                        resp.setStatusCode("OK");
                        return resp;
                    } catch (Exception e) {

                        throw new VOWrongDataException("Błąd" + e.getMessage());
                    }

                } // AWIZO DOSTWY
                else if (messageObj instanceof Desadv) {
                    Desadv desadv = (Desadv) messageObj;
                    try {
                        Document orderDoc = orderImport.parseDesadv(cmpSender, cmpRecipent, desadv);
                        resp.setDocId(orderDoc.getId().toString());
                        resp.setDocNumber( orderDoc.getOwnNumber() );
                        resp.setStatusCode("OK");
                        return resp;
                    } catch (Exception e) {

                        throw new VOWrongDataException("Błąd" + e.getMessage());
                    }
                } // PRICAT
                else if (messageObj instanceof Pricat) {

                    Pricat pricat = (Pricat) messageObj;
                    try {
                        Document pricatDoc = pricatImport.parseAndProcess(cmpSender, cmpRecipent, pricat);
                        resp.setDocId(pricatDoc.getId().toString());
                        resp.setDocNumber( pricatDoc.getOwnNumber() );
                        resp.setStatusCode("OK");
                        return resp;
                    } catch (Exception e) {

                        throw new VOWrongDataException("Błąd" + e.getMessage());
                    }

                }

            }
        }
        return resp;
    }
}
