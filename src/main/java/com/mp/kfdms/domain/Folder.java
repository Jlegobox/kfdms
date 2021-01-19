package com.mp.kfdms.domain;

import java.util.Date;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/1/11
 * @Time 17:36
 */
public class Folder {
    public int folder_id;
    public String folder_name;
    public int folder_parent_id;
    public int folder_owner_id;
    public int folder_type;
    public int is_private;
    public String folder_description;
    public Date folder_create_time;
    public Date folder_modified_time;

    public int getFolder_id() {
        return folder_id;
    }

    public void setFolder_id(int folder_id) {
        this.folder_id = folder_id;
    }

    public String getFolder_name() {
        return folder_name;
    }

    public void setFolder_name(String folder_name) {
        this.folder_name = folder_name;
    }

    public int getFolder_parent_id() {
        return folder_parent_id;
    }

    public void setFolder_parent_id(int folder_parent_id) {
        this.folder_parent_id = folder_parent_id;
    }

    public int getFolder_owner_id() {
        return folder_owner_id;
    }

    public void setFolder_owner_id(int folder_owner_id) {
        this.folder_owner_id = folder_owner_id;
    }

    public int getFolder_type() {
        return folder_type;
    }

    public void setFolder_type(int folder_type) {
        this.folder_type = folder_type;
    }

    public int getIs_private() {
        return is_private;
    }

    public void setIs_private(int is_private) {
        this.is_private = is_private;
    }

    public String getFolder_description() {
        return folder_description;
    }

    public void setFolder_description(String folder_description) {
        this.folder_description = folder_description;
    }

    public Date getFolder_create_time() {
        return folder_create_time;
    }

    public void setFolder_create_time(Date folder_create_time) {
        this.folder_create_time = folder_create_time;
    }

    public Date getFolder_modified_time() {
        return folder_modified_time;
    }

    public void setFolder_modified_time(Date folder_modified_time) {
        this.folder_modified_time = folder_modified_time;
    }
}
