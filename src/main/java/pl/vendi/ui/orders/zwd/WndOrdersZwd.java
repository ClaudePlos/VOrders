/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.orders.zwd;

import com.itextpdf.text.DocumentException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.vendi.ui.VOLookup;
import pl.vendi.ui.VendiOrdersUI;
import pl.vendi.ui.documents.elements.DocumentWindow;
import pl.vendi.ui.documents.elements.DocumentsTable;
import pl.vendi.ui.orders.reports.ReportsZWD;
import pl.vendi.ui.priceLists.WndPriceList;
import pl.vo.VOConsts;
import pl.vo.documents.model.Document;

/**
 *
 * @author Piotr
 */
public class WndOrdersZwd extends Window
{

    VerticalLayout vboxMain = new VerticalLayout();
    HorizontalLayout hboxTop = new HorizontalLayout();
    
    DocumentsTable tabOrders = new DocumentsTable("Zamówienia ZWD", VOConsts.DOC_TYPE_ZWD );
    
    Button butAdd = new Button("Dodaj ZWD");
    Button butRefresh = new Button("Odśwież");
    
    Button butReports = new Button("Raporty");
     
    public WndOrdersZwd()
    {
        super("Zamówienia");
        vboxMain.setMargin( true );
                
        
        this.setContent( vboxMain );
        vboxMain.setSizeFull();
        
        vboxMain.addComponent( hboxTop );
        vboxMain.addComponent(tabOrders );
        
        vboxMain.setExpandRatio(tabOrders, 1);
        tabOrders.setSizeFull(); 
       
        
        tabOrders.setDocumentTypes( new String[]{ VOConsts.DOC_TYPE_ZWD } );
        
        hboxTop.addComponent( butAdd );
        
        butAdd.addClickListener( new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event)
            {
              WndOrderZwd wnd = new WndOrderZwd();
                VendiOrdersUI.showWindow(wnd);
             
              wnd.newDocument( VOConsts.DOC_TYPE_ZWD );
            }
        });
        
        
        hboxTop.addComponent( butRefresh);
         butRefresh.addClickListener( new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event)
            {
              tabOrders.refresh();
            }
        });
        
        hboxTop.addComponent( butReports );
        
        butReports.addClickListener( new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event)
            {
                ReportsZWD rZWD;
                try {
                    rZWD = new ReportsZWD();
                    VendiOrdersUI.showWindow(rZWD);
                } catch (DocumentException ex) {
                    Logger.getLogger(WndOrdersZwd.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(WndOrdersZwd.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
        });
        
        tabOrders.refresh();
    }
}
