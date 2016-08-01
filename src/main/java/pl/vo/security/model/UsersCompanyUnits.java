/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.security.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import pl.vo.common.AuditEntityBase;
import pl.vo.common.model.DictionaryValue;
import pl.vo.organisation.model.OrganisationUnit;

/**
 *
 * @author Piotr
 */
@Entity
@Table(name="vo_users_company_units")
@XmlRootElement
public class UsersCompanyUnits extends AuditEntityBase implements Serializable {
    
     @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; 
    
    @ManyToOne
    @JoinColumn(name="company_unit_id")
    private OrganisationUnit companyUnit;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public OrganisationUnit getCompanyUnit() {
        return companyUnit;
    }

    public void setCompanyUnit(OrganisationUnit companyUnit) {
        this.companyUnit = companyUnit;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.user);
        hash = 79 * hash + Objects.hashCode(this.companyUnit);
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
        final UsersCompanyUnits other = (UsersCompanyUnits) obj;
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        if (!Objects.equals(this.companyUnit, other.companyUnit)) {
            return false;
        }
        return true;
    }

    
    
}
