/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.common;

import java.util.Date;

/**
 *
 * @author Piotr
 */
public interface IVoAuditable {

    Date getCreatedAt();

   
    String getCreatedByName();

    Long getId();

    Date getModifiedAt();

   
    String getModifiedByName();

    void setCreatedAt(Date createdAt);

    void setCreatedByName(String createdByName);

    void setId(Long id);

    void setModifiedAt(Date modifiedAt);

    void setModifiedByName(String modifiedByName);
    
    String getInstanceCode();
    void setInstanceCode(String val);
    
}
