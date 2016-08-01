/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.products.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import pl.vo.common.AuditEntityBase;
import pl.vo.organisation.model.OrganisationUnit;

/**
 *
 * @author Piotr
 */
@Entity
@Table(name="vo_units_products")
@XmlRootElement
public class UnitsProducts extends AuditEntityBase implements Serializable 
{
    @ManyToOne
    @JoinColumn(name="unit_id")
    private OrganisationUnit unit; 
    
    @ManyToOne
    @JoinColumn(name="product_id")
    private Product product; 

    public OrganisationUnit getUnit() {
        return unit;
    }

    public void setUnit(OrganisationUnit unit) {
        this.unit = unit;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + Objects.hashCode(this.unit);
        hash = 23 * hash + Objects.hashCode(this.product);
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
        final UnitsProducts other = (UnitsProducts) obj;
        if (!Objects.equals(this.unit, other.unit)) {
            return false;
        }
        if (!Objects.equals(this.product, other.product)) {
            return false;
        }
        return true;
    }
    
    
    
    
}
