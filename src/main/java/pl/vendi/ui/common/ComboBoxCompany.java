/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.common;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;
import pl.vendi.ui.VOLookup;
import pl.vendi.ui.VendiOrdersUI;
import pl.vo.company.model.Company;
import pl.vo.security.model.User;

/**
 *
 * @author Piotr
 */
public class ComboBoxCompany  extends ComboBox
{
    
    BeanItemContainer<Company> cntUnits = new BeanItemContainer<Company>(Company.class);
    
    
    
    public ComboBoxCompany(String caption)
    {
        super(caption);
        // check logged user 
        User loggedUser = VOLookup.lookupVoUserSession().getLoggedUser();
          this.setContainerDataSource( cntUnits );
        if ( loggedUser.isInternal() )
        cntUnits.addAll( VOLookup.lookupCompanysApi().findAll( ) );
        else if ( loggedUser.isExternal())
        {
            cntUnits.addItem( loggedUser.getCompany() );
            this.setValue( loggedUser.getCompany());
        }
      
        
        this.setItemCaptionMode( AbstractSelect.ItemCaptionMode.PROPERTY );
        this.setItemCaptionPropertyId("name");
    }
    
    public Company getValueCompany() { 
        return (Company) getValue();
    }
    
    
      public Long getCompanyId(){
       if ( getValueCompany() != null )
           return getValueCompany().getId();
       else
           return null; 
    }
    
    public void setValueCompany( Company cmp )
    {
        Container cnt = getContainerDataSource();
        if ( cnt != null ) { 
            for (Object ids :  cnt.getItemIds() ) 
            {
                Company dv = (Company) ids;
                if (dv != null && cmp != null && dv.getId().equals( cmp.getId() ) ) {
                    this.select( dv);
                    return; 
                }
            }
        }
        setValue( cmp );
    }
    
    public void setValueCompanyId(Long cmpId )
    {
        Container cnt = getContainerDataSource();
        if ( cnt != null ) { 
            for (Object ids :  cnt.getItemIds() ) 
            {
                Company dv = (Company) ids;
                if (dv != null && cmpId != null && dv.getId().equals(cmpId ) ) {
                    this.select( dv);
                    return; 
                }
            }
        }
        //setValue( cmp );
    }
        
}
