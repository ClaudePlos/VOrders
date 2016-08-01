package pl.vendi.ui;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import java.security.Principal;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.ws.rs.core.Context;
import pl.vendi.ui.main.UIMainWindow;
import pl.vo.VOConsts;
import pl.vo.common.VoUserSession;
import pl.vo.documents.DocumentsApi;
import pl.vo.security.api.UsersApi;
import pl.vo.security.model.User;

/**
 *
 */
@Theme("vendiOrdersTheme")
@Widgetset("pl.vendi.vendiorders.ui.VendiOrdersWidgetset")
public class VendiOrdersUI extends UI
{

    VoUserSession voSession;

    UIMainWindow mainWindow = new UIMainWindow();
    
    @EJB
    UsersApi usersApi; 
    
    
    private String logger_username; 
    private User logged_user ; 
    
    
    @Override
    protected void init(VaadinRequest vaadinRequest) {
//
//        Principal principal = vaadinRequest.getUserPrincipal();
//        if (principal == null) {
//            throw new RuntimeException("Access denied");
//        }
//
//        // put principal
//        registry.putResource("principal", principal);
//        // get user
//        User user = VOLookup.lookupUsersApi().getUserByName( principal.getName() );
//        registry.putResource(VOConsts.REGISTRY_USER, user);
//        
        
        
        Principal pr = vaadinRequest.getUserPrincipal();
                  String user = vaadinRequest.getRemoteUser();
                  
        UsersApi usersApi =  VOLookup.lookupUsersApi();
              
               
          this.logger_username = user; 
          this.logged_user = usersApi.getByName(user);
           usersApi.setContextUser(logged_user);
     
               
//               String un = usersApi.getUsername();
//               
//               User u3 = usersApi.getLoggedUser();
        
          
        voSession = VOLookup.lookupVoUserSession();
//        
//        User u2 = usersApi.getLoggedUser();
//        
//        UsersApi docApi = VOLookup.lookupUsersApi();
//        User u = docApi.getLoggedUser();

        try {
            voSession.getLoggedUser();
        } catch (Exception e) {
            setContent(new Label("Błąd - użytkownik nie zalogoowany"));
            return;
        }
        setContent(mainWindow);
        mainWindow.setSizeFull();

    }

    @WebServlet(urlPatterns = {"/app/*", "/VAADIN/*"}, name = "VendiOrdersUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = VendiOrdersUI.class, productionMode = false)
    //  @HttpMethodConstraint(value = "GET", rolesAllowed={"vendi_user2"}) 
    public static class VendiOrdersUIServlet extends VaadinServlet {

//       
//        @Override
//    protected Application getNewApplication(HttpServletRequest request) throws
//            ServletException {
//        DemoApp app = application.get();
//        
//         Principal principal = vaadinRequest.getUserPrincipal();
//          if (principal == null) {
//            throw new ServletException("Access denied");
//        }
//          
//    }
    }

    public static void showWindow(Window w) {
        UI.getCurrent().addWindow(w);
        w.center();
        w.setWidth("85%");
        w.setHeight("85%");
    }

    public static VendiOrdersUI getInstance() {
        return (VendiOrdersUI) UI.getCurrent();
    }
    
    public static String getLoggedUsername()
    {
        return getInstance().logger_username; 
    }
    
     public static User getLoggedUser()
    {
        return getInstance().logged_user; 
    }

    public String getLogger_username() {
        return logger_username;
    }

    public void setLogger_username(String logger_username) {
        this.logger_username = logger_username;
    }

    
}
