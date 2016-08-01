/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.invoice;

import com.vaadin.ui.Button;
import java.util.ArrayList;
import java.util.List;
import pl.vendi.ui.VOLookup;
import pl.vendi.ui.VendiOrdersUI;
import pl.vendi.ui.common.VO_UI_Consts;
import pl.vendi.ui.documents.elements.DocumentWindow;
import pl.vendi.ui.documents.elements.WndChildDocuments;
import pl.vendi.ui.orders.zwk.ElNewZwkDocItem;
import pl.vo.VOConsts;
import pl.vo.common.model.DictionaryValue;
import pl.vo.company.model.Company;
import pl.vo.documents.model.Document;

/**
 *
 * @author Piotr
 */
public class WndInvoice extends DocumentWindow {

    ElNewInvoiceItem elNewInvoiceItem = new ElNewInvoiceItem(this);

    public WndInvoice() {
        super(VOConsts.DOC_TYPE_INVOICE);
        setCaption("Faktura");

        addItemEditBox(elNewInvoiceItem);
        addLinkedDocumentsButtons();
    }

    public void newDocument(String type) 
    {
        Document doc = new Document();

        Company cmpClient = VOLookup.lookupCompanysApi().getByNip(VOConsts.NIP_VENDI);
        doc.setType(type);
        doc.setClient(cmpClient);
        doc.setStatus(VOConsts.DOC_STATUS_OPEN);

        setDocument(doc);

    }

    @Override
    public void setDocument(Document doc) {
        super.setDocument(doc);
        elNewInvoiceItem.setDocument(doc, tblPositions.getCnt());
        setAvailableActions();
        // read related documents

    }

    private void setAvailableActions() {
        List<DictionaryValue> newStats = new ArrayList<DictionaryValue>();
//        
//        if (document == null) {
//            return;
//        }
//
//        if (document.getStatus().equals(VOConsts.DOC_STATUS_OPEN)) {
//            newStats.add(VO_UI_Consts.actionAccept);
//            newStats.add(VO_UI_Consts.actionCancel);
//        } else if (document.getStatus().equals(VOConsts.DOC_STATUS_ACCEPTED)) {
//            newStats.add(VO_UI_Consts.actionSendToRealization);
//            newStats.add(VO_UI_Consts.actionBackToOpen);
//            newStats.add(VO_UI_Consts.actionCancel);
//
//        } else if (document.getStatus().equals(VOConsts.DOC_STATUS_DELIVERY)) {
//            newStats.add(VO_UI_Consts.actionCancelRealization);
//        }

        setNextStatus(newStats);
    }

    private void addLinkedDocumentsButtons() {
        Button butLinkedZwd = new Button("Powiązane zamówienia do dostawcy (ZWD)");
        hboxBottom.addComponent(butLinkedZwd);
        butLinkedZwd.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                WndChildDocuments wch = new WndChildDocuments(document, VOConsts.DOC_TYPE_ZWD);
                VendiOrdersUI.showWindow(wch);
            }
        });
    }
}
