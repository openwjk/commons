package com.openwjk.commons.enums;
/**
 * @author wangjunkai
 * @description
 * @date 2023/6/23 14:01
 */
public enum FileTypeEnum {
    JPG("FFD8FF", "JPG"),
    PNG("89504E47", "PNG"),
    GIF("47494638", "GIF"),
    TIF("49492A00", "TIF"),
    TIFF("4D4D002A", "TIFF"),
    BMP("424D36", "BMP"),
    DWG("41433130", "DWG"),
    HTML("68746D6C3E", "HTML"),
    RTF("7B5C727466", "RTF"),
    XML("3C3F786D6C", "XML"),
    ZIP("504B0304", "ZIP"),
    RAR("52617221", "RAR"),
    PSD("38425053", "PSD"),
    EML("44656C69766572792D646174653A", "EML"),
    DBX("CFAD12FEC5FD746F", "DBX"),
    PST("2142444E", "PST"),
    OFFICE("D0CF11E0", "OFFICE"),
    MDB("000100005374616E64617264204A", "MDB"),
    WPD("FF575043", "WPD"),
    EPS("252150532D41646F6265", "EPS"),
    PS("252150532D41646F6265", "PS"),
    PDF("255044462D312E", "PDF"),
    QDF("AC9EBD8F", "QDF"),
    PWL("E3828596", "PWL"),
    WAV("57415645", "WAV"),
    AVI("41564920", "AVI"),
    RAM("2E7261FD", "RAM"),
    RM("2E524D46", "RM"),
    MPG("000001BA", "MPG"),
    MOV("6D6F6F76", "MOV"),
    ASF("3026B2758E66CF11", "ASF"),
    MID("4D546864", "MID")
    ;

    private final String code;
    private final String name;

    FileTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static FileTypeEnum get(String code){
        for (FileTypeEnum fileTypeEnum : FileTypeEnum.values()) {
            if (code.toUpperCase().startsWith(fileTypeEnum.getCode())) {
                return fileTypeEnum;
            }
        }
        return null;
    }
}
