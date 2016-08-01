/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.delivery.pz;

import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import javax.ejb.EJB;
import pl.vendi.ui.VOLookup;
import pl.vendi.ui.VendiOrdersUI;
import pl.vendi.ui.documents.elements.DocumentsTable;
import pl.vo.VOConsts;
import pl.vo.documents.api.DocumentsActionsPzApi;
import pl.vo.documents.model.Document;

/**
 *
 * @author Piotr
 */
public class WndSelectDpz extends Window implements Button.ClickListener {

    DocumentsActionsPzApi documentsPzApi;

    VerticalLayout vboxMain = new VerticalLayout();
    HorizontalLayout hboxBottom = new HorizontalLayout();

    DocumentsTable tabOrders = new DocumentsTable("WZ od dostawców", VOConsts.DOC_TYPE_DPZ);
    
    // filters 

    Button butAdd = new Button("Dodaj Dostawę");
    Button butAddFromDpz = new Button("Dodaj Dostawę na podstawie DPZ");
    Button butCancel = new Button("Anuluj");

    public WndSelectDpz()
    {

        super("Wybierz WZ dostawcy na podstawie którego chcesz wprowadzić dostawę");
        documentsPzApi = VOLookup.lookupDocumentsActionsPzApi();
        vboxMain.setMargin(true);

        this.setContent(vboxMain);
        vboxMain.setSizeFull();
        vboxMain.setSpacing(true);

        vboxMain.addComponent(tabOrders);
        vboxMain.addComponent(hboxBottom);
        vboxMain.setExpandRatio(tabOrders, 1);
        tabOrders.setSizeFull();

        hboxBottom.addComponent(butAdd);
        hboxBottom.addComponent(butCancel);
        butCancel.addStyleName(ValoTheme.BUTTON_DANGER);
        butAdd.addStyleName(ValoTheme.BUTTON_PRIMARY);

        butAdd.addClickListener(this);
        butCancel.addClickListener(this);
        tabOrders.setDocumentTypes(new String[]{VOConsts.DOC_TYPE_DPZ});

        tabOrders.setShowDocumentOdDblClk(false);
        tabOrders.refresh();

        tabOrders.getTable().addItemClickListener(new ItemClickEvent.ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                onSelectedDpz();
            }
        });

    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton().equals(butAdd)) {
            onSelectedDpz();
        } else if (event.getButton().equals(butCancel)) {
            this.close();
        }
    }

    private void onSelectedDpz() {
        BeanItem<Document> biDoc = (BeanItem<Document>) tabOrders.getTable().getItem(tabOrders.getTable().getValue());
        if (biDoc != null) {
            Document docDpz = biDoc.getBean();
            if (docDpz != null) {
                Document docPz = documentsPzApi.createPzFromDpz(docDpz);
                WndPz wndpz = new WndPz();
                wndpz.setDocument(docPz);
                VendiOrdersUI.showWindow(wndpz);
                this.close();
            }
        }
    }
}
