/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.company.api;

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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import pl.common.dao.GenericDao;
import pl.vo.common.VoUserSession;
import pl.vo.common.model.DictionaryValue;
import pl.vo.company.model.Company;
import pl.vo.exceptions.VOWrongDataException;
import pl.vo.exceptions.VoNoResultException;
import pl.vo.security.model.User;

/**
 *
 * @author Piotr
 */
@Stateless(name = "CompanysApi", mappedName = "CompanysApi")
@LocalBean
public class CompanysApi extends GenericDao< Company, Long> implements Serializable {

    @EJB
    VoUserSession voSession;

    public CompanysApi() {
        super(Company.class);
    }

    public List<Company> findAllCompanys()
    {
        List<Company> ret = findAll();
        return ret;
    }

    public Company getByNip(String nip) throws VoNoResultException {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Company> cq = cb.createQuery(Company.class);
        Root<Company> root = cq.from(Company.class);

        Predicate eq = cb.equal(root.get("nip"), cb.literal(nip));
        cq.where(eq);
        cq.select(root);
        try {
            Company ret = (Company) em.createQuery(cq).getSingleResult();
            return ret;
        } catch (NoResultException nre) {
            throw new VoNoResultException("Nie znaleziono firmy o nipie:" + nip);
        }

    }
    
    public Company getById(Long id) throws VoNoResultException {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Company> cq = cb.createQuery(Company.class);
        Root<Company> root = cq.from(Company.class);

        Predicate eq = cb.equal(root.get("id"), cb.literal(id));
        cq.where(eq);
        cq.select(root);
        try {
            Company ret = (Company) em.createQuery(cq).getSingleResult();
            return ret;
        } catch (NoResultException nre) {
            throw new VoNoResultException("Nie znaleziono firmy o id:" + id);
        }

    }

    public Company save(Company company, String username) throws VOWrongDataException {
        if (company.getId() != null) {
            company = em.merge(company);
        }

        VoUserSession.fillAudit(company,getLoggedUser());
        try {
            em.persist(company);
            em.flush();
        } catch (ConstraintViolationException cve) {
            throw new VOWrongDataException("Nie udało się zapisać firmy:" + cve.getConstraintViolations().toString());
        } catch (Exception e) {
            throw new VOWrongDataException("Nie udało się zapisać firmy:" + e.getMessage());
        }
        return company;
    }

}
