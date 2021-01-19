package com.mp.kfdms.mapper;

import com.mp.kfdms.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/1/10
 * @Time 14:17
 */
@Mapper
public interface UserMapper {
    public List<User> findAll();
    public User findOneByUsername(User user);
    public User findOneByEmail(User user);
    public User findOneByTelephone(User user);
    public int changePassword(User user);
    public int changeTelephone(User user);
    public int login(User user);
    public int deleteUser(User user);
    public int registerUser(User user);
    public int activateUser(User user);
    public int changeImagePath(User user);
    public int changeUserName(User user);
    public int setBaseFolderId(User user);
}
