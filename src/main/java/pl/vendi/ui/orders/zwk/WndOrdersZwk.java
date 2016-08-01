/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.orders.zwk;

import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import pl.vendi.ui.VOLookup;
import pl.vendi.ui.VendiOrdersUI;
import pl.vendi.ui.documents.elements.DocumentWindow;
import pl.vendi.ui.documents.elements.DocumentsTable;
import pl.vendi.ui.priceLists.WndPriceList;
import pl.vo.VOConsts;
import pl.vo.documents.model.Document;

/**
 *
 * @author Piotr
 */
public class WndOrdersZwk extends Window
{

    VerticalLayout vboxMain = new VerticalLayout();
    DocumentsTable tabOrders = new DocumentsTable("Zamówienia",  VOConsts.DOC_TYPE_ZWK);
    
    Button butAdd = new Button("Dodaj ZWK");
    
        
    public WndOrdersZwk()
    {
        super("Zamówienia");
        vboxMain.setMargin( true );
                
        
        this.setContent( vboxMain );
        vboxMain.setSizeFull();
        
        vboxMain.addComponent(tabOrders );
        
        vboxMain.setExpandRatio(tabOrders, 1);
        tabOrders.setSizeFull(); 
        tabOrders.getTable().setSelectable( true );
        
        tabOrders.setDocumentTypes( new String[]{ VOConsts.DOC_TYPE_ZWK } );
        butAdd.setIcon( FontAwesome.PLUS ) ;
        tabOrders.addToBar( butAdd );
        tabOrders.getCmbSupplier().setVisible( false );
        
        butAdd.addClickListener( new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event)
            {
              WndOrderZwk wnd = new WndOrderZwk();
                VendiOrdersUI.showWindow(wnd);
             
              wnd.newDocument( VOConsts.DOC_TYPE_ZWK );
            }
        });
        
       
       
        
        tabOrders.refresh();
    }
}
