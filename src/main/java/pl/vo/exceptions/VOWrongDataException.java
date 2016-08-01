/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.exceptions;

import javax.ejb.ApplicationException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 *
 * @author Piotr
 */
@ApplicationException
public class VOWrongDataException extends Exception
{

    public VOWrongDataException() {
    }

    public VOWrongDataException(String message) {
        super(message);
    }

    public VOWrongDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public VOWrongDataException(Throwable cause) {
        super(cause);
    }

    public VOWrongDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
    
    public static VOWrongDataException  getForConstraintViolation( String message, ConstraintViolationException cve)
    {
       
        String c = "";
        
        for (ConstraintViolation cv : cve.getConstraintViolations())
        {
            if ( c.length() != 0 ) 
                c+=",";
            c+=cv.getMessage();
        }
        
        return new VOWrongDataException(message+":"+c);
    }
    
}
