package com.openwjk.commons.utils;

import lombok.Data;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EncryptUtil {

    private static final String KEY_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";
    private static final String ENCODING_UTF8 = "UTF-8";
    private static final String PUBLIC_KEY = "RSAPublicKey";
    private static final String PRIVATE_KEY = "RSAPrivateKey";
    private static final String ENCRYPT_AES = "AES";
    private static final String AES_ECB_ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final String AES_CBC_ALGORITHM = "AES/CBC/PKCS5Padding";

    /**
     * RSA秘钥长度
     */
    private static final int KEY_SIZE = 2048;
    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = KEY_SIZE / 8 - 11;
    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = KEY_SIZE / 8;

    public static RsaKey createRsaKey() throws Exception {
        Map<String, Object> keyMap = initKey();
        String publicKey = getPublicKey(keyMap).replaceAll("[\n\r]", "");
        String privateKey = getPrivateKey(keyMap).replaceAll("[\n\r]", "");
        RsaKey rsaKey = new RsaKey();
        rsaKey.setPublicKey(publicKey);
        rsaKey.setPrivateKey(privateKey);
        return rsaKey;
    }

    private static Map<String, Object> initKey() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(KEY_SIZE);
        KeyPair keyPair = keyPairGen.generateKeyPair();

        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }

    private static String getPublicKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return encodeBASE64(key.getEncoded());
    }

    private static String getPrivateKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return encodeBASE64(key.getEncoded());
    }

    public static PrivateKey getPrivateKey(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = Base64.decodeBase64(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;

    }

    public static PublicKey getPublicKey(String key) throws Exception {
        byte[] keyBytes;
        keyBytes = Base64.decodeBase64(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;

    }

    public static String encodeBASE64(byte[] key) {
        Base64 base = new Base64();
        byte[] encodedBuffer = base.encode(key);
        return new String(encodedBuffer);
    }

    public static byte[] decodeBASE64(String base64) {
        Base64 base = new Base64();
        return base.decode(base64);
    }


    /**
     * SHA256WithRSA签名
     */
    public static byte[] signSHA256WithRSA(String data, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException,
            SignatureException, UnsupportedEncodingException {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(data.getBytes(ENCODING_UTF8));
        return signature.sign();
    }

    /**
     * SHA256WithRSA验签
     */
    public static boolean verifySHA256WithRSA(String data, byte[] sign, PublicKey publicKey) throws Exception {
        if (data == null || sign == null || publicKey == null) {
            return false;
        }
        Signature signetcheck = Signature.getInstance(SIGNATURE_ALGORITHM);
        signetcheck.initVerify(publicKey);
        signetcheck.update(data.getBytes(ENCODING_UTF8));
        return signetcheck.verify(sign);
    }

    /**
     * 获取拼接后的字符串
     */
    public static String getConnectedParamString(Map<String, String> map) {
        ArrayList<String> list = new ArrayList<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (null != entry.getValue() && !"".equals(entry.getValue())) {
                list.add(entry.getKey() + "=" + entry.getValue() + "&");
            }
        }
        int size = list.size();
        String[] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();
        result = result.substring(0, result.length() - 1);
        return result;
    }


    /**
     * 公钥加密
     * @see EncryptUtil#decryptByPrivateKey(byte[], java.lang.String) 解密
     * @param data      源数据
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey)
            throws Exception {
        byte[] keyBytes = decodeBASE64(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }


    /**
     * 使用私钥解密
     * @see  EncryptUtil#encryptByPublicKey(byte[], java.lang.String) 加密
     * @param data 已加密数据
     */
    public static String decryptByPrivateKey(byte[] data, String privateKey) {
        // 加密
        String str = "";
        try {
            byte[] keyBytes = decodeBASE64(privateKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory fac = KeyFactory.getInstance(KEY_ALGORITHM);
            RSAPrivateKey privateK = (RSAPrivateKey) fac.generatePrivate(keySpec);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateK);

            int inputLen = data.length;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int offset = 0;
            byte[] cache;
            int i = 0;
            // 对数据分段解密
            while (inputLen - offset > 0) {
                if (inputLen - offset > MAX_DECRYPT_BLOCK) {
                    cache = cipher.doFinal(data, offset, MAX_DECRYPT_BLOCK);
                } else {
                    cache = cipher.doFinal(data, offset, inputLen - offset);
                }
                out.write(cache, 0, cache.length);
                i++;
                offset = i * MAX_DECRYPT_BLOCK;
            }
            byte[] decryptedData = out.toByteArray();
            out.close();
            // 解密后的内容
            str = new String(decryptedData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    @Data
    public static class RsaKey implements Serializable {
        private static final Long serialVersionUID = 1L;
        private String publicKey;
        private String privateKey;
    }



    /**
     * AES加密
     * AES-ECB  PKCS5Padding补位
     * @param key 128 192 256 位
     */
    public static String aesECBEncrypt(String src, String key) throws Exception {
        byte[] raw = key.getBytes(ENCODING_UTF8);
        SecretKeySpec skeySpec = new SecretKeySpec(raw, ENCRYPT_AES);
        Cipher cipher = Cipher.getInstance(AES_ECB_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(src.getBytes(ENCODING_UTF8));
        return encodeBASE64(encrypted);
    }

    /**
     * AES解密
     * AES-ECB  PKCS5Padding补位
     * @param key 128 192 256 位
     */
    public static String aesECBDecrypt(byte[] src, String key) throws Exception {
        byte[] raw = key.getBytes(ENCODING_UTF8);
        SecretKeySpec skeySpec = new SecretKeySpec(raw, ENCRYPT_AES);
        Cipher cipher = Cipher.getInstance(AES_ECB_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] original = cipher.doFinal(src);
        return new String(original, ENCODING_UTF8);
    }
    /**
     * AES解密
     * AES-CBC  PKCS5Padding补位
     * @param key 密钥 128 192 256 位
     * @param iv 偏移向量 128 位
     */
    public static String aesCBCEncrypt(String data, String key, String iv) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_CBC_ALGORITHM);
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ENCRYPT_AES);
        IvParameterSpec ivParams = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));

        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParams);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

        return encodeBASE64(encryptedBytes);
    }
    /**
     * AES解密
     * AES-CBC  PKCS5Padding补位
     * @param key 密钥 128 192 256 位
     * @param iv 偏移向量 128 位
     */
    public static String aesCBCDecrypt(String data, String key, String iv) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_CBC_ALGORITHM);
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ENCRYPT_AES);
        IvParameterSpec ivParams = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));

        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParams);
        byte[] decryptedBytes = cipher.doFinal(decodeBASE64(data));

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}