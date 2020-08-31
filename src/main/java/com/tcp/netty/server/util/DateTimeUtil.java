
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tcp.netty.server.util;

import java.text.SimpleDateFormat;
import java.util.*;

public class DateTimeUtil {

    public DateTimeUtil() {

    }

    public static long getTimeStamp() {
        return (new Date()).getTime();
    }

    /**
     * *********************************************************************************************
     * 현재날짜를 YYYY-MM-DD HH:MM:SS 형식으로 만들어 리턴 <br/>
     * *********************************************************************************************
     */
    public static String getDateTimeMS() {
        String str = null;

        try {
            TimeZone timeZone = new SimpleTimeZone(9 * 60 * 60 * 1000, "KST");
            TimeZone.setDefault(timeZone);

            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy'-'MM'-'dd' 'HH':'mm':'ss':'SSS");
            str = simpleDateFormat.format(date);
        } catch (Exception e) {
        }
        return str;
    }
}
