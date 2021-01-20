package com.mp.kfdms.pojo;

import java.util.Date;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/1/19
 * @Time 16:43
 */
public class LoginInfo {
    private String email;
//    private String telephone;
//    private String username;
    private String password;
    private String time;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
