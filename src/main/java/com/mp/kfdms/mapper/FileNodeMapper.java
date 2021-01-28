package com.mp.kfdms.mapper;

import com.mp.kfdms.domain.FileNode;
import org.apache.ibatis.annotations.Mapper;

import java.io.File;
import java.util.List;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/1/14
 * @Time 15:00
 */
@Mapper
public interface FileNodeMapper {
    /**
     * 根据文件名返回指定文件夹下文件
     * @param fileNode
     * @return
     */
    public FileNode getFileByName(FileNode fileNode);

    /**
     * 添加一个文件
     * @param fileNode
     * @return
     */
    public int addNewFile(FileNode fileNode);

    /**
     * 删除一个文件
     * @param fileNode
     * @return
     */
    public int deleteFile(FileNode fileNode);

    /**
     * 根据文件MD5值返回文件
     * @param fileNode
     * @return
     */
    public FileNode getFileByMD5(FileNode fileNode);

}
