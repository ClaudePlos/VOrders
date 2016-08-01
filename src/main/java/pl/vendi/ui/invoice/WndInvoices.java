/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.invoice;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import pl.vendi.ui.VendiOrdersUI;
import pl.vendi.ui.documents.elements.DocumentsTable;
import pl.vendi.ui.orders.zwk.WndOrderZwk;
import pl.vo.VOConsts;

/**
 *
 * @author Piotr
 */
public class WndInvoices extends Window
{

    VerticalLayout vboxMain = new VerticalLayout();
    DocumentsTable tabOrders = new DocumentsTable("Faktury",  VOConsts.DOC_TYPE_INVOICE);
    
    Button butAdd = new Button("Dodaj Fakturę");
    
        
    public WndInvoices()
    {
        super("Faktury");
        vboxMain.setMargin( true );
                
        
        this.setContent( vboxMain );
        vboxMain.setSizeFull();
        
        vboxMain.addComponent(tabOrders );
        
        vboxMain.setExpandRatio(tabOrders, 1);
        tabOrders.setSizeFull(); 
        tabOrders.getTable().setSelectable( true );
        
        tabOrders.setDocumentTypes( new String[]{ VOConsts.DOC_TYPE_INVOICE } );
        butAdd.setIcon( FontAwesome.PLUS ) ;
        tabOrders.addToBar( butAdd );
        tabOrders.getCmbSupplier().setVisible( false );
        
        butAdd.addClickListener( new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event)
            {
              WndOrderZwk wnd = new WndOrderZwk();
                VendiOrdersUI.showWindow(wnd);
             
              wnd.newDocument( VOConsts.DOC_TYPE_INVOICE );
            }
        });
        
       
       
        
        tabOrders.refresh();
    }
}
