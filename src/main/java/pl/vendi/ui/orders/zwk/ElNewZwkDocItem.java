/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.orders.zwk;

import com.google.common.eventbus.EventBus;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Link;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import java.math.BigDecimal;
import java.util.List;
import pl.vendi.ui.VOLookup;
import pl.vendi.ui.common.ComboBoxProducts;
import pl.vendi.ui.documents.elements.DocumentWindow;
import pl.vendi.ui.events.DocumentChangedEvent;
import pl.vo.company.api.CompanysApi;
import pl.vo.company.model.Company;
import pl.vo.documents.model.Document;
import pl.vo.documents.model.DocumentItem;
import pl.vo.organisation.model.OrganisationUnit;
import pl.vo.products.model.Product;
import pl.vo.products.model.ProductCmpCode;
import pl.vo.road_distance.api.RoadDistanceApi;
import pl.vo.road_distance.model.RoadDistance;

/**
 *
 * @author Piotr
 */
public class ElNewZwkDocItem extends HorizontalLayout {

    BeanItemContainer<Product> cntProducts = new BeanItemContainer<Product>(Product.class);
    ComboBoxProducts cmbProduct = new ComboBoxProducts("Towar", cntProducts);
    
       
    
    TextField tfAmount = new TextField("Ilość");

    Button butAdd = new Button("Dodaj");
    Button butAddMany = new Button("Dodaj Wiele");

    Document document;
    BeanItemContainer<DocumentItem> cntPositions;

    EventBus eventBus;
    DocumentWindow parentWindow;
    
    Company cmp;
    
    RoadDistanceApi apiRoad;
    
    
    Button butZamienniki = new Button("Aktywuj zamiennik");
    TextField tfAmountZam = new TextField("Ilość");
    Button butAddZam = new Button("Dodaj zamiennik");
    

    public ElNewZwkDocItem(DocumentWindow parentWindow)
    {
        this.parentWindow = parentWindow;
      
        apiRoad = VOLookup.lookupRoadDistanceApi();
        
        eventBus = new EventBus("document");

        List<Product> products = VOLookup.lookupProductsApi().findAll();
        cntProducts.addAll(products);

        this.setDefaultComponentAlignment(Alignment.BOTTOM_CENTER);
        this.setSpacing(true);
        
        
        this.addComponent(cmbProduct);
        cmbProduct.setWidth("400px");
        
        this.addComponent(tfAmount);
        this.addComponent(butAdd);
        this.addComponent( butAddMany);
        
      

        butAdd.addStyleName(ValoTheme.BUTTON_PRIMARY);
        butAdd.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                addItem();
            }
        });
        
       butAddMany.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                addManyItems();
            }
        });
       
       
       
       
       tfAmount.setVisible(false);
        butAdd.setVisible(false);
        
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
    
    private void addManyItems() { 
        
        if (this.document.getCompanyUnit() == null ){
            Notification.show("Wybierz najpierw jednostkę organizacyjną", Notification.Type.ERROR_MESSAGE);
            return ; 
        }
        WndZwkAddManyItems wnd = new WndZwkAddManyItems( this.document, cntPositions, parentWindow );
       UI.getCurrent().addWindow( wnd );
       wnd.center();
       wnd.setWidth("80%");
       wnd.setHeight("80%");
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
            amount = new BigDecimal(tfAmount.getValue().replace(",", "."));
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

        di.setProduct(cmbProduct.getProduct());
        di.setAmount(amount);

        document.getItems().add(di);
        cntPositions.addItem(di);

        DocumentChangedEvent ev = new DocumentChangedEvent(document);
        eventBus.post(ev);

        parentWindow.setModified(true);
    }
    
    private void activeReplacement()
    {
        butZamienniki.setVisible(false);
        cmbProduct.setVisible(true);
        tfAmount.setVisible(true);
        butAdd.setVisible(true);

        List<Product> products = VOLookup.lookupProductsApi().getByCmpId( document.getCompanyUnit().getId() );
        cntProducts.addAll(products);
    }

}
