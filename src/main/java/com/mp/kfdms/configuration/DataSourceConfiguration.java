package com.mp.kfdms.configuration;


import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/1/7
 * @Time 15:17
 */
@Configuration
public class DataSourceConfiguration {

    public Resource[] mapperFiles;
    public Resource mybatisConfig;
    @Value("${spring.datasource.driver-class-name}")
    public String driverClassName;
    @Value("${spring.datasource.url}")
    public String url;
    @Value("${spring.datasource.username}")
    public String username;
    @Value("${spring.datasource.password}")
    public String password;

    @Bean
    public DataSource dataSource(){
        //使用final修饰变量，保证ds不会被重新new
        //这里可以自定义的修改数据库连接配置
        final DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName(driverClassName);
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        return ds;
    }

}
