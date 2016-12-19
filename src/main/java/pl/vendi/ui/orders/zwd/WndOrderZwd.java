/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.orders.zwd;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import pl.vendi.ui.orders.zwk.ElNewZwkDocItem;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.imageio.ImageIO;
import pl.vendi.ui.VOLookup;
import pl.vendi.ui.VendiOrdersUI;
import pl.vendi.ui.common.VO_UI_Consts;
import pl.vendi.ui.delivery.dpz.WndDeliveryDpz;
import pl.vendi.ui.documents.elements.DocumentWindow;
import pl.vendi.ui.orders.fv.FvPrint;
import pl.vendi.ui.orders.fv.PdfExport;
import pl.vo.VOConsts;
import pl.vo.common.model.DictionaryValue;
import pl.vo.documents.api.DocumentsActionsDpzApi;
import pl.vo.documents.api.DocumentsActionsZwdApi;
import pl.vo.documents.model.Document;
import pl.vo.exceptions.VOWrongDataException;
import pl.vo.integration.edifact.EdifactExport;


import com.itextpdf.text.pdf.PdfWriter;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Piotr
 */
public class WndOrderZwd extends DocumentWindow {

    ElNewZwkDocItem elNewZwkItem = new ElNewZwkDocItem(this);

    DocumentsActionsZwdApi documentsZwdApi;

    DocumentsActionsDpzApi documentsDpzApi;

    Button butAddDPZ = new Button("Wprowadź deklarowaną dostawę");
    Button butShowDPZ = new Button("Pokaż dostawy DPZ");
    Button butGenerateEdifact = new Button("GENERUJ EDIFACT");
    
    

    public WndOrderZwd() {

        super(VOConsts.DOC_TYPE_ZWD);

        documentsZwdApi = VOLookup.lookupDocumentsActionsZwdApi();
        documentsDpzApi = VOLookup.lookupDocumentsActionsDpzApi();

        setCaption("Zamówienie ZWD");

        addItemEditBox(elNewZwkItem);
        
        
     
        
        
        Button butInvoice = new Button("FV");
        
        StreamResource myResource = getPDFStream();
        FileDownloader fileDownloader = new FileDownloader(myResource);
        fileDownloader.extend(butInvoice);

        hboxBottom.addComponent( butInvoice );  
        
    }
    
    
    private StreamResource getPDFStream() {
        StreamResource.StreamSource source = new StreamResource.StreamSource() {

            public InputStream getStream() {
                
                // step 1
            com.itextpdf.text.Document document = new com.itextpdf.text.Document();
            // step 2
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
                try {
                    com.itextpdf.text.pdf.PdfWriter.getInstance(document, baos);
                    
                    // step 3
                    document.open();

                    document.add(Chunk.NEWLINE);   //Something like in HTML :-)

                    document.add(new Paragraph("TEST" ));


                    document.add(Chunk.NEWLINE);   //Something like in HTML :-)							    

                    document.newPage();            //Opened new page

                    //document.add(list);            //In the new page we are going to add list

                    document.close();

                    //file.close();

                    System.out.println("Pdf created successfully..");
                    
                    
                    
                    
                } catch (DocumentException ex) {
                    Logger.getLogger(WndOrderZwd.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                ByteArrayOutputStream stream = baos;
                InputStream input = new ByteArrayInputStream(stream.toByteArray());
                  return input;

            }
        };
      StreamResource resource = new StreamResource ( source, "test.pdf" );
        return resource;
    }
    
   
    

    public void newDocument(String type) {
        Document doc = new Document();
        doc.setType(type);
        doc.setStatus(VOConsts.DOC_STATUS_OPEN);
        setDocument(doc);

    }

    @Override
    public void setDocument(Document doc) {
        super.setDocument(doc);
        elNewZwkItem.setDocument(doc, tblPositions.getCnt());
        setAvailablePriceListStatus();

        // 
        if (document.getStatus().equals(VOConsts.DOC_STATUS_CONFIRMED_BY_SUPPLIER)
                || document.getStatus().equals(VOConsts.DOC_STATUS_DELIVERY)  ||  true)
        {

            hboxBottom.addComponent(butAddDPZ);
            butAddDPZ.addClickListener(new Button.ClickListener() {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                    WndDeliveryDpz wndDpz = new WndDeliveryDpz();
                    VendiOrdersUI.showWindow(wndDpz);
                    // create dpz for zwd 
                    Document newZwd = documentsDpzApi.createNewDpzForZwd(document);
                    wndDpz.setDocument(newZwd);

                }
            });
            
                 
            hboxBottom.addComponent(butGenerateEdifact);
            butGenerateEdifact.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                   EdifactExport eoe = new EdifactExport();
                    try {
                        String edi = eoe.generate(document);
                        Notification.show(edi, Notification.Type.ERROR_MESSAGE);
                    } catch (VOWrongDataException wre) {
                        Notification.show(wre.getMessage(), Notification.Type.ERROR_MESSAGE);
                    }
                }
            });
            
            
            
            
            
        }
    }

    private void setAvailablePriceListStatus() {
        List<DictionaryValue> newStats = new ArrayList<DictionaryValue>();
        if (document == null) {
            return;
        }

        if (document.getStatus().equals(VOConsts.DOC_STATUS_OPEN)) {
            newStats.add(VO_UI_Consts.actionAccept);
            newStats.add(VO_UI_Consts.actionCancel);
        } else if (document.getStatus().equals(VOConsts.DOC_STATUS_ACCEPTED)) {
            newStats.add(VO_UI_Consts.actionSendToSupplier);
            //  newStats.add(VO_UI_Consts.actionBackToOpen);
            newStats.add(VO_UI_Consts.actionCancel);

        } else if (document.getStatus().equals(VOConsts.DOC_STATUS_SENDED_TO_SUPPLIER)) 
        {
            newStats.add(VO_UI_Consts.actionSupplierConfirmReceive);
            newStats.add(VO_UI_Consts.actionCancelRealization);
        } else if (document.getStatus().equals(VOConsts.DOC_STATUS_RECEIVED_BY_SUPPLIER)) {
            newStats.add(VO_UI_Consts.actionSupplierConfirmAvailability);
        }

        setNextStatus(newStats);
    }
    
    
     


    
}
