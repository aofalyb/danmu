package com.barrage.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author liyang
 * @description:
 * @date 2018/3/16
 */
public class Log {

    private static Logger defaultLogger = LoggerFactory.getLogger("default");

    public static void e(String e,Exception exp){
        if(e != null){
            System.out.println(e);
        }
        if(exp != null){
            exp.printStackTrace();
        }
    }

    public static void d(String e){
        if(defaultLogger.isDebugEnabled()) {
            defaultLogger.info(e);
        }
    }
}
