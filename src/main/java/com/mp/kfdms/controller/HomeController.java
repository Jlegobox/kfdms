package com.mp.kfdms.controller;

import com.mp.kfdms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/1/10
 * @Time 14:08
 */
//@Controller
@RestController
public class HomeController {
    private static final String CHARSET_BY_AJAX = "text/html; charset=utf-8";
    @Autowired
    public UserService userservice;

//    @RequestMapping(value = { "/douploadFile.ajax" }, produces = { CHARSET_BY_AJAX })
    @ResponseBody
    public String douploadFile(final HttpServletRequest request, final HttpServletResponse response,
                               @RequestParam("file1") final MultipartFile file) throws IOException {
        String base_path="C:\\Users\\J\\GitHub\\DFDMS\\kfdms\\src\\main\\resources\\tem_files";
        File tem_file = new File(base_path+"\\"+"new_file.xls");
        tem_file.createNewFile();
        file.transferTo(tem_file);
        return "OK";
    }
}
