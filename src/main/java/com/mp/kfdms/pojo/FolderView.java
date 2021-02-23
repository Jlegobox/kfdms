package com.mp.kfdms.pojo;

import com.mp.kfdms.domain.FileNode;
import com.mp.kfdms.domain.Folder;

import java.util.ArrayList;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/1/29
 * @Time 22:42
 */
public class FolderView {
    private ArrayList<Folder> folders = new ArrayList<>();
    private ArrayList<FileNode> files = new ArrayList<>();
    // TODO: 2021/2/23  更换为FilePermission 进行权限验证
    private ArrayList<String> foldersPermission = new ArrayList<>();
    private ArrayList<String> filesPermission = new ArrayList<>();

    private PageParam pageParam = new PageParam();

    public PageParam getPageParam() {
        return pageParam;
    }

    public void setPageParam(PageParam pageParam) {
        this.pageParam = pageParam;
    }

    public ArrayList<Folder> getFolders() {
        return folders;
    }

    public void setFolders(ArrayList<Folder> folders) {
        this.folders = folders;
    }

    public ArrayList<FileNode> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<FileNode> files) {
        this.files = files;
    }

    public ArrayList<String> getFoldersPermission() {
        return foldersPermission;
    }

    public void setFoldersPermission(ArrayList<String> foldersPermission) {
        this.foldersPermission = foldersPermission;
    }

    public ArrayList<String> getFilesPermission() {
        return filesPermission;
    }

    public void setFilesPermission(ArrayList<String> filesPermission) {
        this.filesPermission = filesPermission;
    }
}
