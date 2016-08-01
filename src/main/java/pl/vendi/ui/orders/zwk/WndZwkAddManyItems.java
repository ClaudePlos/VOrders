/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.orders.zwk;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;
import pl.vendi.ui.VOLookup;
import pl.vendi.ui.documents.elements.DocumentWindow;
import pl.vo.documents.model.Document;
import pl.vo.documents.model.DocumentItem;
import pl.vo.products.api.ProductsApi;
import pl.vo.products.model.Product;
import pl.vo.products.model.UnitsProducts;

/**
 *
 * @author Piotr
 */
public class WndZwkAddManyItems extends Window {
    
    private static final Logger logger = Logger.getLogger(WndZwkAddManyItems.class.getName());
    
    private Document order;
    private BeanItemContainer<Product> productsAll = new BeanItemContainer<Product>(Product.class);
    BeanItemContainer<DocumentItem> cntPositions;
    private Table tblProducts = new Table();
    
    VerticalLayout vboxMain = new VerticalLayout();
    HorizontalLayout hboxDol = new HorizontalLayout();
    
    Button butSave = new Button("Zapisz");
    Button butCancel = new Button("Anuluj");
      DocumentWindow parentWindow;
    
    public WndZwkAddManyItems(Document order, BeanItemContainer<DocumentItem> cntPositions,  DocumentWindow parentWindow) {
        this.order = order;
        this.parentWindow = parentWindow                ; 
        logger.fine("WndZwkAddManyItems constructor");
        
        this.cntPositions = cntPositions;
        
        this.setContent(vboxMain);
        vboxMain.setSizeFull();;
        vboxMain.addComponent(tblProducts);
        vboxMain.addComponent(hboxDol);
        hboxDol.setHeight("40px");
        vboxMain.setExpandRatio(tblProducts, 1);
        vboxMain.setSpacing(true);
        vboxMain.setMargin(true);
        
        hboxDol.addComponent(butSave);
        hboxDol.addComponent(butCancel);
        butSave.addClickListener(new Button.ClickListener() {
            
            @Override
            public void buttonClick(Button.ClickEvent event) {
                save();
            }
        });
        
        butCancel.addClickListener(new Button.ClickListener() {
            
            @Override
            public void buttonClick(Button.ClickEvent event) {
                close();
            }
        });
        
        init();
    }
    
    private void init() {
        // list products for company

        List<UnitsProducts> products = VOLookup.lookupUnitsProductsApi().findForUnit(this.order.getCompanyUnit().getId());
        products.forEach((up) -> {
            productsAll.addBean(up.getProduct());
            order.getItems().forEach((di) -> {
                if (di.getProduct().getId().equals(up.getProduct().getId())) {
                    up.getProduct().setQuantity(di.getAmount());
                }
            });
        });
        
        tblProducts.setContainerDataSource(productsAll);
        tblProducts.setVisibleColumns(new String[]{"abbr", "quantity"});
        tblProducts.addGeneratedColumn("quantity", new Table.ColumnGenerator() {
            
            @Override
            public Object generateCell(Table source, final Object itemId, Object columnId) {
                
                TextField lab = new TextField();
                Product product = (Product) itemId;
                if (product != null && product.getQuantity() != null) {
                    lab.setValue(product.getQuantity().toString());
                } else {
                    lab.setValue("");
                }
                
                lab.addValueChangeListener(new Property.ValueChangeListener() {
                    
                    @Override
                    public void valueChange(Property.ValueChangeEvent event) {
                        String sval = (String) event.getProperty().getValue();
                        Product product = (Product) itemId;
                        product.setQuantity(new BigDecimal(sval));
                    }
                });
                
                return lab;
                
            }
        });
        
    }
    
    private void save() {
        for (Product prod : productsAll.getItemIds()) {
            if (prod.getQuantity() != null && !prod.getQuantity().equals(0)) {
                DocumentItem di = new DocumentItem();
                boolean fDodaj = true;
                for (DocumentItem ddi : order.getItems()) {
                    if (ddi.getProduct().getId().equals(prod.getId())) {
                        di = ddi;
                        fDodaj = false;
                    }
                }
                di.setProduct(prod);
                di.setAmount(prod.getQuantity());
                if ( !fDodaj) {
                    
                }
                if (fDodaj) {
                    order.getItems().add(di);
                    cntPositions.addItem(di);
                } else {                    
                    
                }
            }
        }
        parentWindow.refreshTable();
        parentWindow.setModified( true );
        close();
    }
    
    
    
}
