/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.security.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import pl.vo.VOConsts;
import pl.vo.common.AuditEntityBase;
import pl.vo.common.IVoAuditable;
import pl.vo.company.model.Company;
import pl.vo.utils.BooleanToStringConverter;

/**
 *
 * @author Piotr
 */
@Entity
@Table(name = "vo_users")
@XmlRootElement
public class User extends AuditEntityBase implements Serializable, IVoAuditable {

    @NotNull
    @Size(max = 100)
    private String username;

    @NotNull
    @Size(max = 100)
    private String password;

    @Convert(converter = BooleanToStringConverter.class)
    @Column(name = "active")
    private Boolean active;

    @NotNull
    @Size(max = 100)
    private String firstName;

    @NotNull
    @Size(max = 100)
    private String lastName;

    @NotNull
    private String type;
    
    @Size(max=100)
    private String remoteToken ; 

    @Column(name="login_token")
    private String login_token; 
  

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UsersRoles> roles = new HashSet<UsersRoles>();

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UsersCompanyUnits> units = new HashSet<UsersCompanyUnits>();

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * super.hashCode();
        hash = 67 * hash + Objects.hashCode(this.username);
        hash = 67 * hash + Objects.hashCode(this.password);
        hash = 67 * hash + Objects.hashCode(this.active);
        hash = 67 * hash + Objects.hashCode(this.firstName);
        hash = 67 * hash + Objects.hashCode(this.lastName);
        hash = 67 * hash + Objects.hashCode(this.type);
        hash = 67 * hash + Objects.hashCode(this.company);
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
        final User other = (User) obj;
        if (!Objects.equals(this.username, other.username)) {
            return false;
        }
        if (!Objects.equals(this.password, other.password)) {
            return false;
        }
        if (!Objects.equals(this.active, other.active)) {
            return false;
        }
        if (!Objects.equals(this.firstName, other.firstName)) {
            return false;
        }
        if (!Objects.equals(this.lastName, other.lastName)) {
            return false;
        }
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        if (!Objects.equals(this.company, other.company)) {
            return false;
        }
        return true;
    }

   
    
    

    public Set<UsersRoles> getRoles() {
        return roles;
    }

    public void setRoles(Set<UsersRoles> roles) {
        this.roles = roles;
    }

    public Set<UsersCompanyUnits> getUnits() {
        return units;
    }

    public void setUnits(Set<UsersCompanyUnits> units) {
        this.units = units;
    }

    public boolean hasRole(String roleCode) {
        for (UsersRoles role : roles) {
            if (role.getRole() != null) {
                if (role.getRole().getValue().equals(roleCode)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isExternal() {
        return type.equals(VOConsts.USER_TYPE_EXTERNAL);
    }

    public boolean isInternal() {
        return type.equals(VOConsts.USER_TYPE_INTERNAL);
    }

    public String getRemoteToken() {
        return remoteToken;
    }

    public void setRemoteToken(String remoteToken) {
        this.remoteToken = remoteToken;
    }

    public String getLogin_token() {
        return login_token;
    }

    public void setLogin_token(String login_token) {
        this.login_token = login_token;
    }
    
    
}
