/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.utils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 *
 * @author Piotr
 */
@Converter
public class BooleanToStringConverter implements AttributeConverter<Boolean, String> {

    @Override
    public String convertToDatabaseColumn(Boolean value) 
    {
        
        return (value != null && value) ? "Y" : "N";            
        }    

    @Override
    public Boolean convertToEntityAttribute(String value)
    {
        if ( value == null)
        return false; 
            
        if ( value.toUpperCase().equals( "Y"))
            return true; 
        return false;
        
    }
}