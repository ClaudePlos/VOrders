/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.config.companys;

import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.util.List;
import pl.vendi.ui.VOLookup;
import pl.vendi.ui.VendiOrdersUI;
import pl.vendi.ui.common.CntContainerUtils;
import pl.vendi.ui.common.ComboBoxDV;
import pl.vendi.ui.common.VO_UI_Consts;
import pl.vendi.ui.common.VoExceptionHandler;
import pl.vo.common.model.DictionaryValue;
import pl.vo.company.api.CompanysApi;
import pl.vo.company.model.Company;

/**
 *
 * @author Piotr
 */
public class WndCompanys extends Window {

    BeanContainer<Long, Company> cntCompanys = new BeanContainer<Long, Company>(Company.class);
    
    BeanItemContainer<DictionaryValue> cntIntegrationTypes = new BeanItemContainer<DictionaryValue>(DictionaryValue.class);
    BeanItemContainer<DictionaryValue> cntIntegrationTransports = new BeanItemContainer<DictionaryValue>(DictionaryValue.class);
    Table tblCompany = new Table("Firmy");
    
    VerticalLayout vboxEdit = new VerticalLayout();
    
    TextField tfName = new TextField("Nazwa firmy");
    TextField tfAbbr = new TextField("Skrót");
    TextField tfExternalId = new TextField("Obce ID");
    TextField tfNip = new TextField("Nip");
    TextField tfCity = new TextField("Miasto");
    TextField tfPostCode = new TextField("Kod");
    TextField tfAddress = new TextField("Adres");
    ComboBoxDV cmbIntegrationType = new ComboBoxDV("Typ integracji", cntIntegrationTypes);
     ComboBoxDV cmbIntegrationTransports = new ComboBoxDV("Transport integracji", cntIntegrationTransports);
     
     
     
    TextField tfIntegrationUrl = new TextField("Url integracji");
    
    TextField tfToken = new TextField("Token przy wysyłaniu");
    
    HorizontalLayout vboxMain = new HorizontalLayout();
    
    CompanysApi api;
    
    private Company selectedCompany;
    
    public WndCompanys() {
        
        super("Konfiguracja firm");
        
        api = VOLookup.lookupCompanysApi();
        
        this.setContent(vboxMain);
        vboxMain.setSizeFull();;
        vboxMain.setSpacing(true);
        vboxMain.setMargin(true);
        
        vboxMain.addComponent(tblCompany);
        Panel panelEdit= new Panel();
        panelEdit.setContent(vboxEdit);
        vboxMain.addComponent(panelEdit);
        
        vboxMain.setExpandRatio(tblCompany, 0.6f);
        vboxMain.setExpandRatio(panelEdit, 0.3f);
//        panelEdit.setWidth("300px");
        panelEdit.setHeight("100%");
       
        // 
        
        tblCompany.setContainerDataSource(cntCompanys);
        tblCompany.setWidth("100%");
        // create box edit
        cntCompanys.setBeanIdProperty("id");
        tblCompany.setVisibleColumns(new String[]{"id", "externalId", "abbr", "name", "nip", "postCode", "city", "address","integrationType"});
        tblCompany.setColumnHeaders(new String[]{"Id", "Obce id", "Skrót", "Nazwa", "Nip", "Kod pocztowy", "Miasto", "Adres","Typ integracji"});
        
        tblCompany.setSelectable(true);
        vboxEdit.setWidth("100%");
        vboxEdit.addComponent(tfName);
        vboxEdit.addComponent(tfAbbr);
        vboxEdit.addComponent(tfNip);
        vboxEdit.addComponent(tfExternalId);
        vboxEdit.addComponent(tfCity);
        vboxEdit.addComponent(tfPostCode);
        vboxEdit.addComponent(tfAddress);
        
        vboxEdit.addComponent(cmbIntegrationType);
        cmbIntegrationType.setWidth("100%");
        vboxEdit.addComponent( cmbIntegrationTransports );
        cmbIntegrationTransports.setWidth("100%");
        vboxEdit.addComponent(tfIntegrationUrl);
        tfIntegrationUrl.setWidth("100%");
        
        vboxEdit.addComponent( tfToken );
        tfToken.setWidth("100%");
        
        cntIntegrationTypes.addItem(VO_UI_Consts.dvIntegrationTypeLocalUser);
        cntIntegrationTypes.addItem(VO_UI_Consts.dvIntegrationTypeIMag);
        
        cntIntegrationTransports.addItem(VO_UI_Consts.dvIntegrationTransportREST);
         cntIntegrationTransports.addItem(VO_UI_Consts.dvIntegrationTransportEmail);
        
        tfName.setNullRepresentation("");
        tfAbbr.setNullRepresentation("");
        tfCity.setNullRepresentation("");
        tfExternalId.setNullRepresentation("");
        tfPostCode.setNullRepresentation("");
        tfAddress.setNullRepresentation("");
        tfNip.setNullRepresentation("");
        tfIntegrationUrl.setNullRepresentation(null);
        tfToken.setNullRepresentation( null );
        
        tfName.setRequired(true);
        tfAbbr.setRequired(true);
        tfNip.setRequired(true);
        
        Button butAdd = new Button("Dodaj");
        Button butSave = new Button("Zapisz");
        
        vboxEdit.addComponent(butAdd);
        vboxEdit.addComponent(butSave);
        
        refreshCompanys();
        
        butAdd.addClickListener(new Button.ClickListener() {
            
            @Override
            public void buttonClick(Button.ClickEvent event) {
                selectedCompany = new Company();
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
                BeanItem<Company> biUser = (BeanItem<Company>) event.getItem();
                
                selectedCompany = biUser != null ? biUser.getBean() : null;
                modelToView();
            }
        });
        
    }
    
    private void refreshCompanys() {
        List<Company> items = api.findAllCompanys();
        cntCompanys.removeAllItems();
        cntCompanys.addAll(items);
        
    }
    
    private void modelToView() {
        if (selectedCompany != null) {
            tfExternalId.setValue(selectedCompany.getExternalId());
            tfNip.setValue(selectedCompany.getNip());
            tfName.setValue(selectedCompany.getName());
            tfAbbr.setValue(selectedCompany.getAbbr());
            tfCity.setValue(selectedCompany.getCity());
            tfPostCode.setValue(selectedCompany.getPostCode());
            tfAddress.setValue(selectedCompany.getAddress());
            
            tfIntegrationUrl.setValue(selectedCompany.getIntegrationUrl());
            tfToken.setValue( selectedCompany.getIntegrationSecretToken() );
            cmbIntegrationType.setDictionaryValue(selectedCompany.getIntegrationType());
            cmbIntegrationTransports.setDictionaryValue( selectedCompany.getIntegrationTransport() );
            
        } else {
            tfExternalId.setValue(null);
            tfNip.setValue(null);
            tfName.setValue(null);
            tfAbbr.setValue(null);
            tfCity.setValue(null);
            tfPostCode.setValue(null);
            tfAddress.setValue(null);
            tfIntegrationUrl.setValue(null);
            tfToken.setValue( null  );
        }
        
    }
    
    private void viewToModel() {
        if (selectedCompany != null) {
            selectedCompany.setName(tfName.getValue());
            selectedCompany.setAbbr(tfAbbr.getValue());
            selectedCompany.setNip(tfNip.getValue());
            selectedCompany.setAddress(tfAddress.getValue());
            selectedCompany.setCity(tfCity.getValue());
            selectedCompany.setExternalId(tfExternalId.getValue());
            selectedCompany.setPostCode(tfPostCode.getValue());
            selectedCompany.setIntegrationType(cmbIntegrationType.getDictionaryValueCode());
            selectedCompany.setIntegrationUrl(tfIntegrationUrl.getValue());
            selectedCompany.setIntegrationTransport( cmbIntegrationTransports.getDictionaryValueCode() );
            selectedCompany.setIntegrationSecretToken( tfToken.getValue() );
            
        }
    }
    
    private void onClickSave() {
        
        if (selectedCompany == null) {
            selectedCompany = new Company();
        }
        
        if (selectedCompany != null) {
            viewToModel();
            try {
                selectedCompany = api.save(selectedCompany);
            } catch (Exception wre) {
                VoExceptionHandler.handleException(wre);
                return;                
            }
            
            CntContainerUtils.replaceItemWithIdOrAdd(cntCompanys, selectedCompany.getId(), selectedCompany);
            tblCompany.refreshRowCache();
            selectedCompany = null;
            modelToView();
        }
    }
    
}
