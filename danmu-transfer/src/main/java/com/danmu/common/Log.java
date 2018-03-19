package com.danmu.common;

/**
 * @author liyang
 * @description:
 * @date 2018/3/16
 */
public class Log {


    public static void e(String e,Exception exp){
        if(e != null){
            System.out.println(e);
        }
        if(exp != null){
            exp.printStackTrace();
        }
    }

    public static void d(String e){
        System.out.println(e);
    }
}
