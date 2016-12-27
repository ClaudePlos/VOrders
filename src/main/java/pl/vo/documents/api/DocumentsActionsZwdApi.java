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

/**
 *
 * @author Piotr
 */
@Stateless(name = "DocumentsActionsZwdApi", mappedName = "DocumentsActionsZwdApi")
@LocalBean
public class DocumentsActionsZwdApi  extends DocumentActionBase  implements Serializable {

    @EJB
    DocumentsApi documentsApi;
    
    @EJB
    SupplierIntegrationApi integrationApi; 

    public Document runDocumentAction(Document doc, String action) throws VOWrongDataException 
    {
        if (action.equals(VOConsts.ACTION_SEND_TO_REALIZATION)) {
            doc = actionSendToRealization(doc);
        } 
        else if ( action.equals(VOConsts.ACTION_SEND_TO_SUPPLIER)){
            doc = actionSendToSupplier(doc);
        }
        else if ( action.equals(VOConsts.ACTION_SUPPLIER_CONFIRM_RECEIVE)){
             doc = actionSupplierConfirmReceive(doc);
        }
          else if ( action.equals(VOConsts.ACTION_SUPPLIER_CONFIRM_AVAILABILITY)){
             doc = actionSupplierConfirmAvailability(doc);
        }
          else if ( action.equals( VOConsts.ACTION_CANCEL_REALIZATION)) {
              doc = actionCancelRealization(doc);
          }
            else {
            throw new VOWrongDataException("Błąd DPA47 - nie obsługiwana akcja:" + action);
        }
        return doc;
    }

    private Document actionSendToRealization(Document doc) throws VOWrongDataException {

        doc.setStatus(VOConsts.DOC_STATUS_DELIVERY);

        doc = documentsApi.save(doc);

        return doc;
    }
    
    
     private Document actionSendToSupplier(Document doc) throws VOWrongDataException {

         // send 
        integrationApi.sendOrderToSupplier( doc );
         
         // czasowo wylaczone - test wysylania
         // change stats
        //doc.setStatus(VOConsts.DOC_STATUS_SENDED_TO_SUPPLIER);
        //doc = documentsApi.save(doc);

        return doc;
    }
     
     private Document actionSupplierConfirmReceive( Document doc) throws VOWrongDataException
     {
         // change status
         doc.setStatus(VOConsts.DOC_STATUS_RECEIVED_BY_SUPPLIER);
          return doc;
     }
     
      private Document actionSupplierConfirmAvailability ( Document doc) throws VOWrongDataException
     {
         
         integrationApi.sendSupplierConfirmAvailability( doc );
         
         // change status
         doc.setStatus(VOConsts.DOC_STATUS_CONFIRMED_BY_SUPPLIER);
          return doc;
     }
      
      private Document actionCancelRealization( Document doc ) throws VOWrongDataException
      {
            doc.setStatus(VOConsts.DOC_STATUS_ACCEPTED);
          return doc;
      }
}
