/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui;

import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

/**
 *
 * @author Piotr
 */
public class LoginView extends VerticalLayout {
    
    TextField tfUsername = new TextField("Użytkownik");
    TextField tfPassword = new TextField("Hasło");
    
    Button butLogin = new Button();
    
  
    
    public LoginView() { 
    
            this.addComponent( tfUsername );
            this.addComponent( tfPassword);
            this.addComponent( butLogin);
            butLogin.addClickListener( new Button.ClickListener()
            {

                @Override
                public void buttonClick(Button.ClickEvent event) {
                   login();
                }
            });
    }
    
    private void login () { 
        if ( tfUsername.getValue() != null && tfPassword.getValue() != null )
        {
            
        }
    }
}
