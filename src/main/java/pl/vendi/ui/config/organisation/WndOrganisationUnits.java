/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.config.organisation;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.vendi.ui.VOLookup;
import pl.vendi.ui.common.CntContainerUtils;
import pl.vendi.ui.common.CntRoadDistance;
import pl.vendi.ui.common.VoExceptionHandler;
import pl.vo.company.api.CompanysApi;
import pl.vo.company.model.Company;
import pl.vo.exceptions.VOWrongDataException;
import pl.vo.organisation.OrganisationApi;
import pl.vo.organisation.model.OrganisationUnit;
import pl.vo.road_distance.api.RoadDistanceApi;
import pl.vo.road_distance.model.RoadDistance;

/**
 *
 * @author Piotr
 */
public class WndOrganisationUnits extends Window
{
    BeanContainer<Long,OrganisationUnit> cntCompanys = new BeanContainer<Long,OrganisationUnit>(OrganisationUnit.class);
    
    BeanContainer<Long, RoadDistance> cntDistances = new BeanContainer<Long, RoadDistance>(RoadDistance.class);

    Table tblCompany = new Table("Firmy");
    
    Table tblDistance = new Table("Odległość do dostawców");

    VerticalLayout vboxEdit = new VerticalLayout();
    
    VerticalLayout vboxDistance = new VerticalLayout();

    TextField tfName = new TextField("Nazwa Obiektu");
    TextField tfCode = new TextField("Kod");
    TextField tfAddress = new TextField("Adres");
    
    TextField tfDistance = new TextField("Odległość w km");

    HorizontalLayout vboxMain = new HorizontalLayout();
    
    List<RoadDistance> listRoadDistance;

    OrganisationApi api;
    RoadDistanceApi apiRoad;
    CompanysApi apiCompanys;

    private OrganisationUnit selectedOrganisationUnit;
    
    private RoadDistance selectedRoadDistance;

    public WndOrganisationUnits()
    {

        super("Konfiguracja jednostek organizacyjnych");

        api = VOLookup.lookupOrganisationApi();       
        apiRoad = VOLookup.lookupRoadDistanceApi();
        apiCompanys = VOLookup.lookupCompanysApi();

        this.setContent(vboxMain);
        vboxMain.setSizeFull();;
        vboxMain.setSpacing(true);
        vboxMain.setMargin(true);

        
        vboxMain.addComponent(tblCompany);
        vboxMain.addComponent(vboxEdit);
        vboxMain.addComponent(vboxDistance);

        vboxMain.setExpandRatio(tblCompany, 0.6f);
        vboxMain.setExpandRatio(vboxEdit, 0.3f);
        vboxEdit.setWidth("200px");
        vboxDistance.setWidth("300px");
        // 
        
        tblCompany.setContainerDataSource(cntCompanys);
        tblCompany.setWidth("100%");
        // create box edit
        cntCompanys.setBeanIdProperty("id");
        tblCompany.setVisibleColumns(new String[]{"id","name","code", "address"});
        
        tblCompany.setSelectable( true );
        
        
        vboxEdit.addComponent(tfName);
        vboxEdit.addComponent(tfCode);
        vboxEdit.addComponent( tfAddress );
    
        
        tfName.setNullRepresentation( "");
        tfCode.setNullRepresentation( "");
        tfAddress.setNullRepresentation( "");
     
        
        tfName.setRequired(true);
        tfCode.setRequired(true);
        
       
        Button butAdd = new Button("Dodaj");
        Button butSave = new Button("Zapisz");

        vboxEdit.addComponent(butAdd);
        vboxEdit.addComponent(butSave);
        
        Button butAddSuppliers = new Button("Dodaj brakujących dostawców");
        Button butSaveDistance = new Button("Zapisz odległość");
        butAddSuppliers.setVisible(false);
        
        tblDistance.setContainerDataSource(cntDistances);
        cntDistances.setBeanIdProperty("id");
        tblDistance.setVisibleColumns(new String[]{"id", "companyUnitsId", "companyId", "distance"});
        tblDistance.setColumnHeaders(new String[]{"ID", "companyUnitsId", "companyId", "distance"});
        tblDistance.setWidth("100%");
        tblDistance.setHeight("180px");
        vboxDistance.addComponent( tblDistance );
        
        tblDistance.setSelectable( true );
        vboxDistance.addComponent( tfDistance );
        vboxDistance.addComponent( butSaveDistance );
        vboxDistance.addComponent( butAddSuppliers );
        
    

        

        refreshCompanys();
        
        

        butAdd.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                selectedOrganisationUnit = new OrganisationUnit();
                modelToView();
            }
        });

        butSave.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                onClickSave();
            }
        });
        
        butSaveDistance.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                onClicSaveDistance();
            }
        });
        
        
        tblCompany.addItemClickListener(new ItemClickEvent.ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                BeanItem<OrganisationUnit> biUser = (BeanItem<OrganisationUnit>) event.getItem();
                selectedOrganisationUnit = biUser != null ? biUser.getBean()  : null;
                modelToView();
                
                refreshDistances( selectedOrganisationUnit.getId() );
                butAddSuppliers.setVisible(true);
            }
        });
        
        
        tblDistance.addItemClickListener(new ItemClickEvent.ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                BeanItem<RoadDistance> biUser = (BeanItem<RoadDistance>) event.getItem();
                selectedRoadDistance = biUser != null ? biUser.getBean()  : null;
                modelToViewDistance();
            }
        });
        
        
        butAddSuppliers.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                onClicAddSuppliers();
            }
        });

    }

    private void refreshCompanys() {
        List<OrganisationUnit> items = api.findAll();
        cntCompanys.removeAllItems();
        cntCompanys.addAll(items);

    }
    
    

    private void modelToView() {
        if (selectedOrganisationUnit != null) 
        {
           tfName.setValue(selectedOrganisationUnit.getName());
           tfCode.setValue(selectedOrganisationUnit.getCode());
           tfAddress.setValue (selectedOrganisationUnit.getAddress());
           tfDistance.setValue("");
        }
        else { 
           tfName.setValue(null);
           tfCode.setValue( null);
           tfAddress.setValue (null);
           tfDistance.setValue("");
        }
       
    }

    private void viewToModel()
    {
        if (selectedOrganisationUnit != null)
        {
            selectedOrganisationUnit.setName( tfName.getValue() );
            selectedOrganisationUnit.setCode(tfCode.getValue() );
         
            selectedOrganisationUnit.setAddress( tfAddress.getValue() );
            
          

        }
    }

    private void onClickSave() {

        if (selectedOrganisationUnit == null )
            selectedOrganisationUnit = new OrganisationUnit();
        
        if (selectedOrganisationUnit != null)
        {
            viewToModel();
            try {
                selectedOrganisationUnit = api.save(selectedOrganisationUnit);
            }
            catch ( Exception wre )
            {
                VoExceptionHandler.handleException( wre );
                return; 
            }
            
            
            CntContainerUtils.replaceItemWithIdOrAdd(cntCompanys,selectedOrganisationUnit.getId(), selectedOrganisationUnit);
            tblCompany.refreshRowCache();
            selectedOrganisationUnit = null;
            modelToView();
        }
    }
    
    
    private void modelToViewDistance() {
        
        if (selectedRoadDistance != null) 
        {
            tfDistance.setValue( selectedRoadDistance.getDistance().toString() );           
        }
        else {
            tfDistance.setValue( "" );  
        }     
    }
    
    private void viewToModelDistance()
    {
        if (selectedRoadDistance != null)
        {
            selectedRoadDistance.setDistance( Long.parseLong(tfDistance.getValue()) );
        }
    }
    
    private void refreshDistances( Long companyUnitId ) { 
        listRoadDistance = apiRoad.listByCompanyUnitId( companyUnitId );
        cntDistances.removeAllItems();
        cntDistances.addAll(listRoadDistance);  
    }
    
    private void onClicSaveDistance() {
        
        if (selectedRoadDistance != null)
        {
            viewToModelDistance();
            try {
                selectedRoadDistance = apiRoad.save(selectedRoadDistance);
            }
            catch ( Exception wre )
            {
                VoExceptionHandler.handleException( wre );
                return; 
            }
            
            
            CntRoadDistance.replaceItemWithIdOrAdd(cntDistances, selectedRoadDistance.getId(), selectedRoadDistance);
            tblDistance.refreshRowCache();
            selectedRoadDistance = null;
            modelToViewDistance();
        }
        
    }
    
    private void onClicAddSuppliers() {
        
        List<RoadDistance> distSuppliers = new ArrayList<RoadDistance>();
        List<Company> companys = apiCompanys.findAllCompanys();
        
        for ( Company c : companys )
        {
            boolean spr = listRoadDistance.stream().anyMatch( rd -> rd.getCompanyId().equals( c.getId() ));
            if( spr == false )
            {
                RoadDistance rdNew = new RoadDistance();
                rdNew.setCompanyUnitsId( selectedOrganisationUnit.getId() );
                rdNew.setCompanyId( c.getId() );
                rdNew.setDistance( Long.valueOf("0") );
                
                try {
                      rdNew = apiRoad.save(rdNew);
                    } catch (VOWrongDataException ex) {
                        Logger.getLogger(WndOrganisationUnits.class.getName()).log(Level.SEVERE, null, ex);
                    }
                
                CntRoadDistance.replaceItemWithIdOrAdd(cntDistances, rdNew.getId(), rdNew);
                listRoadDistance.add( rdNew );
                tblDistance.refreshRowCache();        
            }
        }

 
        
    }
    
   
}
