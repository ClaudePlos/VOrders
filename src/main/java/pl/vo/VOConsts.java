/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo;

/**
 *
 * @author Piotr
 */
public class VOConsts {
    
    
    public static String DICT_CODE_SETTINGS = "SETTINGS";
    
    public static String DICT_VAL_CODE_EMAIL_ACCOUNT = "EMAIL_ACCOUNT";
    public static String DICT_VAL_CODE_EMAIL_PASSWORD = "EMAIL_PASSWORD";
    public static String DICT_VAL_CODE_EMAIL_SMTP = "EMAIL_SMTP";
    public static String DICT_VAL_CODE_EMAIL_IMAP = "EMAIL_IMAP";
    
    public static String REGISTRY_PRINCIPAL = "PRINCIPAL";
    public static String REGISTRY_USER = "VO_USER";
    public static String REGISTRY_USERNAME = "VO_USERNAME";
    
    public static String NIP_VENDI = "5222899038";
    public static String USER_TYPE_INTERNAL = "INTERNAL";
    public static String USER_TYPE_EXTERNAL = "EXTERNAL";
    
    // order from object
    public static String DOC_TYPE_ZWK = "VO_ZWK";
    // order to supplier
    public static String DOC_TYPE_ZWD = "VO_ZWD";
    // declared delivery
    public static String DOC_TYPE_DPZ = "VO_DPZ";
    // delivery
    public static String DOC_TYPE_PZ = "VO_PZ";
    // price list
    public static String DOC_TYPE_PRICE_LIST = "VO_PRICE_LIST";
    // invoice
    public static String DOC_TYPE_INVOICE = "VO_INVOICE";
    
    
    // offers status
    public static String DOC_STATUS_OPEN = "OPEN";
    public static String DOC_STATUS_ACCEPTED = "ACCEPTED";
    public static String DOC_STATUS_CANCELED = "CANCELED";
  
    public static String DOC_STATUS_AVAILABILITY_CHECK = "AVAILABILITY_CHECK";
    public static String DOC_STATUS_DELIVERY = "DELIVERY";
    public static String DOC_STATUS_REALIZED = "REALIZED";
    
    /// sended to supplier - awaiting confirmation
    public static String DOC_STATUS_SENDED_TO_SUPPLIER = "SENDED_TO_SUPPLIER";
    public static String DOC_STATUS_RECEIVED_BY_SUPPLIER = "RECEIVED_BY_SUPPLIER";
     public static String DOC_STATUS_CONFIRMED_BY_SUPPLIER = "CONFIRMED_SUPPLIER";
  
    
     public static String DOC_STATUS_BOOKED = "BOOKED";
    
    // actions
    public static String ACTION_BACK_TO_OPEN = "BACK_TO_OPEN";
    public static String ACTION_ACCEPT = "ACCEPT";
    public static String ACTION_CANCEL = "CANCEL";
    public static String ACTION_SEND_TO_REALIZATION = "TO_REALIZATION";
    public static String ACTION_ACCEPT_AVAILABILITY = "ACCEPT_AVAILABILITY";
    
    public static String ACTION_CANCEL_REALIZATION = "CANCEL_REALIZATION";
    
    public static String ACTION_SEND_TO_SUPPLIER = "SEND_TO_SUPPLIER";
    public static String ACTION_SUPPLIER_CONFIRM_RECEIVE = "SUPPLIER_CONFIRM_RECEIVE";
    public static String ACTION_SUPPLIER_CONFIRM_AVAILABILITY = "SUPPLIER_CONFIRM_AVAILABILITY";
    
    public static String ACTION_SEND_TO_CLIENT = "SEND_TO_CLIENT";
    
    // ostateczne ksiegowanie
    public static String ACTION_BOOK = "ACTION_BOOK"; 
    
    // integration types
    public static String INTEGRATION_TYPE_LOCAL_USER = "LOCAL_USER"; // local system user
    public static String INTEGRATION_TYPE_REMOTE_IMAG = "REMOTE_IMAG"; // remote system iMag
    
    public static String INTEGRATION_TRANSPORT_REST = "TRANSPORT_REST";
    public static String INTEGRATION_TRANSPORT_EMAIL = "TRANSPORT_EMAIL"; 
    
    public static String getDocTypeName(String type) { 
        if ( type.equals(DOC_TYPE_ZWK))
                return "ZWK";
        else if ( type.equals(DOC_TYPE_ZWD))
                return "ZWD";
        else if ( type.equals(DOC_TYPE_PZ))
                return "PZ";
        else if ( type.equals(DOC_TYPE_DPZ))
                return "WZ";
        else if ( type.equals(DOC_TYPE_PRICE_LIST))
                return "Cennik";
        else if ( type.equals(DOC_TYPE_INVOICE))
            return "Faktura";
        else 
            return type; 
        
        
    }
    
    
    //
   
    
}
