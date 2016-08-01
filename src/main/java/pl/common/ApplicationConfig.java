/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.common;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import pl.common.dao.GenericResource;
import pl.vo.printing.PrintService;
import pl.vo.rest.VOrdersRest;
import pl.vo.rest.VPricatRest;
import pl.vo.security.api.UsersApi;

/**
 *
 * @author Piotr
 */
@ApplicationPath("/resources")
public class ApplicationConfig extends Application {
    public Set<Class<?>> getClasses()
    {
        Set<Class<?>> resources = new java.util.HashSet<Class<?>>();
        resources.add( PrintService.class  ); 
//        resources.add(UsersApi.class);
        resources.add( VOrdersRest.class) ;
//        resources.add(VPricatRest.class) ;
        return resources;
    }
}
