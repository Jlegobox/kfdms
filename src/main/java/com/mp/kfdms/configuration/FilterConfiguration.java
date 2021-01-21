package com.mp.kfdms.configuration;

import com.mp.kfdms.Filter.EncodingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/1/20
 * @Time 10:55
 */
@Configuration
public class FilterConfiguration implements WebMvcConfigurer {
    @Bean
    public EncodingFilter encodingFilter(){
        return new EncodingFilter();
    }
}
