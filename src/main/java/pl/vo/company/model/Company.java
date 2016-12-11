/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.company.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import pl.vo.common.AuditEntityBase;
import pl.vo.utils.BooleanToStringConverter;

/**
 *
 * @author Piotr
 */
@Entity
@Table(name = "vo_company")
public class Company extends AuditEntityBase implements Serializable {

    @NotNull
    private String name;

    @NotNull
    private String abbr;

    private String externalId;

    @NotNull
    @Size(max = 10, min = 10)
    private String nip;

    private String city;

    private String address;

    private String postCode;
    
    @Id
    @Column(name="id")
    private Long id; 
    
    // integration parameters
    @Size(max =50)
    @Column(name="integration_type")
    private String integrationType; 
    
    @Size(max =500)
    @Column(name="integration_url")
    private String integrationUrl;
    
    
    @Size(max =50)
    @Column(name="integration_transport")
    private String integrationTransport; 
    
    @Size(max=100)
    @Column(name="integration_secret_token")
    private String integrationSecretToken; 
    
    //integration flags
    @Convert(converter = BooleanToStringConverter.class)
    @Column(name="i_check_availability")
    private Boolean iCheckAvailability = false; 
    
    @Convert(converter = BooleanToStringConverter.class)
    @Column(name="i_send_order")
    private Boolean iSendOrder =false; 
    
  

    public Boolean getiCheckAvailability() {
        return iCheckAvailability;
    }

    public void setiCheckAvailability(Boolean iCheckAvailability) {
        this.iCheckAvailability = iCheckAvailability;
    }

    public Boolean getiSendOrder() {
        return iSendOrder;
    }

    public void setiSendOrder(Boolean iSendOrder) {
        this.iSendOrder = iSendOrder;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

  

    
    
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

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.name);
        hash = 41 * hash + Objects.hashCode(this.abbr);
        hash = 41 * hash + Objects.hashCode(this.externalId);
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
        final Company other = (Company) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.abbr, other.abbr)) {
            return false;
        }
        if (!Objects.equals(this.externalId, other.externalId)) {
            return false;
        }
        return true;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getIntegrationType() {
        return integrationType;
    }

    public void setIntegrationType(String integrationType) {
        this.integrationType = integrationType;
    }

    public String getIntegrationUrl() {
        return integrationUrl;
    }

    public void setIntegrationUrl(String integrationUrl) {
        this.integrationUrl = integrationUrl;
    }

    public String getIntegrationTransport() {
        return integrationTransport;
    }

    public void setIntegrationTransport(String integrationTransport) {
        this.integrationTransport = integrationTransport;
    }

    public String getIntegrationSecretToken() {
        return integrationSecretToken;
    }

    public void setIntegrationSecretToken(String integrationSecretToken) {
        this.integrationSecretToken = integrationSecretToken;
    }

  
    
    

}
