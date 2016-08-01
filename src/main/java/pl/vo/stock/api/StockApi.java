/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.stock.api;

import java.io.Serializable;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import pl.common.dao.GenericDao;
import pl.vo.common.AuditEntityBase;
import pl.vo.stock.model.Stock;

/**
 *
 * @author Piotr
 */
@Stateless(name="StockApi",mappedName = "StockApi")
@LocalBean
public class StockApi extends GenericDao<Stock, Long>{
    
    
    public StockApi() { 
        super(Stock.class);
    }
}
