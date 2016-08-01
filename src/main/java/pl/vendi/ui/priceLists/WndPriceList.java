/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.priceLists;

import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import java.util.ArrayList;
import java.util.List;
import pl.vendi.ui.common.VO_UI_Consts;
import pl.vendi.ui.documents.elements.DocumentWindow;
import pl.vo.VOConsts;
import pl.vo.common.model.DictionaryValue;
import pl.vo.documents.model.Document;
import pl.vo.exceptions.VOWrongDataException;
import pl.vo.integration.edifact.EdifactExport;
import pl.vo.integration.edifact.EdifactExportPricat;

/**
 *
 * @author Piotr
 */
public class WndPriceList extends DocumentWindow {

    PriceListAddItem priceItemEdit = new PriceListAddItem(this);

    Button butGenerateEdifactPricat = new Button("GENERUJ EDIFACT");

    public WndPriceList() {
        super(VOConsts.DOC_TYPE_PRICE_LIST);
        setCaption("Cennik");

        hboxBottom.addComponent(butGenerateEdifactPricat);
        butGenerateEdifactPricat.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                EdifactExportPricat eoe = new EdifactExportPricat();
                try {
                    String edi = eoe.generate(document);
                    Notification.show(edi, Notification.Type.ERROR_MESSAGE);
                } catch (VOWrongDataException wre) {
                    Notification.show(wre.getMessage(), Notification.Type.ERROR_MESSAGE);
                }
            }
        });

        addItemEditBox(priceItemEdit);
    }

    public void newDocument() {
        Document doc = new Document();
        doc.setType(VOConsts.DOC_TYPE_PRICE_LIST);
        doc.setStatus(VOConsts.DOC_STATUS_OPEN);
        setDocument(doc);
        priceItemEdit.setDocument(doc, tblPositions.getCnt());
        setAvailablePriceListStatus();
    }

    @Override
    public void setDocument(Document doc) {
        super.setDocument(doc);
        priceItemEdit.setDocument(doc, tblPositions.getCnt());
        setAvailablePriceListStatus();
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
            newStats.add(VO_UI_Consts.actionSendToClient);
            newStats.add(VO_UI_Consts.actionBackToOpen);
            newStats.add(VO_UI_Consts.actionCancel);
        } else if (document.getStatus().equals(VOConsts.DOC_STATUS_RECEIVED_BY_SUPPLIER)) {
            newStats.add(VO_UI_Consts.actionAccept);
            newStats.add(VO_UI_Consts.actionSendToClient);
            newStats.add(VO_UI_Consts.actionBackToOpen);
            newStats.add(VO_UI_Consts.actionCancel);
        }
        setNextStatus(newStats);
    }
}
