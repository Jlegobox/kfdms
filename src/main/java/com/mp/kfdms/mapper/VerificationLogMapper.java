package com.mp.kfdms.mapper;

import com.mp.kfdms.domain.VerificationLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/3/3
 * @Time 20:48
 */
@Mapper
public interface VerificationLogMapper {
    public int addLog(VerificationLog verificationLog);
    public int updateLog(VerificationLog verificationLog);
    public VerificationLog findOne(String code);
    public List<VerificationLog> findAll(int ownerId);
}
