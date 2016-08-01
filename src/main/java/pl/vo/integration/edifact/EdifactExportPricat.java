/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.integration.edifact;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.milyn.edi.unedifact.d96a.D96AInterchangeFactory;
import org.milyn.edi.unedifact.d96a.PRICAT.SegmentGroup3;
import org.milyn.edi.unedifact.d96a.PRICAT.Pricat;
import org.milyn.edi.unedifact.d96a.PRICAT.SegmentGroup16;
import org.milyn.edi.unedifact.d96a.PRICAT.SegmentGroup2;
import org.milyn.edi.unedifact.d96a.PRICAT.SegmentGroup33;
import org.milyn.edi.unedifact.d96a.PRICAT.SegmentGroup35;
import org.milyn.edi.unedifact.d96a.common.BeginningOfMessage;
import org.milyn.edi.unedifact.d96a.common.DateTimePeriod;
import org.milyn.edi.unedifact.d96a.common.DutyTaxFeeDetails;
import org.milyn.edi.unedifact.d96a.common.ItemDescription;
import org.milyn.edi.unedifact.d96a.common.LineItem;
import org.milyn.edi.unedifact.d96a.common.Measurements;
import org.milyn.edi.unedifact.d96a.common.NameAndAddress;
import org.milyn.edi.unedifact.d96a.common.ProductGroupInformation;
import org.milyn.edi.unedifact.d96a.common.Reference;
import org.milyn.edi.unedifact.d96a.common.field.DateTimePeriodC507;
import org.milyn.edi.unedifact.d96a.common.field.DocumentMessageNameC002;
import org.milyn.edi.unedifact.d96a.common.field.DutyTaxFeeDetailC243;
import org.milyn.edi.unedifact.d96a.common.field.ReferenceC506;
import org.milyn.edi.unedifact.d96a.common.field.ItemDescriptionC273;
import org.milyn.edi.unedifact.d96a.common.field.PartyIdentificationDetailsC082;
import org.milyn.edi.unedifact.d96a.common.field.PartyNameC080;
import org.milyn.edi.unedifact.d96a.common.field.ProductGroupC288;
import org.milyn.edi.unedifact.d96a.common.field.ValueRangeC174;
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
import pl.vo.documents.model.Document;
import pl.vo.documents.model.DocumentItem;
import pl.vo.exceptions.VOWrongDataException;
import pl.vo.organisation.model.OrganisationUnit;

/**
 *
 * @author k.skowronski
 */
public class EdifactExportPricat {
    
    public static String MESSAGE_TYPE_PRICAT = "PRICAT";
    
    D96AInterchangeFactory factory;
    Document document;
    
    Date messageCreationDate = new Date();
    Long ti = new Long(messageCreationDate.getTime());
    String messageRef = ti.toString();
    
    String messageType = "";
    
    UNEdifactInterchange41 interchange;
    UNEdifactMessage41 message;
    
    public String generate(Document document) throws VOWrongDataException {
        
        this.document = document;
        factory = EdifactProvider.getInterchangeFactory();
        
        interchange = new UNEdifactInterchange41();
        message = new UNEdifactMessage41();
        
        messageType = "PRICAT";
        
        generateCommonFields();
       
        generatePricat();
        
     
        
        
        
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
        interchange.setInterchangeHeader(unb41);
        message.setInterchangeHeader(unb41);

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
        partySender.setId(VOConsts.NIP_VENDI);
  
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
        unb41.setApplicationRef("VENDI_APP_PRICAT");
        unb41.setTestIndicator("PRICAT");
        unb41.setAgreementId("AGGRID");
        unb41.setProcessingPriorityCode("1");
        unb41.setAckRequest("1");
        return unb41;
    }
    
    public UNH41 createUNH41() {

        UNH41 unh41 = new UNH41();
        unh41.setMessageIdentifier(new MessageIdentifier());
        unh41.getMessageIdentifier().setAssociationAssignedCode("PRICAT");
        unh41.getMessageIdentifier().setControllingAgencyCode("UN");
        unh41.getMessageIdentifier().setId(messageType);
        unh41.getMessageIdentifier().setReleaseNum("96A");
        unh41.getMessageIdentifier().setVersionNum("D");
        unh41.setMessageRefNum("1");
        return unh41;

    }
    
    
    public void generatePricat() throws VOWrongDataException {
        
        UNH41 unh41 = createUNH41();
        message.setMessageHeader(unh41);
        
        //  create order message
        Pricat pricatMsg = new Pricat();
      

        message.setMessage(pricatMsg);
        interchange.setMessages(new ArrayList<UNEdifactMessage41>());
        interchange.getMessages().add(message);

        // order number 
        BeginningOfMessage bgm = new BeginningOfMessage();
        bgm.setDocumentMessageNumber(document.getOwnNumber());
        pricatMsg.setBeginningOfMessage(bgm);

        bgm.setDocumentMessageName(new DocumentMessageNameC002());
        bgm.getDocumentMessageName().setDocumentMessageNameCoded("9 - ks02");
        
        
        
        
        
        // data obowiazywania cennika
        
        pricatMsg.setSegmentGroup2( new ArrayList<org.milyn.edi.unedifact.d96a.PRICAT.SegmentGroup2>() );
        
        org.milyn.edi.unedifact.d96a.PRICAT.SegmentGroup2 sg2 = new org.milyn.edi.unedifact.d96a.PRICAT.SegmentGroup2();
        
        sg2.setSegmentGroup3( new ArrayList<org.milyn.edi.unedifact.d96a.PRICAT.SegmentGroup3>() );
        SegmentGroup3 sg3 = new SegmentGroup3();
        
        List <DateTimePeriod> period = new ArrayList<DateTimePeriod>();
        
        if ( document.getValidFrom() == null )
            throw new VOWrongDataException("Błąd EEP-214 - cennik nie ma początku terminu obowiązywania");
        if ( document.getValidTill() == null)
            throw new VOWrongDataException("Błąd EEP-216 - cennik nie ma daty obowiązywania");
        period.add(EdifactProvider.dateToDateTimePeriod( document.getValidFrom(), "157"));
        period.add(EdifactProvider.dateToDateTimePeriod( document.getValidTill(), "21E"));

        sg3.setDateTimePeriod(period);
        sg2.getSegmentGroup3().add(sg3);
        pricatMsg.getSegmentGroup2().add(sg2); 
        
        
        // NAD
        org.milyn.edi.unedifact.d96a.PRICAT.SegmentGroup2 sg2By = new org.milyn.edi.unedifact.d96a.PRICAT.SegmentGroup2();
        /* Cenniik nie ma pola Client 
        NameAndAddress naaBy = createNadFromOrgUnit("BY", document.getClient());
        sg2By.setNameAndAddress(naaBy);
        pricatMsg.getSegmentGroup2().add(sg2By);
        */
        
        // dostawa - SU
        org.milyn.edi.unedifact.d96a.PRICAT.SegmentGroup2 sg2Su = new org.milyn.edi.unedifact.d96a.PRICAT.SegmentGroup2();
        NameAndAddress nadSu = createNAD("SU", document.getSupplier());
        sg2Su.setNameAndAddress(nadSu);
        pricatMsg.getSegmentGroup2().add(sg2Su); 
        // obiekt - DP delivery party
        
        /** Klaud- cennik nie ma jednostki org wiec nie mozna dodac tych pol
        org.milyn.edi.unedifact.d96a.PRICAT.SegmentGroup2 sg2Dp = new org.milyn.edi.unedifact.d96a.PRICAT.SegmentGroup2();
        NameAndAddress nadDp = createNadFromOrgUnit("BY", document.getClient());
        sg2Dp.setNameAndAddress(nadDp);
        pricatMsg.getSegmentGroup2().add(sg2Dp);
        * */
        
        
 
        // create Seg 16
        pricatMsg.setSegmentGroup16(new ArrayList<SegmentGroup16>());
        createLineItemsOrder( pricatMsg.getSegmentGroup16() );

        
        //REF
        // segment RFF - odniesienie do zamowienia
        //Document docOrder = document.getSourceDocument();
        //if (docOrder == null) {
        //    throw new VOWrongDataException("Błąd EOE371 - dokument DPZ nie ma powiązanego dokumentu zamówienia");
        //}
        /*org.milyn.edi.unedifact.d96a.PRICAT.SegmentGroup1 sg1 = new org.milyn.edi.unedifact.d96a.PRICAT.SegmentGroup1();
        sg1.setReference(new Reference());
        sg1.getReference().setReference(new ReferenceC506());
        sg1.getReference().getReference().setReferenceNumber(null);
        sg1.getReference().getReference().setReferenceQualifier("ON"); // kod "ON" oznacza numer zamowienia 
        pricatMsg.setSegmentGroup1(new ArrayList<org.milyn.edi.unedifact.d96a.PRICAT.SegmentGroup1>());
        pricatMsg.getSegmentGroup1().add(sg1);*/
        
        
        
        

      /* pricatMsg.setDateTimePeriod(new ArrayList<DateTimePeriod>());
        insertDateTime(pricatMsg.getDateTimePeriod());

        pricatMsg.setSegmentGroup2(new ArrayList<SegmentGroup2>());
        createParties(pricatMsg.getSegmentGroup2());

        // create items 
        pricatMsg.setSegmentGroup25(new ArrayList<SegmentGroup25>());
        createLineItemsOrder(orderMsg.getSegmentGroup25());

        // create section control
        SectionControl sc = new SectionControl();
        sc.setSectionIdentification("S");
        orderMsg.setSectionControl(sc);*/

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
    
 
    private void createLineItemsOrder(List<SegmentGroup16> list) {
        
        BigDecimal lineItemNumber = new BigDecimal(1);
        
        for (DocumentItem item : document.getItems()) {

            SegmentGroup16 sg16 = new SegmentGroup16();
            list.add(sg16);

            sg16.setSegmentGroup33( new ArrayList<org.milyn.edi.unedifact.d96a.PRICAT.SegmentGroup33>() );
            
            SegmentGroup33 sg33 = new SegmentGroup33();
            sg16.getSegmentGroup33().add(sg33);
            
            //////SG16
            //DTM
            /*List <DateTimePeriod> dtmList = new ArrayList<DateTimePeriod>();
            dtmList.add(EdifactProvider.dateToDateTimePeriod(document.getDateOperation(), "137"));
            sg16.setDateTimePeriod(dtmList);*/
            
            //PGI
            ProductGroupInformation pgi = new ProductGroupInformation();
            ProductGroupC288 pgC288 = new ProductGroupC288();
            pgC288.setProductGroup("TEST - TODO PGI");
            pgi.setProductGroup(pgC288);
            pgi.setProductGroupTypeCoded("11");
            sg16.setProductGroupInformation(pgi);
            

            //////SG33
            //LIN - linia
            LineItem li = new LineItem();
            li.setLineItemNumber(lineItemNumber);
            sg33.setLineItem(li);
            lineItemNumber = lineItemNumber.add(new BigDecimal(1));
            
            //IMD
            List <ItemDescription> imdList = new ArrayList<ItemDescription>();
            sg33.setItemDescription(imdList);

            ItemDescriptionC273 itemDescC277 = new ItemDescriptionC273();
            itemDescC277.setItemDescriptionIdentification( item.getProduct().getName() );
            itemDescC277.setCodeListQualifier(item.getProduct().getIndexNumber());

            ItemDescription itemDesc = new ItemDescription();
            itemDesc.setItemDescription(itemDescC277);

            imdList.add(itemDesc);
            
            //MEA
            List<Measurements> mList = new ArrayList<Measurements>();
            Measurements m = new Measurements();
            mList.add(m);
            ValueRangeC174 vrC174 = new ValueRangeC174();
            vrC174.setMeasureUnitQualifier(item.getProduct().getMeasureUnit().getAbbr());
            vrC174.setMeasurementValue(item.getUnitPriceNet());
            m.setValueRange(vrC174);
            m.setMeasurementApplicationQualifier("AAI");
            sg33.setMeasurements(mList);
            
            
            //SG35
            sg33.setSegmentGroup35( new ArrayList<org.milyn.edi.unedifact.d96a.PRICAT.SegmentGroup35>() );
            SegmentGroup35 sg35 = new SegmentGroup35();
            DutyTaxFeeDetails dTax = new DutyTaxFeeDetails();
            DutyTaxFeeDetailC243 dTaxC243 = new DutyTaxFeeDetailC243();
            dTaxC243.setDutyTaxFeeRate(item.getProduct().getTaxRate().toString());
            dTax.setDutyTaxFeeDetail(dTaxC243);
            dTax.setDutyTaxFeeFunctionQualifier("AAI");
            sg35.setDutyTaxFeeDetails(dTax);
            sg33.getSegmentGroup35().add(sg35);
            
            
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
    
    
    private NameAndAddress createNadFromOrgUnit(String partyQualifier, Company orgUnit) 
    {
        NameAndAddress nad = new NameAndAddress();
        nad.setPartyQualifier(partyQualifier);
        nad.setPartyIdentificationDetails(new PartyIdentificationDetailsC082());
        
        // KLAUD - Cennik nie ma jednostki org nad.getPartyIdentificationDetails().setPartyIdIdentification(orgUnit.getNip());

        nad.setPartyName(new PartyNameC080());
        nad.getPartyName().setPartyName1(orgUnit.getName());
        return nad;
    }
    
    
}
