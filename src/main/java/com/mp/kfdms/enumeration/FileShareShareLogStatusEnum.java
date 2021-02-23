package com.mp.kfdms.enumeration;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/2/22
 * @Time 23:50
 */
public enum FileShareShareLogStatusEnum {
    CREATE_FAIL, // 创建失败
    SHARING, // 分享中
    EXPIRED, // 过期链接（因为时间到期或者访问次数到期）
    CHANCEL, // 被取消而删除的链接（前端不再展示）
    DELETE,  // 已删除的链接 （前端不再展示，到期后删除）
}
