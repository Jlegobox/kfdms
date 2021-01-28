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
    private String file_encode_name;
    private String file_name;
    private int file_folder_id;
    private int file_owner_id;
    private int file_type;
    private String data_type;
    private String file_description;
    private long file_size;
    private String file_md5;
    private int file_permission;
    private Date file_create_time;
    private int file_download_num;
    private int file_visited_num;
    private Date file_modified_time;

    public int getFile_id() {
        return file_id;
    }

    public void setFile_id(int file_id) {
        this.file_id = file_id;
    }

    public String getFile_encode_name() {
        return file_encode_name;
    }

    public void setFile_encode_name(String file_encode_name) {
        this.file_encode_name = file_encode_name;
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

    public String getData_type() {
        return data_type;
    }

    public void setData_type(String data_type) {
        this.data_type = data_type;
    }

    public String getFile_description() {
        return file_description;
    }

    public void setFile_description(String file_description) {
        this.file_description = file_description;
    }

    public long getFile_size() {
        return file_size;
    }

    public void setFile_size(long file_size) {
        this.file_size = file_size;
    }

    public String getFile_md5() {
        return file_md5;
    }

    public void setFile_md5(String file_MD5) {
        this.file_md5 = file_MD5;
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

    public int getFile_visited_num() {
        return file_visited_num;
    }

    public void setFile_visited_num(int file_visited_num) {
        this.file_visited_num = file_visited_num;
    }

    public Date getFile_modified_time() {
        return file_modified_time;
    }

    public void setFile_modified_time(Date file_modified_time) {
        this.file_modified_time = file_modified_time;
    }
}
