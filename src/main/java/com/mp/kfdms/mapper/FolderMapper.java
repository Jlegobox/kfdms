package com.mp.kfdms.mapper;

import com.mp.kfdms.domain.Folder;
import com.mp.kfdms.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/1/11
 * @Time 17:47
 */
@Mapper
public interface FolderMapper {
    public Folder getFolderById(int folderId);
    public int createFolder(Folder folder);
    public int getFolderByName(Folder folder);
    public List<Folder> getFolderByFolderParentId(int folder_id);

    public Folder getBaseFolderByUser(User user);
    public Folder getParentFolderByFolderParentId(int folder_parent_id);

    /**
     * 根据folder id查找folder下所有文件夹数目
     * @param currentFolder
     * @return
     */
    public int countFolder(Folder currentFolder);

    public int deleteFolderById(int folderId);

    public int updateFolder(Folder folder);
}
