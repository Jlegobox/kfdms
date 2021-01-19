package com.mp.kfdms.pojo;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/1/16
 * @Time 22:01
 */
public class PublicKeyInfo {
    private String publicKey;
    private long time;

    public String getPublicKey() {
        return this.publicKey;
    }

    public void setPublicKey(final String publicKey) {
        this.publicKey = publicKey;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(final long time) {
        this.time = time;
    }
}
