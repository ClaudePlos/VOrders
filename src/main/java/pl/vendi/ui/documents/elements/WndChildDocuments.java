/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.documents.elements;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import pl.vo.documents.model.Document;

/**
 *
 * @author Piotr
 */
public class WndChildDocuments extends Window
{
 
    
    private Document doc; 
    DocumentsTable tab;
    VerticalLayout boxMain = new VerticalLayout(); 
    
    HorizontalLayout hboxTop = new HorizontalLayout();
    
    public WndChildDocuments( Document doc, String docType )
    {
        super("Dokumenty powiązane");
        this.doc = doc; 
        
        tab = new DocumentsTable("Powiązane dokumenty",docType);
        this.setContent( boxMain );
        boxMain.setSpacing( true );
        boxMain.setMargin(true );
        hboxTop.setHeight("40px");
        boxMain.addComponent( hboxTop);
        boxMain.addComponent( tab );
        boxMain.setExpandRatio( tab, 1);
        boxMain.setSizeFull(); 
        tab.setSizeFull();
        
        for ( Document chDoc : doc.getChildDocuments())
        {
            if ( chDoc.getType().equals( docType ))
                tab.getCnt().addBean(chDoc );
        }
    
        
      
        
        
        hboxTop.addComponent( new Label("Dokumenty powiazane do dokumentu:" + doc.getOwnNumber() ));
       
    }
}
