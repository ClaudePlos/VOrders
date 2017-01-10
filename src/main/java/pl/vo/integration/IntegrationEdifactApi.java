/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.integration;

import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import pl.vo.documents.model.Document;
import pl.vo.exceptions.VOWrongDataException;
import pl.vo.integration.edifact.EdifactExport;
import pl.vo.integration.edifact.EdifactExportPricat;

/**
 *
 * @author Piotr
 */
@Stateless(name = "IntegrationEdifactApi", mappedName = "IntegrationEdifactApi")
@LocalBean
public class IntegrationEdifactApi {

    Logger logger = Logger.getLogger(IntegrationEdifactApi.class.getName());

    // creates message
    public String createSendOrderToSupplier(Document doc) throws VOWrongDataException {
        String ret = "";
        logger.fine("createSendOrderToSupplier");

        EdifactExport ediOrderExport = new EdifactExport();

        ret = ediOrderExport.generate(doc);

        return ret;
    }
    
    // add ks creates message ORDRSP -  odpowiedź na zamówienie ilosci, zamienniki ewentualnie
    public String createSendOrderConfirmToClient(Document doc) throws VOWrongDataException {
        String ret = "";
        logger.fine("createSendOrderConfirmToClient");

        EdifactExport ediOrderExport = new EdifactExport();

        ret = ediOrderExport.generate(doc);

        return ret;
    }

    public String createSendDpzToClient(Document doc) throws VOWrongDataException {
        String ret = "";
        logger.fine("createSendOrderToSupplier");

        EdifactExport ediOrderExport = new EdifactExport();

        ret = ediOrderExport.generate(doc);

        return ret;
    }

    public String createSendPriceListToClient(Document doc) throws VOWrongDataException {

        EdifactExportPricat ediExportPricat = new EdifactExportPricat();
        String ret = ediExportPricat.generate(doc);
        return ret;
    }

}
