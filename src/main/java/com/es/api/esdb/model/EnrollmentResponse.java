/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.es.api.esdb.model;

import java.io.Serializable;
import lombok.Data;

/**
 *
 * @author anwar
 */
@Data
public class EnrollmentResponse implements Serializable{
    private boolean responseStatus;
    private String message;
    
    public EnrollmentResponse(){
        
    }
    
    public EnrollmentResponse(boolean responseStatus, String message) {
        this.responseStatus = responseStatus;
        this.message = message;
    }
}
