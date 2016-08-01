/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.common;

import com.vaadin.ui.Notification;
import javax.ejb.EJBException;
import pl.vendi.ui.VOErrorWindow;

/**
 *
 * @author Piotr
 */
public class VoExceptionHandler {
    
    public static void handleException( Exception e )
    {
        if (e instanceof EJBException) {
            EJBException ejbe = (EJBException) e;
            
            Exception source = ejbe.getCausedByException();
            VOErrorWindow.showPopupError( source.getMessage() );
//            Notification.show(source.getMessage(), Notification.Type.ERROR_MESSAGE) ;
        }
        else {
            VOErrorWindow.showPopupError( e.getMessage() );
//            Notification.show(e.getMessage(), Notification.Type.ERROR_MESSAGE) ;
        }
    }
}
