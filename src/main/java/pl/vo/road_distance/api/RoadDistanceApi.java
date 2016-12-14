/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.road_distance.api;

import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolationException;
import pl.common.dao.GenericDao;
import pl.vendi.ui.VOLookup;
import pl.vo.common.VoUserSession;
import pl.vo.company.api.CompanysApi;
import pl.vo.exceptions.VOWrongDataException;
import pl.vo.exceptions.VoNoResultException;
import pl.vo.road_distance.model.RoadDistance;

/**
 *
 * @author k.skowronski
 */

@Stateless(name = "RoadDistanceApi",mappedName = "RoadDistanceApi")
@LocalBean
public class RoadDistanceApi extends GenericDao<RoadDistance, Long> implements Serializable{
    
    
    @EJB
    VoUserSession voSession;
    
    @EJB
    CompanysApi apiCompanys;
    
    public RoadDistanceApi() {
        super(RoadDistance.class);
    }

    public List<RoadDistance> findAllRoadDistance()
    {
        List<RoadDistance> ret = findAll();
        return ret;
    }
    
    public List<RoadDistance> listByCompanyUnitId( Long companyUnitId )
    {
       CriteriaBuilder cb =  em.getCriteriaBuilder(); 
       CriteriaQuery<RoadDistance> cq = cb.createQuery(RoadDistance.class);
       Root<RoadDistance> root = cq.from( RoadDistance.class);
        
       Predicate eq = cb.equal(root.get("companyUnitsId"), cb.literal( companyUnitId ));
      
         addInstanceCodeCriteria(cb, cq, root, eq);
       
          cq.select(root);
        List<RoadDistance> ret = ( List<RoadDistance> ) em.createQuery( cq).getResultList(); 
        
        for ( RoadDistance rd : ret )
        {
            rd.setSupplierName( apiCompanys.getById( rd.getCompanyId() ).getName() );
        }
        
        return ret; 
     }
    
    
    public RoadDistance getById(Long id) throws VoNoResultException {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RoadDistance> cq = cb.createQuery(RoadDistance.class);
        Root<RoadDistance> root = cq.from(RoadDistance.class);

        Predicate eq = cb.equal(root.get("id"), cb.literal(id));
        cq.where(eq);
        cq.select(root);
        try {
            RoadDistance ret = (RoadDistance) em.createQuery(cq).getSingleResult();
            return ret;
        } catch (NoResultException nre) {
            throw new VoNoResultException("Nie znaleziono distance o id:" + id);
        }

    }
    
    
    public RoadDistance getByCmpUnitIdAndCmpId(Long idCompanyUnits, Long idCompany) throws VoNoResultException {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RoadDistance> cq = cb.createQuery(RoadDistance.class);
        Root<RoadDistance> root = cq.from(RoadDistance.class);

        Predicate eq = cb.and(cb.equal(root.get("companyUnitsId"), cb.literal(idCompanyUnits)),
                              cb.equal(root.get("companyId"), cb.literal(idCompany)));
        cq.where(eq);
        cq.select(root);
        try {
            RoadDistance ret = (RoadDistance) em.createQuery(cq).getSingleResult();
            return ret;
        } catch (NoResultException nre) {
            throw new VoNoResultException("Nie znaleziono distance o id:" + idCompanyUnits);
        }

    }
    
    
    
    public RoadDistance save(RoadDistance distance, String username) throws VOWrongDataException {
        if (distance.getId() != null) {
            distance = em.merge(distance);
        }

        //VoUserSession.fillAudit(distance,getLoggedUser());
        try {
            em.persist(distance);
            em.flush();
        } catch (ConstraintViolationException cve) {
            throw new VOWrongDataException("Nie udało się zapisać distance:" + cve.getConstraintViolations().toString());
        } catch (Exception e) {
            throw new VOWrongDataException("Nie udało się zapisać distance:" + e.getMessage());
        }
        
        distance.setSupplierName( apiCompanys.getById( distance.getCompanyId() ).getName() );
        
        return distance;
    }
    
    
}
