/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.common.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.XmlRootElement;
import pl.vo.common.AuditEntityBase;

/**
 *
 * @author Piotr
 */
@Entity
@Table(name = "vo_dictionary_value")
@XmlRootElement
public class DictionaryValue extends AuditEntityBase implements Serializable {

    @NotNull
    @Size(max = 100)
    @Column(name = "dictionary_code")
    private String dictionaryCode;

    @NotNull
    @Size(max = 100)
    private String value;

    @NotNull
    @Size(max = 100)
    private String description;

    @Column(name = "number_value")
    private BigDecimal numberValue;

    public String getDictionaryCode() {
        return dictionaryCode;
    }

    public DictionaryValue() {
    }

    public DictionaryValue(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public void setDictionaryCode(String dictionaryCode) {
        this.dictionaryCode = dictionaryCode;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.dictionaryCode);
        hash = 53 * hash + Objects.hashCode(this.value);
        hash = 53 * hash + Objects.hashCode(this.description);
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
        final DictionaryValue other = (DictionaryValue) obj;
        if (!Objects.equals(this.dictionaryCode, other.dictionaryCode)) {
            return false;
        }
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        return true;
    }

    public BigDecimal getNumberValue() {
        return numberValue;
    }

    public void setNumberValue(BigDecimal numberValue) {
        this.numberValue = numberValue;
    }

}
