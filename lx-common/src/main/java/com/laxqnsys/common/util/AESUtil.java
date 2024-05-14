package com.laxqnsys.common.util;

import com.laxqnsys.common.enums.ErrorCodeEnum;
import com.laxqnsys.common.exception.BusinessException;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Java使用AES加密算法进行加密解密
 */
public class AESUtil {

    /**
     * AES算法加密
     *
     * @Param:text原文
     * @Param:key密钥
     */
    public static String encrypt(String text, String key) {
        try {
            // 创建AES加密算法实例(根据传入指定的秘钥进行加密)
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");

            // 初始化为加密模式，并将密钥注入到算法中
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);

            // 将传入的文本加密
            byte[] encrypted = cipher.doFinal(text.getBytes());

            //生成密文
            // 将密文进行Base64编码，方便传输
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "AES加密失败", e);
        }
    }

    /**
     * AES算法解密
     *
     * @Param:base64Encrypted密文
     * @Param:key密钥
     */
    public static String decrypt(String base64Encrypted, String key) {
        try {
            // 创建AES解密算法实例
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");

            // 初始化为解密模式，并将密钥注入到算法中
            cipher.init(Cipher.DECRYPT_MODE, keySpec);

            // 将Base64编码的密文解码
            byte[] encrypted = Base64.getDecoder().decode(base64Encrypted);

            // 解密
            byte[] decrypted = cipher.doFinal(encrypted);
            return new String(decrypted);
        } catch (Exception e) {
            throw new BusinessException(ErrorCodeEnum.ERROR.getCode(), "AES解密失败！", e);
        }
    }

    public static void main(String[] args) {
        //明文
        String text = "1n2n3n4n5i6kh7j6kuaq";
        //秘钥(需要使用长度为16、24或32的字节数组作为AES算法的密钥，否则就会遇到java.security.InvalidKeyException异常)
        String key = "23423jAFsdf323ls";
        //加密，生成密文
        String base64Encrypted = encrypt(text, key);

        System.out.println(base64Encrypted);
        System.out.println(base64Encrypted.length());
        //解密，获取明文
        String text2 = decrypt(base64Encrypted, key);

        System.out.println(text2);
    }
}