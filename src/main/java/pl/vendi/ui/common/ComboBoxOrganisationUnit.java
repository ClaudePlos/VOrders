/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.common;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.ComboBox;
import pl.vendi.ui.VOLookup;
import pl.vo.company.model.Company;
import pl.vo.organisation.OrganisationApi;
import pl.vo.organisation.model.OrganisationUnit;
import pl.vo.security.model.UsersCompanyUnits;

/**
 *
 * @author Piotr
 */
public class ComboBoxOrganisationUnit extends ComboBox
{

    
    BeanItemContainer<OrganisationUnit> cntUnits = new BeanItemContainer<OrganisationUnit>(OrganisationUnit.class);
    
    
     public ComboBoxOrganisationUnit(String caption) {
         super(caption);
         init( caption, false);
     }
    public ComboBoxOrganisationUnit(String caption, Boolean fShowAll)
    {
        super(caption);
       init(caption, fShowAll);
    }
    
    private void init( String caption, Boolean fShowAll) 
    { 
        if ( fShowAll)
          cntUnits.addAll( VOLookup.lookupOrganisationApi().listMyUnits() );
        else  {
            for ( UsersCompanyUnits uu :  VOLookup.lookupVoUserSession().getLoggedUser().getUnits() ) { 
                cntUnits.addItem( uu.getCompanyUnit() );
            }
        }
        this.setContainerDataSource( cntUnits );
        
        this.setItemCaptionMode( ItemCaptionMode.PROPERTY );
        this.setItemCaptionPropertyId("name");
    }
    
    public OrganisationUnit getOrganisationUnit() { 
        return (OrganisationUnit) getValue();
    }
        
    
    public Long getOrganisationUnitId(){
       if ( getOrganisationUnit() != null )
           return getOrganisationUnit().getId();
       else
           return null; 
    }
    
    public void setValueOrganisationUnit( OrganisationUnit cmp )
    {
        Container cnt = getContainerDataSource();
        if ( cnt != null ) { 
            for (Object ids :  cnt.getItemIds() ) 
            {
                OrganisationUnit dv = (OrganisationUnit) ids;
                if (dv != null && cmp != null && dv.getId().equals( cmp.getId() ) ) {
                    this.select( dv);
                    return; 
                }
            }
        }
        setValue( cmp );
    }
}
