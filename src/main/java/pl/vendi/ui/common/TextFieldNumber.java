/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.common;

import com.vaadin.ui.TextField;
import java.math.BigDecimal;

/**
 *
 * @author Piotr
 */
public class TextFieldNumber extends TextField {

    public TextFieldNumber() {
    }

    public TextFieldNumber(String caption) {
        super(caption);
    }
    
    
    public void setValue( BigDecimal bd) 
    {
        if ( bd == null )
            this.setValue( ( String) null);
        else {
            this.setValue(bd.toString());
        }
    }
    
    public BigDecimal getValueNumber() { 
        if (this.getValue() == null || this.getValue().length() == 0 )
            return null; 
        
        BigDecimal ret = new BigDecimal( this.getValue() );
        return ret; 
    }
}
