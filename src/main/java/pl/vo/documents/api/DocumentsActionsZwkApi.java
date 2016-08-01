/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.documents.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import pl.vo.VOConsts;
import pl.vo.documents.DocumentsApi;
import pl.vo.documents.model.Document;
import pl.vo.documents.model.DocumentItem;
import pl.vo.exceptions.VOWrongDataException;
import pl.vo.numeration.model.api.NumerationApi;
import pl.vo.products.api.UnitsProductsSuppliersApi;

/**
 *
 * @author Piotr
 */
@Stateless(name = "DocumentsActionsZwkApi", mappedName = "DocumentsActionsZwkApi")
@LocalBean
public class DocumentsActionsZwkApi  extends DocumentActionBase implements Serializable {

    @EJB
    DocumentsApi documentsApi;

    @EJB
    UnitsProductsSuppliersApi unitProductsSuppliersApi;

    @EJB
    PriceListsApi priceListsApi;
    
    @EJB
    NumerationApi numerationApi; 

    public Document runDocumentAction(Document doc, String action) throws VOWrongDataException
    {
        if (action.equals(VOConsts.ACTION_ACCEPT)) {
            doc = actionZwkAccept(doc);
        } else if (action.equals(VOConsts.ACTION_ACCEPT_AVAILABILITY)) {
            doc = actionZwkConfirmAvailability(doc);
        }  else if (action.equals(VOConsts.ACTION_BACK_TO_OPEN)) {
            doc = actionZwkBackToOpen(doc);
        } else if (action.equals(VOConsts.ACTION_SEND_TO_REALIZATION)) {
            doc = actionSendToRealization(doc);
        }
        else if ( action.equals(VOConsts.ACTION_CANCEL_REALIZATION)){
             doc = actionCancelRealization(doc);
        }
            else {
            throw new VOWrongDataException("Błąd DPA47 - nie obsługiwana akcja:" + action);
        }
        return doc;
    }

    private Document actionZwkConfirmAvailability(Document doc) {
        return doc;
    }

  
    private Document actionZwkBackToOpen(Document doc) throws VOWrongDataException {
        for (DocumentItem item : doc.getItems()) {
            item.setStatus(VOConsts.DOC_STATUS_OPEN);
        }

// change status to ACCEPTED
        doc = documentsApi.changeStatus(doc, VOConsts.DOC_STATUS_OPEN);
        return doc;
    }

    // akceptacja dokumentu ZWK przez użytkownika  zablokowanie edycji , przypisanie do dostawcow i cenniki
    private Document actionZwkAccept(Document doc) throws VOWrongDataException
    {
        // find suppliers and prices
        unitProductsSuppliersApi.assignSuppliers(doc);
        // find prices
        priceListsApi.assignPricesInDocument(doc);

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
        Map<Long, Document> zwds = new HashMap<Long, Document>();

        // parse positions and create ZWD 
        for (DocumentItem item : doc.getItems()) {
            Document zwd = null;
            if (zwds.containsKey(item.getUnitProductSupplier().getSupplier().getId())) {
                zwd = zwds.get(item.getUnitProductSupplier().getSupplier().getId());
            } else {
                // create zwd 
                zwd = new Document();
                zwd.setStatus( VOConsts.DOC_STATUS_ACCEPTED );
                zwd.setType(VOConsts.DOC_TYPE_ZWD);
                zwd.setClient( doc.getClient() );
                zwd.setSupplier(item.getUnitProductSupplier().getSupplier());
                zwd.setDateOperation(doc.getDateOperation());
                zwd.setDateDelivery(doc.getDateDelivery());
                zwd.setOwnNumber( numerationApi.getNumberForDocument( zwd ));
                zwd.setCompanyUnit( doc.getCompanyUnit() );
                zwd.setSourceDocument( doc );
                zwds.put(zwd.getSupplier().getId(), zwd);

            }

            // copy positions
            DocumentItem zwdItem = new DocumentItem();
            zwdItem.setAmount(item.getAmount());
            zwdItem.setPriceItem(item.getPriceItem());
            zwdItem.setProduct(item.getProduct());
            zwdItem.setSourceItem(item);
            zwdItem.setUnitPriceNet(item.getUnitPriceNet());
            zwdItem.setUnitProductSupplier(item.getUnitProductSupplier());
            zwdItem.setValueBrut(item.getValueBrut());
            zwdItem.setValueNet(item.getValueNet());
            zwdItem.setValueTax(item.getValueTax());
            
            zwd.getItems().add(zwdItem);
        }

        // sve new zwd
        for (Document zwd : zwds.values()) {
            try {
                zwd = documentsApi.save(zwd);
            } catch (VOWrongDataException wre) {
                throw new VOWrongDataException("Błąd DAZA-124 nie udało się utworzyć dokumentu ZWD:" + wre.getMessage());
            }
        }
        
        
        doc.setStatus( VOConsts.DOC_STATUS_DELIVERY );
        doc = documentsApi.save( doc );
        return doc;
    }
    
    private Document actionCancelRealization( Document doc )throws VOWrongDataException
    {
        // delete ZWD documents
        
        // change status
        doc.setStatus( VOConsts.DOC_STATUS_ACCEPTED );
        doc = documentsApi.save( doc );
        return doc; 
    }
}
