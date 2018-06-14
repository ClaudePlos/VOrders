/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.integration.edifact;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.enterprise.context.SessionScoped;
import org.milyn.edi.unedifact.d96a.D96AInterchangeFactory;
import org.milyn.edi.unedifact.d96a.DESADV.Desadv;
import org.milyn.edi.unedifact.d96a.DESADV.SegmentGroup10;
import org.milyn.edi.unedifact.d96a.DESADV.SegmentGroup15;
import org.milyn.edi.unedifact.d96a.ORDERS.Orders;
import org.milyn.edi.unedifact.d96a.ORDERS.SegmentGroup1;
import org.milyn.edi.unedifact.d96a.ORDERS.SegmentGroup2;
import org.milyn.edi.unedifact.d96a.ORDERS.SegmentGroup25;
import org.milyn.edi.unedifact.d96a.ORDERS.SegmentGroup28;
import org.milyn.edi.unedifact.d96a.common.AdditionalProductId;
import org.milyn.edi.unedifact.d96a.common.BeginningOfMessage;
import org.milyn.edi.unedifact.d96a.common.DateTimePeriod;
import org.milyn.edi.unedifact.d96a.common.NameAndAddress;
import org.milyn.edi.unedifact.d96a.common.Quantity;
import org.milyn.smooks.edi.unedifact.model.UNEdifactInterchange;
import org.milyn.smooks.edi.unedifact.model.r41.UNEdifactInterchange41;
import org.milyn.smooks.edi.unedifact.model.r41.UNEdifactMessage41;
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
 * @author Piotr
 */
@SessionScoped
@Stateful(mappedName = "EdifactOrderImport", name = "EdifactOrderImport")
@LocalBean
public class EdifactOrderImport implements Serializable {

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
    public Document parseAndProcess(Company cmpSeder, Company cmpRecipent, Orders orders) throws VOWrongDataException {
        // parse file
        Document orderDoc = parseOrders(orders);

        // sprawdz zgodnosc z nadawca o odbiorca..
        // ustaw konteskt - bardzo wazne !!!
        orderDoc.setInstanceCode(cmpRecipent.getInstanceCode());

        // recalculate
        documentsApi.recalculateDocument(orderDoc);
        // create number
        numerationApi.fillNumerIfNotExists(orderDoc);
        //
        orderDoc = documentsApi.save(orderDoc);
        return orderDoc;
        // 
    }

    public Document parseDesadv(Company cmpSeder, Company cmpRecipent, Desadv desadv) throws VOWrongDataException {
        // parse file
        Document dpzDoc = parseDesadv(desadv);
          numerationApi.fillNumerIfNotExists(dpzDoc);
        dpzDoc.setInstanceCode(cmpRecipent.getInstanceCode());

        // get prices for items 
        for (DocumentItem item : dpzDoc.getItems()) {

            try {
                DocumentItem priceItem = priceListsApi.findPriceForProduct(item.getProduct(), dpzDoc.getSupplier(), dpzDoc.getDateOperation());
                item.setPriceItem(priceItem);
                item.setUnitPriceNet(priceItem.getUnitPriceNet());
            } catch (VoNoResultException nre) {
                throw new VOWrongDataException("Nie można przyjąć dokumentu " + nre.getMessage());
//                + item.getProduct().getName() 
//                + " dostawca: " + dpzDoc.getSupplier().getAbbr() + " data:" + dpzDoc.getDateOperation().toString());
            }
        }
        // recalculate
        documentsApi.recalculateDocument(dpzDoc);
        dpzDoc = documentsApi.save(dpzDoc);
        return dpzDoc;
    }

    // parsuje sekcje z desadv
    public Document parseDesadv(Desadv desadv) throws VOWrongDataException {
        this.doc = new Document();

        // fill header
        doc.setDateSend(new Date());
        doc.setType(VOConsts.DOC_TYPE_DPZ);
        doc.setStatus(VOConsts.DOC_STATUS_CONFIRMED_BY_SUPPLIER);
        doc.setClient( companyApi.getByNip( VOConsts.NIP_VENDI ));
        
       

        iParseBOM(doc, desadv.getBeginningOfMessage());

        // segment group2  - buyer and supplier
        for (org.milyn.edi.unedifact.d96a.DESADV.SegmentGroup2 sg2 : desadv.getSegmentGroup2()) {
            iParseNAD(doc, sg2.getNameAndAddress());
        }
 
        for (DateTimePeriod dtp : desadv.getDateTimePeriod()) {
            iParseDTP(doc, dtp);
        }
        // segment - pozycje
        for (SegmentGroup10 sg10 : desadv.getSegmentGroup10()) {

            for (SegmentGroup15 sg15 : sg10.getSegmentGroup15()) {
                DocumentItem docItem = new DocumentItem();
                String itemDescriptionText;

                doc.getItems().add(docItem);
                docItem.setDocument(doc);

                for (org.milyn.edi.unedifact.d96a.common.ItemDescription itd : sg15.getItemDescription()) {
                    itemDescriptionText = itd.getItemDescription().getItemDescription1();
                }

                iparseDocumentItemDesAdv(docItem, sg15.getAdditionalProductId(), sg15.getQuantity());

                //TODO - ustalic powiazana pozycje zamowienia !
                // price 
//                for (SegmentGroup28 sg28 : sg15.getSegmentGroup28()) 
//                {
//                    if (sg28.getPriceDetails() != null) {
//                        docItem.setUnitPriceNet(sg28.getPriceDetails().getPriceInformation().getPrice());
//                    }
//                }
            }

        }

        return doc;
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

    private void iparseDocumentItemDesAdv(DocumentItem docItem, List<AdditionalProductId> prodIds, List<Quantity> quantities) throws VOWrongDataException
    {
        for (AdditionalProductId prodId : prodIds)
        {

            if (prodId.getItemNumberIdentification1().getItemNumberTypeCoded().equals("SA")) {
                String SupplierProductId = prodId.getItemNumberIdentification1().getItemNumber();
                // find product by this code 
                Product prod = null ;
                try {
                     prod = productsApi.getByCmpIndex(SupplierProductId, this.doc.getSupplier().getId());
                   
                } catch (VoNoResultException nre) {
                    
                    // proboj znalezc wg glownego indexu
                    
//                    throw new VOWrongDataException("Nie udało się przetworzyć zamóienia - nieznany towar o indeksie:" + SupplierProductId);
                }
                if (prod==null){
                    try {
                         prod = productsApi.getByIndex(SupplierProductId);

                    } catch (VoNoResultException nre) {

                    }
                }
                if ( prod == null )
                {
                   throw new VOWrongDataException("Nie udało się przetworzyć zamóienia - nieznany towar o indeksie:" + SupplierProductId); 
                }
                 docItem.setProduct(prod);
            }
            if (prodId.getItemNumberIdentification1().getItemNumberTypeCoded().equals("BP")) {
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
                     prod = productsApi.getByIndex(buyerProductId);
                     docItem.setProduct( prod );
                   
                } catch (VoNoResultException nre) {
                    
                }
            }
            
            
            
        }

        for (Quantity quant : quantities)
        {
            String measureUnit = quant.getQuantityDetails().getMeasureUnitQualifier();
            BigDecimal quantity = quant.getQuantityDetails().getQuantity();
            docItem.setAmount( new BigDecimal( quantity.doubleValue() ) );
            // TODO - takie uproszczenie ..
            docItem.setAmountConfirmed( new BigDecimal( quantity.doubleValue() ) );
        }
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
                    throw new VOWrongDataException("Nie udało się przetworzyć zamóienia - nieznany towar o indeksie:" + SupplierProductId
                    + " sprawdz hasla");
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
                     prod = productsApi.getByIndex(buyerProductId);
                     docItem.setProduct( prod );
                   
                } catch (VoNoResultException nre) {
                    
                }
                
            }
        }

        for (Quantity quant : quantities)
        {
            String measureUnit = quant.getQuantityDetails().getMeasureUnitQualifier();
            BigDecimal quantity = quant.getQuantityDetails().getQuantity();
            docItem.setAmount( new BigDecimal( quantity.doubleValue() ) );
            // TODO - takie uproszczenie ..
            docItem.setAmountConfirmed( new BigDecimal( quantity.doubleValue() ) );
        }
    }

    private void iParseQuantity(DocumentItem docItem, Quantity quant) {

    }
    // parsuje sekcje z orders

    public Document parseOrders(Orders orders) throws VOWrongDataException {
        Document orderDoc = new Document();

        // fill header
        orderDoc.setDateSend(new Date());
        orderDoc.setType(VOConsts.DOC_TYPE_ZWD);
        orderDoc.setStatus(VOConsts.DOC_STATUS_RECEIVED_BY_SUPPLIER);

        iParseBOM(orderDoc, orders.getBeginningOfMessage());

        // segment group2  - buyer and supplier
        for (SegmentGroup2 sg2 : orders.getSegmentGroup2()) {
            iParseNAD(orderDoc, sg2.getNameAndAddress());

        }

        for (DateTimePeriod dtp : orders.getDateTimePeriod()) {
            iParseDTP(orderDoc, dtp);
        }
        // segment - pozycje
        for (SegmentGroup25 sg25 : orders.getSegmentGroup25()) {
            DocumentItem docItem = new DocumentItem();
            String itemDescriptionText;

            orderDoc.getItems().add(docItem);
            docItem.setDocument(orderDoc);

            for (org.milyn.edi.unedifact.d96a.common.ItemDescription itd : sg25.getItemDescription()) {
                itemDescriptionText = itd.getItemDescription().getItemDescription1();
            }
            
            if ( this.doc == null )
            {
                this.doc = orderDoc;
            }

            iparseDocumentItem(docItem, sg25.getAdditionalProductId(), sg25.getQuantity());

            // price 
            for (SegmentGroup28 sg28 : sg25.getSegmentGroup28()) {
                if (sg28.getPriceDetails() != null) {
                    docItem.setUnitPriceNet(sg28.getPriceDetails().getPriceInformation().getPrice());
                }
            }

        }

        // segmen7 - currences
        return orderDoc;
    }

}
