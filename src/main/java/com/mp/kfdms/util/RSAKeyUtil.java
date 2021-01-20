package com.mp.kfdms.util;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

/**
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/1/16
 * @Time 21:49
 */

/**
 * 服务器的公钥和密钥服务9
 */
@Component
public class RSAKeyUtil {
    private static final int KEY_SIZE = 1024;
    private static Key publicKey;
    private static Key privateKey;
    private static Base64.Encoder encoder;
    private static Base64.Decoder decoder;
    private static KeyFactory kf;
    private static Cipher c;
    // 公钥和密钥
    private static String publicKeyStr;
    private static String privateKeyStr;

    static {
        RSAKeyUtil.encoder = Base64.getEncoder();
        RSAKeyUtil.decoder = Base64.getDecoder();
        try {
            RSAKeyUtil.kf = KeyFactory.getInstance("RSA");
            RSAKeyUtil.c = Cipher.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e2) {
            e2.printStackTrace();
        }
    }

    /**
     * 使用私钥解密公钥加密的信息
     * @param context
     * @param privateKey
     * @return
     */
    public static String dncryption(final String context, final String privateKey) {
        final byte[] b = RSAKeyUtil.decoder.decode(privateKey);
        final byte[] s = RSAKeyUtil.decoder.decode(context.getBytes(StandardCharsets.UTF_8));
        final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(b);
        try {
            final PrivateKey key = RSAKeyUtil.kf.generatePrivate(spec);
            RSAKeyUtil.c.init(Cipher.DECRYPT_MODE, key);
            final byte[] f = RSAKeyUtil.c.doFinal(s);
            return new String(f);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("错误：RSA解密失败。");
        }
        return null;
    }

    /**
     * 使用公钥进行加密
     * @param context
     * @param key
     * @return
     */
    public static String encryption(final String context,final String key){ //使用公钥进行加密
        try {
            RSAKeyUtil.c.init(Cipher.ENCRYPT_MODE, publicKey);
            final byte[] cipherText = RSAKeyUtil.c.doFinal(context.getBytes(StandardCharsets.UTF_8));
            return RSAKeyUtil.encoder.encodeToString(cipherText);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("错误：RSA加密失败。");
        }
        return null;
    }

    public RSAKeyUtil() {
        try {
            final KeyPairGenerator g = KeyPairGenerator.getInstance("RSA");
            g.initialize(KEY_SIZE);
            final KeyPair pair = g.genKeyPair();
            this.publicKey = pair.getPublic();
            this.privateKey = pair.getPrivate();
            this.publicKeyStr = new String(this.encoder.encode(this.publicKey.getEncoded()), StandardCharsets.UTF_8);
            this.privateKeyStr = new String(this.encoder.encode(this.privateKey.getEncoded()), StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e.getMessage());
            System.out.println("错误：RSA密钥生成失败。");
        }
    }

    public static String getPublicKey() {
        return RSAKeyUtil.publicKeyStr;
    }

    public static String getPrivateKey() {
        return RSAKeyUtil.privateKeyStr;
    }

    public static int getKeySize() {
        return KEY_SIZE;
    }
}
