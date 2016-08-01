/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.documents.api;

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
import pl.vo.products.api.UnitsProductsSuppliersApi;

/**
 *
 * @author Piotr
 */
@Stateless(name = "DocumentsProcessApi", mappedName = "DocumentsProcessApi")
@LocalBean
public class DocumentsProcessApi implements Serializable {

    @EJB
    DocumentsApi documentsApi;

    @EJB
    UnitsProductsSuppliersApi unitProductsSuppliersApi;

    @EJB
    PriceListsApi priceListsApi;

    @EJB
    DocumentsActionsZwkApi documentsZwkApi;
     @EJB
    DocumentsActionsZwdApi documentsZwdApi;
 @EJB
    DocumentsActionsPzApi documentsPzApi;
 
 @EJB
 DocumentsActionsPriceList documentsPriceList; 
 
       @EJB
    DocumentsActionsDpzApi documentsDpzApi;

    public Document runDocumentAction(Long dokId, String action) throws VOWrongDataException {

        if (action == null) {
            throw new VOWrongDataException("Błąd DPA34 - nie przekazano kodu akcji");
        }
        if (dokId == null) {
            throw new VOWrongDataException("Błąd DPA36 - nie przekazano id dokumentu");
        }

        Document doc = documentsApi.get(dokId);

        if (doc.getType().equals(VOConsts.DOC_TYPE_ZWK)) {
            return documentsZwkApi.runDocumentAction(doc, action);

        }
        else if (doc.getType().equals(VOConsts.DOC_TYPE_ZWD))
        {
            return documentsZwdApi.runDocumentAction(doc, action);

        }
        else if (doc.getType().equals(VOConsts.DOC_TYPE_DPZ))
        {
            return documentsDpzApi.runDocumentAction(doc, action);
        }
         else if (doc.getType().equals(VOConsts.DOC_TYPE_PZ))
        {
            return documentsPzApi.runDocumentAction(doc, action);
        }
        else if (doc.getType().equals(VOConsts.DOC_TYPE_PRICE_LIST))
        {
            return documentsPriceList.runDocumentAction(doc, action);
            
        } else {
            throw new VOWrongDataException("Błąd DPA48 - ten typ do kumentu nie jest obsługiwany");
        }
       
    }

    
}
