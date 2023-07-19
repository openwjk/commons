package com.openwjk.commons.utils;


import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author wangjunkai
 * @description
 * @date 2023/6/23 09:10
 */
public class StackTraceUtil {
    /**
     * 完整的堆栈信息
     *
     * @param e Exception
     * @return Full StackTrace
     */

    public static String getStackTrace(Exception e) {
        StringWriter sw = null;
        PrintWriter pw = null;
        try {
            sw = new StringWriter();
            pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.flush();
            sw.flush();
        } finally {
            if (sw != null) {
                try {
                    sw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (pw != null) {
                pw.close();
            }
        }
        return sw.toString();
    }
}
