/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.stock;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.util.ArrayList;
import java.util.List;
import pl.vendi.ui.VOLookup;
import pl.vendi.ui.VendiOrdersUI;
import pl.vendi.ui.common.ComboBoxProducts;
import pl.vendi.ui.common.VoExceptionHandler;
import pl.vo.exceptions.VOWrongDataException;
import pl.vo.products.api.ProductsApi;
import pl.vo.products.model.Product;
import pl.vo.stock.api.StockApi;
import pl.vo.stock.model.Stock;

/**
 *
 * @author Piotr
 */
public class WndStock extends Window implements Button.ClickListener {
    
    VerticalLayout vboxMain = new VerticalLayout();
    HorizontalLayout hboxAdd = new HorizontalLayout();
    ComboBoxProducts cmbProductToAdd = new ComboBoxProducts("Towar do dodania", null);
    Button butAdd = new Button("Dodaj towar");
    Button butAddAll = new Button("Dodaj wszystkie towary");
    Button butSave = new Button("Zapisz");
    
    Table tblStock = new Table();
    BeanItemContainer< Stock> cntStock = new BeanItemContainer<Stock>(Stock.class);
    
    StockApi stockApi = VOLookup.lookupStockApi();
    ProductsApi productApi = VOLookup.lookupProductsApi();
    List<Stock> stocks = new ArrayList<>();

    public WndStock() {
        
        super("Stany magazynowe");
        this.setContent(vboxMain);
        vboxMain.setSpacing( true );
        vboxMain.setMargin( true );
        
        vboxMain.setSizeFull();;
        vboxMain.addComponent(tblStock);
        vboxMain.addComponent(hboxAdd);
        hboxAdd.addComponent(cmbProductToAdd);
        hboxAdd.addComponent(butAdd);
        hboxAdd.addComponent(butAddAll);
        hboxAdd.addComponent(butSave);
        vboxMain.setExpandRatio(tblStock, 1);
        tblStock.setSizeFull();
        hboxAdd.setWidth("100%");
        
        tblStock.setSizeFull();
        cmbProductToAdd.setDefaultDp(); 
        
        butAdd.addClickListener(this);
        butAddAll.addClickListener(this);
        butSave.addClickListener(this);
        
        tblStock.setEditable( true );
        cntStock.addNestedContainerProperty("product.name");
        tblStock.setContainerDataSource( cntStock );
        tblStock.setVisibleColumns(new Object[]{"product.name","stockCount","unlimited"});
        
        refresh();
    }
    
    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton().equals(butAddAll)) {
           // add al products
            
            List<Product> products = productApi.findAll();
            for (Product prod : products) {                
                boolean exists = stocks.stream().anyMatch(s -> {
                    return s.getProduct().equals(prod);
                });
                if (!exists) {
                    Stock st = new Stock();
                    st.setProduct(prod);
                    cntStock.addItem(st);
                    
                }
            }  
        }
        else if ( event.getButton().equals( butSave)) {
            // save
            for ( Stock stock : cntStock.getItemIds() ){
                try {
                 stockApi.save( stock );
                }
                catch ( VOWrongDataException wre ){
                    VoExceptionHandler.handleException( wre );
                    return ; 
                }
                }
            refresh();;
        }
    }
    
    private void refresh() {        
        cntStock.removeAllItems();
        stocks = stockApi.findAll();
        cntStock.addAll(stocks);
    }
    
}
