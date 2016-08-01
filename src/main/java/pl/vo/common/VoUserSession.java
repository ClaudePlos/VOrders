/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.common;

import java.io.Serializable;
import java.security.Principal;
import java.util.Date;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.transaction.TransactionSynchronizationRegistry;
import pl.vo.exceptions.VoNoResultException;
import pl.vo.security.api.UsersApi;
import pl.vo.security.model.User;

/**
 *
 * @author Piotr
 */
@SessionScoped
@Stateful(mappedName = "VoUserSession",name = "VoUserSession")
//@LocalBean
public class VoUserSession implements Serializable
{
    @Resource
    private SessionContext sessionContext;
    
    @EJB
    UsersApi usersApi; 

    private User loggedUser; 
    
     @Resource
    protected TransactionSynchronizationRegistry transactionRegistry;
    
    
    public String getLoggedUsername(){ 
        
        Principal pr = sessionContext.getCallerPrincipal();
        if ( pr == null)
            throw new RuntimeException("Użytkownik nie zalogowany");
        if ( pr.getName().equals("ANONYMOUS"))
            throw new RuntimeException("Użytkownik nie zalogowany");
        
        
      return pr.getName();
    }
    
    public User getLoggedUser () { 
        
        User trUser = (User) transactionRegistry.getResource("VOUSER");
        if ( trUser != null)
            return trUser; 
        
        if ( loggedUser == null ){
            String userName = getLoggedUsername();
            try {
             loggedUser = usersApi.getUserByName( userName ) ;
            }
            catch( VoNoResultException nre )
            {
                throw new RuntimeException("Nie udało się zalogować" + nre.getMessage());
            }
        }
        return loggedUser; 
    }
    
    
    public void setLoggedUser( User user )
    {
        this.loggedUser = user; 
        transactionRegistry.putResource("VOUSER", user);
    }
    
    public static void fillAudit( IVoAuditable obj, User user )
    {
        if ( obj.getId() == null )
        {
            obj.setCreatedAt( new Date());
            obj.setCreatedByName( user.getUsername() );
            obj.setInstanceCode( user.getInstanceCode() );
//            obj.setCreatedBy( user );
        }
        else {
            obj.setModifiedAt( new Date());
            obj.setModifiedByName( user.getUsername() );
       //     obj.setModifiedBy( user );
        }
    }
    public void fillAuditData( IVoAuditable obj) 
    {
        User user = getLoggedUser();
        fillAudit(obj, user);
        
    }
    
    
    
}
