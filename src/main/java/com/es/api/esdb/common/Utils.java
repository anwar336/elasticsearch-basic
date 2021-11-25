/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.es.api.esdb.common;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author anwar
 */
public class Utils {
    public static Date addDays(Date date, int day) {
        if(date == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, day);
        return cal.getTime();
    }
    
    public static boolean isOk(String str) {
        return !(str == null || str.trim().isEmpty());
    }

}
