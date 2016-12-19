/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.orders.zwd;

import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import pl.vendi.ui.orders.zwk.ElNewZwkDocItem;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import pl.vendi.ui.VOLookup;
import pl.vendi.ui.VendiOrdersUI;
import pl.vendi.ui.common.VO_UI_Consts;
import pl.vendi.ui.delivery.dpz.WndDeliveryDpz;
import pl.vendi.ui.documents.elements.DocumentWindow;
import pl.vo.VOConsts;
import pl.vo.common.model.DictionaryValue;
import pl.vo.documents.api.DocumentsActionsDpzApi;
import pl.vo.documents.api.DocumentsActionsZwdApi;
import pl.vo.documents.model.Document;
import pl.vo.exceptions.VOWrongDataException;
import pl.vo.integration.edifact.EdifactExport;

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
    
    Button butInvoice = new Button("FV");

    public WndOrderZwd() {

        super(VOConsts.DOC_TYPE_ZWD);

        documentsZwdApi = VOLookup.lookupDocumentsActionsZwdApi();
        documentsDpzApi = VOLookup.lookupDocumentsActionsDpzApi();

        setCaption("Zamówienie ZWD");

        addItemEditBox(elNewZwkItem);
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
            
            hboxBottom.addComponent( butInvoice );       
            butInvoice.addClickListener(new Button.ClickListener() {
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
