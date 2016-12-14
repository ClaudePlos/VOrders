/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.products.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import pl.vo.common.AuditEntityBase;

/**
 *
 * @author Piotr
 */
@Entity
@Table(name="vo_product_codes")
@XmlRootElement
public class ProductCmpCode extends AuditEntityBase implements Serializable
{
    
    @ManyToOne()
    @JoinColumn(name="product_id")
    Product product; 
    
    @Column(name="company_id")
    Long cmpId;
    
    @Column(name="code")
    String code;
    
    @Transient
    private Long distanceDelivery; 

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Long getCmpId() {
        return cmpId;
    }

    public void setCmpId(Long cmpId) {
        this.cmpId = cmpId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getDistanceDelivery() {
        return distanceDelivery;
    }

    public void setDistanceDelivery(Long distanceDelivery) {
        this.distanceDelivery = distanceDelivery;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.cmpId);
        hash = 97 * hash + Objects.hashCode(this.code);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ProductCmpCode other = (ProductCmpCode) obj;
        if (!Objects.equals(this.cmpId, other.cmpId)) {
            return false;
        }
        if (!Objects.equals(this.code, other.code)) {
            return false;
        }
        return true;
    }
    
    
    
    
    
    
}
