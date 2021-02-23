package com.mp.kfdms.configuration;

import com.mp.kfdms.customArgumentResolver.CurrentUserArgumentResolver;
import com.mp.kfdms.customArgumentResolver.FileShareLinkInfoHandlerMethodArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/2/23
 * @Time 10:35
 */
@Configuration
public class ArgumentResolverConfiguration implements WebMvcConfigurer {
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new CurrentUserArgumentResolver());
        resolvers.add(new FileShareLinkInfoHandlerMethodArgumentResolver());
    }
}
