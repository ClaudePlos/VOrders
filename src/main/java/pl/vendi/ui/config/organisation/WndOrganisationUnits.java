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
import java.util.List;
import pl.vendi.ui.VOLookup;
import pl.vendi.ui.common.CntContainerUtils;
import pl.vendi.ui.common.VoExceptionHandler;
import pl.vo.company.api.CompanysApi;
import pl.vo.company.model.Company;
import pl.vo.organisation.OrganisationApi;
import pl.vo.organisation.model.OrganisationUnit;

/**
 *
 * @author Piotr
 */
public class WndOrganisationUnits extends Window
{
    BeanContainer<Long,OrganisationUnit> cntCompanys = new BeanContainer<Long,OrganisationUnit>(OrganisationUnit.class);

    Table tblCompany = new Table("Firmy");

    VerticalLayout vboxEdit = new VerticalLayout();

    TextField tfName = new TextField("Nazwa Obiektu");
    TextField tfCode = new TextField("Kod");
    TextField tfAddress = new TextField("Adres");

    HorizontalLayout vboxMain = new HorizontalLayout();

    OrganisationApi api;

    private OrganisationUnit selectedOrganisationUnit;

    public WndOrganisationUnits()
    {

        super("Konfiguracja jednostek organizacyjnych");

        api = VOLookup.lookupOrganisationApi();

        this.setContent(vboxMain);
        vboxMain.setSizeFull();;
        vboxMain.setSpacing(true);
        vboxMain.setMargin(true);

        vboxMain.addComponent(tblCompany);
        vboxMain.addComponent(vboxEdit);

        vboxMain.setExpandRatio(tblCompany, 0.6f);
        vboxMain.setExpandRatio(vboxEdit, 0.3f);
        vboxEdit.setWidth("200px");
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
        
        
        tblCompany.addItemClickListener(new ItemClickEvent.ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                BeanItem<OrganisationUnit> biUser = (BeanItem<OrganisationUnit>) event.getItem();
                selectedOrganisationUnit = biUser != null ? biUser.getBean()  : null;
                modelToView();
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
           
        }
        else {
            
            tfName.setValue(null);
         
           tfCode.setValue( null);
           tfAddress.setValue (null);
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
    
   
}
