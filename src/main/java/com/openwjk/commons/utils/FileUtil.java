package com.openwjk.commons.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author wangjunkai
 * @description
 * @date 2023/6/23 14:01
 */
public class FileUtil {
    private static final String TEMP_PATH = "java.io.tmpdir";

    public static File getTempFile(String suffix) {
        if (StringUtils.isBlank(suffix) || StringUtils.equals(Constant.POINT, suffix.trim())) {
            throw new RuntimeException("suffix is empty");
        }
        if (!suffix.contains(Constant.POINT)) {
            suffix = Constant.POINT + suffix;
        }
        String directory = System.getProperty(TEMP_PATH);
        String date = DateUtil.formatDate(DateUtil.getNow(), DateUtil.FORMAT_DATE_NORMAL);
        String[] dateArr = date.split(Constant.MIDDLE_LINE);
        for (String str : dateArr) {
            directory += File.separator + str;
        }
        String fileName = directory + File.separator + RandomCodeUtil.generateRandomNo(Constant.EMPTY_STR, Constant.INT_EIGHT) + suffix;
        return prepareFile(fileName);
    }

    public static File prepareFile(String fullPath) {
        File file = new File(fullPath);
        File fileParent = file.getParentFile();
        if (!fileParent.exists()) {
            fileParent.mkdirs();
        }
        return file;
    }

    public static String getFileHeader(File file) throws IOException {
        FileInputStream is = new FileInputStream(file);
        byte[] b = new byte[20];
        is.read(b, 0, b.length);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            int v = b[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString().toUpperCase();
    }
}
