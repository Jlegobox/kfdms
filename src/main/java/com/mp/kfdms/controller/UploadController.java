package com.mp.kfdms.controller;

import com.mp.kfdms.service.FileService;
import com.mp.kfdms.service.FolderService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/1/14
 * @Time 14:43
 */
@RestController
public class UploadController {
    @Resource
    private FileService fileService;

    @Resource
    private FolderService folderService;

    /**
     * 上传单个文件
     * @param request
     * @param response
     * @param file
     * @return
     */
    @RequestMapping("/doUploadFile.ajax")
    @ResponseBody
    public String doUploadFile(final HttpServletRequest request, final HttpServletResponse response,
                           @RequestParam("upload_file") final MultipartFile file){
        fileService.doUploadFile(request, response, file);
        return "OK";
    }

}
