/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.documents.api;

import com.vaadin.client.ui.dd.VAcceptCriterionFactory;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import pl.vo.VOConsts;
import pl.vo.documents.DocumentsApi;
import pl.vo.documents.model.Document;
import pl.vo.documents.model.DocumentItem;
import pl.vo.exceptions.VOWrongDataException;
import pl.vo.integration.IntegrationEdifactApi;
import pl.vo.integration.SupplierIntegrationApi;
import pl.vo.numeration.model.api.NumerationApi;

/**
 *
 * @author Piotr
 */
@Stateless(name = "DocumentsActionsDpzApi", mappedName = "DocumentsActionsDpzApi")
@LocalBean
public class DocumentsActionsDpzApi extends DocumentActionBase implements Serializable {

    @EJB
    DocumentsApi documentsApi;

    @EJB
    NumerationApi numerationApi;
    
    @EJB
    SupplierIntegrationApi integrationApi; 

    public Document runDocumentAction(Document doc, String action) throws VOWrongDataException {
        if (action.equals(VOConsts.ACTION_ACCEPT)) {
            doc = actionAccept(doc);
        } else if (action.equals(VOConsts.ACTION_BACK_TO_OPEN)) {
            doc = actionBackToOpen(doc);
        } else if (action.equals(VOConsts.ACTION_SEND_TO_REALIZATION)) {
            doc = actionSendToRealization(doc);
        }
        else {
            throw new VOWrongDataException("Błąd DPA47 - nie obsługiwana akcja:" + action);
        }
        return doc;
    }

    private Document actionBackToOpen(Document doc) throws VOWrongDataException {
        for (DocumentItem item : doc.getItems()) {
            item.setStatus(VOConsts.DOC_STATUS_OPEN);
        }

// change status to ACCEPTED
        doc = documentsApi.changeStatus(doc, VOConsts.DOC_STATUS_OPEN);
        return doc;
    }

    // akceptacja dokumentu ZWK przez użytkownika  zablokowanie edycji , przypisanie do dostawcow i cenniki
    private Document actionAccept(Document doc) throws VOWrongDataException {

        // create number
        if (doc.getOwnNumber() == null) {
            doc.setOwnNumber(numerationApi.getNumberForDocument(doc));
        }
        
        // recalculate
   documentsApi.recalculateDocument(doc);        
// change status of every position
        for (DocumentItem item : doc.getItems()) {
            item.setStatus(VOConsts.DOC_STATUS_ACCEPTED);
        }

// change status to ACCEPTED
        doc = documentsApi.changeStatus(doc, VOConsts.DOC_STATUS_ACCEPTED);
        return doc;
    }

    private Document actionSendToRealization(Document doc) throws VOWrongDataException {
       
          // send 
        integrationApi.sendDpzToClient( doc );
       
        // doc = documentsApi.changeStatus(doc, VOConsts.DOC_STATUS_DELIVERY);
        return doc;
    }
    

    public Document createNewDpzForZwd(Document docZwd) {
        Document dpz = new Document();
        dpz.setStatus(VOConsts.DOC_STATUS_OPEN);
        dpz.setSourceDocument(docZwd);
        dpz.setType(VOConsts.DOC_TYPE_DPZ);
        dpz.setSupplier(docZwd.getSupplier());
        dpz.setClient( docZwd.getClient() );
        dpz.setCompanyUnit(docZwd.getCompanyUnit());

        // copy items
        for (DocumentItem item : docZwd.getItems()) {

            DocumentItem dpzItem = new DocumentItem();
            dpz.getItems().add(dpzItem);

            dpzItem.setProduct(item.getProduct());
            dpzItem.setPriceItem(item.getPriceItem());
            dpzItem.setSourceItem(item);
            dpzItem.setUnitPriceNet(item.getUnitPriceNet());
            dpzItem.setAmountConfirmed(item.getAmountConfirmed());

            // sum amount already declared on other dpdz
            BigDecimal sumAlreadyOnDpz = new BigDecimal(0);

            dpzItem.setAmountOnDpzs(sumAlreadyOnDpz);
            if (dpzItem.getAmountConfirmed() != null) {
                dpzItem.setAmountLeftToDelivery(dpzItem.getAmountConfirmed().subtract(dpzItem.getAmountOnDpzs()));
            } else {
                dpzItem.setAmountLeftToDelivery(new BigDecimal(0));
            }

        }
        return dpz;
    }

}
