/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.integration;

import com.google.gson.Gson;
import com.vaadin.ui.Notification;
import java.io.Serializable;
import java.net.URI;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import pl.vo.VOConsts;
import pl.vo.common.VoUserSession;
import pl.vo.common.api.DictionaryApi;
import pl.vo.common.model.DictionaryValue;
import pl.vo.company.model.Company;
import pl.vo.documents.model.Document;
import pl.vo.exceptions.VOWrongDataException;
import pl.vo.exceptions.VoNoResultException;
import pl.vo.integration.model.IntegrationMessage;
import pl.vo.rest.VoRestResponse;

/**
 * Klasa do wysylania komunikatow integracyjnych
 *
 * @author Piotr
 */
@Stateless(name = "VOIntegrationSender", mappedName = "VOIntegrationSender")
@LocalBean
public class VOIntegrationSender implements Serializable {

    //  @Resource(name="mail/voMail")
    private Session voMailSession;

    @PersistenceContext(name = "pu")
    protected EntityManager em;
    
    @EJB
            private DictionaryApi dictionaryAi; 
            
        
    

    Logger logger = Logger.getLogger(VOIntegrationSender.class.getName());

    // sends a message to a clinet
    public void sendMessage(String topic, String message, String messageType, Document doc, Company cmp) throws VOWrongDataException {
        logger.fine("sendMessage");
        IntegrationMessage msg = new IntegrationMessage();
        msg.setCmpReceiver(doc.getSupplier());
        msg.setCmpSender(doc.getClient());
        msg.setDateSend(new Date());
        msg.setMessage(message);
        msg.setMessageType(messageType);
        msg.setRemoteAddres(cmp.getIntegrationUrl());

        if (cmp.getIntegrationUrl() == null) {
            throw new VOWrongDataException("Błąd VOIS-44 - firma nie ma adresu integracji");
        }

        if (cmp.getIntegrationTransport() == null) {
            throw new VOWrongDataException("Błąd VOIS-48- firma nie ma podanego transportu integracji");
        }

        if (cmp.getIntegrationTransport().equals(VOConsts.INTEGRATION_TRANSPORT_REST)) {
            VoRestResponse resp =  sendRest(message, cmp);
            if ( resp != null ){
               
            }
        } else if (cmp.getIntegrationTransport().equals(VOConsts.INTEGRATION_TRANSPORT_EMAIL))
        {
            sendEmail(topic, message, cmp);
        } else {
            throw new VOWrongDataException("Błąd VOIS-57 nie obsługiwany typ tranportu");
        }

    }

    private VoRestResponse sendRest(String message, Company cmp) throws VOWrongDataException
    {
 Gson gson = new Gson();
        Client client = ClientBuilder.newClient();
        URI uri = UriBuilder.fromUri(cmp.getIntegrationUrl()).build();
        WebTarget target = client.target(uri);

        Form form = new Form();
        form.param("message", message);
        com.google.gson.JsonObject jsonob = new com.google.gson.JsonObject();
        jsonob.addProperty("message", message);

      
       // works Response res = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(jsonob.toString(), MediaType.APPLICATION_JSON));
        Response res = target
                .request(MediaType.APPLICATION_JSON)
                .header("voToken",cmp.getIntegrationSecretToken())
                .post(Entity.entity(message, MediaType.TEXT_PLAIN));

        String jsonLine = res.readEntity(String.class);

        if (res.getStatus() == 200) {
            // ok 
            try {
            VoRestResponse resp = gson.fromJson(jsonLine, VoRestResponse.class) ;
              Notification.show("Wysłane poprawnie! Numer dokumentu u dostawcy: " + resp.getDocNumber() 
                      + " dokId: " + resp.getDocId() , Notification.Type.WARNING_MESSAGE);
            return resp;
            }
            catch (Exception e){ 
                Notification.show("Nie rozpoznany wynik wysyłki dokumentu pod adres:" + uri.toString() + " <br/> " + jsonLine);
                return null; 
            }
        }
        if (jsonLine != null && jsonLine.length() > 0) {
           
             Map m = null; 
            try {
                m = gson.fromJson(jsonLine, Map.class);
            }
            catch ( Exception e )
            {
                throw new VOWrongDataException("Błąd wysyłki:"+jsonLine);
            }
            throw new VOWrongDataException("Błąd:" + m.get("errorDescription"));
        } else {
            throw new VOWrongDataException("Błąd:" + res.toString());
        }

    }
    /*
     mail.smtp.host=cartrack.nazwa.pl
     mail.smtp.port=465
     mail.transport.protocol=smtp
     mail.smtp.socketFactory.class=javax.net.ssl.SSLSocketFactory
     mail.smtp.auth=true
     mail.smtp.user=crm@cartrack.pl
     mail.smtp.connectiontimeout=2000
     password=CTcrm24@
     */

    private void sendEmail(String mailTopic, String messageString, Company cmp) throws VOWrongDataException {
        logger.fine("send email");

        String email_password = "";
        String email_account = "";
        String email_host_name = "";
        // read parameters
        try {
            email_account = dictionaryAi.getValue( VOConsts.DICT_CODE_SETTINGS, VOConsts.DICT_VAL_CODE_EMAIL_ACCOUNT).getDescription();
            email_password = dictionaryAi.getValue( VOConsts.DICT_CODE_SETTINGS, VOConsts.DICT_VAL_CODE_EMAIL_PASSWORD).getDescription();
              email_host_name = dictionaryAi.getValue( VOConsts.DICT_CODE_SETTINGS, VOConsts.DICT_VAL_CODE_EMAIL_SMTP).getDescription();
        }
        catch ( VoNoResultException nre ){
            throw new VOWrongDataException("Nie można wysłać komunikatu przez email: "+ nre.getMessage());
        }
        // create session
        Properties props = new Properties();
        props.put("mail.smtp.host",email_host_name); // "smtp.vendiservis.pl");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("password", email_password); //"cC@AYGc3*yTw");
        props.put("mail.smtp.password", email_password); // "cC@AYGc3*yTw");
        props.put("mail.smtp.user",email_account); //  "zamowienia@vendiservis.pl");
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.port", "465");

        final String email_password_fi = email_password
                ;
        voMailSession = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        "zamowienia@vendiservis.pl",email_password_fi);// "cC@AYGc3*yTw");
            }
        });
        String emailAddressTo = cmp.getIntegrationUrl();
        MimeMessage message = new MimeMessage(voMailSession);
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        try {
            Transport transport = voMailSession.getTransport("smtp");
            //String usersTo = "crm@cartrack.pl";

            message.setFrom(new InternetAddress(email_account)); // "zamowienia@vendiservis.pl"));

            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailAddressTo));

            message.setRecipients(Message.RecipientType.CC, InternetAddress.parse("piotrek@mente.pl"));

            message.setSubject(mailTopic);

            messageBodyPart.setContent(messageString, "text/html; charset=utf-8;");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            /*
			
             if ( mailQueue.getAttachmnents() != null )
             {
				
             String name = "Spotkanie.ics";
             String contentType = String.format("text/calendar; name=%s", name);
             ByteArrayDataSource bds = new ByteArrayDataSource(mailQueue.getAttachmnents() ,  mailQueue.getAttachmentContentType());
             MimeBodyPart attachmentPart = new MimeBodyPart() ;
             attachmentPart.setDataHandler( new DataHandler(bds));
             //	attachmentPart.setc
             //	attachmentPart.setFileName("spotkanie.ics");
					 				  
             multipart.addBodyPart( attachmentPart);
             }*/

            message.setContent(multipart);

            transport.send(message);

            transport.close();
        } catch (Exception e) {
            throw new VOWrongDataException("Nie udało się wysłać email:" + e.toString());
        }

    }

}
