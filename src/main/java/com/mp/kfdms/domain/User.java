package com.mp.kfdms.domain;

import java.util.Date;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/1/10
 * @Time 14:15
 */
public class User {
    public int id;
    public String email;
    public String telephone;
    public String username;
    public String password;
    public int sex;
    public String student_id;
    public Date birthday;
    public int start_year;
    public int user_type;
    public String verification;
    public int active_status;
    public int base_folder_id;
    public String image_path;
    public Date register_time;
    public Date last_login;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public int getStart_year() {
        return start_year;
    }

    public void setStart_year(int start_year) {
        this.start_year = start_year;
    }

    public int getUser_type() {
        return user_type;
    }

    public void setUser_type(int user_type) {
        this.user_type = user_type;
    }

    public String getVerification() {
        return verification;
    }

    public void setVerification(String verification) {
        this.verification = verification;
    }

    public int getActive_status() {
        return active_status;
    }

    public void setActive_status(int active_status) {
        this.active_status = active_status;
    }

    public int getBase_folder_id() {
        return base_folder_id;
    }

    public void setBase_folder_id(int base_folder_id) {
        this.base_folder_id = base_folder_id;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public Date getRegister_time() {
        return register_time;
    }

    public void setRegister_time(Date register_time) {
        this.register_time = register_time;
    }

    public Date getLast_login() {
        return last_login;
    }

    public void setLast_login(Date last_login) {
        this.last_login = last_login;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", telephone='" + telephone + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", sex='" + sex + '\'' +
                ", student_id='" + student_id + '\'' +
                ", birthday=" + birthday +
                ", start_year=" + start_year +
                ", user_type=" + user_type +
                ", verification='" + verification + '\'' +
                ", active_status=" + active_status +
                ", base_folder_id=" + base_folder_id +
                ", image_path='" + image_path + '\'' +
                ", register_time=" + register_time +
                ", last_login=" + last_login +
                '}';
    }
}
