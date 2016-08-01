/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.common.api;

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
import pl.vo.common.model.DictionaryValue;
import pl.vo.exceptions.VOWrongDataException;
import pl.vo.exceptions.VoNoResultException;

/**
 *
 * @author Piotr
 */
@Stateless(name = "DictionaryApi",mappedName = "DictionaryApi")
@LocalBean
public class DictionaryApi extends GenericDao<DictionaryValue, Long>  implements Serializable 
{
    
    @EJB
    VoUserSession voSession; 
            
     public DictionaryApi()
     {
            super(DictionaryValue.class) ;
            
     }
   
     public List<DictionaryValue> findAll()
     { 
       List<DictionaryValue> ret = super.findAll();
       return ret; 
     }
     
     public List<DictionaryValue> listByDictionaryCode( String code )
     {
         CriteriaBuilder cb =  em.getCriteriaBuilder(); 
        CriteriaQuery<DictionaryValue> cq = cb.createQuery(DictionaryValue.class);
       Root<DictionaryValue> root = cq.from( DictionaryValue.class);
        
       Predicate eq = cb.equal(root.get("dictionaryCode"), cb.literal( code ));
      
         addInstanceCodeCriteria(cb, cq, root, eq);
       
          cq.select(root);
        List<DictionaryValue> ret = ( List<DictionaryValue> ) em.createQuery( cq).getResultList(); 
        return ret; 
     }
    
     public DictionaryValue save( DictionaryValue item) throws VOWrongDataException
     {
         if ( item.getId() != null )
              item = em.merge(item );
         
         VoUserSession.fillAudit(item ,getLoggedUser());
         try {
            em.persist(item );
            em.flush(); 
         }
         catch ( ConstraintViolationException cve )
         {
             throw new VOWrongDataException("Nie udało się zapisać slownika:" + cve.getConstraintViolations().toString());
         }
         catch( Exception e) {
             throw new VOWrongDataException("Nie udało się zapisać slownika:" + e.getMessage());
         }
         return item; 
     }
     
     public DictionaryValue getValue( String dict, String valueCode) throws VoNoResultException
     {
          CriteriaBuilder cb =  em.getCriteriaBuilder(); 
        CriteriaQuery<DictionaryValue> cq = cb.createQuery(DictionaryValue.class);
       Root<DictionaryValue> root = cq.from( DictionaryValue.class);
        
       Predicate eq = cb.and(
                cb.equal(root.get("dictionaryCode"), cb.literal( dict )),
               cb.equal(root.get("value"), cb.literal( valueCode )));
      
         addInstanceCodeCriteria(cb, cq, root, eq);
       
          cq.select(root);
          try {
        DictionaryValue ret = (DictionaryValue) em.createQuery( cq).getSingleResult(); 
        return ret; 
        
          }
          catch ( NonUniqueResultException nur){
              throw new VoNoResultException("Znaleziono kilka wartości słownika dla: " + valueCode);
          }
           catch ( NoResultException nre){
              throw new VoNoResultException("Nie znaleziono wartości słownika dla: " + valueCode);
          }
     }
}

