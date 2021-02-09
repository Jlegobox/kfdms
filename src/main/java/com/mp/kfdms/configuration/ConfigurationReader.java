package com.mp.kfdms.configuration;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/1/7
 * @Time 15:45
 */

import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 配置文件的统一类，包括所有涉及到的配置项目的集合
 * 在项目启动完成后，会获取各种配置项目添加到其中，方便配置的更新。
 * 初始化时仅有application.properties配置，其他配置全部以后台配置的方式在网页上进行
 * 整体的配置会写到一个总的配置文件中，如果不存在此配置文件，才会重新初始化配置文件。
 * 定时对配置进行持久化。关机前会持久化配置
 */
@Configuration
public class ConfigurationReader {
    private static volatile ConfigurationReader instance;
    private Map<String,String> configurations;
    private String path;
    private String confdir;
    private ServerConfiguration serverConfig;
    private DataSourceConfiguration dataSourceConfig;


    public static ConfigurationReader instance(){
        if( ConfigurationReader.instance == null){
            synchronized (ConfigurationReader.class){
                if(ConfigurationReader.instance == null)
                    ConfigurationReader.instance = new ConfigurationReader();
            }
        }
        return ConfigurationReader.instance;
    };

    public ConfigurationReader(){
//        this.path = System.getProperty("user.dir");
//        this.confdir = this.path + File.separator + "conf" + File.separator;
//        this.serverConfig = new ServerConfiguration();
//        this.dataSourceConfig = new DataSourceConfiguration();
        configurations = new HashMap<String,String>();
    }

    public String getConf(String key){
        return configurations.get(key);
    }


}
