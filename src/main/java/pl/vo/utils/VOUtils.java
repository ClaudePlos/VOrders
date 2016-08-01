/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.lang.time.DateUtils;

/**
 *
 * @author Piotr
 */
public class VOUtils {
    
    
  	public static String formaDateYYYYMMDDHHMI(Date date) {
		if (date == null)
			return "";

		SimpleDateFormat df = new SimpleDateFormat("YYYY-MM-dd HH:mm");
		return df.format(date);

	} 
        
        public static String formatCurrency( BigDecimal val ) { 
            if (val == null )
                return "";
            
            DecimalFormat df = new DecimalFormat("#,##");
            String ret = df.format(val)+" zł";
            return ret; 
        }
	
	public static String formaDateHHMI(Date date) {
		if (date == null)
			return "";

		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		return df.format(date);

	}
	
	public static String formaDateHHMISS(Date date) {
		if (date == null)
			return "";

		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		return df.format(date);

	}

	public static String formaDateYYYYMMDD(Date date) {
		if (date == null)
			return "";

		SimpleDateFormat df = new SimpleDateFormat("YYYY-MM-dd");
		return df.format(date);

	}
        
        public static Date firstDayOfMonth( Date data)
        {
           return  DateUtils.round(data,Calendar.MONTH );
        }
        
        public static Date lastDayOfMonth( Date data)
        {
           Calendar cal = Calendar.getInstance();
           cal.setTime( data );
           cal = DateUtils.round(cal,Calendar.MONTH );
           cal.set( Calendar.DAY_OF_MONTH, cal.getActualMaximum( Calendar.DAY_OF_MONTH));
           return cal.getTime() ;
        }
}
