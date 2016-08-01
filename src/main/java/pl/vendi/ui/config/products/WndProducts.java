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
import com.vaadin.ui.*;

import java.util.List;
import pl.vendi.ui.VOLookup;
import pl.vendi.ui.VendiOrdersUI;
import pl.vendi.ui.common.CntContainerUtils;
import pl.vendi.ui.common.ComboBoxCompany;
import pl.vendi.ui.common.ComboBoxDV;
import pl.vendi.ui.common.VO_UI_Consts;
import pl.vendi.ui.common.VoExceptionHandler;
import pl.vo.common.api.DictionaryApi;
import pl.vo.common.model.DictionaryValue;
import pl.vo.company.api.CompanysApi;
import pl.vo.company.model.Company;
import pl.vo.products.api.MeasureUnitsApi;
import pl.vo.products.api.ProductsApi;
import pl.vo.products.model.MeasureUnit;
import pl.vo.products.model.Product;
import pl.vo.products.model.ProductCmpCode;

/**
 *
 * @author Piotr
 */
public class WndProducts extends Window {

    BeanContainer<Long, Product> container = new BeanContainer<Long, Product>(Product.class);

    BeanItemContainer<DictionaryValue> cntTaxRates = new BeanItemContainer<DictionaryValue>(DictionaryValue.class);
    BeanItemContainer<MeasureUnit> cntUnits = new BeanItemContainer<MeasureUnit>(MeasureUnit.class);

    Table tbl = new Table("Produkty");

    VerticalLayout vboxEdit = new VerticalLayout();

    TextField tfName = new TextField("Nazwa towaru");
    TextField tfAbbr = new TextField("Skrót");
    TextField tfIndex = new TextField("Indeks");
    TextField tfExternalCode = new TextField("Kod zewnętrzny");
    ComboBoxDV cmbTaxCode = new ComboBoxDV("Stawka Vat", cntTaxRates);
    ComboBox cmbUnit = new ComboBox("Jednostka miary");

    HorizontalLayout vboxMain = new HorizontalLayout();

    ProductsApi api;
    MeasureUnitsApi measureUnitsApi;
    DictionaryApi dictionaryApi;

    private Product selectedItem;
    
    Table tblCodes = new Table("Kody towarów u dostawców");
    BeanItemContainer<ProductCmpCode> cntProductsCodes = new BeanItemContainer<ProductCmpCode>(ProductCmpCode.class);

    public WndProducts() {

        super("Konfiguracja produktów");

        api = VOLookup.lookupProductsApi();
        measureUnitsApi = VOLookup.lookupMeasureUnitsApi();
        dictionaryApi = VOLookup.lookupDictionaryApi();

        this.setContent(vboxMain);
        vboxMain.setSizeFull();;
        vboxMain.setSpacing(true);
        vboxMain.setMargin(true);


        vboxMain.addComponent(tbl);

        Panel panelEdit= new Panel();
        panelEdit.setContent(vboxEdit);
        panelEdit.setHeight("100%");
        vboxMain.addComponent(panelEdit);

        vboxMain.setExpandRatio(tbl, 0.6f);
        vboxMain.setExpandRatio(panelEdit, 0.3f);
        vboxEdit.setWidth("200px");
        // 

        tbl.setContainerDataSource(container);
        tbl.setWidth("100%");
        // create box edit
        container.setBeanIdProperty("id");
        container.addNestedContainerProperty("measureUnit.name");
        tbl.setVisibleColumns(new String[]{"id", "abbr", "name", "indexNumber", "externalCode", "taxRate", "measureUnit.name"});
        tbl.setColumnHeaders(new String[]{"id", "Skrót", "Nazwa", "Indeks", "Kod zewnętrzny", "Vat", "Jm."});

        tbl.setSelectable(true);

        vboxEdit.addComponent(tfName);
        vboxEdit.addComponent(tfAbbr);
        vboxEdit.addComponent(tfIndex);
        vboxEdit.addComponent(tfExternalCode);
        vboxEdit.addComponent(cmbTaxCode);
        vboxEdit.addComponent(cmbUnit);

        tfName.setWidth("100%");

        tfName.setNullRepresentation("");
        tfAbbr.setNullRepresentation("");
        tfIndex.setNullRepresentation("");
        tfExternalCode.setNullRepresentation("");

        tfName.setRequired(true);
        tfAbbr.setRequired(true);
        cmbTaxCode.setRequired(true);
        cmbUnit.setRequired(true);

        cmbUnit.setContainerDataSource(cntUnits);
        cmbUnit.setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        cmbUnit.setItemCaptionPropertyId("abbr");

        Button butAdd = new Button("Dodaj");
        Button butSave = new Button("Zapisz");
        Button butAddCode = new Button("Dodaj kod");

        vboxEdit.setWidth("100%");
        vboxEdit.addComponent( tblCodes );
        tblCodes.setHeight("150px");
        tblCodes.setWidth("100%");
        tblCodes.setContainerDataSource( cntProductsCodes );
        tblCodes.addGeneratedColumn("company",new Table.ColumnGenerator() {

            @Override
            public Object generateCell(Table source, final Object itemId, Object columnId) {
                final ComboBoxCompany cmbCo = new ComboBoxCompany(null);
                cmbCo.addValueChangeListener( new Property.ValueChangeListener() {

                    @Override
                    public void valueChange(Property.ValueChangeEvent event) {
                        ProductCmpCode code = (ProductCmpCode) itemId;
                        code.setCmpId(cmbCo.getCompanyId());
                    }
                });
                 ProductCmpCode code = (ProductCmpCode) itemId;
                cmbCo.setValueCompanyId(code.getCmpId());
                return cmbCo;
            }
        });
        tblCodes.setVisibleColumns("company", "code");
         tblCodes.setColumnHeaders("Firma", "Kod");
        tblCodes.setEditable( true );
        
        
        HorizontalLayout hboxBut = new HorizontalLayout();
        hboxBut.addComponent(butAdd);
        hboxBut.addComponent(butSave);
        hboxBut.addComponent( butAddCode);
        vboxEdit.addComponent( hboxBut );
        vboxEdit.setSpacing(true);

        refreshProducts();

        butAdd.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                selectedItem = new Product();
                modelToView();
            }
        });

        butSave.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                onClickSave();
            }
        });
        
        butAddCode.addClickListener( new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if ( selectedItem != null ) {
                    ProductCmpCode code = new ProductCmpCode();
                    code.setProduct( selectedItem );
                    selectedItem.getCodes().add( code );
                    cntProductsCodes.addItem( code );
                }
            }
        });

        tbl.addItemClickListener(new ItemClickEvent.ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                BeanItem<Product> biUser = (BeanItem<Product>) event.getItem();
                selectedItem = biUser != null ? biUser.getBean() : null;
                modelToView();
            }
        });

        // load units tax
        cntTaxRates.addAll(dictionaryApi.listByDictionaryCode(VO_UI_Consts.DICTIONARY_CODE_VAT_CODES));
        cntUnits.addAll(measureUnitsApi.findAll());

    }

    private void refreshProducts() {
        List<Product> items = api.findAll();
        container.removeAllItems();
        container.addAll(items);

    }

    private void modelToView() {
        if (selectedItem != null) {
            tfExternalCode.setValue(selectedItem.getExternalCode());
            tfIndex.setValue(selectedItem.getIndex());
            tfName.setValue(selectedItem.getName());
            tfAbbr.setValue(selectedItem.getAbbr());

            cmbTaxCode.setDictionaryValueByNumberVal(selectedItem.getTaxRate());

            if (selectedItem.getMeasureUnit() != null) {
                cmbUnit.select(selectedItem.getMeasureUnit());
            } else {
                cmbUnit.setValue(null);
            }
            
            cntProductsCodes.removeAllItems();
            if ( selectedItem != null && cntProductsCodes != null  )
              cntProductsCodes.addAll( selectedItem.getCodes() );

        } else {
            tfExternalCode.setValue(null);
            tfIndex.setValue(null);
            tfName.setValue(null);
            tfAbbr.setValue(null);
            cmbTaxCode.setValue(null);
            cmbUnit.setValue(null);

        }

    }

    private void viewToModel() {
        if (selectedItem != null) {
            selectedItem.setName(tfName.getValue());
            selectedItem.setAbbr(tfAbbr.getValue());
            selectedItem.setIndex(tfIndex.getValue());
            selectedItem.setExternalCode(tfExternalCode.getValue());

            if (cmbTaxCode.getDictionaryValue() != null) {
                selectedItem.setTaxRate(cmbTaxCode.getDictionaryValue().getNumberValue());
            } else {
                selectedItem.setTaxRate(null);
            }

            MeasureUnit unit = (MeasureUnit) cmbUnit.getValue();
            if (unit != null) {
                selectedItem.setMeasureUnit(unit);
            } else {
                selectedItem.setMeasureUnit(null);
            }
        }
    }

    private void onClickSave() {

        if (selectedItem == null) {
            selectedItem = new Product();
        }

        if (selectedItem != null) {
            viewToModel();
            try {
                selectedItem = api.save(selectedItem);
            } catch (Exception wre) {
                VoExceptionHandler.handleException(wre);
                return;
            }

            CntContainerUtils.replaceItemWithIdOrAdd(container, selectedItem.getId(), selectedItem);
            tbl.refreshRowCache();
            selectedItem = null;
            modelToView();
        }
    }

}
