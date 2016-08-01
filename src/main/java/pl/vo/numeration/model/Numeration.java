/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.numeration.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Piotr
 */
@Entity
@Table(name = "vo_numeration")
@XmlRootElement
public class Numeration implements Serializable
{
    
     @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "seq_vo2")
    @SequenceGenerator(name = "seq_vo2", sequenceName = "seq_vo2", allocationSize = 5)
    private Long id;
     
     @NotNull(message = "Typ dokumentu serii numeracji nie może być pusty")
     @Size(max=50)
    private String docType; 
    
     @Temporal(TemporalType.DATE)
     private Date seriesDate; 
     
     @NotNull(message="Ostatni numer nie może być pusty")
     private Long lastNumber; 

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public Date getSeriesDate() {
        return seriesDate;
    }

    public void setSeriesDate(Date seriesDate) {
        this.seriesDate = seriesDate;
    }

    public Long getLastNumber() {
        return lastNumber;
    }

    public void setLastNumber(Long lastNumber) {
        this.lastNumber = lastNumber;
    }
     
     
}
