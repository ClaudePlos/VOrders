/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.documents.elements;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vaadin.data.Property;
import com.vaadin.ui.*;

import com.vaadin.ui.themes.ValoTheme;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.vaadin.dialogs.ConfirmDialog;
import pl.vendi.ui.VOLookup;
import pl.vendi.ui.VendiOrdersUI;
import pl.vendi.ui.common.ComboBoxCompany;
import pl.vendi.ui.common.ComboBoxOrganisationUnit;
import pl.vendi.ui.common.DateFieldPl;
import pl.vendi.ui.common.VO_UI_Consts;
import pl.vendi.ui.common.VoExceptionHandler;
import pl.vendi.ui.events.DocumentChangedEvent;
import pl.vendi.ui.main.UIMainWindow;
import pl.vo.VOConsts;
import pl.vo.common.model.DictionaryValue;
import pl.vo.documents.DocumentsApi;
import pl.vo.documents.api.DocumentsProcessApi;
import pl.vo.documents.model.Document;
import pl.vo.documents.model.DocumentItem;
import pl.vo.exceptions.VOWrongDataException;
import pl.vo.organisation.model.OrganisationUnit;

/**
 *
 * @author Piotr
 */
public class DocumentWindow extends Window implements Button.ClickListener, Property.ValueChangeListener {

    private VerticalLayout vboxMain = new VerticalLayout();
    private HorizontalLayout hboxSourceDocument = new HorizontalLayout();
    private HorizontalLayout hboxTop = new HorizontalLayout();
    protected HorizontalLayout hboxBottom = new HorizontalLayout();
//    protected DocumentItemsTable tblPositions;
    protected DocumentItemsWithFilter tblPositions; 

    protected Document document;

    private boolean isHeaderEditable = false;

    // header elements
    private Label labId = new Label("Id");
    private TextField tfNumberOwn = new TextField("Numer własny");
    private TextField tfNumberExternal = new TextField("Numer obcy");
    private ComboBoxOrganisationUnit cmbOrganisationUnit = new ComboBoxOrganisationUnit("Jednostka org.");
    private DateFieldPl dfDocDate = new DateFieldPl("Data dokumentu");
    private DateFieldPl dfDeliveryDate = new DateFieldPl("Data dostawy");
    private ComboBoxCompany cmbSupplier = new ComboBoxCompany("Dostawca");
    private TextField tfDocType = new TextField("Typ");
    private DateFieldPl priceListDateFrom = new DateFieldPl("Cennik od");
    private DateFieldPl priceListDateTill = new DateFieldPl("Cennik do");
    private Label labStatus = new Label("Status");

    Button butSave = new Button("Zapisz");
    Button butCancel = new Button("Anuluj");
    Button butPrint = new Button("Drukuj");
    
//    Button butChildDocuments = new Button("Dokumenty powiązane");

    DocumentsApi documentsApi;
    DocumentsProcessApi documentsProcessApi;

    MenuBar menuStatus = new MenuBar();
    Button butCreateCopy = new Button("Utwórz kopię");
    MenuBar.MenuItem miChangeStatus;
    MenuBar.Command menuCommand;
    List<DictionaryValue> actions;

    private String documentType;
    private Component elAddItem = null;
    private Component elAddItemSupplier = null;

    // source document
    Label labSourceDocument = new Label();
    protected Button butSelectSourceDocument = new Button("Wybierz dokument źródłowy");

    private boolean _modified = false;

    public DocumentWindow(String documentType) {

        EventBus eventBus = new EventBus("document");
        eventBus.register(this);

        this.documentType = documentType;

        documentsApi = VOLookup.lookupDocumentsApi();
        documentsProcessApi = VOLookup.lookupDocumentsProcessApi();
        tblPositions = new DocumentItemsWithFilter(documentType, this);

        Panel panelContent= new Panel();
        panelContent.setId("panelContent");
        panelContent.setContent(vboxMain);
        panelContent.setHeight("100%");
        panelContent.setWidth("100%");
        this.setContent(panelContent);
        vboxMain.setId("vboxMain");
        vboxMain.setSizeFull();
        vboxMain.setMargin(true);
        vboxMain.setSpacing(true);
        vboxMain.addComponent(hboxSourceDocument);


        //
        Panel topPanel = new Panel();
        topPanel.setContent(hboxTop);
        topPanel.setWidth("100%");

        vboxMain.addComponent(topPanel);
        vboxMain.addComponent(tblPositions);
        vboxMain.addComponent(hboxBottom);

        hboxSourceDocument.setVisible(false);

        // hboxBottom.setHeight("100px");
        vboxMain.setExpandRatio(tblPositions, 1);
        tblPositions.setSizeFull();

        // create header
        hboxTop.setSpacing(true);
        hboxTop.addComponent(labId);

        if (documentType.equals(VOConsts.DOC_TYPE_ZWK)
                || documentType.equals(VOConsts.DOC_TYPE_ZWD)
                || (documentType.equals(VOConsts.DOC_TYPE_DPZ))) {
            hboxTop.addComponent(tfNumberOwn);
        }

        tfNumberOwn.setNullRepresentation(null);
        tfNumberOwn.setEnabled(false);
        tfNumberExternal.setNullRepresentation(null);

        tfNumberExternal.addValueChangeListener(this);
        tfNumberOwn.addValueChangeListener(this);
        cmbSupplier.addValueChangeListener(this);
        tfNumberExternal.setWidth("150px");
        cmbOrganisationUnit.addValueChangeListener( new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
               document.setModified( true );
               document.setCompanyUnit( (OrganisationUnit) cmbOrganisationUnit.getValue() );
            }
        });
        dfDeliveryDate.addValueChangeListener(this);
        dfDocDate.addValueChangeListener(this);
        priceListDateFrom.addValueChangeListener(this);
        priceListDateTill.addValueChangeListener(this);

        if (documentType.equals(VOConsts.DOC_TYPE_PRICE_LIST)
                || documentType.equals(VOConsts.DOC_TYPE_DPZ)
                || documentType.equals(VOConsts.DOC_TYPE_PZ)
                || documentType.equals(VOConsts.DOC_TYPE_INVOICE)) {
            hboxTop.addComponent(tfNumberExternal);
        }

        if (documentType.equals(VOConsts.DOC_TYPE_ZWK) || documentType.equals(VOConsts.DOC_TYPE_DPZ)
                || documentType.equals(VOConsts.DOC_TYPE_PZ)
                || documentType.equals(VOConsts.DOC_TYPE_ZWD)) {
            hboxTop.addComponent(cmbOrganisationUnit);
            cmbOrganisationUnit.setRequired(true);
            cmbOrganisationUnit.setRequiredError("Wybierz jednostkę organizacyjna");
        }

        hboxTop.addComponent(dfDocDate);
        dfDocDate.setWidth("150px");
        dfDocDate.setRequired(true);
        dfDocDate.setRequiredError("Wprowadź datę dokumentu");

        if (documentType.equals(VOConsts.DOC_TYPE_PRICE_LIST)
                || documentType.equals(VOConsts.DOC_TYPE_ZWD)
            
                || documentType.equals(VOConsts.DOC_TYPE_DPZ) || documentType.equals(VOConsts.DOC_TYPE_PZ)
                || documentType.equals(VOConsts.DOC_TYPE_INVOICE)) {
            hboxTop.addComponent(cmbSupplier);
            cmbSupplier.setRequired(true);
            cmbSupplier.setRequiredError("Wybierz dostawcę");
            cmbSupplier.setWidth("150px");
        }

        if (documentType.equals(VOConsts.DOC_TYPE_ZWK) || documentType.equals(VOConsts.DOC_TYPE_ZWD)
                || documentType.equals(VOConsts.DOC_TYPE_DPZ) || documentType.equals(VOConsts.DOC_TYPE_PZ)) {
            hboxTop.addComponent(dfDeliveryDate);
            dfDeliveryDate.setRequired(true);
            dfDeliveryDate.setRequiredError("Wprowadź datę dostawy");
        }

        hboxTop.addComponent(tfDocType);
        tfDocType.setEnabled(false);
        hboxTop.setEnabled(false);

        if (documentType.equals(VOConsts.DOC_TYPE_PRICE_LIST)) {
            hboxTop.addComponent(priceListDateFrom);
            priceListDateFrom.setRequired(true);
            priceListDateFrom.setRequiredError("Wprowadź początkową datę obowiązywania cennika");

            hboxTop.addComponent(priceListDateTill);
            priceListDateTill.setRequired(true);
            priceListDateTill.setRequiredError("Wprowadź końcową datę obowiązywania cennika");

            priceListDateFrom.setWidth("150px");
            priceListDateTill.setWidth("150px");
        }

        // footer
        hboxBottom.setSpacing(true);
        hboxBottom.addComponent(butCancel);
        hboxBottom.addComponent(butSave);
        hboxBottom.addComponent( butPrint );
        
        butSave.addStyleName(ValoTheme.BUTTON_PRIMARY);
        butCancel.addStyleName(ValoTheme.BUTTON_DANGER);
        butSave.addClickListener(this);
        butCancel.addClickListener(this);
        butPrint.addClickListener( this );
   
        
        
        //
        labStatus.setCaption("Status");
        hboxTop.addComponent(labStatus);
        miChangeStatus = menuStatus.addItem("Wykonaj akcję:", null);
        hboxBottom.addComponent(menuStatus);
        
        hboxBottom.addComponent(butCreateCopy);
        menuCommand = new MenuBar.Command() {

            @Override
            public void menuSelected(MenuBar.MenuItem selectedItem) {
                onMenuItem(selectedItem, false);
            }
        };

        butCreateCopy.addClickListener( new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                onClickCreateCopy();
            }
        });
//        hboxBottom.addComponent(butChildDocuments);
//        butChildDocuments.setVisible(false);
//
//        butChildDocuments.addClickListener(new Button.ClickListener() {
//
//            @Override
//            public void buttonClick(Button.ClickEvent event) {
//                showChildDocuments();
//            }
//        });

        // 
        hboxSourceDocument.addComponent(labSourceDocument);
        // hboxSourceDocument.addComponent( butSelectSourceDocument);
        butSelectSourceDocument.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                onClickSelectSourceDocument();
            }
        });

    }

    @Override
    public void buttonClick(Button.ClickEvent event) {
        if (event.getButton().equals(butSave)) {
            viewToModel(document);
            try {
                
                document = documentsApi.save(document);
                //document = document;
                setDocument(document);
            } catch (Exception e) {
                VoExceptionHandler.handleException(e);
                return;
            }

            Notification.show("Zapisano poprawnie");

        } else if (event.getButton().equals(butCancel)) {
            this.close();
        }
        else if ( event.getButton().equals(butPrint))
        {
            String url = "../resources/print/document/" + this.document.getId();
            getUI().getPage().open(url, "_blank");
        }
      
    }

    public void setDocument(Document doc) {
        document = doc;
        //  document = (Document) SerializationUtils.clone( document );
        // analise document and set controls

        if (document != null) {

            if (document.getId() == null || document.getStatus().equals(VOConsts.DOC_STATUS_OPEN)) {
                // new doc
                isHeaderEditable = true;
            }

            if (document.getId() != null) {
                document.setChildDocuments(documentsApi.listChildDocuments(document.getId()));
                if (document.getChildDocuments().size() > 0) {
//                    butChildDocuments.setVisible(true);
//                    butChildDocuments.setCaption("Dokumenty powiązane:" + document.getChildDocuments().size());
                }
            }

            if (document.getSourceDocument() != null) {
                hboxSourceDocument.setVisible(true);
                labSourceDocument.setValue(document.getSourceDocument().getDescription());
            }

        }
        modelToView();

        // tblPositions.setEditable(true );
        tblPositions.setDocument(document);
        tblPositions.setWidth("100%");
        setEnabledState();
        setModified(!(document.getId() != null));
    }

    private void setEnabledState() {
        hboxTop.setEnabled(isHeaderEditable);
        if (elAddItem != null && document != null) {
            elAddItem.setVisible( document.getStatus().equals(VOConsts.DOC_STATUS_OPEN) );
        }
        
        if (elAddItemSupplier != null && document != null) {
            elAddItemSupplier.setVisible( document.getStatus().equals(VOConsts.DOC_STATUS_RECEIVED_BY_SUPPLIER));
        }

    }

    private void modelToView() {

        if (document != null) {
            if (document.getId() != null) {
                labId.setValue(document.getId().toString());
            } else {
                labId.setValue("");
            }

            tfDocType.setValue(document.getType());
            tfNumberExternal.setValue(document.getExternalNumber());
            tfNumberOwn.setValue(document.getOwnNumber());
            dfDocDate.setValue(document.getDateOperation());
            cmbSupplier.setValueCompany(document.getSupplier());
            labStatus.setValue(VO_UI_Consts.getStatusName(document.getStatus()));
            dfDeliveryDate.setValue(document.getDateDelivery());
            cmbOrganisationUnit.setValueOrganisationUnit(document.getCompanyUnit());
            if (document.getType().equals(VOConsts.DOC_TYPE_PRICE_LIST)) {
                priceListDateFrom.setValue(document.getValidFrom());
                priceListDateTill.setValue(document.getValidTill());
            }

        }
    }

    private void viewToModel(Document target) {
        if (target != null) {
            target.setExternalNumber(tfNumberExternal.getValue());
            target.setOwnNumber(tfNumberOwn.getValue());
            target.setDateOperation(dfDocDate.getValue());

            target.setDateDelivery(dfDeliveryDate.getValue());

            target.setSupplier(cmbSupplier.getValueCompany());
            target.setCompanyUnit(cmbOrganisationUnit.getOrganisationUnit());

            if (target.getType().equals(VOConsts.DOC_TYPE_PRICE_LIST)) {
                target.setValidFrom(priceListDateFrom.getValue());
                target.setValidTill(priceListDateTill.getValue());
            }
            // get positions from container
            Set<DocumentItem> items = new HashSet<DocumentItem>();
            items.addAll(tblPositions.getCnt().getItemIds()); 
            document.setItems(items);
        }
    }

    protected void addItemEditBox(Component cmp) {
        vboxMain.addComponent(cmp, vboxMain.getComponentIndex(hboxBottom));
        elAddItem = cmp;
    }
    
    protected void addItemEditBoxSupplier(Component cmp) {
        vboxMain.addComponent(cmp, vboxMain.getComponentIndex(hboxBottom));
        elAddItemSupplier = cmp;
    }

    protected void setNextStatus(List<DictionaryValue> nextStatuses) {
        miChangeStatus.removeChildren();
        this.actions = nextStatuses;
        for (DictionaryValue dvStat : nextStatuses) {
            MenuBar.MenuItem miSt = miChangeStatus.addItem(dvStat.getDescription(), menuCommand);
        }
    }

    private void onClickCreateCopy()
    {
        try {
        documentsApi.createDocumetCopy(this.document);
        }
        catch( VOWrongDataException e )
        {
            Notification.show("Nie udało się zrobić kopii dokumentu: " + e.getMessage(), Notification.Type.ERROR_MESSAGE);
            return ; 
        }
        Notification.show("Kopia dokumentu została utworzona");
    }
    private void onMenuItem(final MenuBar.MenuItem item, boolean fConfirmed) {
        DictionaryValue dvAction = null;
        // locate status code
        for (DictionaryValue dv : actions) {
            if (dv.getDescription().equals(item.getText())) {
                dvAction = dv;
            }
        }

        if (dvAction == null) {
            return;
        }

        if (!fConfirmed) {
            ConfirmDialog.show(UI.getCurrent(), "Potwierdź", "Czy na pewno chcesz wykonać akcję:" + dvAction.getDescription(), "Tak", "Nie",
                    new ConfirmDialog.Listener() {

                        public void onClose(ConfirmDialog dialog) {
                            if (dialog.isConfirmed()) {
                                // Confirmed to continue
                                onMenuItem(item, true);
                            } else {
                                // User did not confirm
                                return;
                            }
                        }
                    });
            return;
        } else {

            // change status
            try {
                document = documentsProcessApi.runDocumentAction(document.getId(), dvAction.getValue());
                setDocument(document);
            } catch (Exception e) {
                VoExceptionHandler.handleException(e);
                return;
            }
        }
    }

    private void showChildDocuments()
    {
//        WndChildDocuments wch = new WndChildDocuments(document);
//        VendiOrdersUI.showWindow(wch);
    }

    private void onClickSelectSourceDocument() {

    }

    @Override
    public void valueChange(Property.ValueChangeEvent event) {
        setModified(true);
       
    }

    public boolean isModified() {
        return _modified;
    }

    public void setModified(boolean _modified) {
        this._modified = _modified;
        butSave.setEnabled(_modified);
        miChangeStatus.setEnabled(!_modified);
    }
    
    public void refreshTable() { 
        this.tblPositions.refreshRows();
    }

}
