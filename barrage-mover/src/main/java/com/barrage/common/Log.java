package com.barrage.common;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author liyang
 * @description:
 * @date 2018/3/16
 */
public interface Log {

    Logger defLogger = LogManager.getLogger("defLogger");

    Logger errorLogger =  LogManager.getLogger("errorLogger");

}
