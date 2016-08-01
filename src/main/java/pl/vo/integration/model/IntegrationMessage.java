/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.integration.model;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import pl.vo.common.AuditEntityBase;
import pl.vo.company.model.Company;

/**
 *
 * @author Piotr
 */
@Entity
@Table(name="vo_integration_messages")
public class IntegrationMessage extends AuditEntityBase
{
    @ManyToOne()
    @JoinColumn(name="receiver_id")
    private Company cmpReceiver; 
    
    @ManyToOne()
    @JoinColumn(name="sender_id")
    private Company cmpSender;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(columnDefinition = "Date")
    private Date dateSend; 
    
    @Column(name="message_type")
    private String messageType; 
    
    
    private String message;
   
    @Column(name="remote_address")
    private String remoteAddres;
    
    private String status; 

    public Company getCmpReceiver() {
        return cmpReceiver;
    }

    public void setCmpReceiver(Company cmpReceiver) {
        this.cmpReceiver = cmpReceiver;
    }

    public Company getCmpSender() {
        return cmpSender;
    }

    public void setCmpSender(Company cmpSender) {
        this.cmpSender = cmpSender;
    }

    public Date getDateSend() {
        return dateSend;
    }

    public void setDateSend(Date dateSend) {
        this.dateSend = dateSend;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRemoteAddres() {
        return remoteAddres;
    }

    public void setRemoteAddres(String remoteAddres) {
        this.remoteAddres = remoteAddres;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    
    
}
