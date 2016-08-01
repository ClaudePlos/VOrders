/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.integration.edifact;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import org.milyn.edi.unedifact.d96a.ORDERS.Orders;
import org.milyn.edi.unedifact.d96a.PRICAT.Pricat;
import org.milyn.edi.unedifact.d96a.PRICAT.SegmentGroup16;
import org.milyn.edi.unedifact.d96a.PRICAT.SegmentGroup2;
import org.milyn.edi.unedifact.d96a.PRICAT.SegmentGroup3;
import org.milyn.edi.unedifact.d96a.PRICAT.SegmentGroup33;
import org.milyn.edi.unedifact.d96a.PRICAT.SegmentGroup35;
import org.milyn.edi.unedifact.d96a.common.DateTimePeriod;
import org.milyn.edi.unedifact.d96a.common.NameAndAddress;
import pl.vo.VOConsts;
import pl.vo.company.api.CompanysApi;
import pl.vo.company.model.Company;
import pl.vo.documents.DocumentsApi;
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
@Stateful
@SessionScoped
@LocalBean
public class EdifactPricatImport implements Serializable {
    
    @EJB
    CompanysApi companyApi;
    
    @EJB
    DocumentsApi documentsApi;
    
    @EJB
    OrganisationApi organisationApi;
    
    @EJB
    NumerationApi numerationApi; 
    
    @EJB
    ProductsApi productsApi;
    
    private String username; 
    
    public Document parseAndProcess(Company cmpSeder, Company cmpRecipent, Pricat pricat) throws VOWrongDataException 
    {
        
        Document pricatDoc = parsePricat(pricat);
        
        pricatDoc.setInstanceCode(cmpRecipent.getInstanceCode());
        
     
        // create number
        numerationApi.fillNumerIfNotExists(pricatDoc);
        //
       
        pricatDoc = documentsApi.save(pricatDoc);
        
        return pricatDoc;
    }
    
    
    public Document parsePricat(Pricat pricat) throws VOWrongDataException {
        Document pricatDoc = new Document();
        
        // fill header
        pricatDoc.setDateSend(new Date());
        pricatDoc.setType(VOConsts.DOC_TYPE_PRICE_LIST);
        pricatDoc.setStatus(VOConsts.DOC_STATUS_ACCEPTED);
        
        // cennik from - until
      
        for (SegmentGroup2 sg2 : pricat.getSegmentGroup2() ) 
        {
            //NAD
            iParseNAD(pricatDoc, sg2.getNameAndAddress());  
        }
        
        
        //DTM pricat from until
        for (DateTimePeriod dtp : pricat.getDateTimePeriod()) 
        {
            iParseDTP(pricatDoc, dtp);
        }
         
         // chec
        if ( pricatDoc.getSupplier() == null ) 
            throw new VOWrongDataException("EPI80: otrzymany cennik nie ma dostawcy");
        // segment - pozycje
        for (SegmentGroup16 sg16 : pricat.getSegmentGroup16() ) 
        {
            
            for (SegmentGroup33 sg33 : sg16.getSegmentGroup33())
            {
                String itemDescriptionName = null;
                String tax = null;
                BigDecimal unitPriceNet = null;
                
                DocumentItem docItem = new DocumentItem();
                pricatDoc.getItems().add(docItem);
                docItem.setDocument(pricatDoc);
                Product product = null;
         
                for (org.milyn.edi.unedifact.d96a.common.ItemDescription itd : sg33.getItemDescription()) {
                   
                    product  = productsApi.getByCmpIndex(itd.getItemDescription().getCodeListQualifier() , pricatDoc.getSupplier().getId());
                   
                    itemDescriptionName = itd.getItemDescription().getItemDescriptionIdentification();  
                    
                }
                
                for (org.milyn.edi.unedifact.d96a.common.Measurements m : sg33.getMeasurements()) {
                    unitPriceNet = m.getValueRange().getMeasurementValue();
                }
                
                for ( SegmentGroup35 sg35 : sg33.getSegmentGroup35()) {
                    tax = sg35.getDutyTaxFeeDetails().getDutyTaxFeeDetail().getDutyTaxFeeRate();
                }

                //product.setName(itemDescriptionName);
                product.setVersion(1);
                product.setTaxRate( new BigDecimal(tax) );
                docItem.setProduct(product);
                docItem.setUnitPriceNet(unitPriceNet);
                docItem.setAmount(new BigDecimal(0));
            }
            
        }
        
        
        return pricatDoc;
    }
    
    
    private void iParseDTP(Document doc, DateTimePeriod dtp) {
        String pDateYYYYMMDD = dtp.getDateTimePeriod().getDateTimePeriod();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

        Date date = null;
        try {
            date = df.parse(pDateYYYYMMDD);
        } catch (ParseException ex) {
            Logger.getLogger(EdifactPricatImport.class.getName()).log(Level.SEVERE, null, ex);
        }
                
  
        if (dtp.getDateTimePeriod().getDateTimePeriodQualifier().equals("157")) {
            doc.setValidFrom(date);
        } else if (dtp.getDateTimePeriod().getDateTimePeriodQualifier().equals("21E")) {
            doc.setValidTill(date);
        }
        
        doc.setDateOperation( new Date() );

    }
    
    private void iParseNAD(Document doc, NameAndAddress nad) {
        if (nad.getPartyQualifier().equals("BY"))
        {
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
                throw new RuntimeException("Nie udało się przetworzyć cennika - nieznana firma dostawcy o nipie:" + supplierId);
            }
        } else if (nad.getPartyQualifier().equals("DP")) {
            // Delivery Party - kod SK 
            String unitCode = nad.getPartyIdentificationDetails().getPartyIdIdentification();
            OrganisationUnit ou = organisationApi.getByCode(unitCode);
            doc.setCompanyUnit(ou);
        }

    }
    
}
