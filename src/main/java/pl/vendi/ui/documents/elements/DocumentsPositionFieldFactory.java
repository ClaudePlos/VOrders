/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.documents.elements;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import java.math.BigDecimal;
import pl.vendi.ui.common.ComboBoxProducts;
import pl.vo.documents.model.DocumentItem;
import pl.vo.products.model.Product;

/**
 *
 * @author Piotr
 */
public class DocumentsPositionFieldFactory extends DefaultFieldFactory {

    BeanItemContainer<Product> cntProducts;
    Table table; 
    
    
    private String documentType; 
    public DocumentsPositionFieldFactory(BeanItemContainer<Product> cntProducts, Table table)
    {
       
        this.cntProducts = cntProducts;
        this.table = table; 
    }

    @Override
    public Field<?> createField(Container container, final Object itemId, Object propertyId, Component uiContext)
    {
        Object val = table.getValue();
        if (!itemId.equals(table.getValue()))
        {
          return null;
        }
        if (propertyId.equals("product"))
        {
            final ComboBoxProducts cmbProd = new ComboBoxProducts(null, cntProducts);

             cmbProd.addValueChangeListener(new Property.ValueChangeListener() {

                @Override
                public void valueChange(Property.ValueChangeEvent event)
                
                {
                    DocumentItem docItem = (DocumentItem) itemId;
                    if (docItem != null) {
                        docItem.setProduct(cmbProd.getProduct());
                    }
                    
                }
            });
            return cmbProd;

        }
        
        else if ( propertyId.equals("unitPriceNet ") ) 
        {
            final TextField tfCena = new TextField();
            tfCena.addValueChangeListener( new Property.ValueChangeListener() {

                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                   DocumentItem docItem = (DocumentItem) itemId;
                    if (docItem != null) {
                        try {
                            BigDecimal nw = new BigDecimal(( String ) event.getProperty().getValue() );
                              docItem.setUnitPriceNet( nw );
                        }
                        catch ( Exception e ) 
                        {
                            
                                
                                }
                      
                    }
                }
            });
        }

        return super.createField(container, itemId, propertyId, uiContext);
    }
}
