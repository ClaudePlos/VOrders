/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.integration.edifact;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.milyn.edi.unedifact.d96a.D96AInterchangeFactory;
import org.milyn.edi.unedifact.d96a.ORDRSP.*;
import org.milyn.edi.unedifact.d96a.ORDRSP.SegmentGroup3;
import org.milyn.edi.unedifact.d96a.ORDRSP.Ordrsp;
import org.milyn.edi.unedifact.d96a.ORDRSP.SegmentGroup26;
import org.milyn.edi.unedifact.d96a.common.AdditionalProductId;
import org.milyn.edi.unedifact.d96a.common.BeginningOfMessage;
import org.milyn.edi.unedifact.d96a.common.DateTimePeriod;
import org.milyn.edi.unedifact.d96a.common.ItemDescription;
import org.milyn.edi.unedifact.d96a.common.LineItem;
import org.milyn.edi.unedifact.d96a.common.NameAndAddress;
import org.milyn.edi.unedifact.d96a.common.PriceDetails;
import org.milyn.edi.unedifact.d96a.common.Quantity;
import org.milyn.edi.unedifact.d96a.common.RelatedIdentificationNumbers;
import org.milyn.edi.unedifact.d96a.common.SectionControl;
import org.milyn.edi.unedifact.d96a.common.field.DocumentMessageNameC002;
import org.milyn.edi.unedifact.d96a.common.field.IdentificationNumberC206;
import org.milyn.edi.unedifact.d96a.common.field.ItemDescriptionC273;
import org.milyn.edi.unedifact.d96a.common.field.ItemNumberIdentificationC212;
import org.milyn.edi.unedifact.d96a.common.field.PartyIdentificationDetailsC082;
import org.milyn.edi.unedifact.d96a.common.field.PartyNameC080;
import org.milyn.edi.unedifact.d96a.common.field.PriceInformationC509;
import org.milyn.edi.unedifact.d96a.common.field.QuantityDetailsC186;
import org.milyn.smooks.edi.unedifact.model.r41.UNEdifactInterchange41;
import org.milyn.smooks.edi.unedifact.model.r41.UNEdifactMessage41;
import org.milyn.smooks.edi.unedifact.model.r41.UNT41;
import org.milyn.smooks.edi.unedifact.model.r41.UNZ41;
import pl.vo.VOConsts;
import pl.vo.company.model.Company;
import pl.vo.documents.model.Document;
import pl.vo.documents.model.DocumentItem;
import pl.vo.exceptions.VOWrongDataException;
import pl.vo.exceptions.VoNoResultException;
import static pl.vo.integration.edifact.EdifactExport.MESSAGE_TYPE_DESADV;
import pl.vo.organisation.model.OrganisationUnit;



/**
 *
 * @author k.skowronski (ClaudePlos)
 */
public class EdifactExportOrdrsp {
    
    D96AInterchangeFactory factory;
    Document document;

    UNEdifactInterchange41 interchange;
    UNEdifactMessage41 message;
    
    String messageType = "";
    
    Date messageCreationDate = new Date();
    Long ti = new Long(messageCreationDate.getTime());
    String messageRef = ti.toString();
    

    
    public EdifactExportOrdrsp() {

    }

    public UNEdifactInterchange41 generateZwdConfirm( Document doc, UNEdifactMessage41 message ) throws VOWrongDataException {
        
        document = doc;
        
        interchange = new UNEdifactInterchange41();

        //  create order message
        Ordrsp orderMsg = new Ordrsp();

        message.setMessage(orderMsg);
        interchange.setMessages(new ArrayList<UNEdifactMessage41>());
        interchange.getMessages().add(message);

        // order number 
        BeginningOfMessage bgm = new BeginningOfMessage();
        bgm.setDocumentMessageNumber(document.getOwnNumber());
        orderMsg.setBeginningOfMessage(bgm);

        bgm.setDocumentMessageName(new DocumentMessageNameC002());
        bgm.getDocumentMessageName().setDocumentMessageNameCoded("220");

        orderMsg.setDateTimePeriod(new ArrayList<DateTimePeriod>());
        insertDateTime(orderMsg.getDateTimePeriod());

        orderMsg.setSegmentGroup3( new ArrayList<SegmentGroup3>() );
        createParties( orderMsg.getSegmentGroup3() );

        // create items 
        orderMsg.setSegmentGroup26(new ArrayList<SegmentGroup26>());
        createLineItemsOrder(orderMsg.getSegmentGroup26());
        

        // create section control
        SectionControl sc = new SectionControl();
        sc.setSectionIdentification("S");
        orderMsg.setSectionControl(sc);

        message.setMessageTrailer(new UNT41());
        message.getMessageTrailer().setSegmentCount(1);

        message.getMessageTrailer().setMessageRefNum(messageRef);
        // trailer
        //message.setGroupTrailer( new UNE41());
        //message.getGroupTrailer().setControlCount(1);
        //message.getGroupTrailer().setGroupRef(messageRef);

        interchange.setInterchangeTrailer(new UNZ41());
        interchange.getInterchangeTrailer().setControlCount(1);
        interchange.getInterchangeTrailer().setControlRef(messageRef);
        
        return interchange;

    }
    
    private void createParties(List<SegmentGroup3> list) throws VOWrongDataException {
        // buyer info
        if (document.getClient() == null) {
            throw new VOWrongDataException("Błąd - dokument nie ma wypełnionego pola klienta");
        }
        SegmentGroup3 sg3by = new SegmentGroup3();
        NameAndAddress naaBy = createNAD("BY", document.getClient());
        sg3by.setNameAndAddress(naaBy);
        list.add(sg3by);

        // DP - delivery partry ( kod SK ) 
        SegmentGroup3 sg3Dp = new SegmentGroup3();
        list.add(sg3Dp);
        NameAndAddress nadDp = createNadFromOrgUnit("DP", document.getCompanyUnit());
        sg3Dp.setNameAndAddress(nadDp);

        // supplier info
        SegmentGroup3 sg3su = new SegmentGroup3();
        NameAndAddress nadSu = createNAD("SU", document.getSupplier());
        sg3su.setNameAndAddress(nadSu);



        list.add(sg3su);

   
    }
    
    
    
    private void createLineItemsOrder(List<SegmentGroup26> list) throws VOWrongDataException {

        BigDecimal lineItemNumber = new BigDecimal(1);

        for (DocumentItem item : document.getItems()) {

            SegmentGroup26 sg26 = new SegmentGroup26();
            list.add(sg26);
            sg26.setAdditionalProductId(new ArrayList<AdditionalProductId>());
            // line item 

            sg26.setLineItem(createLineItem(lineItemNumber, item));
            sg26.setAdditionalProductId(createAdditionalProductIds(item));
            sg26.setItemDescription(createItemDescription(item));

            sg26.setQuantity(new ArrayList<Quantity>());

            Quantity quant = createQuantity("21", item.getAmount(), item.getProduct().getMeasureUnit().getAbbr());
            sg26.getQuantity().add(quant);
            
            Quantity quantConfirm = createQuantity("113", item.getAmountConfirmed(), item.getProduct().getMeasureUnit().getAbbr());
            sg26.getQuantity().add(quantConfirm);
            // supplier code

            ///
            /// don't think thats needed..
            // LineItem li = new LineItem();
            //li.setLineItemNumber(BigDecimal.ONE)
            //  
            // 
            // sg28 with price - not needed
            PriceDetails pri = createPriceDetails("NTP", item.getUnitPriceNet());
            SegmentGroup30 sg30 = new SegmentGroup30();
            sg30.setPriceDetails(pri);
            sg26.setSegmentGroup30(new ArrayList<SegmentGroup30>());
            sg26.getSegmentGroup30().add(sg30);
//            
//            sg25.set
//            // dodatkowe info - kod ref
//            sg25.setAdditionalInformation( new ArrayList<AdditionalInformation>());
//            AdditionalInformation ai = new AdditionalInformation();
//            ai.set

            // nie wysylamy tego dla zamowienia
            if (document.getType().equals(VOConsts.DOC_TYPE_DPZ)) {
                sg26.setRelatedIdentificationNumbers(getRINforItem(item));
            }
            lineItemNumber = lineItemNumber.add(new BigDecimal(1));
        }

    }
    
    
    
    
    
    private void insertDateTime(List<DateTimePeriod> list) {
        // create DTM ( 2 - delivery time, 137 - document date
        list.add(EdifactProvider.dateToDateTimePeriod(document.getDateOperation(), "137"));
        // 
        if (messageType.equals("ORDRSP")) {
            list.add(EdifactProvider.dateToDateTimePeriod(document.getDateDelivery(), "2"));
        }
        if (messageType.equals(MESSAGE_TYPE_DESADV)) {
            /// 17 - planowana data dostawy
            list.add(EdifactProvider.dateToDateTimePeriod(document.getDateDelivery(), "17"));
        }
    }
    
    private NameAndAddress createNAD(String partyQualifier, Company company) {
        NameAndAddress nad = new NameAndAddress();
        nad.setPartyQualifier(partyQualifier);

        PartyIdentificationDetailsC082 pidBy = new PartyIdentificationDetailsC082();
        pidBy.setPartyIdIdentification(company.getNip());
        nad.setPartyIdentificationDetails(pidBy);

        nad.setPartyName(new PartyNameC080());
        nad.getPartyName().setPartyName1(company.getName());

        return nad;
    }
    
    private NameAndAddress createNadFromOrgUnit(String partyQualifier, OrganisationUnit orgUnit) {
        NameAndAddress nad = new NameAndAddress();
        nad.setPartyQualifier(partyQualifier);
        nad.setPartyIdentificationDetails(new PartyIdentificationDetailsC082());
        nad.getPartyIdentificationDetails().setPartyIdIdentification(orgUnit.getCode());

        nad.setPartyName(new PartyNameC080());
        nad.getPartyName().setPartyName1(orgUnit.getName());
        return nad;
    }
    
    private LineItem createLineItem(BigDecimal lineItemNumber, DocumentItem item) {
        LineItem li = new LineItem();
        li.setLineItemNumber(lineItemNumber);
        li.setItemNumberIdentification(new ItemNumberIdentificationC212());
        li.getItemNumberIdentification().setItemNumber(item.getProduct().getIndex());
        li.getItemNumberIdentification().setItemNumberTypeCoded("EN");

        return li;

    }
    
    private List<AdditionalProductId> createAdditionalProductIds(DocumentItem item) throws VOWrongDataException {
        List<AdditionalProductId> ret = new ArrayList<AdditionalProductId>();
        AdditionalProductId prodCode = new AdditionalProductId();
        // ustal index towaru dla dostawcy

       

        AdditionalProductId prodCodeBp = new AdditionalProductId();
        prodCodeBp.setItemNumberIdentification1(new ItemNumberIdentificationC212());
        prodCodeBp.getItemNumberIdentification1().setItemNumber(item.getProduct().getIndex());
        prodCodeBp.getItemNumberIdentification1().setItemNumberTypeCoded("BP");
        prodCodeBp.setProductIdFunctionQualifier("5");
        ret.add(prodCodeBp);
        
        
        try {
             String supplier_code = null;
            supplier_code = item.getProduct().getCodeForSupplier(document.getSupplier().getId());
            prodCode.setItemNumberIdentification1(new ItemNumberIdentificationC212());
            prodCode.getItemNumberIdentification1().setItemNumber(supplier_code);
            prodCode.getItemNumberIdentification1().setItemNumberTypeCoded("SA");
            prodCode.setProductIdFunctionQualifier("5");
            ret.add(prodCode);
        } catch (VoNoResultException nre) {
//            throw new VOWrongDataException("Błąd eksportu:" + nre.getMessage(), nre);
        }

        return ret;
    }

    private List<ItemDescription> createItemDescription(DocumentItem item) {
        List<ItemDescription> ret = new ArrayList<ItemDescription>();
        ItemDescription itd = new ItemDescription();
        itd.setItemDescription(new ItemDescriptionC273());
        itd.getItemDescription().setItemDescription1(item.getProduct().getName());
        itd.setItemDescriptionTypeCoded("A");
        ret.add(itd);

        return ret;

    }

    private Quantity createQuantity(String qualifier, BigDecimal quantity, String measureUnit) {
        Quantity quant = new Quantity();
        quant.setQuantityDetails(new QuantityDetailsC186());
        quant.getQuantityDetails().setQuantity(quantity);
        quant.getQuantityDetails().setQuantityQualifier(qualifier);
        quant.getQuantityDetails().setMeasureUnitQualifier(measureUnit);
        return quant;

    }

    private PriceDetails createPriceDetails(String priceQualifier, BigDecimal price) {
        PriceDetails pri = new PriceDetails();
        pri.setPriceInformation(new PriceInformationC509());
        pri.getPriceInformation().setPrice(price);
        pri.getPriceInformation().setPriceQualifier("NTP"); // netto cena jednostkowa
        return pri;
    }
    
    private List<RelatedIdentificationNumbers> getRINforItem(DocumentItem item) {
        List<RelatedIdentificationNumbers> ret = new ArrayList<RelatedIdentificationNumbers>();
        RelatedIdentificationNumbers rid = new RelatedIdentificationNumbers();
        rid.setIdentificationNumber1(new IdentificationNumberC206());
        rid.getIdentificationNumber1().setIdentityNumber(item.getId().toString());
        rid.getIdentificationNumber1().setIdentityNumberQualifier("VORDERS_ITEMID");
        ret.add(rid);
        return ret;
    }
    
}
