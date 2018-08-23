package com.barrage.common;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author liyang
 * @description:
 * @date 2018/3/16
 */
public class Log {

    static Logger logger = LogManager.getLogger("RollingRandomAccessFileLogger");

    public static void e(String e,Exception exp){
        if(e != null){
            System.out.println(e);
        }
        if(exp != null){
            exp.printStackTrace();
        }
    }

    public static void d(String e){
        logger.info(e);
    }
}
