package com.mp.kfdms.controller;

import com.mp.kfdms.domain.Folder;
import com.mp.kfdms.domain.User;
import com.mp.kfdms.service.FileService;
import com.mp.kfdms.service.FolderService;
import com.mp.kfdms.service.UserService;
import com.mp.kfdms.util.GsonUtil;
import com.mp.kfdms.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

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
@RestController()
@RequestMapping("/DataCloud")
public class DataCloudController {
    @Autowired
    public UserService userservice;
    @Autowired
    public FolderService folderService;
    @Autowired
    public FileService fileService;

    @RequestMapping(value = {"/createFolder.ajax"})
    public String createFolder(final HttpServletRequest request, final HttpServletResponse response){
        String return_msg = folderService.createFolder(request, response);
        return return_msg;
    }

    /**
     * 根据folder_id返回文件夹下文件包括文件夹
     * @param request
     * @param folder_id
     * @return
     */
    @RequestMapping(value = {"/listFolders.ajax"})
    public String listFolders(final HttpServletRequest request, @RequestParam("folder_id") int folder_id){
        String return_msg="error";
        if(folder_id == 0){
            User user = UserUtil.getUserFromToken(request.getHeader("lg_token"));
            Folder baseFolder = folderService.getBaseFolderByUser(user);
            folder_id = baseFolder.getFolder_id();
        }
        String folders = folderService.listFolders(request, folder_id);
        if("error".equals(folders)){
            return return_msg;
        }else {
            return folders;
        }
    }

//    @RequestMapping(value = { "/doUploadFile.ajax" })
//    public String douploadFile(final HttpServletRequest request, final HttpServletResponse response,
//                               @RequestParam("file1") final MultipartFile file) throws IOException {
//        String base_path="C:\\Users\\J\\GitHub\\DFDMS\\kfdms\\src\\main\\resources\\tem_files";
//        File tem_file = new File(base_path+"\\"+"new_file.xls");
//        tem_file.createNewFile();
//        file.transferTo(tem_file);
//        return "OK";
//    }

    @RequestMapping("/doUploadFile.ajax") //使用filter来做编码设定,所以不需要 produces = { CHARSET_BY_AJAX }
    public String doUploadFile(final MultipartHttpServletRequest request){
        fileService.doUploadFile(request);
        return "OK";
    }

    @RequestMapping("testupload.ajax")
    public String testWebUpload(final MultipartHttpServletRequest request){
        return "success";
    }

}
