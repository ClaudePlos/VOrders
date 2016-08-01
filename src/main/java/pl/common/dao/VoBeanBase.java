/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.common.dao;

import javax.annotation.Resource;
import javax.transaction.TransactionSynchronizationRegistry;
import pl.vendi.ui.VendiOrdersUI;
import pl.vo.VOConsts;
import pl.vo.security.model.User;

/**
 *
 * @author Piotr
 */
public class VoBeanBase {
    
      @Resource
    protected TransactionSynchronizationRegistry registry;

    public String getLoggedUsername(){ 
       String user = (String)
         registry.getResource(VOConsts.REGISTRY_USERNAME );
       
       if ( user == null)
           throw new RuntimeException("GD165 - nie zalogowany");
       
       return user; 
    }
    
    public User getLoggedUser(){
        
        Object regUser = registry.getResource(VOConsts.REGISTRY_USER);; 
        if ( regUser != null ){
            return (User) regUser;
        }
        
        User user = VendiOrdersUI.getLoggedUser();
        
        if ( user == null)
           throw new RuntimeException("GD165 - nie zalogowany");
//       
       return user; 
//        Object o = registry.getResource(VOConsts.REGISTRY_USER );;
//        Object o2 = registry.getResource(VOConsts.REGISTRY_USERNAME ); 
//        
//        User user = (User)
//         registry.getResource(VOConsts.REGISTRY_USER );
//       
//       
    }
}
