package com.mp.kfdms.controller;

import com.mp.kfdms.annotation.CurrentUser;
import com.mp.kfdms.domain.User;
import com.mp.kfdms.pojo.FileShareLinkInfo;
import com.mp.kfdms.pojo.FolderView;
import com.mp.kfdms.pojo.JsonModel;
import com.mp.kfdms.service.FileShareService;
import com.mp.kfdms.util.FileShareUtil;
import com.mp.kfdms.util.GsonUtil;
import com.mp.kfdms.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.plaf.PanelUI;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/2/22
 * @Time 20:14
 */
@Controller
@RequestMapping("/DataCloud/Share")
public class DataCloudShareController {

    @Autowired
    private FileShareService fileShareService;

    @RequestMapping("initTest")
    public String test(HttpServletResponse response) {
        System.out.println("进入");
        return "forward:/login.html";
    }

    @RequestMapping("createLink.ajax")
    @ResponseBody
    public String createLink(@CurrentUser final User currentUser, final FileShareLinkInfo fileShareLinkInfo) {
        // 权限校验
        JsonModel jsonModel = fileShareService.createLink(currentUser, fileShareLinkInfo);

        return GsonUtil.instance().toJson(jsonModel);
    }

    @RequestMapping("getShareLinkList.ajax")
    @ResponseBody
    public String getShareLinkList(@CurrentUser final User currentUser) {
        JsonModel shareLinkList = fileShareService.getShareLinkList(currentUser);
        return GsonUtil.instance().toJson(shareLinkList);
    }

    @RequestMapping("cancelShareLink.ajax")
    @ResponseBody
    public String cancelShareLink(@CurrentUser final User currentUser, int shareLogId) {
        JsonModel jsonModel = fileShareService.cancelShareLink(currentUser, shareLogId);
        return GsonUtil.instance().toJson(jsonModel);
    }

    @RequestMapping("cancelAllShareLink.ajax")
    @ResponseBody
    public String cancelShareLink(@CurrentUser final User currentUser, @RequestParam(value = "shareLogIdList[]", defaultValue = "0") List<Integer> shareLogIdList) {
        JsonModel jsonModel = fileShareService.cancelAllShareLink(currentUser, shareLogIdList);
        return GsonUtil.instance().toJson(jsonModel);
    }

    @RequestMapping("checkShareLink.ajax")
    @ResponseBody
    public String checkShareLink(@CurrentUser final User currentUser, final String shareLinkData) {
        JsonModel jsonModel = fileShareService.checkShareLink(currentUser, shareLinkData);
        return GsonUtil.instance().toJson(jsonModel);
    }

    @RequestMapping("getSharedFile.ajax")
    @ResponseBody
    public String getSharedFile(final String authCode) {
        JsonModel jsonModel = fileShareService.getSharedFile(authCode);
        return GsonUtil.instance().toJson(jsonModel);
    }

    @RequestMapping("downloadSharedFile.ajax")
    @ResponseBody
    public String downloadSharedFile(@CurrentUser User currentUser,HttpServletRequest request,
                                     HttpServletResponse response, String authCode, int shareLogId) {
        return fileShareService.downloadSharedFile(currentUser,request, response, authCode, shareLogId);
    }
}
