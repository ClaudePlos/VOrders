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

/**
 *
 * @author Piotr
 */
@Entity
@Table(name="vo_users_roles")
@XmlRootElement
public class UsersRoles extends AuditEntityBase implements Serializable
{
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; 
    
    @ManyToOne
    @JoinColumn(name="role_id")
    private DictionaryValue role;
    
    private String username; 
    
    private String rolename; 

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public DictionaryValue getRole() {
        return role;
    }

    public void setRole(DictionaryValue role) {
        this.role = role;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.user);
        hash = 67 * hash + Objects.hashCode(this.role);
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
        final UsersRoles other = (UsersRoles) obj;
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        if (!Objects.equals(this.role, other.role)) {
            return false;
        }
        return true;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }
   
    
    
}
