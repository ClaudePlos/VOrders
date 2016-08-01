/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.documents.api;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import pl.vo.VOConsts;
import pl.vo.documents.DocumentsApi;
import pl.vo.documents.model.Document;
import pl.vo.exceptions.VOWrongDataException;
import pl.vo.integration.SupplierIntegrationApi;
import pl.vo.numeration.model.api.NumerationApi;

/**
 *
 * @author Piotr
 */
@Stateless(name = "DocumentsActionsPriceList", mappedName = "DocumentsActionsPriceList")
@LocalBean
public class DocumentsActionsPriceList extends DocumentActionBase  implements Serializable 
{

    @EJB
    DocumentsApi documentsApi;

    @EJB
    NumerationApi numerationApi;

    @EJB
    SupplierIntegrationApi integrationApi;

    public Document runDocumentAction(Document doc, String action) throws VOWrongDataException {
        if (action.equals(VOConsts.ACTION_ACCEPT)) {
            doc = actionPriceListAccept(doc);
        } else if (action.equals(VOConsts.ACTION_BACK_TO_OPEN)) {
            doc = actionPriceListBackToOpen(doc);
        } else if (action.equals(VOConsts.ACTION_SEND_TO_CLIENT)) {
            doc = actionPriceListSendToClient(doc);

        } else {
            throw new VOWrongDataException("Błąd DPA47 - nie obsługiwana akcja:" + action);
        }
        return doc;
    }

    private Document actionPriceListAccept(Document doc) throws VOWrongDataException {

        doc = documentsApi.changeStatus(doc, VOConsts.DOC_STATUS_ACCEPTED);
        return doc;
    }

    private Document actionPriceListBackToOpen(Document doc) throws VOWrongDataException {
        doc = documentsApi.changeStatus(doc, VOConsts.DOC_STATUS_OPEN);
        return doc;
    }

    private Document actionPriceListSendToClient(Document doc) throws VOWrongDataException {
        integrationApi.sendPriceList(doc);
        doc = documentsApi.changeStatus(doc, VOConsts.DOC_STATUS_OPEN);
        return doc;
    }
}
