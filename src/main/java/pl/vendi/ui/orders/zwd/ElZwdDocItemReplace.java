/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.orders.zwd;

import com.google.common.eventbus.EventBus;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import pl.vendi.ui.VOLookup;
import pl.vendi.ui.common.ComboBoxProducts;
import pl.vendi.ui.documents.elements.DocumentWindow;
import pl.vendi.ui.events.DocumentChangedEvent;
import pl.vo.company.model.Company;
import pl.vo.documents.DocumentsApi;
import pl.vo.documents.api.PriceListsApi;
import pl.vo.documents.model.Document;
import pl.vo.documents.model.DocumentItem;
import pl.vo.exceptions.VOWrongDataException;
import pl.vo.products.api.UnitsProductsApi;
import pl.vo.products.model.Product;
import pl.vo.products.model.UnitsProducts;
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
        
        this.setDefaultComponentAlignment(Alignment.BOTTOM_CENTER);
        this.setSpacing(true);
        
        
        this.addComponent(cmbProduct); 
        cmbProduct.setWidth("400px");
        this.addComponent(tfAmountZam);  
        this.addComponent(butAddZam);
        butAddZam.addStyleName(ValoTheme.BUTTON_PRIMARY);
        butAddZam.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                addItemReplacement();
            }
        });
        
        
        cmbProduct.setVisible(false);
        tfAmountZam.setVisible(false);
        butAddZam.setVisible(false);
        
        this.addComponent(butZamienniki);
        butZamienniki.addStyleName(ValoTheme.BUTTON_PRIMARY);
        butZamienniki.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                activeReplacement();
            }
        });
       
        
        

    }
    
    
    public void setDocument(Document doc, BeanItemContainer<DocumentItem> cntPositions) {
        this.document = doc;
        this.cntPositions = cntPositions;
        this.setEnabled(document != null);
    }
    
    private void activeReplacement()
    {
        butZamienniki.setVisible(false);
        cmbProduct.setVisible(true);
        tfAmountZam.setVisible(true);
        butAddZam.setVisible(true);

        List<Product> products = VOLookup.lookupProductsApi().getByCmpId( document.getCompanyUnit().getId() );
        cntProducts.addAll(products);
    }
    
    private void addItem() {

        BigDecimal amount = null;

        if (cmbProduct.getProduct() == null) {
            return;
        }
        if (document == null) {
            Notification.show("Brak dokumentu!", Notification.Type.ERROR_MESSAGE);
            return;
        }
        try {
            amount = new BigDecimal(tfAmountZam.getValue().replace(",", "."));
        } catch (NumberFormatException nfe) {
            Notification.show("Błędny format ilości", Notification.Type.ERROR_MESSAGE);
            return;
        }
        
        
        
        // sprawdz czy nie ma juz tej pozycji towarowej
        if (document.hasItemWithProduct(cmbProduct.getProduct())) {
            Notification.show("Dokument zawiera już pozycję z towarem:" + cmbProduct.getProduct());
            return;
        }
        DocumentItem di = new DocumentItem();
        
        di.setProduct( cmbProduct.getProduct() );
        di.setAmount(amount);
            

        document.getItems().add(di);
        
        
        cntPositions.addItem(di);

        DocumentChangedEvent ev = new DocumentChangedEvent(document);
        eventBus.post(ev);

        parentWindow.setModified(true);
    }
    
    
    
    private void addItemReplacement() {  // dodaj zamiennik

        BigDecimal amount = null;

        if (cmbProduct.getProduct() == null) {
            return;
        }
        if (document == null) {
            Notification.show("Brak dokumentu!", Notification.Type.ERROR_MESSAGE);
            return;
        }
        try {
            amount = new BigDecimal(tfAmountZam.getValue().replace(",", "."));
        } catch (NumberFormatException nfe) {
            Notification.show("Błędny format ilości", Notification.Type.ERROR_MESSAGE);
            return;
        }
        
        
        
        // sprawdz czy nie ma juz tej pozycji towarowej
        if (document.hasItemWithProduct(cmbProduct.getProduct())) {
            Notification.show("Dokument zawiera już pozycję z towarem:" + cmbProduct.getProduct());
            return;
        }
        DocumentItem di = new DocumentItem();
        
        di.setProduct( cmbProduct.getProduct() );
        di.setAmount( BigDecimal.ZERO );
        di.setAmountConfirmed(amount);
            

        document.getItems().add(di);
        
        
        cntPositions.addItem(di);

        DocumentChangedEvent ev = new DocumentChangedEvent(document);
        eventBus.post(ev);

        parentWindow.setModified(true);
    }
    
    
    
    
    
    
}



