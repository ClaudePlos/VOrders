/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.logging.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.Table;
import pl.vo.common.AuditEntityBase;

/**
 *
 * @author Piotr
 */
@Entity
@Table(name="vo_logs")
public class VOLog extends AuditEntityBase implements Serializable
{
    
    private String module;
    
    private String operation;

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
    
    
    
}
