package com.danmu.common;


import java.util.*;

/**
 * @author liyang
 * @description:把Map转化为，斗鱼自创序列化、反序列化算法。即STT序列化。《斗鱼弹幕服务器第三方接入协议v1.6.2》#2.2序列化
 * @date 2018/3/19
 */
public class DouyuSerializeUtil {


    public static String serialize(Map kvs) {

        return null;
    }

    public static Map unSerialize(String serializedString){


        Map attrs = new HashMap();


        String[] strings = serializedString.split("@=");

        String thisVk = null;

        String nextVk = null;

        for (int i = 0; i < strings.length; i++) {

            thisVk = strings[i];
            if(i != strings.length - 1){
                nextVk = strings[i+1];
            }

            if(!thisVk.contains("/")){

                String[] nextVks = nextVk.split("/");
                if(nextVks.length == 0){
                    attrs.put(thisVk,"");
                }else if(nextVks.length <= 2){
                    attrs.put(thisVk,nextVks[0]);
                }else if(nextVks.length > 2){
                    //说明是数组
                    attrs.put(thisVk,Arrays.asList(nextVks));
                }
            }else {

                String[] thisVks = thisVk.split("/");
                if(thisVks.length == 0){
                    //结束了
                    continue;
                }else if(thisVks.length == 2){

                    String[] nextVks = nextVk.split("/");
                    if(nextVks.length == 0){
                        attrs.put(thisVks[1],"");
                    }else if(nextVks.length <= 2){
                        attrs.put(thisVks[1],nextVks[0]);
                    }else if(nextVks.length > 2){
                        //说明是数组
                        attrs.put(thisVks[1],Arrays.asList(nextVks));
                    }
                }
            }


        }



        return attrs;
    }


}
