/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.products.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import pl.vo.common.AuditEntityBase;
import pl.vo.exceptions.VoNoResultException;

/**
 *
 * @author Piotr
 */
@Entity
@Table(name="vo_product")
@XmlRootElement
public class Product extends AuditEntityBase implements Serializable
{
    
    

    @NotNull
    @Size(max=100)
    private String abbr; 
    
     @NotNull
    @Size(max=500)
    private String name;
     
     
      
    @Size(max=50)
    private String indexNumber;
       
       
    @Size(max=50)
    @Column(name="external_code")
    private String externalCode;
    
     @Size(max=20)
    @Column(name="ean")
    private String ean;
     
     @NotNull
     @Column(name="tax_rate")
     private BigDecimal taxRate;
     
     @ManyToOne()
     @JoinColumn(name="measure_unit_id")
     @NotNull
     private MeasureUnit measureUnit;
     
     @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true,mappedBy = "product")
     private Set<ProductCmpCode> codes = new HashSet<ProductCmpCode>();
     
     @Transient
     private BigDecimal quantity; 

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIndex() {
        return indexNumber;
    }

    public void setIndex(String index) {
        this.indexNumber = index;
    }

    public String getExternalCode() {
        return externalCode;
    }

    public void setExternalCode(String externalCode) {
        this.externalCode = externalCode;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public MeasureUnit getMeasureUnit() {
        return measureUnit;
    }

    public void setMeasureUnit(MeasureUnit measureUnit) {
        this.measureUnit = measureUnit;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.abbr);
        hash = 53 * hash + Objects.hashCode(this.name);
        hash = 53 * hash + Objects.hashCode(this.indexNumber);
        hash = 53 * hash + Objects.hashCode(this.externalCode);
        hash = 53 * hash + Objects.hashCode(this.taxRate);
        hash = 53 * hash + Objects.hashCode(this.measureUnit);
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
        final Product other = (Product) obj;
        if (!Objects.equals(this.abbr, other.abbr)) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.indexNumber, other.indexNumber)) {
            return false;
        }
        if (!Objects.equals(this.externalCode, other.externalCode)) {
            return false;
        }
        if (!Objects.equals(this.taxRate, other.taxRate)) {
            return false;
        }
        if (!Objects.equals(this.measureUnit, other.measureUnit)) {
            return false;
        }
        return true;
    }

    public String getIndexNumber() {
        return indexNumber;
    }

    public void setIndexNumber(String indexNumber) {
        this.indexNumber = indexNumber;
    }

    public Set<ProductCmpCode> getCodes() {
        return codes;
    }

    public void setCodes(Set<ProductCmpCode> codes) {
        this.codes = codes;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
     
     
    public String getCodeForSupplier( Long suppl_id ) throws VoNoResultException
    {
        for ( ProductCmpCode pcc : codes){
            if ( pcc.getCmpId().equals(suppl_id))
                return pcc.getCode();
        }
        
        throw new VoNoResultException("Brak kodu dostawcy dla towaru: " + this.abbr + " i dostwcy id " + suppl_id);
    }
     
   
     
}
