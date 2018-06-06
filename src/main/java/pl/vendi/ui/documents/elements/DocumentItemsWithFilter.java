/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.documents.elements;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.ArrayList;
import pl.vendi.ui.common.VoExceptionHandler;
import pl.vo.documents.model.Document;
import pl.vo.documents.model.DocumentItem;

/**
 *
 * @author Piotr
 */
public class DocumentItemsWithFilter extends VerticalLayout {

    DocumentItemsTable itemsTable;
    DocumentWindow parentWindow;

    private Document document;
//    private List<DocumentItem> visibleItems = new ArrayList<DocumentItem>();

    BeanItemContainer<DocumentItem> cnt = new BeanItemContainer< DocumentItem>(DocumentItem.class);
    BeanItemContainer<DocumentItem> cntVisible = new BeanItemContainer< DocumentItem>(DocumentItem.class);
    HorizontalLayout hboxGora = new HorizontalLayout();
    TextField tfFiltr = new TextField();
    Label labDiscount = new Label();
    

    public DocumentItemsWithFilter(String documentTypeGroup, DocumentWindow parentWindow) {
        this.parentWindow = parentWindow;
        
        this.itemsTable = new DocumentItemsTable(documentTypeGroup, parentWindow, this);
        
        
        this.addComponent(hboxGora);
        hboxGora.addComponent( new Label("Filtr:"));
        hboxGora.addComponent( tfFiltr);
        hboxGora.addComponent( labDiscount );
        this.addComponent( itemsTable );
        this.setSizeFull();
        this.setExpandRatio( itemsTable, 1);
        itemsTable.setSizeFull();
        
        cnt.addNestedContainerProperty("product.abbr");
        cnt.addNestedContainerProperty("product.measureUnit");
        cnt.addNestedContainerProperty("product.measureUnit.abbr");

        cnt.addNestedContainerProperty("unitProductSupplier.supplier");
        cnt.addNestedContainerProperty("unitProductSupplier.supplier.abbr");
        
        
        cntVisible.addNestedContainerProperty("product.abbr");
        cntVisible.addNestedContainerProperty("product.measureUnit");
        cntVisible.addNestedContainerProperty("product.measureUnit.abbr");

        cntVisible.addNestedContainerProperty("unitProductSupplier.supplier");
        cntVisible.addNestedContainerProperty("unitProductSupplier.supplier.abbr");
        
        tfFiltr.addValueChangeListener( new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
               doFilter();
            }
        });
        
        cnt.addItemSetChangeListener( new Container.ItemSetChangeListener() {

            @Override
            public void containerItemSetChange(Container.ItemSetChangeEvent event) {
//                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                doFilter();;
            }
        });
    }
    
    
    public void removeItem(DocumentItem itemId) {
        cnt.removeItem(itemId);
        if ( this.cntVisible.containsId(itemId))
            cntVisible.removeItem(itemId);
        setModified();
    }

    public void setModified()
    {
        parentWindow.setModified(true);
    }
    public void setDocument(Document document) {
 
        this.document = document;
        refreshRows();
        this.itemsTable.setDocument( document );
        this.itemsTable.setContainer(cntVisible);
        // set column
//        setColumnSet();

        showDiscount();
    }
    
    
    private void showDiscount()
    {
        if ( !this.document.dicountIsEmpty() )
        {
            labDiscount.setStyleName( ValoTheme.LABEL_SUCCESS);
            labDiscount.setValue( " Rabat: " + this.document.getDiscount() + "%" );
        }
    }

    public BeanItemContainer<DocumentItem> getCnt() {
        return cnt;
    }

    public void refreshRows()
    {
        if (document != null) {

               cnt.removeAllItems();

    //            if (this.isEditable()) {
                //  document.getItems().add( new DocumentItem( ));
    //            }

                try {
                    cnt.addAll(document.getItems());
                } catch (Exception e) {
                    return;
                } 
            
            
            doFilter();
        }
    }
    
    private void doFilter() { 
        cntVisible.removeAllItems();
        String filter = this.tfFiltr.getValue();
        for ( DocumentItem di : cnt.getItemIds()){
            if ( filter.length() == 0 || di.getProduct().getName().toUpperCase().indexOf( filter.toUpperCase() ) >=0 ){
                cntVisible.addItem( di ) ;
            }
            
        }
    }
    
    
    public void getInfo(DocumentItem itemId) {
        
        
    }
    
}
