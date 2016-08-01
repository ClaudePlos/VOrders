/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.config.dictionares;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.util.List;
import pl.vendi.ui.VOLookup;
import pl.vendi.ui.VendiOrdersUI;
import pl.vendi.ui.common.CntContainerUtils;
import pl.vendi.ui.common.ComboBoxDV;
import pl.vendi.ui.common.TextFieldNumber;
import pl.vendi.ui.common.VO_UI_Consts;
import pl.vendi.ui.common.VoExceptionHandler;
import pl.vo.common.api.DictionaryApi;
import pl.vo.common.model.DictionaryValue;
import pl.vo.company.api.CompanysApi;
import pl.vo.company.model.Company;

/**
 *
 * @author Piotr
 */
public class WndDictionaries extends Window
{
    BeanContainer<Long,DictionaryValue> container = new BeanContainer<Long,DictionaryValue>(DictionaryValue.class);
     BeanItemContainer<DictionaryValue> cntDictTypes = new BeanItemContainer<DictionaryValue>(DictionaryValue.class);
     
    Table table = new Table("Słowniki");

    VerticalLayout vboxEdit = new VerticalLayout();

    ComboBoxDV cmbSelectDict = new ComboBoxDV("Słownik:", cntDictTypes);
    
    
    TextField tfValue = new TextField("Wartość");
    TextField tfDescription = new TextField("Opis");
    TextFieldNumber tfNumberValue = new TextFieldNumber("Wartość liczbowa");
   
    HorizontalLayout vboxMain = new HorizontalLayout();

    DictionaryApi api;

    private DictionaryValue selectedItem;

    private DictionaryValue selectedDictionary; 
    
    public WndDictionaries() {

        super("Konfiguracja słowników");

        api = VOLookup.lookupDictionaryApi();

        this.setContent(vboxMain);
        
        vboxMain.setSizeFull();;
        vboxMain.setSpacing(true);
        vboxMain.setMargin(true);

        
        vboxMain.addComponent( cmbSelectDict );
        vboxMain.addComponent(table);
        vboxMain.addComponent(vboxEdit);
        

        cmbSelectDict.setHeight("30px");
        vboxMain.setExpandRatio(table, 0.6f);
        vboxMain.setExpandRatio(vboxEdit, 0.3f);
        vboxEdit.setWidth("200px");
        // 
        
        table.setContainerDataSource(container);
        table.setWidth("100%");
        // create box edit
        container.setBeanIdProperty("id");
        table.setVisibleColumns(new String[]{"id","dictionaryCode","value", "description", "numberValue"});
        
        table.setSelectable( true );

        vboxEdit.addComponent(tfValue);
        vboxEdit.addComponent(tfDescription);
        vboxEdit.addComponent( tfNumberValue);
        tfNumberValue.setNullRepresentation(null);
        
        tfValue.setNullRepresentation(null);
        tfDescription.setNullRepresentation( null);
       
        tfValue.setRequired(true);
        tfDescription.setRequired(true);
       
        vboxEdit.setSpacing(true );
       
        Button butAdd = new Button("Dodaj");
        Button butSave = new Button("Zapisz");

        vboxEdit.addComponent(butAdd);
        vboxEdit.addComponent(butSave);

        refreshItems();

        butAdd.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                selectedItem = new DictionaryValue();
                modelToView();
            }
        });

        butSave.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                onClickSave();
            }
        });
        
        
        table.addItemClickListener(new ItemClickEvent.ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                BeanItem<DictionaryValue> biUser = (BeanItem<DictionaryValue>) event.getItem();
                
                selectedItem = biUser != null ? biUser.getBean()  : null;
                modelToView();
            }
        });
        
        
        cmbSelectDict.addValueChangeListener( new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
               selectedDictionary = ( DictionaryValue)  event.getProperty().getValue();
                             
               refreshItems();
            }
        });
        
        readDictionaryTypes();

    }

    private void refreshItems()
    {
         container.removeAllItems();
        if (selectedDictionary != null ) {
            List<DictionaryValue> items = api.listByDictionaryCode( selectedDictionary.getValue() );
           container.addAll(items);
        }

    }

    private void modelToView() {
        if (selectedItem != null) 
        {
            tfDescription.setValue(selectedItem.getDescription());
            tfValue.setValue(selectedItem.getValue());
            tfNumberValue.setValue( selectedItem.getNumberValue() );
           
        }
        else {
          tfDescription.setValue(null);
            tfValue.setValue(null);
            tfNumberValue.setValue( (String)null );
        }
       
    }

    private void viewToModel()
    {
        if (selectedItem != null)
        {
            selectedItem.setDescription( tfDescription.getValue() );
            selectedItem.setValue( tfValue.getValue() );
            
            selectedItem.setNumberValue( tfNumberValue.getValueNumber() );
            
            if (selectedItem.getId() == null )
            {
                if ( selectedDictionary == null ) {
                    Notification.show("Wybierz słownik do którego chcesz dodać wartość",Notification.Type.ERROR_MESSAGE);
                    return; 
                }
                selectedItem.setDictionaryCode( selectedDictionary.getValue() );
            }
          

        }
    }

    private void onClickSave() {

        if (selectedItem == null )
            selectedItem = new DictionaryValue();
        
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
            table.refreshRowCache();
            selectedItem = null;
            modelToView();
        }
    }
    
    
    private void readDictionaryTypes()
    {
        List<DictionaryValue> dicts = api.listByDictionaryCode( VO_UI_Consts.DICTIONARY_CODE_DICTIONARY_TYPES);
        cntDictTypes.removeAllItems();
        cntDictTypes.addAll( dicts );
        
    }
   
}

