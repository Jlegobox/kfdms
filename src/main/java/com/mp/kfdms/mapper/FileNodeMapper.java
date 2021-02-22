package com.mp.kfdms.mapper;

import com.mp.kfdms.domain.FileNode;
import com.mp.kfdms.domain.Folder;
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
     * @param MD5
     * @return
     */
    public List<FileNode> getFileByMD5(String MD5);

    public List<FileNode> getFileByFileFolderId(int fileNode);

    /**
     * 根据folder id查找folder下所有文件数目
     * @param folder
     * @return
     */
    public int countFile(Folder folder);

    public int deleteFileById(int fileId);

    public FileNode getFileById(int fileId);

    public int updateFileNode(FileNode fileNode);
}
