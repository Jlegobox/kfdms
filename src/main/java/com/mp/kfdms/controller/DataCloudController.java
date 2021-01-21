package com.mp.kfdms.controller;

import com.mp.kfdms.service.FileService;
import com.mp.kfdms.service.FolderService;
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
 * @Date 2021/1/21
 * @Time 10:40
 */
@RestController("/DataCloud")
public class DataCloudController {
    @Autowired
    public UserService userservice;
    @Autowired
    public FolderService folderService;
    @Autowired
    public FileService fileService;

    @RequestMapping(value = {"/createFolder.ajax"})
    @ResponseBody
    public String createFolder(final HttpServletRequest request, final HttpServletResponse response){
        folderService.createFolder(request,response);
        return "success";
    }

    @RequestMapping(value = { "/doUploadFile.ajax" }) //使用filter来做编码设定,所以不需要 produces = { CHARSET_BY_AJAX }
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
