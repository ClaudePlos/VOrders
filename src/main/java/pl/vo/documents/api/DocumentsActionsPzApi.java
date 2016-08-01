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
import pl.vo.documents.model.DocumentItem;
import pl.vo.exceptions.VOWrongDataException;
import pl.vo.numeration.model.api.NumerationApi;

/**
 *
 * @author Piotr
 */
@Stateless(name = "DocumentsActionsPzApi", mappedName = "DocumentsActionsPzApi")
@LocalBean
public class DocumentsActionsPzApi  extends DocumentActionBase  implements Serializable {

    @EJB
    DocumentsApi documentsApi;

    @EJB
    NumerationApi numerationApi;

    public Document runDocumentAction(Document doc, String action) throws VOWrongDataException {
        if (action.equals(VOConsts.ACTION_ACCEPT)) {
            doc = actionAccept(doc);
        } else if (action.equals(VOConsts.ACTION_BACK_TO_OPEN)) {
            doc = actionBackToOpen(doc);
        } else if (action.equals(VOConsts.ACTION_BOOK)) {
            doc = actionBook(doc);
        } else {
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

        documentsApi.recalculateDocument(doc);

        numerationApi.fillNumerIfNotExists(doc);
// change status to ACCEPTED
        doc = documentsApi.changeStatus(doc, VOConsts.DOC_STATUS_ACCEPTED);
        return doc;
    }

    private Document actionBook(Document doc) throws VOWrongDataException {
        doc = documentsApi.changeStatus(doc, VOConsts.DOC_STATUS_BOOKED);
        return doc;
    }

    public Document createPzFromDpz(Document docDpz) {
        Document pz = new Document();
        pz.setSupplier(docDpz.getSupplier());
        pz.setCompanyUnit(docDpz.getCompanyUnit());
        pz.setExternalNumber(docDpz.getExternalNumber());
        pz.setSourceDocument(docDpz);
        pz.setType(VOConsts.DOC_TYPE_PZ);
        pz.setStatus(VOConsts.DOC_STATUS_OPEN);
        pz.setDateOperation( docDpz.getDateOperation() );
        pz.setDateDelivery( docDpz.getDateDelivery());
        
        for (DocumentItem idpz : docDpz.getItems()) {
            DocumentItem ipz = new DocumentItem();
            ipz.setProduct(idpz.getProduct());
            ipz.setPriceItem(idpz.getPriceItem());
            ipz.setSourceItem(ipz);
            ipz.setAmount(idpz.getAmount());
            ipz.setUnitPriceNet(idpz.getUnitPriceNet());
            pz.getItems().add(ipz);
        }

        return pz;
    }
}
