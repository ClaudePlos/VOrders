/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui;

import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 *
 * @author Piotr
 */
public class VOErrorWindow extends Window
{
    private TextArea tf = new TextArea();
    
    private VerticalLayout vbox= new VerticalLayout();
    
    public VOErrorWindow( String errorText)
    {
        this.tf.setValue( errorText);
        this.setWidth("50%");
        this.setHeight("50%");
        
        this.setContent( vbox );
        vbox.setSizeFull();
        vbox.addComponent( tf );
        tf.addStyleName( ValoTheme.LABEL_FAILURE);
//         tf.addStyleName( ValoTheme.LABEL_COLORED);
        tf.setSizeFull();
        tf.setWidth("100%");
        setCaption("Błąd przy wykonywaniu operacji.");
        
    }
    
    
    public static VOErrorWindow showPopupError(String  errorText){
        VOErrorWindow w = new VOErrorWindow(errorText);
        VendiOrdersUI.showWindow(w);
        w.center();
        
        return w; 
    }
    
    
}
