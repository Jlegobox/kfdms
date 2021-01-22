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
    public Folder getFolderById(Folder folder);
    public int createFolder(Folder folder);
    public int getFolderByName(Folder folder);
    public List<Folder> getFolderByFolderParentId(int folder_id);

    public Folder getBaseFolderByUser(User user);
}
