package com.mp.kfdms.customArgumentResolver;

import com.mp.kfdms.annotation.CurrentUser;
import com.mp.kfdms.domain.User;
import com.mp.kfdms.pojo.LoginInfo;
import com.mp.kfdms.util.UserUtil;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/2/23
 * @Time 10:53
 */
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterType().isAssignableFrom(User.class)
                && methodParameter.hasParameterAnnotation(CurrentUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        String lg_token = nativeWebRequest.getHeader("lg_token");
        if(lg_token == null)
            return null;

        User currentUser=null;
        try{
            currentUser = UserUtil.getUserFromToken(lg_token);
        }catch (Exception e){
            e.printStackTrace();
        }
        return currentUser;
    }
}
