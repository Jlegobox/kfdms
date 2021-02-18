package com.mp.kfdms.util;

import com.mp.kfdms.pojo.FileInfo;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/1/21
 * @Time 12:58
 */

/**
 * 处理requset提取属性的操作
 */
public class RequestUtil {
    /**
     * 提取request的parameter，抓换为int。不存在则返回0
     *
     * @param request
     * @param parameterName
     * @return
     */
    public static int getInt(final HttpServletRequest request, final String parameterName) {
        String parameter = request.getParameter(parameterName);
        return parameter == null ? 0 : Integer.parseInt(parameter);
    }

    public static String getStr(final HttpServletRequest request, final String parameterName) {
        String parameter = request.getParameter(parameterName);
        return parameter == null ? "" : parameter;
    }

    public static FileInfo collectFileInfo(final HttpServletRequest request) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setId(request.getParameter("id"));
        fileInfo.setOriginFileName(request.getParameter("name"));
        String size = request.getParameter("size");
        fileInfo.setFileSize(Long.parseLong(size == null ? "0" : size));
        fileInfo.setFileType(request.getParameter("fileType"));
        fileInfo.setLastModifiedDate(request.getParameter("lastModifiedDate"));
        String chunks = request.getParameter("chunks");
        String chunk = request.getParameter("chunk");
        fileInfo.setChunks(Integer.parseInt(chunks == null ? "1" : chunks)); // 至少一个分片
        fileInfo.setCurrentChunk(Integer.parseInt(chunk == null ? "0" : chunk));
        fileInfo.setMD5(request.getParameter("uploadFileMD5"));
        if (fileInfo.getChunks() == 1 && request.getParameter("uploadFileSliceMD5") == null) {
            //总数为一个分片说明没有经历过分片，也就没有计算分片的MD5。分片的MD5就是整个文件的MD5
            fileInfo.setCurrentChunkMD5(fileInfo.getMD5());
        } else {
            fileInfo.setCurrentChunkMD5(request.getParameter("uploadFileSliceMD5"));
        }


        return fileInfo;
    }
}
