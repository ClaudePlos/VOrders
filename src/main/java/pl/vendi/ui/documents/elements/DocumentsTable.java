/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.documents.elements;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.vaadin.dialogs.ConfirmDialog;
import pl.vendi.ui.VOLookup;
import pl.vendi.ui.VendiOrdersUI;
import pl.vendi.ui.common.ComboBoxCompany;
import pl.vendi.ui.common.ComboBoxOrganisationUnit;
import pl.vendi.ui.common.DateFieldPl;
import pl.vendi.ui.common.VODateToStringConverter;
import pl.vendi.ui.common.VO_UI_Consts;
import pl.vendi.ui.common.VoExceptionHandler;
import pl.vendi.ui.delivery.dpz.WndDeliveryDpz;
import pl.vendi.ui.delivery.pz.WndPz;
import pl.vendi.ui.orders.zwd.WndOrderZwd;
import pl.vendi.ui.orders.zwk.WndOrderZwk;
import pl.vo.VOConsts;
import pl.vo.documents.DocumentsApi;
import pl.vo.documents.model.Document;
import pl.vo.exceptions.VOWrongDataException;

/**
 *
 * @author Piotr
 */
public class DocumentsTable extends VerticalLayout implements Property.ValueChangeListener {

    private BeanContainer<Long, Document> cnt = new BeanContainer<Long, Document>(Document.class);
    private String[] documentTypes;

    private DocumentsApi documentsApi;

    private String documentType;

    List<String> columns = new ArrayList<String>();
    List<String> columnCaptions = new ArrayList<String>();

    private boolean ShowDocumentOdDblClk = true;

    HorizontalLayout hboxTop = new HorizontalLayout();
    private Table table = new Table();

    // filters
    ComboBoxOrganisationUnit cmbOrganisationUnit = new ComboBoxOrganisationUnit("Jednostka org");
    ComboBoxCompany cmbSupplier = new ComboBoxCompany("Dostawca");
    DateFieldPl dfDocumentMonth = new DateFieldPl("Miesiąc dokumentu");
    Button butRefresh = new Button("Odśwież");

    public DocumentsTable(String caption, String documentType) {

        super();

        this.setSpacing( true);
        this.addComponent(hboxTop);
        this.addComponent(table);
        this.setSizeFull();
        this.setExpandRatio(table, 1);
        table.setSizeFull();
        table.setSelectable(true);

        
        hboxTop.setDefaultComponentAlignment(Alignment.BOTTOM_RIGHT);
        
        
        
        if ( documentType.equals( VOConsts.DOC_TYPE_ZWK) || documentType.equals(VOConsts.DOC_TYPE_ZWD)
                || documentType.equals(VOConsts.DOC_TYPE_PZ) || documentType.equals(VOConsts.DOC_TYPE_DPZ))
        {
             hboxTop.addComponent(cmbOrganisationUnit);
        }
        hboxTop.addComponent(cmbSupplier);
        hboxTop.addComponent(dfDocumentMonth);
        hboxTop.setSpacing( true );
        
        // add spacer
        VerticalLayout vboxSpacer = new VerticalLayout();
        hboxTop.addComponent( vboxSpacer );
        hboxTop.setExpandRatio( vboxSpacer,1);
    
        butRefresh.setIcon( FontAwesome.REFRESH );
        hboxTop.addComponent( butRefresh );
        hboxTop.setWidth("100%");
        cmbOrganisationUnit.addValueChangeListener(this);
        cmbSupplier.addValueChangeListener(this);
        dfDocumentMonth.addValueChangeListener(this);

        this.documentType = documentType;

        cnt.setBeanIdProperty("id");

        cnt.addNestedContainerProperty("supplier.abbr");
        cnt.addNestedContainerProperty("companyUnit.name");

        
        butRefresh.addClickListener( new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                refresh();
            }
        });
        init();

    }

    private void init() {
        documentsApi = VOLookup.lookupDocumentsApi();
        table.setContainerDataSource(cnt);

        table.setConverter("dateOperation", new VODateToStringConverter());
        table.setConverter("dateDelivery", new VODateToStringConverter());
        table.setConverter("validFrom", new VODateToStringConverter());
        table.setConverter("validTill", new VODateToStringConverter());

         table.addGeneratedColumn("type", new Table.ColumnGenerator() {

            @Override
            public Object generateCell(Table source, final Object itemId, Object columnId) {

                Label lab = new Label();
                BeanItem<Document> bi = (BeanItem<Document>) table.getItem(itemId);
                lab.setValue( VOConsts.getDocTypeName( bi.getBean().getType()));
               return lab;

            }
        });
         
           table.addGeneratedColumn("status", new Table.ColumnGenerator() {

            @Override
            public Object generateCell(Table source, final Object itemId, Object columnId) {

                Label lab = new Label();
                BeanItem<Document> bi = (BeanItem<Document>) table.getItem(itemId);
                lab.setValue( VO_UI_Consts.getStatusName( bi.getBean().getStatus()));
               return lab;

            }
        });
         
         
        table.addGeneratedColumn("delete", new Table.ColumnGenerator() {

            @Override
            public Object generateCell(Table source, final Object itemId, Object columnId) {

                Button but = new Button();
                but.setCaptionAsHtml(true);
                but.setCaption(FontAwesome.TRASH_O.getHtml());
                but.addStyleName(ValoTheme.BUTTON_LINK);

                but.addClickListener(new Button.ClickListener() {

                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        if (itemId != null) {

                            removeDocument(((BeanItem<Document>) table.getItem(itemId)).getBean(), false);
                        }
                    }
                });

                return but;

            }
        });

        table.addItemClickListener(new ItemClickEvent.ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                BeanItem<Document> biDoc = (BeanItem<Document>) event.getItem();
                table.setValue(biDoc.getBean());
                if (biDoc != null && event.isDoubleClick() && ShowDocumentOdDblClk) {
                    openDocument(biDoc);
                }
            }
        });
        
        
    

        initColumns();
    }

    private void openDocument(BeanItem<Document> biDoc) {
        Document doc = VOLookup.lookupDocumentsApi().get(biDoc.getBean().getId());

        if (biDoc.getBean().getType().equals(VOConsts.DOC_TYPE_ZWK)) {
            WndOrderZwk wnd = new WndOrderZwk();
            VendiOrdersUI.showWindow(wnd);
            wnd.setDocument(doc);
        } else if (biDoc.getBean().getType().equals(VOConsts.DOC_TYPE_ZWD)) {
            WndOrderZwd wnd = new WndOrderZwd();
            VendiOrdersUI.showWindow(wnd);
            wnd.setDocument(doc);
        } else if (biDoc.getBean().getType().equals(VOConsts.DOC_TYPE_DPZ)) {
            WndDeliveryDpz wnd = new WndDeliveryDpz();
            VendiOrdersUI.showWindow(wnd);
            wnd.setDocument(doc);
        } else if (biDoc.getBean().getType().equals(VOConsts.DOC_TYPE_PZ)) {
            WndPz wnd = new WndPz();
            VendiOrdersUI.showWindow(wnd);
            wnd.setDocument(doc);
        }
    }

    public void refresh()
    {
        // read filters
        Long orgUnitId = cmbOrganisationUnit.getOrganisationUnitId();
        Long supplId = cmbSupplier.getCompanyId();
        Date month = dfDocumentMonth.getValue() ;
        
        List<Document> docs = documentsApi.findDocuments(documentTypes, orgUnitId, supplId, month,VendiOrdersUI.getLoggedUsername());
        cnt.removeAllItems();
        cnt.addAll(docs);
    }

    public void setDocumentTypes(String[] documentTypes) {
        this.documentTypes = documentTypes;
    }

    private void initColumns() {
        columns.clear();
        columnCaptions.clear();

        //addColumn("id", "Id");
        addColumn("type", "Typ");
        if (documentType == null || documentType.equals(VOConsts.DOC_TYPE_ZWK)
                || documentType.equals(VOConsts.DOC_TYPE_PZ)
                || documentType.equals(VOConsts.DOC_TYPE_ZWD)
                || documentType.equals(VOConsts.DOC_TYPE_DPZ))
        {
            addColumn("companyUnit.name", "Jednostka organizacyjna");
        }
        
     
        if (documentType == null || documentType.equals(VOConsts.DOC_TYPE_PRICE_LIST)
                || documentType.equals(VOConsts.DOC_TYPE_ZWD) || documentType.equals(VOConsts.DOC_TYPE_PZ)
                || documentType.equals(VOConsts.DOC_TYPE_INVOICE)) {
            addColumn("supplier.abbr", "Dostawca");
        }

        if (documentType == null || documentType.equals(VOConsts.DOC_TYPE_PRICE_LIST)) {
            addColumn("dateOperation", "Data oferty");
            addColumn("externalNumber", "Numer obcy");
        } else if (documentType == null || documentType.equals(VOConsts.DOC_TYPE_ZWD) || documentType.equals(VOConsts.DOC_TYPE_ZWK)
                || documentType.equals(VOConsts.DOC_TYPE_PZ) 
        || documentType.equals(VOConsts.DOC_TYPE_DPZ))
                {
            addColumn("dateOperation", "Data dokumentu");
            addColumn("dateDelivery", "Data dostawy");
            addColumn("ownNumber", "Numer własny");
        }
        
        if ( documentType.equals(VOConsts.DOC_TYPE_INVOICE))
        {
             addColumn("externalNumber", "Numer obcy");
             addColumn("ownNumber", "Numer własny");
        }
        
        if ( documentType.equals(VOConsts.DOC_TYPE_PRICE_LIST)){
            addColumn("validFrom", "Ważny od");
               addColumn("validTill", "Ważny do");
        }

        addColumn("status", "Status");
        addColumn("delete", "Usuń");

        table.setVisibleColumns(columns.toArray());
        table.setColumnHeaders(columnCaptions.toArray(new String[0]));
    }

    private void addColumn(String columnName, String caption) {
        columns.add(columnName);
        columnCaptions.add(caption);
    }
    
    public void addToBar( Component cmp ){
        hboxTop.addComponent( cmp, hboxTop.getComponentIndex( butRefresh ));
    }

    private void removeDocument(final Document doc, boolean confirmed) {
        if (!confirmed) {
            ConfirmDialog.show(UI.getCurrent(), "Potwierdź", "Czy na pewno chcesz usunąć ten dokument?:", "Tak", "Nie",
                    new ConfirmDialog.Listener() {

                        public void onClose(ConfirmDialog dialog) {
                            if (dialog.isConfirmed()) {
                                // Confirmed to continue
                                removeDocument(doc, true);
                            } else {
                                // User did not confirm
                                return;
                            }
                        }
                    });
            return;
        } else {
            try {
                documentsApi.delete(doc);
            } catch (VOWrongDataException wre) {
                VoExceptionHandler.handleException(wre);
            }
            cnt.removeItem(doc.getId());

        }
    }

    public BeanContainer<Long, Document> getCnt() {
        return cnt;
    }

    public void setCnt(BeanContainer<Long, Document> cnt) {
        this.cnt = cnt;
    }

    public boolean isShowDocumentOdDblClk() {
        return ShowDocumentOdDblClk;
    }

    public void setShowDocumentOdDblClk(boolean ShowDocumentOdDblClk) {
        this.ShowDocumentOdDblClk = ShowDocumentOdDblClk;
    }

    public Table getTable() {
        return table;
    }

    @Override
    public void valueChange(Property.ValueChangeEvent event) {
       refresh();
    }

    public ComboBoxOrganisationUnit getCmbOrganisationUnit() {
        return cmbOrganisationUnit;
    }

    public void setCmbOrganisationUnit(ComboBoxOrganisationUnit cmbOrganisationUnit) {
        this.cmbOrganisationUnit = cmbOrganisationUnit;
    }

    public ComboBoxCompany getCmbSupplier() {
        return cmbSupplier;
    }

    public void setCmbSupplier(ComboBoxCompany cmbSupplier) {
        this.cmbSupplier = cmbSupplier;
    }
    
    
}
