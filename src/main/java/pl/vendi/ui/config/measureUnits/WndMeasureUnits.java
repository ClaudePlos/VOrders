/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.config.measureUnits;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.util.List;
import pl.vendi.ui.VOLookup;
import pl.vendi.ui.common.CntContainerUtils;
import pl.vendi.ui.common.ComboBoxDV;
import pl.vendi.ui.common.VoExceptionHandler;
import pl.vo.common.model.DictionaryValue;
import pl.vo.products.api.MeasureUnitsApi;
import pl.vo.products.api.ProductsApi;
import pl.vo.products.model.MeasureUnit;
import pl.vo.products.model.Product;

/**
 *
 * @author Piotr
 */
public class WndMeasureUnits extends Window
{
    BeanContainer<Long,MeasureUnit> container = new BeanContainer<Long,MeasureUnit>(MeasureUnit.class);

    Table tbl = new Table("Jednostki miary");

    VerticalLayout vboxEdit = new VerticalLayout();

    TextField tfName = new TextField("Nazwa");
    TextField tfAbbr = new TextField("Skrót");
  
    HorizontalLayout vboxMain = new HorizontalLayout();

    MeasureUnitsApi api;

    private MeasureUnit selectedItem;

    public WndMeasureUnits() {

        super("Konfiguracja jednostek miar");

        api = VOLookup.lookupMeasureUnitsApi();

        this.setContent(vboxMain);
        vboxMain.setSizeFull();;
        vboxMain.setSpacing(true);
        vboxMain.setMargin(true);

        vboxMain.addComponent(tbl);
        vboxMain.addComponent(vboxEdit);

        vboxMain.setExpandRatio(tbl, 0.6f);
        vboxMain.setExpandRatio(vboxEdit, 0.3f);
        vboxEdit.setWidth("200px");
        // 
        
        tbl.setContainerDataSource(container);
        tbl.setWidth("100%");
        // create box edit
        container.setBeanIdProperty("id");
        tbl.setVisibleColumns(new String[]{"id","abbr", "name"});
        
        tbl.setSelectable( true );

        vboxEdit.addComponent(tfName);
        vboxEdit.addComponent(tfAbbr);
      
        
        tfName.setNullRepresentation( "");
        tfAbbr.setNullRepresentation( "");
      
     
        
        tfName.setRequired(true);
        tfAbbr.setRequired(true);
        
       
        Button butAdd = new Button("Dodaj");
        Button butSave = new Button("Zapisz");

        vboxEdit.addComponent(butAdd);
        vboxEdit.addComponent(butSave);

        refreshCompanys();

        butAdd.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                selectedItem = new MeasureUnit();
                modelToView();
            }
        });

        butSave.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                onClickSave();
            }
        });
        
        
        tbl.addItemClickListener(new ItemClickEvent.ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                BeanItem<MeasureUnit> biUser = (BeanItem<MeasureUnit>) event.getItem();
                selectedItem = biUser != null ? biUser.getBean()  : null;
                modelToView();
            }
        });

    }

    private void refreshCompanys() {
        List<MeasureUnit> items = api.findAll();
        container.removeAllItems();
        container.addAll(items);

    }

    private void modelToView() {
        if (selectedItem != null) 
        {
           
            tfName.setValue(selectedItem.getName());
           tfAbbr.setValue(selectedItem.getAbbr());
           //cmbTaxCode.setDictionaryValue( selectedItem.gett);
           
        }
        else {
       
            tfName.setValue(null);
           tfAbbr.setValue(null);
         
        }
       
    }

    private void viewToModel()
    {
        if (selectedItem != null)
        {
            selectedItem.setName( tfName.getValue() );
            selectedItem.setAbbr( tfAbbr.getValue() );
        
        }
    }

    private void onClickSave() 
    {

        if (selectedItem == null )
            selectedItem = new MeasureUnit();
        
        if (selectedItem != null)
        {
            viewToModel();
            try {
                selectedItem = api.save(selectedItem);
            }
            catch ( Exception wre )
            {
                VoExceptionHandler.handleException( wre );
                return; 
            }
            
            
            CntContainerUtils.replaceItemWithIdOrAdd(container,selectedItem.getId(), selectedItem);
            tbl.refreshRowCache();
            selectedItem = null;
            modelToView();
        }
    }
    
   
}
