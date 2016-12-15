/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.delivery.dpz;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.themes.ValoTheme;
import java.math.BigDecimal;
import pl.vendi.ui.common.ComboBoxProducts;
import pl.vendi.ui.documents.elements.DocumentWindow;
import pl.vendi.ui.events.DocumentChangedEvent;
import pl.vo.documents.model.DocumentItem;
import pl.vo.documents.model.Document;
import pl.vo.products.model.Product;
import com.google.common.eventbus.EventBus;
import com.vaadin.ui.Alignment;
import java.util.List;
import pl.vendi.ui.VOLookup;
import pl.vo.products.model.ProductCmpCode;

/**
 *
 * @author k.skowronski
 */
public class ElNewDpzAddItem extends HorizontalLayout{
    
    BeanItemContainer<Product> cntProducts = new BeanItemContainer<Product>(Product.class);
    ComboBoxProducts cmbProduct = new ComboBoxProducts("Towar", cntProducts);
    
    BeanItemContainer<DocumentItem> cntPositions;
    
    Button butZamienniki = new Button("Aktywuj zamiennik");
    
    TextField tfAmount = new TextField("Ilość");
    Button butAdd = new Button("Dodaj zamiennik");
    
    EventBus eventBus;
    Document document;
    
    DocumentWindow parentWindow;
    
    
    
    public ElNewDpzAddItem(DocumentWindow parentWindow)
    {
        this.parentWindow = parentWindow;
        
        eventBus = new EventBus("document");
        
        
        
        this.setDefaultComponentAlignment(Alignment.BOTTOM_CENTER);
        this.setSpacing(true);
        
        this.addComponent(cmbProduct);
        cmbProduct.setWidth("400px");
 
        this.addComponent(tfAmount);
        this.addComponent(butAdd);
        
        cmbProduct.setVisible(false);
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
        
        
        
        butAdd.addStyleName(ValoTheme.BUTTON_PRIMARY);
        butAdd.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                addItem();
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
        tfAmount.setVisible(true);
        butAdd.setVisible(true);

        List<Product> products = VOLookup.lookupProductsApi().getByCmpId( document.getClient().getId() );
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
    
}
