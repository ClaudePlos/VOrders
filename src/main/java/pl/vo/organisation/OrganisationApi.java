/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.organisation;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.validation.ConstraintViolationException;
import javax.xml.registry.infomodel.Organization;
import pl.common.dao.GenericDao;
import pl.vo.common.VoUserSession;
import pl.vo.company.model.Company;
import pl.vo.exceptions.VOWrongDataException;
import pl.vo.organisation.model.OrganisationUnit;

/**
 *
 * @author Piotr
 */
@Stateless(name = "OrganisationApi",mappedName = "OrganisationApi")
@LocalBean
public class OrganisationApi  extends GenericDao<OrganisationUnit, Long>  implements Serializable 
{
    
    @EJB
    VoUserSession voSession; 
            
     public OrganisationApi(){
            super(OrganisationUnit.class) ;
     }
     
     
     public List<OrganisationUnit> findAll()
     { 
       List<OrganisationUnit> ret = super.findAll( );
       return ret; 
     }
     
     //TODO 
     public List<OrganisationUnit> listMyUnits() { 
         List<OrganisationUnit> ret = super.findAll();
       return ret; 
     }
     
     
     public OrganisationUnit save( OrganisationUnit ounit) throws VOWrongDataException
     {
         if ( ounit.getId() != null )
              ounit = em.merge(ounit );
         
         VoUserSession.fillAudit(ounit ,getLoggedUser());
         try {
            em.persist(ounit );
            em.flush(); 
         }
         catch ( ConstraintViolationException cve )
         {
             throw new VOWrongDataException("Nie udało się zapisać jednostki firmy:" + cve.getConstraintViolations().toString());
         }
         catch( Exception e) {
             throw new VOWrongDataException("Nie udało się zapisać jednostki firmy:" + e.getMessage());
         }
         return ounit; 
     }
     
     
     public OrganisationUnit getByCode( String code )
     {
         return getByField("code", code);
     }
     
     
}
