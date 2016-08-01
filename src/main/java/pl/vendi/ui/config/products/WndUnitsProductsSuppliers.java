/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.config.products;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.util.List;
import pl.vendi.ui.VOLookup;
import pl.vendi.ui.common.ComboBoxCompany;
import pl.vendi.ui.common.ComboBoxOrganisationUnit;
import pl.vendi.ui.common.DateFieldPl;
import pl.vendi.ui.common.VODateToStringConverter;
import pl.vendi.ui.common.VoExceptionHandler;
import pl.vo.company.model.Company;
import pl.vo.exceptions.VOWrongDataException;
import pl.vo.organisation.model.OrganisationUnit;
import pl.vo.products.api.UnitsProductsApi;
import pl.vo.products.api.UnitsProductsSuppliersApi;
import pl.vo.products.model.Product;
import pl.vo.products.model.UnitsProducts;
import pl.vo.products.model.UnitsProductsSuppliers;

/**
 *
 * @author Piotr
 */
public class WndUnitsProductsSuppliers extends Window {

    BeanItemContainer<UnitsProductsSuppliers> cntUnitsProducts = new BeanItemContainer<UnitsProductsSuppliers>(UnitsProductsSuppliers.class);

    List<Product> allProducts;
    List<UnitsProducts> unitProducts;
    List<UnitsProductsSuppliers> unitSuppliers;

    BeanItemContainer<UnitsProducts> cntProductsToAdd = new BeanItemContainer<UnitsProducts>(UnitsProducts.class);

    VerticalLayout vboxMain = new VerticalLayout();
    HorizontalLayout hboxTop = new HorizontalLayout();

    HorizontalLayout hboxTables = new HorizontalLayout();

    Table tabProductsToAdd = new Table("Towary przypisane do obiektu");
    Table tabProductsAdded = new Table("Towary i ich dostawcy");

    ComboBoxOrganisationUnit cmbSelectObject = new ComboBoxOrganisationUnit("Wybierz obiekt");
    ComboBoxCompany cmbSelectCompany = new ComboBoxCompany("Wybierz firmę");

    DateFieldPl dfDateFrom = new DateFieldPl("Data Od");
    DateFieldPl dfDateTill = new DateFieldPl("Data Do");

    private OrganisationUnit selectedUnit;
    private Company selectedCompany;
    private UnitsProductsApi unitsProductsApi;

    private UnitsProductsSuppliersApi unitsProductsSuppliersApi;

    public WndUnitsProductsSuppliers() {

        super("Dostawcy towarów w obiektach");
        unitsProductsApi = VOLookup.lookupUnitsProductsApi();
        unitsProductsSuppliersApi = VOLookup.lookupUnitsProductsSuppliersApi();

        this.setContent(vboxMain);
        vboxMain.setSizeFull();
        vboxMain.setMargin(true);
        vboxMain.addComponent(hboxTop);
        vboxMain.addComponent(hboxTables);

        hboxTop.addComponent(cmbSelectObject);

        hboxTop.addComponent(cmbSelectCompany);
        hboxTop.addComponent(dfDateFrom);
        hboxTop.addComponent(dfDateTill);
        hboxTop.setSpacing(true);

        hboxTables.addComponent(tabProductsToAdd);
        hboxTables.addComponent(tabProductsAdded);
        vboxMain.setExpandRatio(hboxTables, 1);

        hboxTables.setWidth("100%");
        hboxTables.setExpandRatio(tabProductsAdded, 0.5f);
        hboxTables.setExpandRatio(tabProductsToAdd, 0.5f);

        tabProductsAdded.setWidth("100%");
        tabProductsToAdd.setWidth("100%");
        hboxTables.setSpacing(true);
        hboxTables.setSizeFull();

        tabProductsAdded.setContainerDataSource(cntUnitsProducts);
        tabProductsToAdd.setContainerDataSource(cntProductsToAdd);

        cntProductsToAdd.addNestedContainerProperty("product.measureUnit");
        cntProductsToAdd.addNestedContainerProperty("product.abbr");
        cntProductsToAdd.addNestedContainerProperty("product.name");
        cntProductsToAdd.addNestedContainerProperty("product.indexNumber");
        cntProductsToAdd.addNestedContainerProperty("product.measureUnit.name");

        tabProductsToAdd.setVisibleColumns(new String[]{"product.abbr", "product.measureUnit.name"});
        tabProductsToAdd.setColumnHeaders(new String[]{"Towar", "Jm."});

        cntUnitsProducts.addNestedContainerProperty("product.measureUnit");
        cntUnitsProducts.addNestedContainerProperty("product.abbr");
        cntUnitsProducts.addNestedContainerProperty("product.name");
        cntUnitsProducts.addNestedContainerProperty("product.indexNumber");
        cntUnitsProducts.addNestedContainerProperty("supplier.abbr");

        cntUnitsProducts.addNestedContainerProperty("product.measureUnit.name");
        tabProductsAdded.setVisibleColumns(new String[]{"product.abbr", "product.measureUnit.name", "supplier.abbr", "dateFrom", "dateTill"});
        tabProductsAdded.setColumnHeaders(new String[]{"Towar", "Jm.", "Dostawca", "Od", "Do"});

        tabProductsAdded.setConverter("dateFrom", new VODateToStringConverter());
        tabProductsAdded.setConverter("dateTill", new VODateToStringConverter());

        tabProductsToAdd.setSelectable(true);
        tabProductsToAdd.addItemClickListener(new ItemClickEvent.ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.isDoubleClick()) {
                    BeanItem<UnitsProducts> biProd = (BeanItem<UnitsProducts>) event.getItem();
                    addProductToUnit((UnitsProducts) biProd.getBean());
                }
            }
        });

        tabProductsAdded.setSelectable(true);
        tabProductsAdded.addItemClickListener(new ItemClickEvent.ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                if (event.isDoubleClick()) {
                    BeanItem<UnitsProductsSuppliers> biProd = (BeanItem<UnitsProductsSuppliers>) event.getItem();
                    removeProductFromUnit(biProd.getBean());
                }
            }
        });

        readAllProducts();

        cmbSelectObject.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                selectedUnit = (OrganisationUnit) event.getProperty().getValue();
                onSelectedUnitChange();
            }
        });

        cmbSelectCompany.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                selectedCompany = (Company) event.getProperty().getValue();
                onSelectedCompanyChange();
            }
        });
    }

    private void readAllProducts() {
        allProducts = VOLookup.lookupProductsApi().findAll();

    }

    private void onSelectedCompanyChange() {
        if (selectedCompany != null) {

        }
    }

    private void onSelectedUnitChange() {
        cntUnitsProducts.removeAllItems();
        cntProductsToAdd.removeAllItems();

        if (selectedUnit != null) {
            unitProducts = unitsProductsApi.findForUnit(selectedUnit.getId());

            // read all suppliers for products
            unitSuppliers = unitsProductsSuppliersApi.findForUnit(selectedUnit.getId(), null);
            cntUnitsProducts.addAll(unitSuppliers);

            // parse products
            for (UnitsProducts prod : unitProducts) {
                Boolean fContains = false;
                for (UnitsProductsSuppliers unsup : unitSuppliers) {
                    if (unsup.getProduct().getId().equals(prod.getProduct().getId())) {
                        fContains = true;
                    }
                }
                if (!fContains) {
                    cntProductsToAdd.addItem(prod);
                }
            }
        }

    }

    private void addProductToUnit(UnitsProducts prod) {
        if (dfDateFrom.getValue() == null) {
            Notification.show("Musisz wybrać datę od kiedy obowiązuje dostawa", Notification.Type.ERROR_MESSAGE);
            return;
        }

        if (selectedCompany == null) {
            Notification.show("Wybierz dostawcę", Notification.Type.ERROR_MESSAGE);
            return;
        }

        try {
            UnitsProductsSuppliers up = unitsProductsSuppliersApi.addProductToUnit(prod.getProduct().getId(), selectedUnit.getId(), selectedCompany.getId(), dfDateFrom.getValue(), dfDateTill.getValue());
            cntProductsToAdd.removeItem(prod);
            cntUnitsProducts.addItem(up);
        } catch (VOWrongDataException wre) {
            VoExceptionHandler.handleException(wre);
        }
    }

    private void removeProductFromUnit(UnitsProductsSuppliers up) {
        try {
            unitsProductsSuppliersApi.delete(up);
            cntProductsToAdd.addItem(up.getProduct());
            cntUnitsProducts.removeItem(up);
        } catch (VOWrongDataException wre) {
            VoExceptionHandler.handleException(wre);
        }
    }
}
