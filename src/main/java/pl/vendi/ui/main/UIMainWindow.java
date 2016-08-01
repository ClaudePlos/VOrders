/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.main;

import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import pl.vendi.ui.VOLookup;
import pl.vendi.ui.VendiOrdersUI;
import pl.vendi.ui.config.companys.WndCompanys;
import pl.vendi.ui.config.dictionares.WndDictionaries;
import pl.vendi.ui.config.measureUnits.WndMeasureUnits;
import pl.vendi.ui.config.organisation.WndOrganisationUnits;
import pl.vendi.ui.config.products.WndProducts;
import pl.vendi.ui.config.products.WndUnitsProducts;
import pl.vendi.ui.config.products.WndUnitsProductsSuppliers;
import pl.vendi.ui.config.users.WndConfigUsers;
import pl.vendi.ui.delivery.pz.WndDpzs;
import pl.vendi.ui.delivery.pz.WndPz;
import pl.vendi.ui.delivery.pz.WndPzs;
import pl.vendi.ui.invoice.WndInvoices;
import pl.vendi.ui.orders.zwk.WndOrderZwk;
import pl.vendi.ui.orders.zwk.WndOrdersZwk;
import pl.vendi.ui.orders.zwd.WndOrdersZwd;
import pl.vendi.ui.priceLists.WndPriceLists;
import pl.vendi.ui.stock.WndStock;
import pl.vo.VOConsts;
import pl.vo.common.VoUserSession;

/**
 *
 * @author Piotr
 */
public class UIMainWindow extends VerticalLayout {

    // 
    private static String MENU_UZYTKOWNICY = "Użytkownicy";
    private static String MENU_OBIEKTY = "Obiekty w firmie";
    private static String MENU_TOWARY = "Towary";
    private static String MENU_TOWARY_OBIEKTOW = "Towary w obiektach";
    private static String MENU_TOWARY_OBIEKTOW_DOSTAWCY = "Dostawcy towarów w obiektach";
    private static String MENU_FIRMY = "Firmy";
    private static String MENU_DICTIONARIES = "Słowniki";

    private static String MENU_KONFIGURACJA = "Konfiguracja";
    private static String MENU_MEASURE_UNITS = "Jednostki Miar";

    private static String MENU_PRICE_LISTS = "Cenniki";

    private static String MENU_ORDERS_ZWK = "Zamówienia ZWK";
    private static String MENU_ORDERS_ZWD = "Zamówienia ZWD";
    private static String MENU_ADD_ORDER = "Dodaj zamówienie ZWK";

    private static String MENU_DPZS = "Dostawy DPZ";
    private static String MENU_PZS = "Dostawy";
    private static String MENU_ADD_PZ = "Dodaj dostawę";

    private static String MENU_STOCK = "Stany magazynowe";
    
    // invoices
    private static String MENU_INVOICES = "Faktury";

    MenuBar menu = new MenuBar();

    VoUserSession userSession;

    HorizontalLayout hboxTop = new HorizontalLayout();
    Button butLogout = new Button("Wyloguj");

    public UIMainWindow() {

        userSession = VOLookup.lookupVoUserSession();
        createMenu();

    }

    private void createMenu() {

        MenuBar.Command menuCommand = new MenuBar.Command() {

            @Override
            public void menuSelected(MenuBar.MenuItem selectedItem) {
                onMenuItem(selectedItem);
            }
        };
        if (userSession.getLoggedUser().hasRole("MENU_TOWARY")) {
            menu.addItem(MENU_TOWARY, menuCommand);
        }

        if (userSession.getLoggedUser().hasRole("MENU_KONFIGURACJA")) {
            MenuBar.MenuItem miConfig = menu.addItem(MENU_KONFIGURACJA, null);
            miConfig.addItem(MENU_UZYTKOWNICY, menuCommand);
            miConfig.addItem(MENU_OBIEKTY, menuCommand);

            miConfig.addItem(MENU_TOWARY_OBIEKTOW, menuCommand);
            miConfig.addItem(MENU_TOWARY_OBIEKTOW_DOSTAWCY, menuCommand);
            miConfig.addItem(MENU_FIRMY, menuCommand);
            miConfig.addItem(MENU_DICTIONARIES, menuCommand);
            miConfig.addItem(MENU_MEASURE_UNITS, menuCommand);
        }

        if (userSession.getLoggedUser().hasRole("MENU_CENNIKI")) {
            menu.addItem(MENU_PRICE_LISTS, menuCommand);
        }

        if (userSession.getLoggedUser().hasRole("MENU_ZAMOWIENIA")) {
            MenuBar.MenuItem miOrders = menu.addItem("Zamówienia", null);
            if (userSession.getLoggedUser().hasRole("MENU_ZAMOWIENIA_ZWK")) {
                miOrders.addItem(MENU_ORDERS_ZWK, menuCommand);
                miOrders.addItem(MENU_ADD_ORDER, menuCommand);
            }
            if (userSession.getLoggedUser().hasRole("MENU_ZAMOWIENIA_ZWD")) {
                miOrders.addItem(MENU_ORDERS_ZWD, menuCommand);
            }
        }

        if (userSession.getLoggedUser().hasRole("MENU_DOSTAWY")) {
            MenuBar.MenuItem miPz = menu.addItem("Dostawy", null);
            if ( userSession.getLoggedUser().hasRole("MENU_DOSTAWY_DPZ")){
                 miPz.addItem(MENU_DPZS, menuCommand);
            }
            
            if ( userSession.getLoggedUser().hasRole("MENU_DOSTAWY_PZ")) {
                  miPz.addItem(MENU_PZS, menuCommand);
                   miPz.addItem(MENU_ADD_PZ, menuCommand);
            }
           
        }
        
        if ( userSession.getLoggedUser().hasRole("MENU_FAKTURY")){
            MenuBar.MenuItem miInvoices = menu.addItem("Faktury", menuCommand);
           
        }

        menu.addItem(MENU_STOCK, menuCommand);

        this.addComponent(hboxTop);
        hboxTop.addComponent(menu);

        String userName = VOLookup.lookupVoUserSession().getLoggedUsername();
        String instanceCode = VOLookup.lookupVoUserSession().getLoggedUser().getInstanceCode();
        HorizontalLayout spac = new HorizontalLayout();
        hboxTop.addComponent(spac);
        hboxTop.setExpandRatio(spac, 1);
        Label labUser = new Label("Zalogowany użytkownik:" + userName + " instancja: " + instanceCode);
        hboxTop.addComponent(labUser);
        labUser.setWidth("250px");
        hboxTop.setWidth("100%");

        hboxTop.addComponent(butLogout);
        butLogout.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                getUI().getSession().close();
                getUI().getSession().getSession().invalidate();
                VaadinService.getCurrentRequest().getWrappedSession().invalidate();
                getUI().getPage().setLocation("/VOrders/app");
            }
        });

    }

    private void onMenuItem(MenuBar.MenuItem selectedItem) {
        Window newW = null;
        if (selectedItem.getText().equals(MENU_UZYTKOWNICY)) {
            newW = new WndConfigUsers();
        } else if (selectedItem.getText().equals(MENU_FIRMY)) {
            newW = new WndCompanys();
        } else if (selectedItem.getText().equals(MENU_OBIEKTY)) {
            newW = new WndOrganisationUnits();
        } else if (selectedItem.getText().equals(MENU_TOWARY)) {
            newW = new WndProducts();
        } else if (selectedItem.getText().equals(MENU_DICTIONARIES)) {
            newW = new WndDictionaries();
        } else if (selectedItem.getText().equals(MENU_MEASURE_UNITS)) {
            newW = new WndMeasureUnits();
        } else if (selectedItem.getText().equals(MENU_TOWARY_OBIEKTOW)) {
            newW = new WndUnitsProducts();
        } else if (selectedItem.getText().equals(MENU_TOWARY_OBIEKTOW_DOSTAWCY)) {
            newW = new WndUnitsProductsSuppliers();
        } else if (selectedItem.getText().equals(MENU_PRICE_LISTS)) {
            newW = new WndPriceLists();
        } else if (selectedItem.getText().equals(MENU_ORDERS_ZWK)) {
            newW = new WndOrdersZwk();
        } else if (selectedItem.getText().equals(MENU_ADD_ORDER)) {
            WndOrderZwk wndOrder = new WndOrderZwk();
            wndOrder.newDocument(VOConsts.DOC_TYPE_ZWK);
            newW = wndOrder;
        } else if (selectedItem.getText().equals(MENU_ORDERS_ZWD)) {
            newW = new WndOrdersZwd();
        } else if (selectedItem.getText().equals(MENU_ADD_PZ)) {
            WndPz wndNewPz = new WndPz();
            wndNewPz.newDocument(VOConsts.DOC_TYPE_PZ);
            newW = wndNewPz;

        } 
        else if (selectedItem.getText().equals(MENU_DPZS)) {
            newW = new WndDpzs();
        } 
        else if (selectedItem.getText().equals(MENU_PZS)) {
            newW = new WndPzs();
        } else if (selectedItem.getText().equals(MENU_STOCK)) {
            newW = new WndStock();
        }
        else if ( selectedItem.getText().equals(MENU_INVOICES)){
            newW = new WndInvoices();
        }
        if (newW != null) {
            VendiOrdersUI.showWindow(newW);
        }
    }
}
