/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.finacialStock;


import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import pl.vendi.ui.common.ComboBoxProducts;
import pl.vo.rest.FinancialStockRestClient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.JsonArray;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Table;
import com.vaadin.ui.themes.ChameleonTheme;
import java.util.ArrayList;
import java.util.List;
import pl.vendi.ui.VOLookup;
import pl.vendi.ui.common.ComboBoxCompany;
import pl.vendi.ui.finacialStock.model.DocItemDTO;
import pl.vo.company.model.Company;
import pl.vo.security.model.User;

/**
 *
 * @author k.skowronski
 */
public class FinancialStock extends Window implements Button.ClickListener {

    VerticalLayout vboxMain = new VerticalLayout();
    HorizontalLayout hboxAdd = new HorizontalLayout();
    
    Company companyZalogowane;
    
    ComboBoxCompany cmbCompany = new ComboBoxCompany("Kontrahent");
    ComboBox cmbCompany2 = new ComboBox("Kontrahent");
    ComboBox cmbRok = new ComboBox("Rok");
    Button butGetData = new Button("Pobierz dane");
    
    FinancialStockRestClient restClient = new FinancialStockRestClient();
    
    String instanceCode = VOLookup.lookupVoUserSession().getLoggedUser().getInstanceCode();
    User loggedUser = VOLookup.lookupVoUserSession().getLoggedUser();
    

    BeanItemContainer<Company> listCompanies = new BeanItemContainer<Company>(Company.class); 

public FinancialStock() {
        
        super("Stany finansowe");
        this.setContent(vboxMain);
        vboxMain.setSpacing( true );
        vboxMain.setMargin( true );
        
        
 
        hboxAdd.setSpacing(true); // Compact layout
        
        vboxMain.setSizeFull();

        //hboxAdd.setWidth("100%");
        
        cmbCompany2.setContainerDataSource( listCompanies );
        cmbCompany2.setItemCaptionPropertyId("name");
      //  cmbCompany2.setItemCaption(Company, Company.getName );
        hboxAdd.addComponent(cmbCompany2);
        
        if ( instanceCode.equals("VENDI") )
        {
            companyZalogowane = loggedUser.getCompany();
            
            for ( Company c : cmbCompany.cntUnits.getItemIds() )
            {
                if ( !c.getNip().equals("5222899038") )
                {
                    listCompanies.addItem( c );
                }
            }
        }
        else if ( instanceCode.equals("MEGAFRUIT") )
        {
            companyZalogowane = loggedUser.getCompany();
            
            for ( Company c : cmbCompany.cntUnits.getItemIds() )
            {
                if ( c.getNip().equals("5222899038") )
                {
                    listCompanies.addItem( c );
                }
            }
        }
        
        
        cmbRok.addItem("2016");
        cmbRok.addItem("2017");
        cmbRok.addItem("2018");
        cmbRok.setValue("2017");
        hboxAdd.addComponent(cmbRok);
        
        
        butGetData.setIcon( FontAwesome.REFRESH );
        hboxAdd.addComponent(butGetData);
        
        hboxAdd.setComponentAlignment(butGetData, Alignment.MIDDLE_CENTER);
        
        hboxAdd.setDefaultComponentAlignment(Alignment.BOTTOM_RIGHT);
        vboxMain.addComponent(hboxAdd);
        
         Table grid = new Table();
        grid.setStyleName("iso3166");
        grid.setPageLength(7);
        grid.setSizeFull();
        grid.setSelectable(true);
        grid.setMultiSelect(false);
        grid.setImmediate(true);
        grid.setColumnReorderingAllowed(true);
        grid.setColumnCollapsingAllowed(true);

        grid.setWidth("100%");
        grid.setHeight("100%");
        vboxMain.addComponent(grid);
        
        vboxMain.setExpandRatio(grid, 1);
        
        butGetData.addClickListener( new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                
            grid.removeAllItems();
            
            
            Company selectC =  (Company) cmbCompany2.getValue();
                
            String json = restClient.getFinancialStock( selectC.getNip() , cmbRok.getValue().toString() );
            JsonParser parser = new JsonParser();

            try {
                    JsonElement parsedJsonData = parser.parse(json);
                    JsonArray jA = parsedJsonData.getAsJsonArray();

                    List<DocItemDTO> listDocItem = new ArrayList<DocItemDTO>();

                    for (int i = 0; i < jA.size(); i++) {

                        JsonObject row = jA.get(i).getAsJsonObject();

                        DocItemDTO dI =  new DocItemDTO();
                        dI.setRozNumer( row.get("rozNumer").getAsString() );
                        dI.setRozTyp( row.get("rozTyp").getAsString() );  
                        dI.setWn( row.get("wn").getAsBigDecimal() );
                        dI.setMa( row.get("ma").getAsBigDecimal() );
                        dI.setSaldo(row.get("saldo").getAsBigDecimal() );
                        dI.setRozliczony(row.get("rozliczony").getAsString() );
                        dI.setRozliczonyNaDzis(row.get("rozliczonyNaDzis").getAsString() );

                       listDocItem.add(dI);
                    }



                    BeanItemContainer<DocItemDTO> dataSource = new BeanItemContainer<DocItemDTO>(DocItemDTO.class);
                    dataSource.addAll(listDocItem);
                     grid.setContainerDataSource( dataSource );
                     grid.setColumnReorderingAllowed(true);
                     //grid.setColumnHeaders(  new String[] {"rozNumer", "rozTyp", "wn", "ma", "saldo", "rozliczony", "rozliczonyNaDzis"} );
                     grid.setVisibleColumns( new Object[] {"rozNumer", "rozTyp", "wn", "ma", "saldo", "rozliczony", "rozliczonyNaDzis"} );
                     grid.setWidth("98%");
                     grid.addStyleName(ChameleonTheme.TABLE_STRIPED);

                } catch (JsonSyntaxException e) {
                    throw new IllegalArgumentException(
                            "Cannot parse the given JSON: \"" + json + "\"");
                }  
                
            }
        });
        

       

        
        

}


@Override
public void buttonClick(Button.ClickEvent event) {

}
    
}
