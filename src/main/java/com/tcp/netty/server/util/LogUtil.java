package com.tcp.netty.server.util;

import static com.tcp.netty.server.constants.CommonConstants.C_IS_DEBUG;

/**
 * Created by powerevan on 2014-12-15.
 */
public class LogUtil {
    public static void log(String text) {
        System.out.println("[" + DateTimeUtil.getDateTimeMS() + "] " + text);
    }

    public static void debug( String text) {

        if(!C_IS_DEBUG) {
            return;
        }

        System.out.println("[" + DateTimeUtil.getDateTimeMS() + "] " + text);
    }
}
