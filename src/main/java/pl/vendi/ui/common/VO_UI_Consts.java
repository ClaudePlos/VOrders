/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.common;

import java.util.HashMap;
import java.util.Map;
import pl.vo.VOConsts;
import pl.vo.common.model.DictionaryValue;

/**
 *
 * @author Piotr
 */
public class VO_UI_Consts {

    public static DictionaryValue dvUserTypeInternal = new DictionaryValue(VOConsts.USER_TYPE_INTERNAL, "Wewnętrzny");
    public static DictionaryValue dvUserTypeExternal = new DictionaryValue(VOConsts.USER_TYPE_EXTERNAL, "Zewnętrzny");

    public static String DICTIONARY_CODE_DICTIONARY_TYPES = "DICTIONARY_TYPES";
    public static String DICTIONARY_CODE_VAT_CODES = "VAT_CODES";
    public static String DICTIONARY_CODE_ROLES = "ROLES";

    // doc status
    public static DictionaryValue dvDocStatusAccepted = new DictionaryValue(VOConsts.DOC_STATUS_ACCEPTED, "Zaakceptowany");
    public static DictionaryValue dvDocStatusAnulowany = new DictionaryValue(VOConsts.DOC_STATUS_CANCELED, "Anulowany");
    public static DictionaryValue dvDocStatusOtwarty = new DictionaryValue(VOConsts.DOC_STATUS_OPEN, "Otwarty");
    public static DictionaryValue dvDocStatusBooked = new DictionaryValue(VOConsts.DOC_STATUS_BOOKED, "Zaksięgowany");
    
    public static DictionaryValue dcDocStatusAvailabilityCheck = new DictionaryValue(VOConsts.DOC_STATUS_AVAILABILITY_CHECK, "Sprawdzanie dostępności");

    public static DictionaryValue dcDocStatusConfirmedBySupplier = new DictionaryValue(VOConsts.DOC_STATUS_CONFIRMED_BY_SUPPLIER, "Potwierdzone przez dostawcę");
    public static DictionaryValue dcDocStatusDelivery = new DictionaryValue(VOConsts.DOC_STATUS_DELIVERY, "W trakcie dostawy");
    public static DictionaryValue dcDocStatusRealized = new DictionaryValue(VOConsts.DOC_STATUS_REALIZED, "Zrealizowane");
    public static DictionaryValue dcDocStatusReceivedBySupplier = new DictionaryValue(VOConsts.DOC_STATUS_RECEIVED_BY_SUPPLIER, "Otrzymane przez dostawcę");
    public static DictionaryValue dcDocStatusSendedToSupplier = new DictionaryValue(VOConsts.DOC_STATUS_SENDED_TO_SUPPLIER, "Wysłane do dostawcy");

    public static DictionaryValue dvDocStatusAvailabilityCheck = new DictionaryValue(VOConsts.DOC_STATUS_AVAILABILITY_CHECK, "Sprawdzanie dostępności");
    public static DictionaryValue dvDocStatusDelivery = new DictionaryValue(VOConsts.DOC_STATUS_DELIVERY, "W trakcie dostaw");
    public static DictionaryValue dvDocStatusRealized = new DictionaryValue(VOConsts.DOC_STATUS_REALIZED, "Zrealizowany");

    public static DictionaryValue actionAccept = new DictionaryValue(VOConsts.ACTION_ACCEPT, "Zatwierdź");
    public static DictionaryValue actionConfirmAvailability = new DictionaryValue(VOConsts.ACTION_ACCEPT_AVAILABILITY, "Potwierdź dostępność");
    public static DictionaryValue actionCancel = new DictionaryValue(VOConsts.ACTION_CANCEL, "Anuluj");
    public static DictionaryValue actionSendToRealization = new DictionaryValue(VOConsts.ACTION_SEND_TO_REALIZATION, "Przekaż do realizacji");
    public static DictionaryValue actionBackToOpen = new DictionaryValue(VOConsts.ACTION_BACK_TO_OPEN, "Wróc do edycji");
    public static DictionaryValue actionCancelRealization = new DictionaryValue(VOConsts.ACTION_CANCEL_REALIZATION, "Wycofaj z realizacji (UWAGA!)");

    public static DictionaryValue actionSendToSupplier = new DictionaryValue(VOConsts.ACTION_SEND_TO_SUPPLIER, "Wyślij do dostawcy");
     public static DictionaryValue actionSendToClient = new DictionaryValue(VOConsts.ACTION_SEND_TO_CLIENT, "Wyślij do klienta");
    public static DictionaryValue actionSupplierConfirmReceive = new DictionaryValue(VOConsts.ACTION_SUPPLIER_CONFIRM_RECEIVE, "Dostawca - potwierdź otrzymanie");
    public static DictionaryValue actionSupplierConfirmAvailability = new DictionaryValue(VOConsts.ACTION_SUPPLIER_CONFIRM_AVAILABILITY, "Dostawca - potwierdź ilości");

    public static DictionaryValue actionBook = new DictionaryValue(VOConsts.ACTION_BOOK, "Księguj ostatecznie (nie można cofnąć)");

    // integration types
    public static DictionaryValue dvIntegrationTypeLocalUser = new DictionaryValue(VOConsts.INTEGRATION_TYPE_LOCAL_USER, "Użytkownik lokalny");
    public static DictionaryValue dvIntegrationTypeIMag = new DictionaryValue(VOConsts.INTEGRATION_TYPE_REMOTE_IMAG, "System zdalny iMag");

    // integration transports
    public static DictionaryValue dvIntegrationTransportREST = new DictionaryValue(VOConsts.INTEGRATION_TRANSPORT_REST, "REST");
    public static DictionaryValue dvIntegrationTransportEmail = new DictionaryValue(VOConsts.INTEGRATION_TRANSPORT_EMAIL, "EMAIL");

    private static Map<String, DictionaryValue> statuses;

    public static Map<String, DictionaryValue> getStatusMap() {
        if (statuses == null) {
            statuses = new HashMap<String, DictionaryValue>();
            statuses.put(dvDocStatusAccepted.getValue(), dvDocStatusAccepted);
            statuses.put(dvDocStatusAnulowany.getValue(), dvDocStatusAnulowany);
            statuses.put(dvDocStatusOtwarty.getValue(), dvDocStatusOtwarty);
            statuses.put(dcDocStatusAvailabilityCheck.getValue(), dcDocStatusAvailabilityCheck);
            statuses.put(dcDocStatusConfirmedBySupplier.getValue(), dcDocStatusConfirmedBySupplier);
            statuses.put(dcDocStatusDelivery.getValue(), dcDocStatusDelivery);
            statuses.put(dcDocStatusRealized.getValue(), dcDocStatusRealized);
            statuses.put(dcDocStatusReceivedBySupplier.getValue(), dcDocStatusReceivedBySupplier);
            statuses.put(dcDocStatusSendedToSupplier.getValue(), dcDocStatusSendedToSupplier);
            statuses.put(dvDocStatusBooked.getValue(), dvDocStatusBooked);

        }

        return statuses;
    }

    public static String getStatusName(String statusCode) {
        Map<String, DictionaryValue> sta = getStatusMap();
        if (sta.containsKey(statusCode)) {
            return sta.get(statusCode).getDescription();
        }

        return statusCode;
    }
}
