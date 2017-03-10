/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.integration.edifact;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.milyn.edi.unedifact.d96a.D96AInterchangeFactory;
import org.milyn.edi.unedifact.d96a.DESADV.Desadv;
import pl.vo.documents.model.Document;
import pl.vo.exceptions.VOWrongDataException;

import org.milyn.edi.unedifact.d96a.ORDERS.*;
import org.milyn.edi.unedifact.d96a.ORDERS.SegmentGroup2;
import org.milyn.edi.unedifact.d96a.ORDRSP.Ordrsp;
import org.milyn.edi.unedifact.d96a.common.AdditionalInformation;
import org.milyn.edi.unedifact.d96a.common.AdditionalProductId;
import org.milyn.edi.unedifact.d96a.common.BeginningOfMessage;
import org.milyn.edi.unedifact.d96a.common.ConsignmentPackingSequence;
import org.milyn.edi.unedifact.d96a.common.DateTimePeriod;
import org.milyn.edi.unedifact.d96a.common.ItemDescription;
import org.milyn.edi.unedifact.d96a.common.LineItem;
import org.milyn.edi.unedifact.d96a.common.NameAndAddress;
import org.milyn.edi.unedifact.d96a.common.PriceDetails;
import org.milyn.edi.unedifact.d96a.common.Quantity;
import org.milyn.edi.unedifact.d96a.common.Reference;
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
import org.milyn.edi.unedifact.d96a.common.field.ReferenceC506;
import org.milyn.edisax.model.internal.Delimiters;
import org.milyn.smooks.edi.unedifact.model.r41.UNB41;
import org.milyn.smooks.edi.unedifact.model.r41.UNEdifactInterchange41;
import org.milyn.smooks.edi.unedifact.model.r41.UNEdifactMessage41;
import org.milyn.smooks.edi.unedifact.model.r41.UNH41;
import org.milyn.smooks.edi.unedifact.model.r41.UNT41;
import org.milyn.smooks.edi.unedifact.model.r41.UNZ41;
import org.milyn.smooks.edi.unedifact.model.r41.types.DateTime;
import org.milyn.smooks.edi.unedifact.model.r41.types.MessageIdentifier;
import org.milyn.smooks.edi.unedifact.model.r41.types.Party;
import org.milyn.smooks.edi.unedifact.model.r41.types.SyntaxIdentifier;
import pl.vo.VOConsts;
import pl.vo.company.model.Company;
import pl.vo.documents.model.DocumentItem;
import pl.vo.exceptions.VoNoResultException;
import pl.vo.organisation.model.OrganisationUnit;

/**
 *
 * @author Piotr
 */
public class EdifactExport {

    public static String MESSAGE_TYPE_ORDERS = "ORDERS";
    public static String MESSAGE_TYPE_ORDRSP = "ORDRSP"; // potwierdzenie cen i ilości dostawcy
    public static String MESSAGE_TYPE_DESADV = "DESADV"; // awizo wysylki

    D96AInterchangeFactory factory;
    Document document;

    UNEdifactInterchange41 interchange;
    UNEdifactMessage41 message;
    Date messageCreationDate = new Date();
    Long ti = new Long(messageCreationDate.getTime());
    String messageRef = ti.toString();

    String messageType = "";

    public EdifactExport() {

    }

    public String generate(Document document) throws VOWrongDataException {
        this.document = document;
        factory = EdifactProvider.getInterchangeFactory();

        interchange = new UNEdifactInterchange41();
        message = new UNEdifactMessage41();
        
        
        // dodalem document.getStatus().equals(VOConsts.DOC_STATUS_ACCEPTED) bo jak jest ZWD po stronie dostawcy
        // i chcemy to ZWD odesłać z potwierdzeniem ilosci lub zamienników to generujemy ORDERSP 
        if (document.getType().equals(VOConsts.DOC_TYPE_ZWD) && document.getStatus().equals(VOConsts.DOC_STATUS_ACCEPTED)) {
            messageType = "ORDERS";
            generateZwd();
        }
        
        if (document.getType().equals(VOConsts.DOC_TYPE_ZWD) && document.getStatus().equals(VOConsts.DOC_STATUS_RECEIVED_BY_SUPPLIER)) {
            messageType = MESSAGE_TYPE_ORDRSP;

            EdifactExportOrdrsp ediOrdrspExport = new EdifactExportOrdrsp();
            interchange = ediOrdrspExport.generateZwdConfirm(document, message);
        }
        
        if (document.getType().equals(VOConsts.DOC_TYPE_DPZ)) {
            messageType = "DESADV";
            generateDpz();
        }
        
        generateCommonFields();
        // save 
        StringWriter ediOutStream = new StringWriter();

        interchange.setInterchangeDelimiters(new Delimiters());
        interchange.getInterchangeDelimiters().setComponent(":");
        interchange.getInterchangeDelimiters().setDecimalSeparator(".");
        interchange.getInterchangeDelimiters().setEscape("?");
        interchange.getInterchangeDelimiters().setField("+");
        interchange.getInterchangeDelimiters().setFieldRepeat(null);
        interchange.getInterchangeDelimiters().setSegment("\n");

        try {

            factory.toUNEdifact(interchange, ediOutStream);
        } catch (IOException ioe) {
            throw new VOWrongDataException("Błąd generacji edifact" + ioe.getLocalizedMessage());
        }

        System.out.println(ediOutStream.toString());
        String re = ediOutStream.toString();
        return ediOutStream.toString();
    }

    private void generateCommonFields() {

        UNB41 unb41 = createPartiesUNB41();
        UNH41 unh41 = createUNH41();
        interchange.setInterchangeHeader(unb41);
        message.setMessageHeader(unh41);
        message.setInterchangeHeader(unb41);

    }

    private void insertDateTime(List<DateTimePeriod> list) {
        // create DTM ( 2 - delivery time, 137 - document date
        list.add(EdifactProvider.dateToDateTimePeriod(document.getDateOperation(), "137"));
        // 
        if (messageType.equals("ORDERS")) {
            list.add(EdifactProvider.dateToDateTimePeriod(document.getDateDelivery(), "2"));
        }
        if (messageType.equals(MESSAGE_TYPE_DESADV)) {
            /// 17 - planowana data dostawy
            list.add(EdifactProvider.dateToDateTimePeriod(document.getDateDelivery(), "17"));
        }
    }

    private UNB41 createPartiesUNB41() {
        UNB41 unb41 = new UNB41();
        unb41.setDate(new DateTime());
        // recipent
        Party partyRec = new Party();
        unb41.setRecipient(partyRec);
        partyRec.setCodeQualifier("14");
        partyRec.setId(document.getSupplier().getNip());
        // sender
        Party partySender = new Party();
        unb41.setSender(partySender);
        partySender.setCodeQualifier("14");
        partySender.setId("5222899038");
        // 
        SyntaxIdentifier sid = new SyntaxIdentifier();
        sid.setId("UNOC");
        sid.setVersionNum("3");
        unb41.setSyntaxIdentifier(sid);

        // create datetime of message
        DateTime dm = new DateTime();
        dm.setDate(messageRef);

        unb41.setDate(EdifactProvider.dateToDateTime(messageCreationDate));
        unb41.setControlRef(messageRef);
        unb41.setApplicationRef("VENDI_APP_ORDERS");
        unb41.setTestIndicator("0");
        unb41.setAgreementId("AGGRID");
        unb41.setProcessingPriorityCode("1");
        unb41.setAckRequest("1");
        return unb41;
    }

    public UNH41 createUNH41() {

        UNH41 unh41 = new UNH41();
        unh41.setMessageIdentifier(new MessageIdentifier());
        unh41.getMessageIdentifier().setAssociationAssignedCode("EAN008");
        unh41.getMessageIdentifier().setControllingAgencyCode("UN");
        unh41.getMessageIdentifier().setId(messageType);
        unh41.getMessageIdentifier().setReleaseNum("96A");
        unh41.getMessageIdentifier().setVersionNum("D");
        unh41.setMessageRefNum("1");
        return unh41;

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

    private Reference createReferenceForNip(Company company) {
        Reference ref = new Reference();
        ref.setReference(new ReferenceC506());
        ref.getReference().setReferenceNumber(company.getNip());
        ref.getReference().setReferenceQualifier("VA");
        return ref;
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

    private void createParties(List<SegmentGroup2> list) throws VOWrongDataException {
        // buyer info
        if (document.getClient() == null) {
            throw new VOWrongDataException("Błąd - dokument nie ma wypełnionego pola klienta");
        }
        SegmentGroup2 sg2by = new SegmentGroup2();
        NameAndAddress naaBy = createNAD("BY", document.getClient());
        sg2by.setNameAndAddress(naaBy);
        sg2by.setSegmentGroup3(new ArrayList<SegmentGroup3>());
        sg2by.getSegmentGroup3().add(new SegmentGroup3());
        sg2by.getSegmentGroup3().get(0).setReference(createReferenceForNip(document.getClient()));

        list.add(sg2by);

        // DP - delivery partry ( kod SK ) 
        SegmentGroup2 sg2Dp = new SegmentGroup2();
        list.add(sg2Dp);
        NameAndAddress nadDp = createNadFromOrgUnit("DP", document.getCompanyUnit());
        sg2Dp.setNameAndAddress(nadDp);

        // supplier info
        SegmentGroup2 sg2su = new SegmentGroup2();
        NameAndAddress nadSu = createNAD("SU", document.getSupplier());
        sg2su.setNameAndAddress(nadSu);

        sg2su.setSegmentGroup3(new ArrayList<SegmentGroup3>());
        sg2su.getSegmentGroup3().add(new SegmentGroup3());
        sg2su.getSegmentGroup3().get(0).setReference(createReferenceForNip(document.getSupplier()));

        list.add(sg2su);

        // Buyer info ( our ) 
//        SegmentGroup2 sg2Bu = new SegmentGroup2();
//
//        sg2Bu.setNameAndAddress(new NameAndAddress());
//        sg2Bu.getNameAndAddress().setPartyQualifier("BY");
//        sg2Bu.getNameAndAddress().setPartyIdentificationDetails(new PartyIdentificationDetailsC082());
//        sg2Bu.getNameAndAddress().getPartyIdentificationDetails().setPartyIdIdentification("5222899038");
//        list.add(sg2Bu);
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

    private void createLineItemsOrder(List<SegmentGroup25> list) throws VOWrongDataException {

        BigDecimal lineItemNumber = new BigDecimal(1);

        for (DocumentItem item : document.getItems()) {

            SegmentGroup25 sg25 = new SegmentGroup25();
            list.add(sg25);
            sg25.setAdditionalProductId(new ArrayList<AdditionalProductId>());
            // line item 

            sg25.setLineItem(createLineItem(lineItemNumber, item));
            sg25.setAdditionalProductId(createAdditionalProductIds(item));
            sg25.setItemDescription(createItemDescription(item));

            sg25.setQuantity(new ArrayList<Quantity>());

            Quantity quant = createQuantity("21", item.getAmount(), item.getProduct().getMeasureUnit().getAbbr());
            sg25.getQuantity().add(quant);
            // supplier code

            ///
            /// don't think thats needed..
            // LineItem li = new LineItem();
            //li.setLineItemNumber(BigDecimal.ONE)
            //  
            // 
            // sg28 with price - not needed
            PriceDetails pri = createPriceDetails("NTP", item.getUnitPriceNet());
            SegmentGroup28 sg28 = new SegmentGroup28();
            sg28.setPriceDetails(pri);
            sg25.setSegmentGroup28(new ArrayList<SegmentGroup28>());
            sg25.getSegmentGroup28().add(sg28);
//            
//            sg25.set
//            // dodatkowe info - kod ref
//            sg25.setAdditionalInformation( new ArrayList<AdditionalInformation>());
//            AdditionalInformation ai = new AdditionalInformation();
//            ai.set

            // nie wysylamy tego dla zamowienia
            if (document.getType().equals(VOConsts.DOC_TYPE_DPZ)) {
                sg25.setRelatedIdentificationNumbers(getRINforItem(item));
            }
            lineItemNumber = lineItemNumber.add(new BigDecimal(1));
        }

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

    private Reference createReference(DocumentItem item) {
        Reference ref = new Reference();
        ref.setReference(new ReferenceC506());
        ref.getReference().setReferenceQualifier("ON");
        ref.getReference().setReferenceNumber(item.getExternalItemId());
        return ref;
    }

    private List<org.milyn.edi.unedifact.d96a.DESADV.SegmentGroup15> createLineItemsDpz() throws VOWrongDataException {

        List<org.milyn.edi.unedifact.d96a.DESADV.SegmentGroup15> list = new ArrayList<org.milyn.edi.unedifact.d96a.DESADV.SegmentGroup15>();
        BigDecimal lineItemNumber = new BigDecimal(1);

        for (DocumentItem item : document.getItems()) {

            org.milyn.edi.unedifact.d96a.DESADV.SegmentGroup15 sg15 = new org.milyn.edi.unedifact.d96a.DESADV.SegmentGroup15();
            list.add(sg15);
            sg15.setAdditionalProductId(new ArrayList<AdditionalProductId>());
            // line item 

            sg15.setLineItem(createLineItem(lineItemNumber, item));
            sg15.setAdditionalProductId(createAdditionalProductIds(item));
            sg15.setItemDescription(createItemDescription(item));

            sg15.setQuantity(new ArrayList<Quantity>());

            // kod 12 - ilosc wysylana
            if (item.getAmount() == null) {
                throw new VOWrongDataException("Błąd EE374 - pozycja wysyłki nie ma podanej ilości");
            }

            Quantity quant = createQuantity("12", item.getAmount(), item.getProduct().getMeasureUnit().getAbbr());
            sg15.getQuantity().add(quant);
            // supplier code

            ///
            /// don't think thats needed..
            // LineItem li = new LineItem();
            //li.setLineItemNumber(BigDecimal.ONE)
            //  
            // 
            // odnosnik - kod pozycji przeslany przez zamawiajcego 
            org.milyn.edi.unedifact.d96a.DESADV.SegmentGroup16 sg16 = new org.milyn.edi.unedifact.d96a.DESADV.SegmentGroup16();
            sg16.setReference(createReference(item));
            // 
            // TODO - s sg16 mozna 
            // sg28 with price - not needed
//            PriceDetails pri = createPriceDetails("NTP", item.getUnitPriceNet());
//            SegmentGroup28 sg28 = new SegmentGroup28();
//            sg28.setPriceDetails(pri);
//            sg15.setse(new ArrayList<SegmentGroup28>());
//            sg15.getSegmentGroup28().add(sg28);

            lineItemNumber = lineItemNumber.add(new BigDecimal(1));
        }
        return list;

    }

    public void generateZwd() throws VOWrongDataException {

        //  create order message
        Orders orderMsg = new Orders();

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

        orderMsg.setSegmentGroup2(new ArrayList<SegmentGroup2>());
        createParties(orderMsg.getSegmentGroup2());

        // create items 
        orderMsg.setSegmentGroup25(new ArrayList<SegmentGroup25>());
        createLineItemsOrder(orderMsg.getSegmentGroup25());

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

    }
    
    

    public void generateDpz() throws VOWrongDataException {

        //  create order message
        Desadv desAdvMessage = new Desadv();

        message.setMessage(desAdvMessage);
        interchange.setMessages(new ArrayList<UNEdifactMessage41>());
        interchange.getMessages().add(message);

        // order number 
        BeginningOfMessage bgm = new BeginningOfMessage();
        bgm.setDocumentMessageNumber(document.getOwnNumber());
        bgm.setDocumentMessageName(new DocumentMessageNameC002());
        bgm.getDocumentMessageName().setDocumentMessageNameCoded("351");// typ komunikatu
        desAdvMessage.setBeginningOfMessage(bgm);

        desAdvMessage.setDateTimePeriod(new ArrayList<DateTimePeriod>());
        insertDateTime(desAdvMessage.getDateTimePeriod());

        // segment RFF - odniesienie do zamowienia
        Document docOrder = document.getSourceDocument();
        if (docOrder == null) {
            throw new VOWrongDataException("Błąd EOE371 - dokument DPZ nie ma powiązanego dokumentu zamówienia");
        }
        org.milyn.edi.unedifact.d96a.DESADV.SegmentGroup1 sg1 = new org.milyn.edi.unedifact.d96a.DESADV.SegmentGroup1();
        sg1.setReference(new Reference());
        sg1.getReference().setReference(new ReferenceC506());
        sg1.getReference().getReference().setReferenceNumber(docOrder.getExternalNumber());
        sg1.getReference().getReference().setReferenceQualifier("ON"); // kod "ON" oznacza numer zamowienia 
        desAdvMessage.setSegmentGroup1(new ArrayList<org.milyn.edi.unedifact.d96a.DESADV.SegmentGroup1>());
        desAdvMessage.getSegmentGroup1().add(sg1);

        // numer WZ mozna przekazac dodatkowo w tym segmencie jako DQ , ale wykorzystamy glowny numer
        desAdvMessage.setSegmentGroup2(new ArrayList<org.milyn.edi.unedifact.d96a.DESADV.SegmentGroup2>());
        // kupujacy - BY
        if (document.getClient() == null) {
            throw new VOWrongDataException("Błąd EOE395 - dokument nie ma klienta");
        }
        org.milyn.edi.unedifact.d96a.DESADV.SegmentGroup2 sg2By = new org.milyn.edi.unedifact.d96a.DESADV.SegmentGroup2();
        NameAndAddress naaBy = createNAD("BY", document.getClient());
        sg2By.setNameAndAddress(naaBy);
        desAdvMessage.getSegmentGroup2().add(sg2By);
        // dostawa - SU
        org.milyn.edi.unedifact.d96a.DESADV.SegmentGroup2 sg2Su = new org.milyn.edi.unedifact.d96a.DESADV.SegmentGroup2();
        NameAndAddress nadSu = createNAD("SU", document.getSupplier());
        sg2By.setNameAndAddress(nadSu);
        desAdvMessage.getSegmentGroup2().add(sg2Su);
        // obiekt - DP delivery party
        org.milyn.edi.unedifact.d96a.DESADV.SegmentGroup2 sg2Dp = new org.milyn.edi.unedifact.d96a.DESADV.SegmentGroup2();
        NameAndAddress nadDp = createNadFromOrgUnit("DP", document.getCompanyUnit());
        sg2Dp.setNameAndAddress(nadDp);
        desAdvMessage.getSegmentGroup2().add(sg2Dp);

        desAdvMessage.setSegmentGroup10(new ArrayList<org.milyn.edi.unedifact.d96a.DESADV.SegmentGroup10>());

        org.milyn.edi.unedifact.d96a.DESADV.SegmentGroup10 sg10 = new org.milyn.edi.unedifact.d96a.DESADV.SegmentGroup10();
        sg10.setConsignmentPackingSequence(new ConsignmentPackingSequence());
        sg10.getConsignmentPackingSequence().setHierarchicalIdNumber("1");

        List<org.milyn.edi.unedifact.d96a.DESADV.SegmentGroup15> items = createLineItemsDpz();
        sg10.setSegmentGroup15(items);
        desAdvMessage.getSegmentGroup10().add(sg10);

//        
//        // create section control
//        SectionControl sc = new SectionControl();
//        sc.setSectionIdentification("S");
//        desAdvMessage.setSectionControl(sc);
        message.setMessageTrailer(new UNT41());
        message.getMessageTrailer().setSegmentCount(1);

        message.getMessageTrailer().setMessageRefNum(messageRef);

        interchange.setInterchangeTrailer(new UNZ41());
        interchange.getInterchangeTrailer().setControlCount(1);
        interchange.getInterchangeTrailer().setControlRef(messageRef);

    }

}
