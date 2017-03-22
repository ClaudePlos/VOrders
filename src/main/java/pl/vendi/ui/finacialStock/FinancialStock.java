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
import com.vaadin.event.ItemClickEvent;
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
import pl.vendi.ui.finacialStock.model.DocDTO;
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
    
    ComboBox cmbCompany2 = new ComboBox("Kontrahent");
    ComboBox cmbRok = new ComboBox("Rok");
    Button butGetData = new Button("Pobierz dane");
    
    FinancialStockRestClient restClient = new FinancialStockRestClient();
    
    String instanceCode = VOLookup.lookupVoUserSession().getLoggedUser().getInstanceCode();
    User loggedUser = VOLookup.lookupVoUserSession().getLoggedUser();
    

    BeanItemContainer<Company> listCompanies = new BeanItemContainer<Company>(Company.class); 
    BeanItemContainer<Company> listCompaniesView = new BeanItemContainer<Company>(Company.class); 

public FinancialStock() {
        
        super("Stany finansowe");
        this.setContent(vboxMain);
        vboxMain.setSpacing( true );
        vboxMain.setMargin( true );
        
        listCompanies.addAll( VOLookup.lookupCompanysApi().findAll( ) );
 
        hboxAdd.setSpacing(true); // Compact layout
        
        vboxMain.setSizeFull();

        //hboxAdd.setWidth("100%");
        
        cmbCompany2.setContainerDataSource( listCompaniesView );
        cmbCompany2.setItemCaptionPropertyId("name");
      //  cmbCompany2.setItemCaption(Company, Company.getName );
        hboxAdd.addComponent(cmbCompany2);
        
        if ( instanceCode.equals("VENDI") )
        {
            companyZalogowane = loggedUser.getCompany();
            
            for ( Company c : listCompanies.getItemIds() )
            {
                if ( !c.getNip().equals("5222899038") )
                {
                    listCompaniesView.addItem( c );
                }
            }
        }
        else if ( instanceCode.equals("MEGAFRUIT") )
        {
            companyZalogowane = loggedUser.getCompany();
            
            for ( Company c : listCompanies.getItemIds() )
            {
                if ( c.getNip().equals("5222899038") )
                {
                    listCompaniesView.addItem( c );
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
        
        Table grid = new Table(); ///rozrachunki
        grid.setStyleName("iso3166");
        grid.setPageLength(7);
        grid.setSizeFull();
        grid.setSelectable(true);
        grid.setMultiSelect(false);
        grid.setImmediate(true);
        grid.setColumnReorderingAllowed(true);
        grid.setColumnCollapsingAllowed(true);

        grid.setWidth("98%");
        grid.setHeight("100%");
        vboxMain.addComponent(grid);
        vboxMain.setExpandRatio(grid, 1);
        
        
        Table gridDok = new Table(); // dokumenty
        gridDok.setStyleName("iso3166");
        gridDok.setPageLength(7);
        gridDok.setSizeFull();
        gridDok.setSelectable(true);
        gridDok.setMultiSelect(false);
        gridDok.setImmediate(true);
        gridDok.setColumnReorderingAllowed(true);
        gridDok.setColumnCollapsingAllowed(true);

        gridDok.setWidth("98%");
        gridDok.setHeight("100%");
        vboxMain.addComponent(gridDok);
        vboxMain.setExpandRatio(gridDok, 1);
        
        
        butGetData.addClickListener( new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                
            gridDok.removeAllItems();
            grid.removeAllItems();
            
            
            Company selectC =  (Company) cmbCompany2.getValue();
                
            String json = null;
                if ( instanceCode.equals("VENDI") )
                {
                    json = restClient.getFinancialStock( selectC.getNip() , cmbRok.getValue().toString() );
                }
                else
                {
                   json = restClient.getFinancialStock( companyZalogowane.getNip() , cmbRok.getValue().toString() );
                }
            
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
                        dI.setOkres(row.get("okres").getAsString() );
                        dI.setRozId(row.get("rozId").getAsBigDecimal() );

                       listDocItem.add(dI);
                    }



                    BeanItemContainer<DocItemDTO> dataSource = new BeanItemContainer<DocItemDTO>(DocItemDTO.class);
                    dataSource.addAll(listDocItem);
                     grid.setContainerDataSource( dataSource );
                     grid.setColumnReorderingAllowed(true);
                     //grid.setColumnHeaders(  new String[] {"rozNumer", "rozTyp", "wn", "ma", "saldo", "rozliczony", "rozliczonyNaDzis"} );
                     grid.setVisibleColumns( new Object[] {"rozNumer", "rozTyp", "wn", "ma", "saldo", "rozliczony", "rozliczonyNaDzis","okres"} );
                     grid.addStyleName(ChameleonTheme.TABLE_STRIPED);

                } catch (JsonSyntaxException e) {
                    throw new IllegalArgumentException(
                            "Cannot parse the given JSON: \"" + json + "\"");
                }  
                
            }
        });
        

        
        grid.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent itemClickEvent) {
                
                gridDok.removeAllItems();
                
                System.out.println(itemClickEvent.getItemId().toString());
                
                DocItemDTO item = (DocItemDTO) itemClickEvent.getItemId();
                
                String jsonDoc = null;
                jsonDoc = restClient.getDocFinancialStock( item.getRozId() );
                
                JsonParser parser = new JsonParser();
                
                JsonElement parsedJsonData = parser.parse(jsonDoc);
                JsonArray jA = parsedJsonData.getAsJsonArray();

                    List<DocDTO> listDoc = new ArrayList<DocDTO>();

                    for (int i = 0; i < jA.size(); i++) {

                        JsonObject row = jA.get(i).getAsJsonObject();

                        DocDTO dI =  new DocDTO();
                        dI.setNumerWlasny(row.get("numerWlasny").getAsString() );
                        if ( row.get("numerObcy") != null )
                            dI.setNumerObcy(row.get("numerObcy").getAsString() ); 
                        
                        dI.setDataWystawienia(row.get("dataWystawienia").getAsString() );
                        dI.setDataZaksiegowania(row.get("dataZaksiegowania").getAsString() );
                        if ( row.get("dataWymagalnosci") != null )
                            dI.setDataWymagalnosci(row.get("dataWymagalnosci").getAsString() );
                        
                        dI.setZaplata(row.get("zaplata").getAsString() );
                        dI.setRozliczona(row.get("rozliczona").getAsString() );
                        
                        dI.setDokOpis(row.get("dokOpis").getAsString() );
                        dI.setPlOpis(row.get("plOpis").getAsString() );

                        dI.setWn(row.get("wn").getAsBigDecimal() );
                        dI.setMa(row.get("ma").getAsBigDecimal() );
      

                       listDoc.add(dI);
                    }
                
                BeanItemContainer<DocDTO> dataSourceDoc = new BeanItemContainer<DocDTO>(DocDTO.class);
                    dataSourceDoc.addAll(listDoc);
                     gridDok.setContainerDataSource( dataSourceDoc );
                     gridDok.setColumnReorderingAllowed(true);                    
            
                gridDok.setVisibleColumns( new Object[] {"numerWlasny", "numerObcy", "dataWystawienia", "dataZaksiegowania", "dataWymagalnosci"
                        , "zaplata", "rozliczona","dokOpis","plOpis","wn","ma"} );
                gridDok.addStyleName(ChameleonTheme.TABLE_STRIPED);
            }
        });

        
        

}


@Override
public void buttonClick(Button.ClickEvent event) {

}
    
}
