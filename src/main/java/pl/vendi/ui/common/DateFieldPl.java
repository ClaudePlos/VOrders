/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.common;

import com.vaadin.ui.DateField;

/**
 *
 * @author Piotr
 */
public class DateFieldPl extends DateField {
    
    
    public DateFieldPl(){
        super();
       initPl();
    }
    
    public DateFieldPl(String caption)
    {
        super(caption);
        initPl();
    }

    private void initPl() { 
         this.setDateFormat("yyyy-MM-dd");
    }
}
