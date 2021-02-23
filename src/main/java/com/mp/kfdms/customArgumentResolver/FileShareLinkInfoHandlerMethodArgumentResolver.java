package com.mp.kfdms.customArgumentResolver;

import com.mp.kfdms.pojo.FileShareLinkInfo;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/2/23
 * @Time 10:10
 */
public class FileShareLinkInfoHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterType().isAssignableFrom(FileShareLinkInfo.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        FileShareLinkInfo fileShareLinkInfo = new FileShareLinkInfo();
        String accessCode = nativeWebRequest.getParameter("accessCode");
        String visitLimit = nativeWebRequest.getParameter("visitLimit");
        if(visitLimit == null)
            return null;
        String validPeriod = nativeWebRequest.getParameter("validPeriod");
        if(validPeriod==null)
            return null;
        String fileId = nativeWebRequest.getParameter("fileId");
        String folderId = nativeWebRequest.getParameter("folderId");

        fileShareLinkInfo.setAccessCode(accessCode);
        fileShareLinkInfo.setVisitLimit(Integer.parseInt(visitLimit));
        fileShareLinkInfo.setValidPeriod(Integer.parseInt(validPeriod));
        fileShareLinkInfo.setFileId(fileId == null?-1:Integer.parseInt(fileId));
        fileShareLinkInfo.setFolderId(folderId == null?-1:Integer.parseInt(folderId));

        return fileShareLinkInfo;
    }
}
