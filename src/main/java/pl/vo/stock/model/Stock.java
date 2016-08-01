/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.stock.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import pl.vo.common.AuditEntityBase;
import pl.vo.products.model.Product;
import pl.vo.utils.BooleanToStringConverter;

/**
 *
 * @author Piotr
 */
@Entity
@Table(name="vo_stock")
@XmlRootElement
public class Stock extends AuditEntityBase implements Serializable
{
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product; 
     
    @Column(name="actualisation_date",columnDefinition = "date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date actualisationDate;
    
    @Column(name="stock_count")
    private BigDecimal stockCount;
    
    @Column(name="unlimited")
    @Convert(converter = BooleanToStringConverter.class)
    private Boolean unlimited;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Date getActualisationDate() {
        return actualisationDate;
    }

    public void setActualisationDate(Date actualisationDate) {
        this.actualisationDate = actualisationDate;
    }

    public BigDecimal getStockCount() {
        return stockCount;
    }

    public void setStockCount(BigDecimal stockCount) {
        this.stockCount = stockCount;
    }

    public Boolean getUnlimited() {
        return unlimited;
    }

    public void setUnlimited(Boolean unlimited) {
        this.unlimited = unlimited;
    }

    @Override
    public int hashCode() {
        int hash = 7; 
        hash = 59 * super.hashCode();
        hash = 59 * hash + Objects.hashCode(this.product);
        hash = 59 * hash + Objects.hashCode(this.actualisationDate);
        hash = 59 * hash + Objects.hashCode(this.stockCount);
        hash = 59 * hash + Objects.hashCode(this.unlimited);
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
        final Stock other = (Stock) obj;
        if (!Objects.equals(this.product, other.product)) {
            return false;
        }
        if (!Objects.equals(this.actualisationDate, other.actualisationDate)) {
            return false;
        }
        if (!Objects.equals(this.stockCount, other.stockCount)) {
            return false;
        }
        if (!Objects.equals(this.unlimited, other.unlimited)) {
            return false;
        }
        return true;
    }
    
    
    
}
