package com.mp.kfdms.mapper;

import com.mp.kfdms.domain.FileShareShareLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 文件分享操作
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/2/23
 * @Time 11:33
 */
@Mapper
public interface FileShareMapper {
    /**
     * 向fileshare_sharelog中添加一条记录
     * @param fileShareShareLog
     * @return
     */
    public int addShareLog(FileShareShareLog fileShareShareLog);

    public int updateShareLog(FileShareShareLog fileShareShareLog);

    public FileShareShareLog getShareLogById(int logId);

    /**
     * 返回用户创建的所有未删除记录
     * @param id
     * @return
     */
    public List<FileShareShareLog> getShareLogByUserId(int id);
}
