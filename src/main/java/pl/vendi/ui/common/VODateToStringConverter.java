/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vendi.ui.common;

import com.vaadin.data.util.converter.StringToDateConverter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 *
 * @author Piotr
 */
public class VODateToStringConverter extends StringToDateConverter {

    @Override

    public DateFormat getFormat(Locale locale) {

        return new SimpleDateFormat("yyyy-MM-dd");

    }

}
