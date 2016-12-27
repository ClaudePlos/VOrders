/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.orders.zwd;

import com.google.common.eventbus.EventBus;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import pl.vendi.ui.VOLookup;
import pl.vendi.ui.common.ComboBoxProducts;
import pl.vendi.ui.documents.elements.DocumentWindow;
import pl.vo.company.model.Company;
import pl.vo.documents.model.Document;
import pl.vo.documents.model.DocumentItem;
import pl.vo.products.model.Product;
import pl.vo.road_distance.api.RoadDistanceApi;

/**
 *
 * @author k.skowronski
 */
public class ElZwdDocItemReplace extends HorizontalLayout {
    
    BeanItemContainer<Product> cntProducts = new BeanItemContainer<Product>(Product.class);
    ComboBoxProducts cmbProduct = new ComboBoxProducts("Towar", cntProducts);
    
    
    Document document;
    BeanItemContainer<DocumentItem> cntPositions;

    EventBus eventBus;
    DocumentWindow parentWindow;
    
    Company cmp;
    
    RoadDistanceApi apiRoad;
    
    
    Button butZamienniki = new Button("Aktywuj zamiennik");
    TextField tfAmountZam = new TextField("Ilość");
    Button butAddZam = new Button("Dodaj zamiennik");
    
    
    
    public ElZwdDocItemReplace(DocumentWindow parentWindow)
    {
        this.parentWindow = parentWindow;

        apiRoad = VOLookup.lookupRoadDistanceApi();

        eventBus = new EventBus("document");
        
        this.addComponent(butZamienniki);
        butZamienniki.addStyleName(ValoTheme.BUTTON_PRIMARY);

    }
    
    
    public void setDocument(Document doc, BeanItemContainer<DocumentItem> cntPositions) {
        this.document = doc;
        this.cntPositions = cntPositions;
        this.setEnabled(document != null);
    }
    
    
    
}



