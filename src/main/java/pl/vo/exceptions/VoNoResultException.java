/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.vo.exceptions;

import javax.ejb.ApplicationException;

/**
 *
 * @author Piotr
 */
@ApplicationException
public class VoNoResultException extends RuntimeException
{

    public VoNoResultException() {
    }

    public VoNoResultException(String message) {
        super(message);
    }
    
}
