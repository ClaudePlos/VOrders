/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.products.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import pl.vo.common.AuditEntityBase;
import pl.vo.company.model.Company;
import pl.vo.organisation.model.OrganisationUnit;

/**
 *
 * @author Piotr
 */
@Entity
@Table(name = "vo_units_products_suppliers")
@XmlRootElement
public class UnitsProductsSuppliers extends AuditEntityBase implements Serializable {

    @ManyToOne
    @JoinColumn(name = "unit_id")
    private OrganisationUnit unit;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Company supplier;

    @Column(name = "date_from")
    @Temporal(TemporalType.DATE)
    private Date dateFrom;

    @Column(name = "date_till")
    @Temporal(TemporalType.DATE)
    private Date dateTill;

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

    public Company getSupplier() {
        return supplier;
    }

    public void setSupplier(Company supplier) {
        this.supplier = supplier;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTill() {
        return dateTill;
    }

    public void setDateTill(Date dateTill) {
        this.dateTill = dateTill;
    }
    
    
    

}
