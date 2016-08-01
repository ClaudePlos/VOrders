/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.priceLists;

import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import pl.vendi.ui.VendiOrdersUI;
import pl.vendi.ui.documents.elements.DocumentWindow;
import pl.vendi.ui.documents.elements.DocumentsTable;
import pl.vo.VOConsts;
import pl.vo.documents.model.Document;

/**
 *
 * @author Piotr
 */
public class WndPriceLists extends Window
{

    VerticalLayout vboxMain = new VerticalLayout();
    HorizontalLayout hboxTop = new HorizontalLayout();
    
    DocumentsTable tabPriceLists = new DocumentsTable("Cenniki",   VOConsts.DOC_TYPE_PRICE_LIST);
    
    Button butAdd = new Button("Dodaj cennik");

    public WndPriceLists()
    {
        super("Cenniki");
        vboxMain.setMargin( true );
                
        
        this.setContent( vboxMain );
        vboxMain.setSizeFull();
        
        vboxMain.addComponent( hboxTop );
        vboxMain.addComponent( tabPriceLists );
        
        vboxMain.setExpandRatio(tabPriceLists, 1);
        tabPriceLists.setSizeFull(); 
      
        tabPriceLists.setDocumentTypes( new String[]{ VOConsts.DOC_TYPE_PRICE_LIST});
        
        hboxTop.addComponent( butAdd );
        
        butAdd.addClickListener( new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event)
            {
              WndPriceList wnd = new WndPriceList();
                VendiOrdersUI.showWindow(wnd);
              wnd.newDocument();
            }
        });
        
        tabPriceLists.getTable().addItemClickListener( new ItemClickEvent.ItemClickListener() {

            @Override
            public void itemClick(ItemClickEvent event) {
               BeanItem<Document> biDoc = ( BeanItem<Document>) event.getItem();
               if ( biDoc != null && event.isDoubleClick())
               {
                   WndPriceList wnd = new WndPriceList();
                   VendiOrdersUI.showWindow(wnd);
                    wnd.setDocument( biDoc.getBean() );
               }
            }
        });
       
        
        tabPriceLists.refresh();
    }
}
