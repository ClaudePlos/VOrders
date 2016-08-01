/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.products.api;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolationException;
import pl.common.dao.GenericDao;
import pl.vo.common.VoUserSession;
import pl.vo.exceptions.VOWrongDataException;
import pl.vo.organisation.OrganisationApi;
import pl.vo.organisation.model.OrganisationUnit;
import pl.vo.products.model.Product;
import pl.vo.products.model.UnitsProducts;

/**
 *
 * @author Piotr
 */
@Stateless(name = "UnitsProductsApi",mappedName = "UnitsProductsApi")
@LocalBean
public class UnitsProductsApi  extends GenericDao<UnitsProducts, Long>  implements Serializable 
{
    
    @EJB
    ProductsApi productsApi; 
    
    @EJB
    OrganisationApi organisationApi; 
  
     public UnitsProductsApi(){
            super(UnitsProducts.class) ;
     }
   
     public List<UnitsProducts> findAll()
     { 
       List<UnitsProducts> ret = super.findAll();
       return ret; 
     }
     
     public List<UnitsProducts> findForUnit( Long unitId )
     {
          CriteriaBuilder cb =  em.getCriteriaBuilder(); 
          CriteriaQuery<UnitsProducts> cq = cb.createQuery(UnitsProducts.class);
        Root<UnitsProducts> units = cq.from( UnitsProducts.class);
        cq.select(units);
        
         Predicate eq = cb.equal( units.get("unit").get("id"), cb.literal( unitId ));
        cq.where( eq);
        
        List<UnitsProducts> ret = ( List<UnitsProducts> ) em.createQuery( cq).getResultList(); 
        return ret; 
     }
    
     public UnitsProducts save( UnitsProducts item) throws VOWrongDataException
     {
        return super.save( item );
       
     }
     
     public String delete( UnitsProducts item )throws VOWrongDataException
     {
         return super.delete( item );
     }
     
     
     public UnitsProducts addProductToUnit( Long prodId, Long unitId )  throws VOWrongDataException 
     {
         UnitsProducts up = new UnitsProducts();
         Product prod = productsApi.get( prodId ) ; 
         OrganisationUnit unit = organisationApi.get(unitId);
         up.setProduct(prod);
         up.setUnit( unit );
         
         up = save( up );
         return up; 
     }
     
     
}

