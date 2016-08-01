/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.common;

import com.vaadin.data.util.BeanContainer;

/**
 *
 * @author Piotr
 */
public class CntContainerUtils {
 
    
    public static void replaceItemWithIdOrAdd(BeanContainer cnt, Long id , Object obj)
    {
          boolean eq = cnt.containsId( id );
            if ( eq ) {
                int idx = cnt.indexOfId( id );
                cnt.removeItem( id );
                cnt.addItemAt(idx, id,obj);
            }
            else
                cnt.addItem(id,obj ) ;
    }
}
