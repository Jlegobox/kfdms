package com.mp.kfdms.util;

import com.mp.kfdms.domain.FileShareShareLog;
import com.mp.kfdms.domain.User;
import com.mp.kfdms.enumeration.FileShareShareLogStatusEnum;
import com.mp.kfdms.pojo.FileShareLinkInfo;

import java.util.Calendar;
import java.util.Random;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/2/23
 * @Time 11:40
 */
public class FileShareUtil {
    // 与需要加密的文件
    private static String CODE_SALT = "codeSalt";
    private static String accessCodeCharacter = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static FileShareShareLog getShareLog(User currentUser, FileShareLinkInfo fileShareLinkInfo) {
        FileShareShareLog fileShareShareLog = new FileShareShareLog();
        fileShareShareLog.setUserId(currentUser.getId());
        fileShareShareLog.setFileId(fileShareLinkInfo.getFileId());
        fileShareShareLog.setFolderId(fileShareLinkInfo.getFolderId());
        String accessCode = fileShareLinkInfo.getAccessCode();
        if ("-1".equals(accessCode)) { // -1为随机
            StringBuilder randomAccessCode = new StringBuilder();
            Random random = new Random();
            for (int i = 0; i < 4; i++) {
                randomAccessCode.append(accessCodeCharacter.charAt(random.nextInt(accessCodeCharacter.length())));
            }
            accessCode = randomAccessCode.toString();
        }
        fileShareShareLog.setAccessCode(accessCode);
        fileShareShareLog.setVisitNum(0);
        if (fileShareLinkInfo.getVisitLimit() == 0)
            return null;
        fileShareShareLog.setVisitLimit(fileShareLinkInfo.getVisitLimit());
        fileShareShareLog.setValidPeriod(fileShareLinkInfo.getValidPeriod());
        // 设置创建的时间
        Calendar instance = Calendar.getInstance();
        fileShareShareLog.setCreateTime(instance.getTime());
        //设置过期时间
        int validPeriod = fileShareLinkInfo.getValidPeriod();
        if (validPeriod != -1) {
            instance.add(Calendar.DATE, validPeriod);
            fileShareShareLog.setExpiredTime(instance.getTime());
        } else {
            fileShareShareLog.setExpiredTime(null);
        }
        fileShareShareLog.setDeleteTime(null);
        // 设置初始状态为创建失败
        fileShareShareLog.setStatus(FileShareShareLogStatusEnum.CREATE_FAIL.ordinal());
        return fileShareShareLog;
    }

    /**
     * 根据logId和accessCode创建一个密文
     *
     * @param shareLog
     * @return
     */
    public static String createShareLink(FileShareShareLog shareLog) {
        int shareLogId = shareLog.getShareLogId();
        // 强行破解还需要CODE_SALT
        String encodedContent = AESUtil.encodeBase64(String.valueOf(shareLogId) + CODE_SALT, shareLog.getAccessCode() + CODE_SALT);
        if (encodedContent != null) {
            return encodedContent;
        }
        return null;
    }

    /**
     * 根据shareLink和输入的AccessCode返回logId
     *
     * @param shareLink
     * @param accessCode
     * @return
     */
    public static int decodeShareLink(String shareLink, String accessCode) {
        if (accessCode == null || shareLink == null)
            return -1;
        String s = AESUtil.decodeBase64(shareLink, accessCode + CODE_SALT);
        if (s != null && s.length() > CODE_SALT.length()) {
            try {
                int logId = Integer.parseInt(s.substring(0, s.length() - CODE_SALT.length()));
                return logId;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }
}
