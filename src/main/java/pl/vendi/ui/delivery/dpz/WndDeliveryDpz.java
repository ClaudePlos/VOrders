/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.delivery.dpz;

import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import pl.vendi.ui.VOLookup;
import pl.vendi.ui.common.VO_UI_Consts;
import pl.vendi.ui.documents.elements.DocumentWindow;
import pl.vendi.ui.orders.zwk.ElNewZwkDocItem;
import pl.vo.VOConsts;
import pl.vo.common.model.DictionaryValue;
import pl.vo.documents.model.Document;
import pl.vo.documents.model.DocumentItem;
import pl.vo.exceptions.VOWrongDataException;
import pl.vo.integration.edifact.EdifactExport;

/**
 *
 * @author Piotr
 */
public class WndDeliveryDpz extends DocumentWindow {
    
    ElNewDpzAddItem elNewDpzAddItem = new ElNewDpzAddItem(this);

    Button butCopyAll = new Button("Skopiuj wszystkie pozycje");

    Button butGenerateEdifact = new Button("GENERUJ EDIFACT");
    
    public WndDeliveryDpz() {
        super(VOConsts.DOC_TYPE_DPZ);
        setCaption("Deklarowana dostawa DPZ");
        
        addItemEditBox(elNewDpzAddItem);

        // add buttons
        hboxBottom.addComponent(butCopyAll);
        butCopyAll.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) 
            {
                // for each pos. copy quantitiy
                for (DocumentItem di : document.getItems()) {
                    di.setAmount(new BigDecimal(di.getAmountLeftToDelivery().doubleValue()));
                }
                tblPositions.refreshRows();
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

    @Override
    public void setDocument(Document doc) {
        super.setDocument(doc);
        setAvailablePriceListStatus();
         // read related documents

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
            newStats.add(VO_UI_Consts.actionSendToRealization);
            newStats.add(VO_UI_Consts.actionBackToOpen);
            newStats.add(VO_UI_Consts.actionCancel);

        }
 else if (document.getStatus().equals(VOConsts.DOC_STATUS_CONFIRMED_BY_SUPPLIER)) {
            newStats.add(VO_UI_Consts.actionSendToRealization);
            newStats.add(VO_UI_Consts.actionBackToOpen);
//            newStats.add(VO_UI_Consts.actionCancel);

        }
        
        setNextStatus(newStats);
    }
}
