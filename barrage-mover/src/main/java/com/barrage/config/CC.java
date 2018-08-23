package com.barrage.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;


/**
 * @author liyang
 * @description:
 * @date 2018/4/4
 */
public interface CC {

    Config cfg = load();

    static Config load(){
        Config config = ConfigFactory.load("rid.conf");
        return config;
    }

    interface douyu {
      String rid = cfg.getString("rid");
    }
}
