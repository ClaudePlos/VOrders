/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.security.api;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import pl.common.dao.GenericDao;
import pl.vo.VOConsts;
import pl.vo.common.VoUserSession;
import pl.vo.exceptions.VOWrongDataException;
import pl.vo.exceptions.VoNoResultException;
import pl.vo.security.model.User;
import pl.vo.security.model.UsersCompanyUnits;
import pl.vo.security.model.UsersRoles;

/**
 *
 * @author Piotr
 */
@Stateless(name = "UsersApi", mappedName = "UsersApi")
@LocalBean
@TransactionAttribute(TransactionAttributeType.REQUIRED)
//@Path("/usersApi")
public class UsersApi extends GenericDao< User, Long> implements Serializable {

    @Resource
    private TransactionSynchronizationRegistry registry2;
          
   
    
    private static Map<String, User> usersCache = new HashMap<String, User>();

    public UsersApi() {
        super(User.class);

    }

    public void setContextUser(User user) {
        registry2.putResource(VOConsts.REGISTRY_USERNAME, user.getUsername());
//          User user = getByName(username);
        registry2.putResource(VOConsts.REGISTRY_USER, user);

        String us = (String) registry2.getResource(VOConsts.REGISTRY_USERNAME);

    }

    public String getUsername() {
        String us = (String) registry2.getResource(VOConsts.REGISTRY_USERNAME);
        return us;
    }

    @Path("/findAllUsers")
    @GET
    @Produces()
    public List<User> findAllUsers() {
        List<User> ret = findAllNoCodeInstance();
        return ret;
    }

    @Path("/save")
    @GET
    @Produces()
    public User save(User user, String username) throws VOWrongDataException {
        User loggedUser = usersApi.getByName(username);
        for (UsersRoles ur : user.getRoles()) {
            VoUserSession.fillAudit(ur, loggedUser);
        }
        for (UsersCompanyUnits ucu : user.getUnits()) {
            VoUserSession.fillAudit(ucu, loggedUser);
        }

        if (user.getId() != null) {
            user = em.merge(user);
        }

        VoUserSession.fillAudit(user, loggedUser);

        try {
            em.persist(user);
            em.flush();
        } catch (ConstraintViolationException cve) {
            throw new VOWrongDataException("Nie udało się zapisać użytkownika:" + cve.getConstraintViolations().toString());
        } catch (Exception e) {
            throw new VOWrongDataException("Nie udało się zapisać użytkownika:" + e.getMessage());
        }
        return user;
    }

    public User getByName(String username) {
        if (usersCache.containsKey(username)) {
            return usersCache.get(username);
        }

        try {
            User ret = getUserByName(username);
            usersCache.put(username, ret);
            return ret;
        } catch (VoNoResultException nre) {
            throw new RuntimeException("Cant find user: " + username);
        }
    }

    public User getUserByName(String username) throws VoNoResultException {
         // check cache

        try {
            User ret = (User) em.createQuery("select u from User u where u.username = :username").setParameter("username", username).getSingleResult();

            return ret;
        } catch (NoResultException nre) {
            throw new VoNoResultException("Nie znaleziono uzytkownika o nazwie:" + username);
        }
    }

    public User getUserByToken(String remoteToken) throws VoNoResultException {
        try {
            User ret = (User) em.createQuery("select u from User u where u.remoteToken = :remoteToken").setParameter("remoteToken", remoteToken).getSingleResult();
            return ret;
        } catch (NoResultException nre) {
            throw new VoNoResultException("Nie znaleziono uzytkownika o remoteToken:" + remoteToken);
        }
    }

}
