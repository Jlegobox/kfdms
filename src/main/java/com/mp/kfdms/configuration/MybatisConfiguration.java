package com.mp.kfdms.configuration;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import javax.sql.DataSource;
import java.io.File;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/1/10
 * @Time 14:12
 */

/**
 * application.properties配置
 */
//@Configuration
public class MybatisConfiguration {
//    @Autowired
//    public DataSource dataSource;
//    @Bean
//    public SqlSessionFactoryBean sqlSessionFactoryBean(){
//        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
//        Resource configLocation = new FileSystemResourceLoader().getResource("classpath:mybatisResource/mybatis-config.xml");
//        sqlSessionFactoryBean.setConfigLocation(configLocation);
//        sqlSessionFactoryBean.setMapperLocations();
//        sqlSessionFactoryBean.setDataSource(dataSource);
//        return sqlSessionFactoryBean;
//    }

//    @Bean
//    MapperScannerConfigurer mapperScannerConfigurer(){
//        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
//        mapperScannerConfigurer.setBasePackage("com.mp.kfdms.mapper");
//        return mapperScannerConfigurer;
//    }
}
