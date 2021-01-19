package com.mp.kfdms.domain;

import java.util.Date;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/1/14
 * @Time 15:05
 */
public class FileNode {
    private int file_id;
    private String file_name;
    private int file_folder_id;
    private int file_owner_id;
    private int file_type;
    private int is_private;
    private String file_path;
    private int file_size;
    private int file_permission;
    private Date file_create_time;
    private int file_download_num;
    private Date file_modified_time;

    public int getFile_id() {
        return file_id;
    }

    public void setFile_id(int file_id) {
        this.file_id = file_id;
    }

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public int getFile_folder_id() {
        return file_folder_id;
    }

    public void setFile_folder_id(int file_folder_id) {
        this.file_folder_id = file_folder_id;
    }

    public int getFile_owner_id() {
        return file_owner_id;
    }

    public void setFile_owner_id(int file_owner_id) {
        this.file_owner_id = file_owner_id;
    }

    public int getFile_type() {
        return file_type;
    }

    public void setFile_type(int file_type) {
        this.file_type = file_type;
    }

    public int getIs_private() {
        return is_private;
    }

    public void setIs_private(int is_private) {
        this.is_private = is_private;
    }

    public String getFile_path() {
        return file_path;
    }

    public void setFile_path(String file_path) {
        this.file_path = file_path;
    }

    public int getFile_size() {
        return file_size;
    }

    public void setFile_size(int file_size) {
        this.file_size = file_size;
    }

    public int getFile_permission() {
        return file_permission;
    }

    public void setFile_permission(int file_permission) {
        this.file_permission = file_permission;
    }

    public Date getFile_create_time() {
        return file_create_time;
    }

    public void setFile_create_time(Date file_create_time) {
        this.file_create_time = file_create_time;
    }

    public int getFile_download_num() {
        return file_download_num;
    }

    public void setFile_download_num(int file_download_num) {
        this.file_download_num = file_download_num;
    }

    public Date getFile_modified_time() {
        return file_modified_time;
    }

    public void setFile_modified_time(Date file_modified_time) {
        this.file_modified_time = file_modified_time;
    }
}
