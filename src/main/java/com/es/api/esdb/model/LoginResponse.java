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
public class LoginResponse extends ServiceResponse implements Serializable{
    private String userName;
    private String token;
    private String expiredIn;

    public LoginResponse(String message) {
        super(false, message);
    }

    public LoginResponse(String userName,String token, String expiredIn) {
        super();
        this.userName = userName;
        this.token = token;
        this.expiredIn = expiredIn;
    }
}
