/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.common;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import pl.vo.security.model.User;

/**
 *
 * @author Piotr
 */
@MappedSuperclass
public class AuditEntityBase implements Serializable, IVoAuditable {

    
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_vo")
    @SequenceGenerator(name = "seq_vo", sequenceName = "seq_vo",allocationSize = 5)
    private Long id;

  //  @Version
    @Column(name = "version", nullable = true)
    private Integer version = 1;
    
   

//    @NotNull(message = "pole createdByName nie może być puste")
    @Size(max=100)
    @Column(name = "created_by_name")
    private String createdByName;

    @Temporal(TemporalType.TIMESTAMP)
//    @NotNull(message = "pole createdAt nie może być puste")
    @Column(name = "created_at",columnDefinition = "date")
    private Date createdAt;

 

    
    @Size(max=100)
    @Column(name = "modified_by_name")
    private String modifiedByName;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modified_at",columnDefinition = "date")
    private Date modifiedAt;

    
    //@NotNull
    @Column(name = "instanceCode")
    private String instanceCode;
    
   
    @Override
    public String getCreatedByName() {
        return createdByName;
    }

    @Override
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    @Override
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

 
    @Override
    public String getModifiedByName() {
        return modifiedByName;
    }

    @Override
    public void setModifiedByName(String modifiedByName) {
        this.modifiedByName = modifiedByName;
    }

    @Override
    public Date getModifiedAt() {
        return modifiedAt;
    }

    @Override
    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + Objects.hashCode(this.id);
      /*
        hash = 61 * hash + Objects.hashCode(this.createdByName);
        hash = 61 * hash + Objects.hashCode(this.createdAt);
        hash = 61 * hash + Objects.hashCode(this.modifiedByName);
        hash = 61 * hash + Objects.hashCode(this.modifiedAt);*/
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
        final AuditEntityBase other = (AuditEntityBase) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
      
      /*
        if (!Objects.equals(this.createdByName, other.createdByName)) {
            return false;
        }
        if (!Objects.equals(this.createdAt, other.createdAt)) {
            return false;
        }
       
        if (!Objects.equals(this.modifiedByName, other.modifiedByName)) {
            return false;
        }
        if (!Objects.equals(this.modifiedAt, other.modifiedAt)) {
            return false;
        }*/
        return true;
    }

    public String getInstanceCode() {
        return instanceCode;
    }

    public void setInstanceCode(String instanceCode) {
        this.instanceCode = instanceCode;
    }

    
    
    
    
}
