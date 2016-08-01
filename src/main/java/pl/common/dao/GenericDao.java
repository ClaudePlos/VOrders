/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.common.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.validation.ConstraintViolationException;
import pl.vendi.ui.VOLookup;
import pl.vendi.ui.VendiOrdersUI;
import pl.vo.VOConsts;
import pl.vo.common.AuditEntityBase;
import pl.vo.common.IVoAuditable;
import pl.vo.common.VoUserSession;
import pl.vo.exceptions.VOWrongDataException;
import pl.vo.security.api.UsersApi;
import pl.vo.security.model.User;

/**
 *
 * @author Piotr
 */
public abstract class GenericDao<T extends AuditEntityBase, K extends Serializable> extends VoBeanBase {

    @PersistenceContext(name = "pu")
    protected EntityManager em;

//    @EJB
//    protected VoUserSession voSession;
    
    
    @EJB
    protected UsersApi usersApi; 

    public Class<T> classType;

    
    
    public GenericDao(Class<T> classType) {
        this.classType = classType;
    }

    public List<T> findAllNoCodeInstance() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(classType);
        Root<T> root = cq.from(classType);
        cq.select(root);
        List<T> ret = (List<T>) em.createQuery(cq).getResultList();
        return ret;
    }

    public List<T> findAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(classType);
        Root<T> root = cq.from(classType);
        cq.select(root);
        addInstanceCodeCriteria(cb, cq, root);
        List<T> ret = (List<T>) em.createQuery(cq).getResultList();
        return ret;
    }

    public T get(Long id) {

        T ret = em.find(classType, id);

        return ret;
    }

    public T getByField(String field, Object value) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(classType);
        Root<T> root = cq.from(classType);
        cq.where(cb.equal(root.get(field), cb.literal(value)));
        cq.select(root);
        T ret = (T) em.createQuery(cq).getSingleResult();
        return ret;
    }

    public T save(T item) throws VOWrongDataException {
        IVoAuditable aud = (IVoAuditable) item;
        if (aud.getId() != null) {
            item = em.merge(item);
        }

        VoUserSession.fillAudit((IVoAuditable) item,getLoggedUser());
        try {
            em.persist(item);
            em.flush();
        } catch (ConstraintViolationException cve) {
            throw new VOWrongDataException("Nie udało się zapisać:" + cve.getConstraintViolations().toString());
        } catch (Exception e) {
            throw new VOWrongDataException("Nie udało się zapisać:" + e.getMessage());
        }
        return item;
    }

    public String delete(T item) throws VOWrongDataException {
        try {
            item = em.merge(item);
            em.remove(item);
            em.flush();
        } catch (Exception e) {
            throw new VOWrongDataException("Nie udało się usunąć:" + e.getMessage());
        }
        return "OK";
    }

    public void addInstanceCodeCriteria(CriteriaBuilder cb, CriteriaQuery cq, Root r) {
        addInstanceCodeCriteria(cb, cq, r, (Predicate) null);
    }

    public void addInstanceCodeCriteria(CriteriaBuilder cb, CriteriaQuery cq, Root r, Predicate where) {
        List<Predicate> wheres = new ArrayList<Predicate>();
        if (where != null) {
            wheres.add(where);
        }
        addInstanceCodeCriterias(cb, cq, r, wheres);
    }

    public void addInstanceCodeCriterias(CriteriaBuilder cb, CriteriaQuery cq, Root r, List<Predicate> wheres)
    {
        
//        voSession = VOLookup.lookupVoUserSession();
//            User u = voSession.getLoggedUser(); 
//        User us = voSession.getLoggedUser();
        
        String instanceCode = getLoggedUser().getInstanceCode();
        Predicate pr = cb.equal(r.get("instanceCode"), cb.literal(instanceCode));
        // get where
        //Predicate where= cq.getRestriction();
        if (wheres == null || wheres.size() == 0) {
            cq.where(pr);

        } else {

           //List<Expression<Boolean>> expressions =  where.getExpressions();
            //Expression<Boolean> exp = expressions.get(0);
          //  cq.where(expressions.get(0));
            // where.getExpressions()
            // List<Exprwhere.getExpressions()
            wheres.add(pr);
            Predicate[] arr = wheres.toArray(new Predicate[1]);
            // cb.and( arr );
            cq.where(cb.and(arr));
        }

    }
    
}
