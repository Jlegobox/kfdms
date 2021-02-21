package com.mp.kfdms.util;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

/**
 * 服务器事件相关操作工具类
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/2/21
 * @Time 11:11
 */
public class ServerTimeUtil {
    public static String accurateToSecond() {
        LocalDateTime ldt = LocalDateTime.now();
        DateTimeFormatter dtfDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");
        return dtfDateTimeFormatter.format(ldt);
    }

    public static String accurateToMinute() {
        LocalDateTime ldt = LocalDateTime.now();
        DateTimeFormatter dtfDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm");
        return dtfDateTimeFormatter.format(ldt);
    }

    public static String accurateToDay() {
        LocalDateTime ldt = LocalDateTime.now();
        DateTimeFormatter dtfDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
        return dtfDateTimeFormatter.format(ldt);
    }

    public static String accurateToLogName() {
        LocalDateTime ldt = LocalDateTime.now();
        DateTimeFormatter dtfDateTimeFormatter = DateTimeFormatter.ofPattern("yyyy_MM_dd");
        return dtfDateTimeFormatter.format(ldt);
    }

    public static Date getServerTime() {
        return new Date();
    }

    /**
     *
     * <h2>从文件实体生成“最后修改时间”标签</h2>
     * <p>
     * 该方法用于生成符合HTTP规范的Last-Modified格式时间截，用于判定资源的最后修改日期。
     * </p>
     *
     * @param block
     *            java.io.File 要生成的文件块对象，应该是文件，但也支持文件夹，或者null
     * @return java.lang.String 记录最后修改日期的时间截，格式类似于“Wed, 29 Apr 2020 08:18:43
     *         GMT”，若传入文件不存在或为null，则返回当前时间
     */
    public static String getLastModifiedFormBlock(File block) {
        ZonedDateTime longToTime;
        if (block != null && block.exists()) {
            longToTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(block.lastModified()), ZoneId.of("GMT"));
        } else {
            longToTime = ZonedDateTime.now(ZoneId.of("GMT"));
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
                .withZone(ZoneId.of("GMT"));
        return longToTime.format(dtf).trim();
    }
}
