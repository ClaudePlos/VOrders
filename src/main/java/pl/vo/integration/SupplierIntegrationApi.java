/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.integration;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import pl.vo.VOConsts;
import pl.vo.company.api.CompanysApi;
import pl.vo.company.model.Company;
import pl.vo.documents.model.Document;
import pl.vo.exceptions.VOWrongDataException;
import pl.vo.integration.model.IntegrationMessageTypes;

/**
 *
 * @author Piotr
 */
@Stateless(name = "SupplierIntegrationApi", mappedName = "SupplierIntegrationApi")
@LocalBean
public class SupplierIntegrationApi implements Serializable {

    @EJB
    IntegrationEdifactApi imag;

    @EJB
    VOIntegrationSender sender;

    @EJB
    CompanysApi companyApi;
        // Klaud - ta klasa odpowiada za integracje z dostawcami - wysylanie plikow zamowien

    public void sendOrderToSupplier(Document doc) throws VOWrongDataException {
        // check supplier integration mode
        Company supplier = doc.getSupplier();

        /// ustal sposob komunikacji z dostawca
        String msg = imag.createSendOrderToSupplier(doc);
        sender.sendMessage("Nowe zamówienia", msg, IntegrationMessageTypes.MESSAGE_TYPE_SEND_ORDER_TO_SUPPLIER, doc, supplier);

    }

    public void sendDpzToClient(Document doc) throws VOWrongDataException {
        // check supplier integration mode
        Company client = doc.getClient();

        /// ustal sposob komunikacji z dostawca
        String msg = imag.createSendDpzToClient(doc);
        sender.sendMessage("Nowe zamówienia", msg, IntegrationMessageTypes.MESSAGE_TYPE_SEND_DPZ_TO_CLIENT, doc, client);

    }

    public void sendPriceList(Document doc) throws VOWrongDataException {
        Company client = companyApi.getByNip(VOConsts.NIP_VENDI);
        String msg = imag.createSendPriceListToClient(doc);
        sender.sendMessage("Nowe zamówienia", msg, IntegrationMessageTypes.MESSAGE_TYPE_SEND_DPZ_TO_CLIENT, doc, client);
    }

    // glowna funkcja przetwarzania przychodzacych wiadomosci

    public void parseIncomingMessage(String message) {

    }

}
