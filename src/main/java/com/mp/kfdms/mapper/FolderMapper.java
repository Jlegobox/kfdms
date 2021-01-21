package com.mp.kfdms.mapper;

import com.mp.kfdms.domain.Folder;
import org.apache.ibatis.annotations.Mapper;

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
}
