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
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolationException;
import pl.common.dao.GenericDao;
import pl.vo.common.VoUserSession;
import pl.vo.company.model.Company;
import pl.vo.exceptions.VOWrongDataException;
import pl.vo.exceptions.VoNoResultException;
import pl.vo.products.model.Product;
import pl.vo.products.model.ProductCmpCode;
import pl.vo.security.model.User;

/**
 *
 * @author Piotr
 */
@Stateless(name = "ProductsApi",mappedName = "ProductsApi")
@LocalBean
public class ProductsApi extends GenericDao<Product, Long>  implements Serializable 
{
    
    @EJB
    VoUserSession voSession; 
            
     public ProductsApi(){
            super(Product.class) ;
     }
   
     public List<Product> findAll()
     { 
       List<Product> ret = super.findAll( );
       return ret; 
     }
     
    
     public Product save( Product item) throws VOWrongDataException
     {
          if ( item.getCodes() != null ) { 
             for ( ProductCmpCode code : item.getCodes()) {
                 VoUserSession.fillAudit( code, getLoggedUser() );
             }
         }
          
         if ( item.getId() != null )
              item = em.merge( item );
         
        
         VoUserSession.fillAudit( item, getLoggedUser()  );
         try {
            em.persist(item );
            em.flush(); 
         }
         catch ( ConstraintViolationException cve )
         {
             throw new VOWrongDataException("Nie udało się zapisać produktu:" + cve.getConstraintViolations().toString());
         }
         catch( Exception e) {
             throw new VOWrongDataException("Nie udało się zapisać produktu:" + e.getMessage());
         }
         return item; 
     }
     
     
      public Product getByIndex(String indexNumber) throws VoNoResultException
      {
          // get 
       String instance_code =  voSession.getLoggedUser().getInstanceCode();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);

        Predicate eq =
                cb.and(cb.equal(root.get("indexNumber"), cb.literal(indexNumber)),
                        cb.equal(root.get("instanceCode"), cb.literal(instance_code)));
        cq.where(eq);
        cq.select(root);
        
        try {
            Product ret = (Product) em.createQuery(cq).getSingleResult();
            return ret;
        } catch (NoResultException nre) {
            throw new VoNoResultException("Nie znaleziono towaru o indexNumber:" + indexNumber);
        }
        catch( NonUniqueResultException nue)
        {
             throw new VoNoResultException("Znaleziono kilka towarów o indeksie o indexNumber:" + indexNumber);
        }

    }
      
      
      // returns product by supplier code
      public Product getByCmpIndex(String indexNumber, Long cmpId) throws VoNoResultException {
          
          CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ProductCmpCode> cqC = cb.createQuery(ProductCmpCode.class);
        Root<ProductCmpCode> rootC = cqC.from(ProductCmpCode.class);
//          
          cqC.where(  cb.and(
                  cb.equal( rootC.get("cmpId"),cb.literal(cmpId))
          , cb.equal( rootC.get("code"), cb.literal(indexNumber))));
          
          cqC.select(rootC); 
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
//        Root<Product> root = cq.from(Product.class);
//
//        Predicate eq = cb.equal(root.get("indexNumber"), cb.literal(indexNumber));
//        cq.where(eq);
//        cq.select(root);
        try {
            ProductCmpCode ret = (ProductCmpCode) em.createQuery( cqC).getSingleResult();
            
//            Product ret = (Product) em.createQuery(cq).getSingleResult();
            return ret.getProduct();
        } catch (NoResultException nre) {
            throw new VoNoResultException("Nie znaleziono towaru o indexNumber:" + indexNumber);
        }

    }
      
      public Product getByName(String nameProduct) throws VoNoResultException {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> cq = cb.createQuery(Product.class);
        Root<Product> root = cq.from(Product.class);

        Predicate eq = cb.equal(root.get("name"), cb.literal(nameProduct));
        cq.where(eq);
        cq.select(root);
        try {
            Product ret = (Product) em.createQuery(cq).getSingleResult();
            return ret;
        } catch (NoResultException nre) {
            throw new VoNoResultException("Nie znaleziono towaru o indexNumber:" + nameProduct);
        }

    }
     
}
