/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.delivery.pz;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
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
public class WndDpzs extends Window
{

    VerticalLayout vboxMain = new VerticalLayout();
    HorizontalLayout hboxTop = new HorizontalLayout();
    
    DocumentsTable tabOrders = new DocumentsTable("Dostawy DPZ",  VOConsts.DOC_TYPE_DPZ);
    
    Button butAdd = new Button("Dodaj Dostawę");
    Button butAddFromDpz = new Button("Dodaj Dostawę na podstawie DPZ");
    Button butRefresh = new Button("Odśwież");
   
    
    public WndDpzs()
    {
        super("Dostawy DPZ ( dokumenty WZ wysłane przez dostawców w trakcie dostawy )");
        vboxMain.setMargin( true );
                
        
        this.setContent( vboxMain );
        vboxMain.setSizeFull();
        
        vboxMain.addComponent( hboxTop );
        vboxMain.addComponent(tabOrders );
        
        vboxMain.setExpandRatio(tabOrders, 1);
        tabOrders.setSizeFull(); 

        
        tabOrders.setDocumentTypes( new String[]{ VOConsts.DOC_TYPE_DPZ } );
        
        hboxTop.addComponent( butAdd );
        hboxTop.addComponent( butAddFromDpz);
        hboxTop.addComponent( butRefresh);
        hboxTop.setSpacing( true );
        
        butAdd.addClickListener( new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event)
            {
              WndOrderZwk wnd = new WndOrderZwk();
                VendiOrdersUI.showWindow(wnd);
             
              wnd.newDocument( VOConsts.DOC_TYPE_PZ );
            }
        });
        
       
        butAddFromDpz.addClickListener( new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event)
            {
              WndSelectDpz wnd = new WndSelectDpz();
               VendiOrdersUI.showWindow(wnd);
             
            }
        });
        
        butRefresh.addClickListener( new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event)
            {
              tabOrders.refresh();
            }
        });
       
        
        //tabOrders.refresh();
    }
}
