/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.config.products;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.util.List;
import javax.xml.registry.infomodel.Organization;
import pl.vendi.ui.VOLookup;
import pl.vendi.ui.common.ComboBoxOrganisationUnit;
import pl.vendi.ui.common.VoExceptionHandler;
import pl.vo.exceptions.VOWrongDataException;
import pl.vo.organisation.model.OrganisationUnit;
import pl.vo.products.api.UnitsProductsApi;
import pl.vo.products.model.Product;
import pl.vo.products.model.UnitsProducts;

/**
 *
 * @author Piotr
 */
public class WndUnitsProducts extends Window
{
    BeanItemContainer<UnitsProducts> cntUnitsProducts = new BeanItemContainer<UnitsProducts>(UnitsProducts.class);

    List<Product> allProducts; 
    List<UnitsProducts> unitProducts;
    
    BeanItemContainer< Product> cntProductsToAdd   = new BeanItemContainer<Product>(Product.class);
    
    VerticalLayout vboxMain = new VerticalLayout();
    HorizontalLayout hboxTop = new HorizontalLayout();  
    
    HorizontalLayout hboxTables = new HorizontalLayout();
    
    Table tabProductsToAdd = new Table("Towary do przypięcia");
    Table tabProductsAdded = new Table("Towary przypisane");
    
    
    ComboBoxOrganisationUnit cmbSelectObject = new ComboBoxOrganisationUnit("Wybierz obiekt");
    
    
    private OrganisationUnit selectedUnit; 
    private UnitsProductsApi unitsProductsApi; 
    
    public WndUnitsProducts(){
        
        super("Towary w obiektach");
        unitsProductsApi = VOLookup.lookupUnitsProductsApi();
        
        this.setContent( vboxMain);
        vboxMain.setSizeFull();
        vboxMain.setMargin( true );
        vboxMain.addComponent( hboxTop );
        vboxMain.addComponent( hboxTables );
        hboxTop.addComponent(cmbSelectObject);
        
        hboxTables.addComponent( tabProductsToAdd);
        hboxTables.addComponent( tabProductsAdded );
        vboxMain.setExpandRatio( hboxTables, 1);
        
        hboxTables.setWidth("100%");
        hboxTables.setExpandRatio( tabProductsAdded, 0.5f);
        hboxTables.setExpandRatio( tabProductsToAdd,0.5f);
        
     tabProductsAdded.setWidth("100%");
     tabProductsToAdd.setWidth("100%");
     hboxTables.setSpacing( true );
     hboxTables.setSizeFull();
        
        tabProductsAdded.setContainerDataSource( cntUnitsProducts );
        tabProductsToAdd.setContainerDataSource( cntProductsToAdd );
        
        
           cntProductsToAdd.addNestedContainerProperty("measureUnit.name");
        tabProductsToAdd.setVisibleColumns(new String[]{"id","abbr",  "indexNumber", "measureUnit.name"});
        
        
          cntUnitsProducts.addNestedContainerProperty("product.measureUnit");
           cntUnitsProducts.addNestedContainerProperty("product.abbr");
            cntUnitsProducts.addNestedContainerProperty("product.name");
             cntUnitsProducts.addNestedContainerProperty("product.indexNumber");
            
         cntUnitsProducts.addNestedContainerProperty("product.measureUnit.name");
        tabProductsAdded.setVisibleColumns(new String[]{"id","product.abbr", "product.indexNumber","product.measureUnit.name"});
        
        tabProductsToAdd.setSelectable(  true );
        tabProductsToAdd.addItemClickListener( new ItemClickEvent.ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.isDoubleClick() ) { 
                    BeanItem<Product> biProd= ( BeanItem<Product>) event.getItem();
                    addProductToUnit( (Product) biProd.getBean() );
            }
        }});
        
        
        tabProductsAdded.setSelectable(  true );
        tabProductsAdded.addItemClickListener( new ItemClickEvent.ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.isDoubleClick() ) { 
                     BeanItem<UnitsProducts> biProd= ( BeanItem<UnitsProducts>) event.getItem();
                    removeProductFromUnit( biProd.getBean() );
            }
        }});
        
        readAllProducts();
        
        cmbSelectObject.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                selectedUnit = ( OrganisationUnit ) event.getProperty().getValue();
                onSelectedUnitChange();
            }
        });
    }
    
    
    
    
    private void readAllProducts()
    {
       allProducts =  VOLookup.lookupProductsApi().findAll();
       
      
    }
    
    private void onSelectedUnitChange ( )
    {
         cntUnitsProducts.removeAllItems();
       cntProductsToAdd.removeAllItems();
       
       if ( selectedUnit != null )
       { 
         unitProducts =   unitsProductsApi.findForUnit( selectedUnit.getId() );
         cntUnitsProducts.addAll( unitProducts);
        // parse products
        for (Product prod : allProducts ) {
            Boolean fContains = false;
            for ( UnitsProducts upr : unitProducts ){
                if ( upr.getProduct().getId().equals( prod.getId() ))
                  fContains = true ;
            }
            if ( !fContains ) {
                cntProductsToAdd.addItem( prod );
            }
        }
       }
    }
    
    
    private void addProductToUnit( Product prod ) 
    { 
        try {
        UnitsProducts up =  unitsProductsApi.addProductToUnit( prod.getId(), selectedUnit.getId());
        cntProductsToAdd.removeItem( prod );
        cntUnitsProducts.addItem( up ) ; 
        }
            catch( VOWrongDataException wre){
                VoExceptionHandler.handleException(wre);
            }
    }
    
    private void removeProductFromUnit( UnitsProducts up )
    {
            try {
            unitsProductsApi.delete( up );
            cntProductsToAdd.addItem( up.getProduct());
            cntUnitsProducts.removeItem( up );
            }
            catch( VOWrongDataException wre){
                VoExceptionHandler.handleException(wre);
            }
    }
}
