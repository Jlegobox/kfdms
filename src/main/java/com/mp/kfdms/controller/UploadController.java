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
@RequestMapping("/upload")
public class UploadController {
    @Resource
    private FileService fileService;

    @Resource
    private FolderService folderService;





}
