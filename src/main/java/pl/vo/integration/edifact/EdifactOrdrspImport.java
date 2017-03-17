/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.integration.edifact;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import org.milyn.edi.unedifact.d96a.ORDRSP.Ordrsp;
import org.milyn.edi.unedifact.d96a.ORDRSP.SegmentGroup2;
import org.milyn.edi.unedifact.d96a.ORDRSP.SegmentGroup26;
import org.milyn.edi.unedifact.d96a.ORDRSP.SegmentGroup3;
import org.milyn.edi.unedifact.d96a.ORDRSP.SegmentGroup30;
import org.milyn.edi.unedifact.d96a.common.AdditionalProductId;
import org.milyn.edi.unedifact.d96a.common.BeginningOfMessage;
import org.milyn.edi.unedifact.d96a.common.DateTimePeriod;
import org.milyn.edi.unedifact.d96a.common.NameAndAddress;
import org.milyn.edi.unedifact.d96a.common.Quantity;
import pl.vo.VOConsts;
import pl.vo.company.api.CompanysApi;
import pl.vo.company.model.Company;
import pl.vo.documents.DocumentsApi;
import pl.vo.documents.api.PriceListsApi;
import pl.vo.documents.model.Document;
import pl.vo.documents.model.DocumentItem;
import pl.vo.exceptions.VOWrongDataException;
import pl.vo.exceptions.VoNoResultException;
import pl.vo.numeration.model.api.NumerationApi;
import pl.vo.organisation.OrganisationApi;
import pl.vo.organisation.model.OrganisationUnit;
import pl.vo.products.api.ProductsApi;
import pl.vo.products.model.Product;

/**
 *
 * @author k.skowronski
 */
@SessionScoped
@Stateful(mappedName = "EdifactOrdrspImport", name = "EdifactOrdrspImport")
@LocalBean
public class EdifactOrdrspImport implements Serializable {
    
    @EJB
    CompanysApi companyApi;

    @EJB
    ProductsApi productsApi;

    @EJB
    DocumentsApi documentsApi;

    @EJB
    OrganisationApi organisationApi;

    @EJB
    PriceListsApi priceListsApi;
   
    @EJB
    NumerationApi numerationApi; 
    
    private String username; 
    Document doc; 
    // parse edifact message and save document in database
    public Document parseAndProcess(Company cmpSeder, Company cmpRecipent, Ordrsp orders) throws VOWrongDataException {
        
        
        this.doc = new Document();

        // fill header
        doc.setDateSend(new Date());
        doc.setType(VOConsts.DOC_TYPE_ZWD);
        doc.setStatus(VOConsts.DOC_STATUS_CONFIRMED_BY_SUPPLIER);
        doc.setClient( companyApi.getByNip( VOConsts.NIP_VENDI ));
        doc.setSupplier(  cmpRecipent );
        
        // parse file
        Document orderDoc = parseOrdrsp(orders);

        // sprawdz zgodnosc z nadawca o odbiorca..
        // ustaw konteskt - bardzo wazne !!!
        orderDoc.setInstanceCode(cmpRecipent.getInstanceCode());
        

        // recalculate
        documentsApi.recalculateDocument(orderDoc);
        // create number
        numerationApi.fillNumerIfNotExists(orderDoc);
        //
        
        // pobrać id zamówienia i pozycji tak aby nie było insert tylko update   
        documentsApi.checkIdDocAndIdItems(orderDoc);
        
        orderDoc = documentsApi.save(orderDoc);
        return orderDoc;
        // 
    }
    
    private void iParseBOM(Document doc, BeginningOfMessage bom) {
        String orderNumber = bom.getDocumentMessageNumber();
        doc.setExternalNumber(orderNumber);
    }

    private void iParseNAD(Document doc, NameAndAddress nad) {
        if (nad.getPartyQualifier().equals("BY")) {
            String buyerId = nad.getPartyIdentificationDetails().getPartyIdIdentification();
            // find buyer by nip
            Company buyer = companyApi.getByNip(buyerId);
            doc.setClient(buyer);
        } else if (nad.getPartyQualifier().equals("SU")) {
            // nip dostawcy
            String supplierId = nad.getPartyIdentificationDetails().getPartyIdIdentification();
            try {
                Company supplier = companyApi.getByNip(supplierId);
                doc.setSupplier(supplier);
            } catch (VoNoResultException nre) {
                throw new RuntimeException("Nie udało się przetworzyć zamówienia - nieznana firma dostawcy o nipie:" + supplierId);
            }
        } else if (nad.getPartyQualifier().equals("DP")) {
            // Delivery Party - kod SK 
            String unitCode = nad.getPartyIdentificationDetails().getPartyIdIdentification();
            OrganisationUnit ou = organisationApi.getByCode(unitCode);
            doc.setCompanyUnit(ou);
        }

    }

    private void iParseDTP(Document doc, DateTimePeriod dtp) 
    {
        String deliveryDateYYYYMMDD = dtp.getDateTimePeriod().getDateTimePeriod();
        Date date = EdifactProvider.parseDate(deliveryDateYYYYMMDD, dtp.getDateTimePeriod().getDateTimePeriodFormatQualifier());

        if (dtp.getDateTimePeriod().getDateTimePeriodQualifier().equals("2")) {

            doc.setDateDelivery(date);
        } else if (dtp.getDateTimePeriod().getDateTimePeriodQualifier().equals("137")) {
            doc.setDateOperation(date);
        } else if (dtp.getDateTimePeriod().getDateTimePeriodQualifier().equals("17")) {
            doc.setDateDelivery(date);
        }

    }
    
    
    public Document parseOrdrsp(Ordrsp orders) throws VOWrongDataException {
        Document orderDoc = new Document();

        // fill header
        orderDoc.setDateSend(new Date());
        orderDoc.setType(VOConsts.DOC_TYPE_ZWD);
        orderDoc.setStatus(VOConsts.DOC_STATUS_CONFIRMED_BY_SUPPLIER);

        iParseBOM(orderDoc, orders.getBeginningOfMessage());

        // segment group2  - buyer and supplier
        for (SegmentGroup3 sg3 : orders.getSegmentGroup3()) {
            iParseNAD(orderDoc, sg3.getNameAndAddress());

        }

        for (DateTimePeriod dtp : orders.getDateTimePeriod()) {
            iParseDTP(orderDoc, dtp);
        }
        // segment - pozycje
        for (SegmentGroup26 sg26 : orders.getSegmentGroup26()) {
            DocumentItem docItem = new DocumentItem();
            String itemDescriptionText;

            orderDoc.getItems().add(docItem);
            docItem.setDocument(orderDoc);

            for (org.milyn.edi.unedifact.d96a.common.ItemDescription itd : sg26.getItemDescription()) {
                itemDescriptionText = itd.getItemDescription().getItemDescription1();
            }

            iparseDocumentItem(docItem, sg26.getAdditionalProductId(), sg26.getQuantity());

            // price 
            for (SegmentGroup30 sg30 : sg26.getSegmentGroup30()) {
                if (sg30.getPriceDetails() != null) {
                    docItem.setUnitPriceNet(sg30.getPriceDetails().getPriceInformation().getPrice());
                }
            }
            
            

        }

        // segmen7 - currences
        return orderDoc;
    }
    
    
    private void iparseDocumentItem(DocumentItem docItem, List<AdditionalProductId> prodIds, List<Quantity> quantities) throws VOWrongDataException
    {

        for (AdditionalProductId prodId : prodIds)
        {

            if (prodId.getItemNumberIdentification1().getItemNumberTypeCoded().equals("SA")) {
                String SupplierProductId = prodId.getItemNumberIdentification1().getItemNumber();
                // find product by this code 
                try {
                    Product prod = productsApi.getByIndex(SupplierProductId);
                    docItem.setProduct(prod);
                } catch (VoNoResultException nre) {
                    throw new VOWrongDataException("Nie udało się przetworzyć zamóienia - nieznany towar o indeksie:" + SupplierProductId);
                }
            }
            if (prodId.getItemNumberIdentification1().getItemNumberTypeCoded().equals("BP")) {
               // String buyerProductId = prodId.getItemNumberIdentification1().getItemNumber();
                String buyerProductId = prodId.getItemNumberIdentification1().getItemNumber();
                 Product prod = null ;
                try {
                     prod = productsApi.getByCmpIndex(buyerProductId, this.doc.getSupplier().getId());
                      docItem.setProduct( prod );
                   
                } catch (VoNoResultException nre) {
                    
                    // proboj znalezc wg glownego indexu
                    
//                    throw new VOWrongDataException("Nie udało się przetworzyć zamóienia - nieznany towar o indeksie:" + SupplierProductId);
                }
                try {
                     prod = productsApi.getByExternalCode(buyerProductId);
                     docItem.setProduct( prod );
                   
                } catch (VoNoResultException nre) {
                    
                }
                
                for ( Quantity q : quantities )
                {
                    if ( q.getQuantityDetails().getQuantityQualifier().equals("21") )
                    {
                       docItem.setAmount( q.getQuantityDetails().getQuantity() ); 
                    }
                    
                    if ( q.getQuantityDetails().getQuantityQualifier().equals("113") )
                    {
                        docItem.setAmountConfirmed( q.getQuantityDetails().getQuantity() ); 
                    }
                }
                
            }
        }
    }
    
    
}
