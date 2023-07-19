package com.openwjk.commons;


import com.openwjk.commons.utils.EncryptUtil;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

/**
 * @author wangjunkai
 * @description
 * @date 2023/6/28 17:14
 */
public class EncryptUtilTest {
    private String PRIVATE_KEY = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCP1MXaZ9e1DcnlkDlQeQ0VxYm01R8gu74Nbm/2u3sp2NtMj6+feuh8nDq2LwxfzclvrAIAKAJYYGsc6c2YHXiOrrDOIVta0OTflZlQPz0KwBxqR7gfeNIceMpBrXln5/yhvXYEmbcEz760i4AujrYOyMZLenNxr7NU9yf/F7wgVy6ez78d8SEhYCPrmeH6O2wbMFZSDDnsFrwHzvui6vyYCOAmnUhsdFN54e2e0ByjqcLB8HFvLE3kEn1YpiWsmLnVmXDXEdbCN8B91R9ub9G5T3RnUl0+jUyq/UZq+hvVUxD8vaRrbz7ZWH8nxFxPMTicSuuJ4/IgZXBSmhRGnIsLAgMBAAECggEAf3oAhcuFhkWbTKhf8+/l5gb3PtupRBTBpBvehhqyVWuLKjcrW7jOdyrsam4WlI7KWvxjpYnQUk3agXzDKG/uWt9/eqJb2ojMYVGw2UvDKVUNmjDIgrRN3Mg90qoSsUIOSbem8vreGBicGF6PlDQOLdpKzbjQutETq1mHDHMX3Lyn4JgjffkPZF/DPBt2wh2PuZ+eEifVRc2U4DjgQT5aYMFkwGG7/9h6vbbVCzKQ7p3BEveILpKgaDWg9LjhPaiUd/Ov+jSvZghiG7ClTcvHcNgJ4NcDofK7T2j0pIqXGDAdl7dmBaYXiukqWXwjDFePNyAKBRlYwkHD/30v55qcsQKBgQD7IgO3XwmRgJxVaEsVJ8U+QXhF43gxVwH7gBYmMVaLsiXvdm085H5vH036QoB9Iz3aXjK48HmBnRw1WZnpI96vfhY+o8xOm66Lk4947mtkHVifQRXjI22brNvOUhsOaa0yV0jGExlnVAWNtlM3lPKpSkwMPYbaYF33RzY+dshH+QKBgQCSnmKdgaYjEIsBtqeSvV3h1hueTzdRpzRky/melcZMZn925H3k/saV46J6b7ogLojdhbkvVFPA/sGrj5HFINMZlZ/V09LOZ5kV8KeshwCniVdRtuGJ0pqaGtNiWELOIvYUaqHCJY/0xbLk4R4Ez9iw0CHjTK8LGlHnG5PKJ8VUIwKBgG3J2gwRfVJiAnaIKMLv5xgncRhEsKgJXLGBtq0txqM9173PbURAtFGKQwHUju2mOYU8xzn4z3XtpIZiTJoS92iLW9g4rbnz76PPd9qriMF03uiIgLQTKfjz4ghZ9sAfTPuEEymc8e8tWVwxyEG1WTzPYV28jDbuCEZxmHJIZZipAoGARi4+Mfp/FcWzZAyURa8TIyija7GrXxBUCfOA9qHmm2dsl/JjOJujXtfpTAXy57ielrqNm9QXiwfmmEJOmjdOFK8EJQwRme8I3fGlsPWmIIujGmAZsSj4C8/PYwGwaI9fjwJDj3T/ZUDEkr4W6mK67FeUAQMRvpTIo4EVyuwijlUCgYB4yv/ls3AhsMHAVDehFhqjXK0Z0zvy5xX331qBHp14QUvTXjFz7vdoRovcxC/VaE3gIGyGB8bwjQAdg0eWbI/Ny88V4/LlaIxHudMRylLK6BSC9wu1tLE2h0qECyag8U0TGRzbWWH0GIdAZ4qvx0emanvFuyKcTZIqlbqLzlE3Jw==";
    private String PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAj9TF2mfXtQ3J5ZA5UHkNFcWJtNUfILu+DW5v9rt7KdjbTI+vn3rofJw6ti8MX83Jb6wCACgCWGBrHOnNmB14jq6wziFbWtDk35WZUD89CsAcake4H3jSHHjKQa15Z+f8ob12BJm3BM++tIuALo62DsjGS3pzca+zVPcn/xe8IFcuns+/HfEhIWAj65nh+jtsGzBWUgw57Ba8B877our8mAjgJp1IbHRTeeHtntAco6nCwfBxbyxN5BJ9WKYlrJi51Zlw1xHWwjfAfdUfbm/RuU90Z1JdPo1Mqv1Gavob1VMQ/L2ka28+2Vh/J8RcTzE4nErriePyIGVwUpoURpyLCwIDAQAB";

    @Test
    public void getRsaKey() throws Exception {
        EncryptUtil.RsaKey rsaKey = EncryptUtil.createRsaKey();
        System.out.println(rsaKey.getPrivateKey());
        System.out.println(rsaKey.getPublicKey());
    }

    @Test
    public void rsaSign() throws Exception {
        String sign = EncryptUtil.encodeBASE64(EncryptUtil.signSHA256WithRSA("xxx", EncryptUtil.getPrivateKey(PRIVATE_KEY)));
        System.out.println(sign);
        System.out.println(EncryptUtil.verifySHA256WithRSA("xxx", EncryptUtil.decodeBASE64(sign), EncryptUtil.getPublicKey(PUBLIC_KEY)));
    }

    @Test
    public void rsaEncrypt() throws Exception {
        String data = EncryptUtil.encodeBASE64(EncryptUtil.encryptByPublicKey(("xxx").getBytes(StandardCharsets.UTF_8), PUBLIC_KEY));
        System.out.println(data);
        System.out.println(EncryptUtil.decryptByPrivateKey(EncryptUtil.decodeBASE64(data),PRIVATE_KEY));
    }

    @Test
    public void aesEcbTest() throws Exception {
        String data = "xxx";
        String key = "xxxqqqwwweeettttxxxqqqwwweeetttt";
        String enData=EncryptUtil.aesECBEncrypt(data,key);
        System.out.println(enData);
        System.out.println(EncryptUtil.aesECBDecrypt(EncryptUtil.decodeBASE64(enData),key));
    }

    @Test
    public void aesCbcTest() throws Exception {
        String data = "xxx";
        String key = "xxxqqqwwweeetttt";
        String iv = "1234567812345678";
        String enData=EncryptUtil.aesCBCEncrypt(data,key,iv);
        System.out.println(enData);
        System.out.println(EncryptUtil.aesCBCDecrypt(enData,key,iv));
    }
}
