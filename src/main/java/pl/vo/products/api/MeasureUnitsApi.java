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
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.validation.ConstraintViolationException;
import pl.common.dao.GenericDao;
import pl.vo.common.VoUserSession;
import pl.vo.exceptions.VOWrongDataException;
import pl.vo.products.model.MeasureUnit;
import pl.vo.products.model.Product;

/**
 *
 * @author Piotr
 */
@Stateful(name = "MeasureUnitsApi",mappedName = "MeasureUnitsApi")
@LocalBean
public class MeasureUnitsApi extends GenericDao<MeasureUnit, Long>  implements Serializable 
{
    
   
            
     public MeasureUnitsApi()
     {
            super(MeasureUnit.class) ;
     }
   
     public List<MeasureUnit> findAll()
     { 
       List<MeasureUnit> ret = super.findAllNoCodeInstance();
       return ret; 
     }
     
    
     public MeasureUnit save( MeasureUnit item) throws VOWrongDataException
     {
         return super.save( item ) ;
     }
     
     
}

