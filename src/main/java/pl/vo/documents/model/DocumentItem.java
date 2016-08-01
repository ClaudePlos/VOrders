/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.documents.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import pl.vo.common.AuditEntityBase;
import pl.vo.products.model.Product;
import pl.vo.products.model.UnitsProductsSuppliers;

/**
 *
 * @author Piotr
 */
@Entity
@Table(name = "vo_document_items")
public class DocumentItem extends AuditEntityBase implements Serializable {

    @ManyToOne
    @JoinColumn(name = "document")
    @NotNull(message = "Dokument nie może być pusty")
    @JsonBackReference
    Document document;

    @ManyToOne
    @NotNull(message = "Towar nie może być pusty")
    Product product;

    // base amount - ordered, delivered etc
    @Column(name = "amount")
    private BigDecimal amount;

    // amount confirmed to delivery
    @Column(name = "amount_confirmed")
    private BigDecimal amountConfirmed;

    @ManyToOne
    @JoinColumn(name = "price_item_id")
    private DocumentItem priceItem;

    @ManyToOne
    @JoinColumn(name = "source_item_id")
    private DocumentItem sourceItem;

    @Column(name = "unit_price_net", columnDefinition = "number(10,2)")
    private BigDecimal unitPriceNet;

    @Column(name = "value_net", columnDefinition = "number(10,2)")
    private BigDecimal valueNet;

    @Column(name = "value_tax", columnDefinition = "number(10,2)")
    private BigDecimal valueTax;

    @Column(name = "value_brut", columnDefinition = "number(10,2)")
    private BigDecimal valueBrut;

    private String status;

    // obce id - zachowane przy imporcie zamowien
    @Column(name = "external_item_id")
    private String externalItemId;

    @ManyToOne
    @JoinColumn(name = "unit_product_supplier_id")
    private UnitsProductsSuppliers unitProductSupplier;

    // 
    @Transient
    private BigDecimal amountOnDpzs;

    @Transient
    private BigDecimal amountLeftToDelivery;

    public DocumentItem() {
    }

    public DocumentItem(Long id) {
        this.setId(id);
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public DocumentItem getPriceItem() {
        return priceItem;
    }

    public void setPriceItem(DocumentItem priceItem) {
        this.priceItem = priceItem;
    }

    public DocumentItem getSourceItem() {
        return sourceItem;
    }

    public void setSourceItem(DocumentItem sourceItem) {
        this.sourceItem = sourceItem;
    }

    public BigDecimal getUnitPriceNet() {
        return unitPriceNet;
    }

    public void setUnitPriceNet(BigDecimal unitPriceNet) {
        this.unitPriceNet = unitPriceNet;
    }

    public BigDecimal getValueNet() {
        return valueNet;
    }

    public void setValueNet(BigDecimal valueNet) {
        this.valueNet = valueNet;
    }

    public BigDecimal getValueTax() {
        return valueTax;
    }

    public void setValueTax(BigDecimal valueTax) {
        this.valueTax = valueTax;
    }

    public BigDecimal getValueBrut() {
        return valueBrut;
    }

    public void setValueBrut(BigDecimal valueBrut) {
        this.valueBrut = valueBrut;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + super.hashCode();
        hash = 37 * hash + Objects.hashCode(this.product);
        hash = 37 * hash + Objects.hashCode(this.amount);
        hash = 37 * hash + Objects.hashCode(this.priceItem);
        hash = 37 * hash + Objects.hashCode(this.unitPriceNet);
        hash = 37 * hash + Objects.hashCode(this.valueNet);
        hash = 37 * hash + Objects.hashCode(this.valueTax);
        hash = 37 * hash + Objects.hashCode(this.valueBrut);
        hash = 37 * hash + Objects.hashCode(this.status);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {

        if (!super.equals(obj)) {
            return false;
        }

        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DocumentItem other = (DocumentItem) obj;
        if (!Objects.equals(this.product, other.product)) {
            return false;
        }
        if (!Objects.equals(this.amount, other.amount)) {
            return false;
        }
        if (!Objects.equals(this.priceItem, other.priceItem)) {
            return false;
        }

        if (!Objects.equals(this.unitPriceNet, other.unitPriceNet)) {
            return false;
        }
        if (!Objects.equals(this.valueNet, other.valueNet)) {
            return false;
        }
        if (!Objects.equals(this.valueTax, other.valueTax)) {
            return false;
        }
        if (!Objects.equals(this.valueBrut, other.valueBrut)) {
            return false;
        }
        if (!Objects.equals(this.status, other.status)) {
            return false;
        }
        return true;
    }

    public UnitsProductsSuppliers getUnitProductSupplier() {
        return unitProductSupplier;
    }

    public void setUnitProductSupplier(UnitsProductsSuppliers unitProductSupplier) {
        this.unitProductSupplier = unitProductSupplier;
    }

    public BigDecimal getAmountConfirmed() {
        return amountConfirmed;
    }

    public void setAmountConfirmed(BigDecimal amountConfirmed) {
        this.amountConfirmed = amountConfirmed;
    }

    public BigDecimal getAmountOnDpzs() {
        return amountOnDpzs;
    }

    public void setAmountOnDpzs(BigDecimal amountOnDpzs) {
        this.amountOnDpzs = amountOnDpzs;
    }

    public BigDecimal getAmountLeftToDelivery() {
        return amountLeftToDelivery;
    }

    public void setAmountLeftToDelivery(BigDecimal amountLeftToDelivery) {
        this.amountLeftToDelivery = amountLeftToDelivery;
    }

    public String getExternalItemId() {
        return externalItemId;
    }

    public void setExternalItemId(String externalItemId) {
        this.externalItemId = externalItemId;
    }

}
