package com.mp.kfdms.configuration;

import com.mp.kfdms.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/1/20
 * @Time 11:13
 */
@Configuration
@ComponentScan("com.mp.kfdms.interceptor")
public class InterceptorConfiguration implements WebMvcConfigurer {
    @Autowired
    LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/home/**.ajax")
                .addPathPatterns("/DataCloud/**.ajax");
    }
}
