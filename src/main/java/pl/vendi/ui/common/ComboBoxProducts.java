/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.common;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;
import java.util.Locale;
import pl.vendi.ui.VOLookup;
import pl.vo.common.model.DictionaryValue;
import pl.vo.products.model.Product;

/**
 *
 * @author Piotr
 */
public class ComboBoxProducts extends ComboBox {
    
    public ComboBoxProducts(String caption, BeanItemContainer<Product> container) {
        
        this.setCaption(caption);
        this.setContainerDataSource(container);
        this.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        this.setItemCaptionPropertyId("nameAndProvider");
        //this.setDescription("whoseProduct");
    }
    
    public Product getProduct() {        
        return (Product) getValue();
    }
    
    public void setProduct(Long prodId) {
        Container cnt = getContainerDataSource();
        if (cnt != null) {            
            for (Object ids : cnt.getItemIds()) {
                Product dv = (Product) ids;
                if (dv != null && dv.getId().equals(prodId)) {
                    this.select(dv);
                    return;                    
                }
            }
        }
    }
    
    public void setProduct(Product value) {
        if (value == null) {
            this.setValue(null);
        } else {
            ComboBoxProducts.this.setProduct(value.getId());
        }
    }
    
    public void setDefaultDp() {        
        BeanItemContainer<Product> container = new BeanItemContainer<Product>(Product.class);
        container.addAll(VOLookup.lookupProductsApi().findAll());
        this.setContainerDataSource(container);
    }
}
