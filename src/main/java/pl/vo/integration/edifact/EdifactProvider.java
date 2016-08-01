/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.integration.edifact;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.milyn.edi.unedifact.d96a.D96AInterchangeFactory;
import org.milyn.edi.unedifact.d96a.common.DateTimePeriod;
import org.milyn.edi.unedifact.d96a.common.field.DateTimePeriodC507;
import org.milyn.smooks.edi.unedifact.model.r41.types.DateTime;
import pl.vo.exceptions.VOWrongDataException;

/**
 *
 * @author Piotr
 */
public class EdifactProvider {

    private static D96AInterchangeFactory factory;

    public static D96AInterchangeFactory getInterchangeFactory() throws VOWrongDataException {
        try {
            if (factory == null) {
                factory = D96AInterchangeFactory.getInstance();
            }
        } catch (Exception ioe) {
            throw new VOWrongDataException("Błą EDP-28 - nie udało się utworzyć edifact facory:" + ioe.getLocalizedMessage(), ioe);
        }
        return factory;
    }

    public static Date parseDate(String date, String dateFormat) {

        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        try {
            Date  ret =  df.parse(date);
            return ret; 
        } catch (ParseException pe) {
            return null;
        }

    }
    
    public static DateTimePeriod dateToDateTimePeriod(Date date, String qualifier) {
        DateTimePeriod dt = new DateTimePeriod();
        SimpleDateFormat df = new SimpleDateFormat("YYYYMMdd");
        dt.setDateTimePeriod( new DateTimePeriodC507());
        dt.getDateTimePeriod().setDateTimePeriod(  df.format(date));
        dt.getDateTimePeriod().setDateTimePeriodFormatQualifier("102");
        dt.getDateTimePeriod().setDateTimePeriodQualifier( qualifier );

        return dt;

    }

    public static DateTime dateToDateTime(Date date) {
        DateTime dt = new DateTime();
        SimpleDateFormat df = new SimpleDateFormat("YYYYMMdd");
        dt.setDate(df.format(date));
        df = new SimpleDateFormat("HHmm");
        dt.setTime(df.format(date));

        return dt;

    }

    static DateTimePeriodC507 dateToDateTimePeriod(Date dateDelivery) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
