/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.rest;

import com.google.gson.Gson;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.enterprise.context.SessionScoped;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import pl.vo.common.VoUserSession;
import pl.vo.exceptions.VoNoResultException;
import pl.vo.integration.IncomingParser;
import pl.vo.security.api.UsersApi;
import pl.vo.security.model.User;

/**
 *
 * @author Piotr
 */
@Path("/vorders")
@Stateless
public class VOrdersRest implements Serializable
{
    
    @EJB
    IncomingParser incomingParser; 
    
    @EJB
    VoUserSession userSession; 
    
    @EJB
    UsersApi usersApi; 
    
    private static Gson gson = new Gson();
    
    private Logger logger;
    
    public VOrdersRest() 
    { 
        logger = Logger.getLogger( VOrdersRest.class.getSimpleName() );
    }
    
    @Path("/")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
  //  @Consumes(MediaType.)
    public Response receiveMessage( String message, @Context UriInfo uriInfo, @Context HttpHeaders headers)
    {
        
        // check for errors
        if (message == null || message.length()==0){
            VoRestResponse re = new VoRestResponse("VOS-49","Nie udało się przetworzyć wiadomości","Wiadomość jest pusta");
            return Response.serverError().status(Response.Status.BAD_REQUEST).entity(re).type(MediaType.APPLICATION_JSON).build();
        }
        
        // check for header with token
        List<String> voToken =  headers.getRequestHeader("voToken");
        if ( voToken == null ||  voToken.size() != 1)
        {
             VoRestResponse re = new VoRestResponse("VOS-60","Błąd bezpieczeństwa","Nie przekazano tokena użytkownika");
            return Response.serverError().status(Response.Status.BAD_REQUEST).entity(re).type(MediaType.APPLICATION_JSON).build();
        }
        
        
        try {
            User user = usersApi.getUserByToken(voToken.get(0));
            userSession.setLoggedUser( user );
            usersApi.setContextUser(user);
        }
        catch ( VoNoResultException nre )
        {
            VoRestResponse re = new VoRestResponse("VOS-60","Błąd bezpieczeństwa","Błędny token użytkownika");
            return Response.serverError().status(Response.Status.BAD_REQUEST).entity(re).type(MediaType.APPLICATION_JSON).build();
        }
        
        // parse mesage
        try {
            VoRestResponse resp = incomingParser.parseIncomingMessage(message);
            return Response.ok().entity(resp).type(MediaType.APPLICATION_JSON).build();
        }
        catch ( Exception wre )
        {
            String cause = wre.getMessage(); 
             VoRestResponse re = new VoRestResponse("VOS-49","Nie udało się przetworzyć wiadomości:" + wre.getMessage(),wre.getMessage());
             return Response.serverError().status(Response.Status.BAD_REQUEST).entity(re).type(MediaType.APPLICATION_JSON).build();
        }
    }
}
