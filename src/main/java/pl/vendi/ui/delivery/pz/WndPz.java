/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.delivery.pz;

import java.util.ArrayList;
import java.util.List;
import pl.vendi.ui.common.VO_UI_Consts;
import pl.vendi.ui.documents.elements.DocumentWindow;
import pl.vendi.ui.orders.zwk.ElNewZwkDocItem;
import pl.vo.VOConsts;
import pl.vo.common.model.DictionaryValue;
import pl.vo.documents.model.Document;

/**
 *
 * @author Piotr
 */
public class WndPz extends DocumentWindow {

    //ElNewZwkDocItem elNewZwkItem = new ElNewZwkDocItem();
    public WndPz() {
        super(VOConsts.DOC_TYPE_PZ);
        setCaption("Dostawa (dokument PZ)");

        // addItemEditBox(elNewZwkItem );
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
        //elNewZwkItem.setDocument( doc ,tblPositions.getCnt()  );
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
        } else if (document.getStatus().equals(VOConsts.DOC_STATUS_ACCEPTED))
        {
            newStats.add(VO_UI_Consts.actionBook);
            newStats.add(VO_UI_Consts.actionBackToOpen);
            newStats.add(VO_UI_Consts.actionCancel);

        } 
//        else if (document.getStatus().equals(VOConsts.DOC_STATUS_DELIVERY)) {
//            newStats.add(VO_UI_Consts.actionCancelRealization);
//        }

        setNextStatus(newStats);
    }
}
