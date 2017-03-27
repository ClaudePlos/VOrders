/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.documents.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import pl.vo.VOConsts;
import pl.vo.common.AuditEntityBase;
import pl.vo.company.model.Company;
import pl.vo.organisation.model.OrganisationUnit;
import pl.vo.products.model.Product;
import pl.vo.utils.VOUtils;

/**
 *
 * @author Piotr
 */
@Entity
@Table(name = "vo_documents")
@XmlRootElement
public class Document extends AuditEntityBase implements Serializable {

    @NotNull(message = "Typ dokumentu nie może być pusty")
    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @NotNull(message = "Status dokumentu nie może być pusty")
    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "own_number", nullable = true, length = 100)
    private String ownNumber;

    @Size(max = 50)
    @Column(name = "external_number")
    private String externalNumber;

    @NotNull(message = "Data dokumentu nie może być pusta")
    @Column(name = "date_operation", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date dateOperation;

    @Column(name = "date_issue")
    @Temporal(TemporalType.DATE)
    private Date dateIssue;

    @Column(name = "date_send")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateSend;

    @Column(name = "date_delivery")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateDelivery;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Company client;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Company supplier;

    @ManyToOne
    @JoinColumn(name = "company_unit_id")
    private OrganisationUnit companyUnit;

    @Column(name = "valid_from")
    @Temporal(TemporalType.DATE)
    private Date validFrom;

    @Column(name = "valid_till")
    @Temporal(TemporalType.DATE)
    private Date validTill;

    @ManyToOne
    @JoinColumn(name = "source_document_id")
    @JsonIgnore
    private Document sourceDocument;

    @Column(name = "value_net", columnDefinition = "number(10,2)")
    private BigDecimal valueNet;

    @Column(name = "value_tax", columnDefinition = "number(10,2)")
    private BigDecimal valueTax;

    @Column(name = "value_brut", columnDefinition = "number(10,2)")
    private BigDecimal valueBrut;
    
    @Column(name = "discount", columnDefinition = "number(10,2)")
    private BigDecimal discount;

    @OneToMany(mappedBy = "document", cascade = {CascadeType.ALL, CascadeType.REMOVE}, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonManagedReference
    @JsonIgnore
    private Set<DocumentItem> items = new HashSet<DocumentItem>();

    @Transient
    @JsonIgnore
    List<Document> childDocuments;

    @Transient
    boolean modified = false;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOwnNumber() {
        return ownNumber;
    }

    public void setOwnNumber(String ownNumber) {
        this.ownNumber = ownNumber;
    }

    public String getExternalNumber() {
        return externalNumber;
    }

    public void setExternalNumber(String externalNumber) {
        this.externalNumber = externalNumber;
    }

    public Date getDateOperation() {
        return dateOperation;
    }

    public void setDateOperation(Date dateOperation) {
        this.dateOperation = dateOperation;
    }

    public Date getDateIssue() {
        return dateIssue;
    }

    public void setDateIssue(Date dateIssue) {
        this.dateIssue = dateIssue;
    }

    public Date getDateSend() {
        return dateSend;
    }

    public void setDateSend(Date dateSend) {
        this.dateSend = dateSend;
    }

    public Date getDateDelivery() {
        return dateDelivery;
    }

    public void setDateDelivery(Date dateDelivery) {
        this.dateDelivery = dateDelivery;
    }

    public Company getClient() {
        return client;
    }

    public void setClient(Company client) {
        this.client = client;
    }

    public Company getSupplier() {
        return supplier;
    }

    public void setSupplier(Company supplier) {
        this.supplier = supplier;
    }

    public OrganisationUnit getCompanyUnit() {
        return companyUnit;
    }

    public void setCompanyUnit(OrganisationUnit companyUnit) {
        this.companyUnit = companyUnit;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidTill() {
        return validTill;
    }

    public void setValidTill(Date validTill) {
        this.validTill = validTill;
    }

    public Document getSourceDocument() {
        return sourceDocument;
    }

    public void setSourceDocument(Document sourceDocument) {
        this.sourceDocument = sourceDocument;
    }

    public Set<DocumentItem> getItems() {
        return items;
    }

    public void setItems(Set<DocumentItem> items) {
        this.items = items;
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

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }
    
    public List<Document> getChildDocuments() {
        return childDocuments;
    }

    public void setChildDocuments(List<Document> childDocuments) {
        this.childDocuments = childDocuments;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(this.type);
        hash = 83 * hash + Objects.hashCode(this.status);
        hash = 83 * hash + Objects.hashCode(this.ownNumber);
        hash = 83 * hash + Objects.hashCode(this.externalNumber);
        hash = 83 * hash + Objects.hashCode(this.dateOperation);
        hash = 83 * hash + Objects.hashCode(this.dateIssue);
        hash = 83 * hash + Objects.hashCode(this.dateSend);
        hash = 83 * hash + Objects.hashCode(this.dateDelivery);
        hash = 83 * hash + Objects.hashCode(this.client);
        hash = 83 * hash + Objects.hashCode(this.supplier);
        hash = 83 * hash + Objects.hashCode(this.companyUnit);
        hash = 83 * hash + Objects.hashCode(this.validFrom);
        hash = 83 * hash + Objects.hashCode(this.validTill);
        hash = 83 * hash + Objects.hashCode(this.sourceDocument);
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
        final Document other = (Document) obj;
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        if (!Objects.equals(this.status, other.status)) {
            return false;
        }
        if (!Objects.equals(this.ownNumber, other.ownNumber)) {
            return false;
        }
        if (!Objects.equals(this.externalNumber, other.externalNumber)) {
            return false;
        }
        if (!Objects.equals(this.dateOperation, other.dateOperation)) {
            return false;
        }
        if (!Objects.equals(this.dateIssue, other.dateIssue)) {
            return false;
        }
        if (!Objects.equals(this.dateSend, other.dateSend)) {
            return false;
        }
        if (!Objects.equals(this.dateDelivery, other.dateDelivery)) {
            return false;
        }
        if (!Objects.equals(this.client, other.client)) {
            return false;
        }
        if (!Objects.equals(this.supplier, other.supplier)) {
            return false;
        }
        if (!Objects.equals(this.companyUnit, other.companyUnit)) {
            return false;
        }
        if (!Objects.equals(this.validFrom, other.validFrom)) {
            return false;
        }
        if (!Objects.equals(this.validTill, other.validTill)) {
            return false;
        }
        if (!Objects.equals(this.sourceDocument, other.sourceDocument)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Document{" + "type=" + type + ", status=" + status + ", ownNumber=" + ownNumber + ", externalNumber=" + externalNumber + ", dateOperation=" + dateOperation + ", dateIssue=" + dateIssue + ", dateSend=" + dateSend + ", dateDelivery=" + dateDelivery + ", client=" + client + ", supplier=" + supplier + ", companyUnit=" + companyUnit + ", validFrom=" + validFrom + ", validTill=" + validTill + ", sourceDocument=" + sourceDocument + ", items=" + items + '}';
    }

    public String getDescription() {
        String ret = "";

        if (type.equals(VOConsts.DOC_TYPE_DPZ)) {
            ret += "WZ dostawcy ";
        } else if (type.equals(VOConsts.DOC_TYPE_ZWK)) {
            ret += "Zamówienie z obiektu";
        } else if (type.equals(VOConsts.DOC_TYPE_ZWD)) {
            ret += "Zamówienie do dostawcy";
        } else if (type.equals(VOConsts.DOC_TYPE_PZ)) {
            ret += "Dostawa PZ";
        }

        ret += ", ";

        if (dateOperation != null) {
            ret += " z dnia:" + VOUtils.formaDateYYYYMMDD(dateOperation) + ", ";
        }

        if (companyUnit != null) {
            ret += "obiekt: " + companyUnit.getName() + ", ";
        }

        if (supplier != null) {
            ret += "Dostawca: " + supplier.getAbbr() + ", ";
        }

        if (ownNumber != null) {
            ret += "Numer:" + ownNumber + ", ";
        }
        if (externalNumber != null) {
            ret += "Numer obcy:" + externalNumber + ", ";
        }

        return ret;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public boolean hasItemWithProduct(Product prod) {
        if (prod == null) {
            return false;
        }
        if (this.items == null) {
            return false;
        }
        for (DocumentItem item : items) {
            if (item.getProduct().equals(prod)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean dicountIsEmpty()
    {
        if ( this.discount == null )
            return true;
        return false;      
    }

}
