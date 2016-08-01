/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import pl.vo.common.VoUserSession;
import pl.vo.common.api.DictionaryApi;
import pl.vo.company.api.CompanysApi;
import pl.vo.documents.DocumentsApi;
import pl.vo.documents.api.DocumentsActionsDpzApi;
import pl.vo.documents.api.DocumentsActionsPzApi;
import pl.vo.documents.api.DocumentsActionsZwdApi;
import pl.vo.documents.api.DocumentsActionsZwkApi;
import pl.vo.documents.api.DocumentsProcessApi;
import pl.vo.organisation.OrganisationApi;
import pl.vo.products.api.MeasureUnitsApi;
import pl.vo.products.api.ProductsApi;
import pl.vo.products.api.UnitsProductsApi;
import pl.vo.products.api.UnitsProductsSuppliersApi;
import pl.vo.security.api.UsersApi;
import pl.vo.stock.api.StockApi;

/**
 *
 * @author Piotr
 */
public class VOLookup {
    
     private static InitialContext initialContext;

	private static InitialContext getContext() 
	{
		if ( initialContext != null)
			return initialContext;
		else {
			try {
			initialContext = new InitialContext();
			}
			catch  ( NamingException ne ){
				throw new RuntimeException("Błąd CL-21 nie udało się utworzyć kontekstu");
			}
			return initialContext;
			
		}
	}
        
        
        public static Object lookupObject( String name )
	{
		 try {
			 InitialContext ctx = getContext();
	            //Object ejbRef =  ctx.lookup("java:global/VendiOrders-ear/VendiOrders-web-1.0-SNAPSHOT/" + name );
                       //  Object ejbRef =  ctx.lookup("java:module/" + name );
	             Object ejbRef =  ctx.lookup("java:module/" + name );
	            return ejbRef;
	         } catch (NamingException ne) {
	            throw new RuntimeException(ne);
	         } catch (Exception e) {
	            throw new RuntimeException(e);
	         }
	}
        
    public static UsersApi lookupUsersApi() { 
        return (UsersApi) lookupObject("UsersApi");
    }
    
    public static VoUserSession lookupVoUserSession(){
         return (VoUserSession) lookupObject("VoUserSession");
    }
    
   public static CompanysApi lookupCompanysApi() { 
       return (CompanysApi) lookupObject("CompanysApi");
   }
   
   public static OrganisationApi lookupOrganisationApi() { 
       return (OrganisationApi) lookupObject("OrganisationApi");
   }
   
   
    public static ProductsApi lookupProductsApi() { 
       return (ProductsApi) lookupObject("ProductsApi");
   }
    
    public static MeasureUnitsApi lookupMeasureUnitsApi() {
        return (MeasureUnitsApi) lookupObject("MeasureUnitsApi");
    }
   
    public static DictionaryApi lookupDictionaryApi() { 
       return (DictionaryApi) lookupObject("DictionaryApi");
   }
    
    public static UnitsProductsApi lookupUnitsProductsApi() { 
        return (UnitsProductsApi) lookupObject("UnitsProductsApi");
    }
    
    public static UnitsProductsSuppliersApi lookupUnitsProductsSuppliersApi() { 
        return (UnitsProductsSuppliersApi) lookupObject("UnitsProductsSuppliersApi");
    }
    
    public static DocumentsApi lookupDocumentsApi() { 
        return (DocumentsApi) lookupObject("DocumentsApi");
    }
    
     public static DocumentsProcessApi lookupDocumentsProcessApi() { 
        return (DocumentsProcessApi) lookupObject("DocumentsProcessApi");
    }
     
     public static DocumentsActionsZwdApi lookupDocumentsActionsZwdApi() { 
        return (DocumentsActionsZwdApi) lookupObject("DocumentsActionsZwdApi");
    }
     
     public static DocumentsActionsZwkApi lookupDocumentsActionsZwkApi() { 
        return (DocumentsActionsZwkApi) lookupObject("DocumentsActionsZwkApi");
    }
     
      public static DocumentsActionsDpzApi lookupDocumentsActionsDpzApi() { 
        return (DocumentsActionsDpzApi) lookupObject("DocumentsActionsDpzApi");
    }
      
      public static DocumentsActionsPzApi lookupDocumentsActionsPzApi() { 
        return (DocumentsActionsPzApi) lookupObject("DocumentsActionsPzApi");
    }
      
      public static StockApi lookupStockApi() { 
          return (StockApi) lookupObject(StockApi.class.getSimpleName());
      }
}
