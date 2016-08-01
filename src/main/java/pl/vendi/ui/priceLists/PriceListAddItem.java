/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.priceLists;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import java.math.BigDecimal;
import java.util.List;
import pl.vendi.ui.VOLookup;
import pl.vendi.ui.common.ComboBoxProducts;
import pl.vo.documents.model.Document;
import pl.vo.documents.model.DocumentItem;
import pl.vo.products.model.Product;

/**
 *
 * @author Piotr
 */
public class PriceListAddItem extends HorizontalLayout
{
    
      BeanItemContainer<Product> cntProducts = new BeanItemContainer<Product>(Product.class);
    ComboBoxProducts cmbProduct = new ComboBoxProducts("Towar", cntProducts);
    TextField tfPrice = new TextField("Cena");
    
    Button butAdd = new Button("Dodaj");
    
    Document document; 
     BeanItemContainer<DocumentItem> cntPositions;
    WndPriceList parenWnd; 
    public PriceListAddItem(WndPriceList parenWnd)
    { 
         List<Product> products = VOLookup.lookupProductsApi().findAll();
        cntProducts.addAll(products);
        this.parenWnd = parenWnd;
        
        
        this.setSpacing(true);
        this.addComponent( cmbProduct);
        this.addComponent( tfPrice );
        this.addComponent( butAdd );
        
        butAdd.addStyleName( ValoTheme.BUTTON_PRIMARY );
        butAdd.addClickListener( new Button.ClickListener() {

             @Override
             public void buttonClick(Button.ClickEvent event) {
                addItem();
             }
         });
        
        
        
    }
    
    public void setDocument( Document doc, BeanItemContainer<DocumentItem> cntPositions)
    {
        this.document = doc; 
        this.cntPositions = cntPositions;
        this.setEnabled( document != null );
    }
    
    private void addItem()
    {
        
        BigDecimal price; 
        
        if ( cmbProduct.getProduct() == null ){
            return; 
        }
        if (document == null ){
            Notification.show("Brak dokumentu!", Notification.Type.ERROR_MESSAGE);
            return; 
        }
        
        price = new BigDecimal( tfPrice.getValue() );
        
        DocumentItem di = new DocumentItem();
        
        di.setProduct( cmbProduct.getProduct() );
        di.setUnitPriceNet( price );
        
        document.getItems().add( di );        
        cntPositions.addItem( di ) ; 
        
        parenWnd.setModified( true );
    }
    
}
