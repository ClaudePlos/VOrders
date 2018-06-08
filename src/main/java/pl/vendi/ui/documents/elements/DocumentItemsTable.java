/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.documents.elements;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.AbstractErrorMessage.ContentMode;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.Table;
import com.vaadin.ui.TableFieldFactory;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import pl.vendi.ui.VOLookup;
import pl.vendi.ui.common.ComboBoxProducts;
import pl.vo.VOConsts;
import pl.vo.company.api.CompanysApi;
import pl.vo.company.model.Company;
import pl.vo.documents.model.Document;
import pl.vo.documents.model.DocumentItem;
import pl.vo.organisation.model.OrganisationUnit;
import pl.vo.products.model.Product;
import pl.vo.products.model.ProductCmpCode;
import pl.vo.road_distance.api.RoadDistanceApi;
import pl.vo.road_distance.model.RoadDistance;
import pl.vo.utils.VOUtils;

/**
 *
 * @author Piotr
 */
public class DocumentItemsTable extends Table {

    BeanItemContainer<DocumentItem> cnt = new BeanItemContainer< DocumentItem>(DocumentItem.class);

    BeanItemContainer<Product> cntProducts = new BeanItemContainer<Product>(Product.class);

    private Table thisTable;

    private String documentTypeGroup;

    List<String> columns = new ArrayList<String>();
    List<String> columnCaptions = new ArrayList<String>();
    
    final DocumentWindow parentWindow; 
    DocumentItemsWithFilter parent; 
    
    RoadDistanceApi apiRoad;
    
    

    public DocumentItemsTable(String documentTypeGroup, DocumentWindow parentWindow , DocumentItemsWithFilter parent)
    {
        this.parentWindow = parentWindow;
        this.parent = parent; 
        thisTable = this;
        this.documentTypeGroup = documentTypeGroup;
        List<Product> products = VOLookup.lookupProductsApi().findAll();
        cntProducts.addAll(products);
        
        apiRoad = VOLookup.lookupRoadDistanceApi();

        this.setContainerDataSource(cnt);

        this.setSelectable(true);
        //this.setEditable( true );

        cnt.addNestedContainerProperty("product.abbr");
        cnt.addNestedContainerProperty("product.measureUnit");
        cnt.addNestedContainerProperty("product.measureUnit.abbr");

        cnt.addNestedContainerProperty("unitProductSupplier.supplier");
        cnt.addNestedContainerProperty("unitProductSupplier.supplier.abbr");

        this.addItemClickListener(new ItemClickEvent.ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
                // Notification.show("AA");   
                thisTable.setValue(event.getItemId());
                // thisTable.setSelectable(!true);
                // thisTable.setEditable( true );
                //thisTable.
            }
        });

          this.addGeneratedColumn("unitPriceNetEdit", new ColumnGenerator() {

                @Override
                public Object generateCell(Table source, Object itemId, Object columnId) {

                    if (itemId != null) {
                        TextField lab = new TextField();
                        DocumentItem di = (DocumentItem) itemId;
                        if (di != null && di.getUnitPriceNet() != null) {
                            lab.setValue( VOUtils.formatCurrency(di.getUnitPriceNet()) );
                        } else {
                            lab.setValue("");
                        }
                        lab.addValueChangeListener( new Property.ValueChangeListener() {

                            @Override
                            public void valueChange(Property.ValueChangeEvent event) {
                                String sval = (String) event.getProperty().getValue();
                                DocumentItem di = (DocumentItem) itemId;
                                di.setUnitPriceNet(new BigDecimal(sval.replace(",", ".")));
                               setModified();
                            }
                        });

                        return lab;
                    }
                    return null;
                }
            });

      
            // non editable - good
            this.addGeneratedColumn("unitPriceNet", new ColumnGenerator() {

                @Override
                public Object generateCell(Table source, Object itemId, Object columnId) {

                    if (itemId != null) {
                        Label lab = new Label();
                        DocumentItem di = (DocumentItem) itemId;
                        if (di != null && di.getUnitPriceNet() != null) {
                            lab.setValue( VOUtils.formatCurrency( di.getUnitPriceNet() ) );
                            lab.setWidth("100%");
                            lab.addStyleName( "labelRight");
                        } else {
                            lab.setValue("");
                        }
                        
                        lab.addValueChangeListener( new Property.ValueChangeListener() {

                            @Override
                            public void valueChange(Property.ValueChangeEvent event) {
                               setModified();
                            }
                        });

                        return lab;
                    }
                    return null;
                }
            });
        

        this.addGeneratedColumn("product", new ColumnGenerator() {

            @Override
            public Object generateCell(Table source, final Object itemId, Object columnId) {

                if (itemId != null) {
                    DocumentItem di = (DocumentItem) itemId;
                   
                    Label lab = new Label();

                    if (di != null && di.getProduct() != null) {
                        lab.setValue(di.getProduct().getName());
                        return lab;
                    }
                }
                return null;
            }
        });

        this.addGeneratedColumn("delete", new ColumnGenerator() {

            @Override
            public Object generateCell(Table source, final Object itemId, Object columnId) {

                Button but = new Button();
                but.setCaptionAsHtml(true);
                but.setCaption(FontAwesome.TRASH_O.getHtml());
                but.addStyleName(ValoTheme.BUTTON_LINK);

                but.addClickListener(new Button.ClickListener() {

                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        parent.removeItem((DocumentItem) itemId);
                    }
                });

                return but;

            }
        });

        // if (document != null && document.getType().equals(VOConsts.DOC_TYPE_ZWD) && document.getStatus().equals(VOConsts.DOC_STATUS_RECEIVED_BY_SUPPLIER)) {
        this.addGeneratedColumn("amountConfirmedEdit", new ColumnGenerator() {

            @Override
            public Object generateCell(Table source, final Object itemId, Object columnId) {

                TextField lab = new TextField();
                DocumentItem di = (DocumentItem) itemId;
                if (di != null && di.getAmountConfirmed() != null) {
                    lab.setValue( VOUtils.formatCurrency(di.getAmountConfirmed()) );
                } else {
                    lab.setValue("");
                }

                lab.addValueChangeListener(new Property.ValueChangeListener() {

                    @Override
                    public void valueChange(Property.ValueChangeEvent event) {
                        String sval = (String) event.getProperty().getValue();
                        DocumentItem di = (DocumentItem) itemId;
                        di.setAmountConfirmed(new BigDecimal(sval.replace(",", ".")));
                        setModified();
                    }
                });

                return lab;

            }
        });
        
        
        this.addGeneratedColumn("amountConfirmed", new ColumnGenerator() {

            @Override
            public Object generateCell(Table source, final Object itemId, Object columnId) {

                Label lab = new Label();
                DocumentItem di = (DocumentItem) itemId;
                if (di != null && di.getAmountConfirmed() != null) {
                    lab.setValue( VOUtils.formatCurrency( di.getAmountConfirmed() ) );
                    lab.setWidth("100%");
                    lab.addStyleName( "labelRight");
                } else {
                    lab.setValue("");
                }

                return lab;

            }

        });

        this.addGeneratedColumn("amountEdit", new ColumnGenerator() {

            @Override
            public Object generateCell(Table source, final Object itemId, Object columnId) {

                TextField lab = new TextField();
                DocumentItem di = (DocumentItem) itemId;
                if (di != null && di.getAmount() != null) {
                    lab.setValue( VOUtils.formatCurrency(di.getAmount()) );
                } else {
                    lab.setValue("");
                }

                lab.addValueChangeListener(new Property.ValueChangeListener() {

                    @Override
                    public void valueChange(Property.ValueChangeEvent event) {
                        String sval = (String) event.getProperty().getValue();
                        DocumentItem di = (DocumentItem) itemId;
                        di.setAmount(new BigDecimal(sval.replace(",", ".")));
                        setModified();
                    }
                });

                return lab;

            }

        });
        
        
        this.addGeneratedColumn("amount", new ColumnGenerator() {

            @Override
            public Object generateCell(Table source, final Object itemId, Object columnId) {

                Label lab = new Label();
                DocumentItem di = (DocumentItem) itemId;
                if (di != null && di.getAmount() != null) {
                    lab.setValue( VOUtils.formatCurrency( di.getAmount() ) );
                    lab.setWidth("100%");
                    lab.addStyleName( "labelRight");
                } else {
                    lab.setValue("");
                }

                return lab;

            }

        });
        
        this.addGeneratedColumn("valueNet", new ColumnGenerator() {

            @Override
            public Object generateCell(Table source, final Object itemId, Object columnId) {

                Label lab = new Label();
                        DocumentItem di = (DocumentItem) itemId;
                        if (di != null && di.getValueNet()!= null) {
                            lab.setValue(VOUtils.formatCurrency(di.getValueNet()));
                            lab.setWidth("100%");
                            lab.addStyleName( "labelRight");
                            
                        } else {
                            lab.setValue("");
                        }
                return lab;

            }
        });
        
         
        this.addGeneratedColumn("valueTax", new ColumnGenerator() {

            @Override
            public Object generateCell(Table source, final Object itemId, Object columnId) {

                Label lab = new Label();
                        DocumentItem di = (DocumentItem) itemId;
                        if (di != null && di.getValueTax()!= null) {
                            lab.setValue( VOUtils.formatCurrency(di.getValueTax()) );
                            lab.setWidth("100%");
                            lab.addStyleName( "labelRight");
                            
                        } else {
                            lab.setValue("");
                        }
                return lab;

            }
        });
        
        this.addGeneratedColumn("info", new ColumnGenerator() {

            @Override
            public Object generateCell(Table source, final Object itemId, Object columnId) {
                

                VerticalLayout popupContent = new VerticalLayout();
                String namePoPup = "Info";
                DocumentItem di = (DocumentItem) itemId;
                
                // KLAUDIUSZ add sprawdzam drogę dostawy 
                
                  
                    OrganisationUnit orgDestination = document.getCompanyUnit();
                    int smallestDistance = 500000;

                    for ( ProductCmpCode pcc : di.getProduct().getCodes() )
                    {
                        Product p = pcc.getProduct();

                        if ( orgDestination != null ) {
                            RoadDistance distance = apiRoad.getByCmpUnitIdAndCmpId( orgDestination.getId(), pcc.getCmpId() );
                            pcc.setDistanceDelivery( distance.getDistance() );  
 
                            if ( pcc.getDistanceDelivery().intValue() < smallestDistance )
                            {
                                smallestDistance = pcc.getDistanceDelivery().intValue();
                            }
                        } else {
                           pcc.setDistanceDelivery(Long.parseLong("0")); 
                        }
                            
                        
                    }
                
                   
                    for ( ProductCmpCode pcc :  di.getProduct().getCodes() )
                    {
                        Company cmp = VOLookup.lookupCompanysApi().getById(  pcc.getCmpId() );
                        Random generator = new Random();
                        String companyUnitAddress = "";
                        String url = "https://www.google.pl/maps/dir/Ożarów+Mazowiecki/Wyszków/";
                        
                        int ilosc = generator.nextInt(1000);
                        Label labTowarDostepnosc = new Label();
                        
                        if ( ilosc > 100 ){
                           labTowarDostepnosc.setValue("Towar dostępny ilosc: " + (ilosc - 100) ); 
                           labTowarDostepnosc.setStyleName( ValoTheme.LABEL_SUCCESS ); 
                        } else {
                            labTowarDostepnosc.setValue("Brak towaru ilosc: 0"); 
                           labTowarDostepnosc.setStyleName( ValoTheme.LABEL_SUCCESS );
                        }
                        
         
                        
                     
                        Link link = new Link("Trasa dostawy!", new ExternalResource(url));
                        link.setTargetName("_blank");
                        
                        if ( document.getCompanyUnit() != null ){
                            companyUnitAddress = document.getCompanyUnit().getAddress();
                        }
                        
                        url = "https://www.google.pl/maps/dir/" + companyUnitAddress + "/" + cmp.getAddress();
                        link.setResource(new ExternalResource(url));
                        
                    
                        if ( pcc.getDistanceDelivery() != null && pcc.getDistanceDelivery().intValue() == smallestDistance )
                        {
                          Label label1 = new Label( new Label( cmp.getName() + " (" + pcc.getDistanceDelivery().toString() + " km)"   ) );
                          label1.setStyleName( ValoTheme.LABEL_SUCCESS);
                          popupContent.addComponent( label1 );
                          popupContent.addComponent( labTowarDostepnosc );
                          popupContent.addComponent(link);
                        }
                        else
                        {
                          Label label1 = new Label( new Label( cmp.getName() + " (" + pcc.getDistanceDelivery().toString() + " km)"   ) );
                          label1.setStyleName( ValoTheme.LABEL_FAILURE);
                          popupContent.addComponent( label1 );
                          popupContent.addComponent( labTowarDostepnosc );
                          popupContent.addComponent(link);  
                        }
                   
                        
                        
                        
                    }
                
                    
                if ( di.getProduct().getCodes().size() > 1 )
                {
                    namePoPup = "Info*";
                }
                

                PopupView popup = new PopupView(namePoPup, popupContent);

                return popup;

            }
        });
        
         this.addGeneratedColumn("document", new ColumnGenerator() {

            @Override
            public Object generateCell(Table source, final Object itemId, Object columnId) {

                Label lab = new Label();
                        DocumentItem di = (DocumentItem) itemId;
                        if (di != null && di.getDocument() != null) {
                            lab.setValue( di.getDocument().getExternalNumber() );
                            lab.setWidth("100%");
                            lab.addStyleName( "labelRight");
                            
                        } else {
                            lab.setValue("");
                        }
                return lab;

            }
        });
         
         this.addGeneratedColumn("product.measureUnit", new ColumnGenerator() {

            @Override
            public Object generateCell(Table source, final Object itemId, Object columnId) {

                Label lab = new Label();
                        DocumentItem di = (DocumentItem) itemId;
                        if (di != null && di.getProduct().getMeasureUnit() != null) {
                            lab.setValue( di.getProduct().getMeasureUnit().getName() );
                            lab.setWidth("100%");
                            lab.addStyleName( "labelRight");
                            
                        } else {
                            lab.setValue("");
                        }
                return lab;

            }
        });
        
        
        // }

        setColumnSet();

        setFieldsFactory();

    }

   
    private Document document;

    public void setDocument(Document document) {

        this.document = document;
      refreshRows();
        // set column
        setColumnSet();

    }
    
    public void setContainer(BeanItemContainer<DocumentItem> cnt){
        this.cnt = cnt; 
         this.setContainerDataSource(cnt);
        setColumnSet();
        
    }
    
    public void refreshRows( ) { 
          if (document != null) {
            cnt.removeAllItems();

            if (this.isEditable()) {
                //  document.getItems().add( new DocumentItem( ));
            }
            cnt.addAll(document.getItems());
        }
    }

    private void setColumnSet() {
        columns.clear();;
        columnCaptions.clear();

        addColumn("product", "Towar");
        addColumn("product.measureUnit.abbr", "Jm");

       
        if (documentTypeGroup.equals(VOConsts.DOC_TYPE_PRICE_LIST)
               
                || documentTypeGroup.equals(VOConsts.DOC_TYPE_ZWD)) 
        {
            if ( document != null && document.getStatus().equals( VOConsts.DOC_STATUS_OPEN)){
               addColumn("unitPriceNetEdit", "Cena Edycja"); 
            } else
            addColumn("unitPriceNet", "Cena PLN");
        }
        
        if (   documentTypeGroup.equals(VOConsts.DOC_TYPE_ZWK) && document != null && !document.getStatus().equals( VOConsts.DOC_STATUS_OPEN))
        {
           addColumn("unitPriceNet", "Cena PLN"); 
        }
       // }

        // for orders
        if (documentTypeGroup.equals(VOConsts.DOC_TYPE_ZWK)) { 
            if ( document != null && document.getStatus().equals( VOConsts.DOC_STATUS_OPEN))
            {
                addColumn("amountEdit", "Ilość zamawiana"); 
                addColumn("info", "Info");
            }
            else
            {
                addColumn("amount", "Ilość zamawiana");    
            }
                 
        }
       
                if ( documentTypeGroup.equals(VOConsts.DOC_TYPE_ZWD)) {

            addColumn("amount", "Ilość");
        }

        if (documentTypeGroup.equals(VOConsts.DOC_TYPE_PZ) && document != null) {
            if (document.getStatus().equals(VOConsts.DOC_STATUS_OPEN)) {
                addColumn("amountEdit", "Ilość dostarczona");
            } else {
                addColumn("amount", "Ilość dostarczona");
            }
        }
        // 
        if (document != null && document.getType().equals(VOConsts.DOC_TYPE_ZWD)) {
            if (document.getStatus().equals(VOConsts.DOC_STATUS_RECEIVED_BY_SUPPLIER)) {
                addColumn("amountConfirmedEdit", "Ilość potwierdzona");
            } else {
                addColumn("amountConfirmed", "Ilość potwierdzona");
            }
        }
        if (document != null && document.getType().equals(VOConsts.DOC_TYPE_DPZ)) {

            addColumn("amountConfirmed", "Ilość potwierdzona");
            addColumn("amountOnDpzs", "Ilość z innych PZ");
            addColumn("amountLeftToDelivery", "Pozostało");
            addColumn("amountEdit", "Zadeklaruj wysyłkę na PZ");
        }

        if (document != null && !document.getStatus().equals(VOConsts.DOC_STATUS_OPEN) &&
                !documentTypeGroup.equals(VOConsts.DOC_TYPE_PRICE_LIST)) 
        {
            addColumn("valueNet", "Netto PLN");
            addColumn("valueTax", "Vat PLN");
            addColumn("valueBrut", "Brutto PLN");
            addColumn("unitProductSupplier.supplier.abbr", "Dostawca");
        }

        if (document != null && !document.getStatus().equals(VOConsts.DOC_STATUS_OPEN)) {
            addColumn("status", "Status");
        }

        if (document != null && document.getStatus().equals(VOConsts.DOC_STATUS_OPEN)) {
            addColumn("delete", "Usuń");
        }
        
        //ks
        addColumn("document", "Dokument");
        addColumn("product.measureUnit", "Miara");

        this.setVisibleColumns(columns.toArray());
        this.setColumnHeaders(columnCaptions.toArray(new String[0]));

    }

    private void addColumn(String columnName, String caption) {
        columns.add(columnName);
        columnCaptions.add(caption);
    }

    /* 
     public void setEditable( boolean val )
     {
     this.editable = val; 
        
     }
     */

    private void setFieldsFactory() {
        //  this.setTableFieldFactory( new DocumentsPositionFieldFactory(cntProducts, this));
    }

    public BeanItemContainer<DocumentItem> getCnt() {
        return cnt;
    }

    public void setCnt(BeanItemContainer<DocumentItem> cnt) {
        this.cnt = cnt;
    }
    
    public void setModified()
    {
        parentWindow.setModified(true);
    }

}
