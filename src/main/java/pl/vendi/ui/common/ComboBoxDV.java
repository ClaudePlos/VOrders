/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.common;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.Container;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.ComboBox;
import java.math.BigDecimal;
import pl.vo.common.model.DictionaryValue;

/**
 *
 * @author Piotr
 */
public class ComboBoxDV extends ComboBox {

	
	public ComboBoxDV( String caption, BeanItemContainer<DictionaryValue> container){
		
		this.setCaption( caption);
		this.setContainerDataSource( container );
		this.setItemCaptionMode( ItemCaptionMode.PROPERTY);
		this.setItemCaptionPropertyId("description");
	}
	
	public DictionaryValue getDictionaryValue( ){ 
		return (DictionaryValue) getValue();
	}
        
        public String getDictionaryValueCode( ) { 
            if ( getDictionaryValue() != null  )
                return getDictionaryValue().getValue(); 
            else
                return null; 
        }
        
        public void setDictionaryValue( String code )
        {
            if (code == null )
            {
                this.setValue( null );
                this.select(null);
                return ; 
            }
            Container cnt = getContainerDataSource();
            if ( cnt != null ) { 
                for (Object ids :  cnt.getItemIds() ) 
                {
                    DictionaryValue dv = (DictionaryValue) ids;
                    if (dv != null && dv.getValue().equals( code ) ) {
                        this.select( dv);
                        return; 
                    }
                }
            }
        }
        
        public void setDictionaryValue( DictionaryValue value)
        {
            if (value == null)
                this.setValue( null );
            else 
                setDictionaryValue( value.getValue());
        }
        
        
        public void setDictionaryValueByNumberVal( BigDecimal val )
        {
            if (val == null )
            {
                this.setValue( null );
                this.select(null);
                return ; 
            }
            Container cnt = getContainerDataSource();
            if ( cnt != null ) { 
                for (Object ids :  cnt.getItemIds() ) 
                {
                    DictionaryValue dv = (DictionaryValue) ids;
                    if (dv != null && dv.getNumberValue()!=null && dv.getNumberValue().equals( val) ) {
                        this.select( dv);
                        return; 
                    }
                }
            } 
        }
}
