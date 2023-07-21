package com.openwjk.commons.utils;

import java.util.Random;
import java.util.UUID;

/**
 * @author wangjunkai
 * @description
 * @date 2023/6/23 14:05
 */
public class RandomCodeUtil {
    private static final String NUM = "0123456789";
    private static final Random random = new Random();

    public static String generateCode(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(CODE_ALPHABET[getRandomIndex()]);
        }
        return sb.toString();
    }

    public static String generateOnlyCode(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(UPPER_CASE_ALPHABET[getAlphabetRandomIndex()]);
        }
        return sb.toString();
    }

    public static String generateNum(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(NUM.charAt((int) (Math.random() * NUM.length())));
        }
        return sb.toString();
    }

    /**
     * 生成UUID
     */
    public static String getUuId() {
        return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }


    public static String generateRandomNo(String prefix, Integer length) {
        return prefix + DateUtil.formatNow(DateUtil.FORMAT_DATETIME_COMPACT) + generateNum(length);
    }

    private static int getRandomIndex() {
        return (int) (Math.random() * CODE_ALPHABET.length);
    }

    private static int getAlphabetRandomIndex() {
        return (int) (Math.random() * UPPER_CASE_ALPHABET.length);
    }

    /**
     * 生成随机数(最大值限制)
     */
    public static int randomInt(int maxNum) {
        return random.nextInt(maxNum);
    }

    private static final String[] UPPER_CASE_ALPHABET = {
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
            "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
            "U", "V", "W", "X", "Y", "Z"
    };
    private static final String[] CODE_ALPHABET = {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "A", "a", "B", "b", "C", "c", "D", "d", "E", "e",
            "F", "f", "G", "g", "H", "h", "I", "i", "J", "j",
            "K", "k", "L", "l", "M", "m", "N", "n", "O", "o",
            "P", "p", "Q", "q", "R", "r", "S", "s", "T", "t",
            "U", "u", "V", "v", "W", "w", "X", "x", "Y", "y",
            "Z", "z"
    };
}
