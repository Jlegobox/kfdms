package com.mp.kfdms.Filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/1/20
 * @Time 10:41
 */
@WebFilter(value="EncodingFilter",urlPatterns = {"/*"})
public class EncodingFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("Filter初始化");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request= (HttpServletRequest) servletRequest;
        HttpServletResponse response= (HttpServletResponse)servletResponse;
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        System.out.println("Filter销毁");
    }
}
