/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.road_distance.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import pl.vo.common.AuditEntityBase;


/**
 *
 * @author k.skowronski
 */
@Entity
@Table(name="vo_road_distance")
@XmlRootElement
public class RoadDistance extends AuditEntityBase implements Serializable{
 
 
    
    @Column(name="company_units_id")
    private Long companyUnitsId; 
    
    @Column(name="company_id")
    private Long companyId; 
    
    @Column(name="distance")
    private Long distance; 
    
    @Transient
    private String supplierName; 


    public Long getCompanyUnitsId() {
        return companyUnitsId;
    }

    public void setCompanyUnitsId(Long companyUnitsId) {
        this.companyUnitsId = companyUnitsId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getDistance() {
        return distance;
    }

    public void setDistance(Long distance) {
        this.distance = distance;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
    
    
    
}
