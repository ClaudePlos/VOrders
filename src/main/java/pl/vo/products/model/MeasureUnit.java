/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.products.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import pl.vo.common.AuditEntityBase;

/**
 *
 * @author Piotr
 */
@Entity
@Table(name="vo_measure_unit")
@XmlRootElement
public class MeasureUnit extends AuditEntityBase implements Serializable{
    
    
    @NotNull
    @Size(max=50)
    private String name;

    @NotNull
    @Size(max=10)
    private String abbr; 
    
    @JoinColumn(name="base_unit_id")
    @ManyToOne
    private MeasureUnit baseUnit;
    
    @Column(name="base_unit_count")
    private BigDecimal baseUnitCount; 

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    public MeasureUnit getBaseUnit() {
        return baseUnit;
    }

    public void setBaseUnit(MeasureUnit baseUnit) {
        this.baseUnit = baseUnit;
    }

    public BigDecimal getBaseUnitCount() {
        return baseUnitCount;
    }

    public void setBaseUnitCount(BigDecimal baseUnitCount) {
        this.baseUnitCount = baseUnitCount;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.name);
        hash = 79 * hash + Objects.hashCode(this.abbr);
        hash = 79 * hash + Objects.hashCode(this.baseUnit);
        hash = 79 * hash + Objects.hashCode(this.baseUnitCount);
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
        final MeasureUnit other = (MeasureUnit) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.abbr, other.abbr)) {
            return false;
        }
        if (!Objects.equals(this.baseUnit, other.baseUnit)) {
            return false;
        }
        if (!Objects.equals(this.baseUnitCount, other.baseUnitCount)) {
            return false;
        }
        return true;
    }
    
    

}
