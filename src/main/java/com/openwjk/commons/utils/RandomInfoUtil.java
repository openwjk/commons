package com.openwjk.commons.utils;


import java.util.Random;

/**
 * @author wangjunkai
 * @description 生成随机信息
 * @date 2023/6/23 14:05
 */
public class RandomInfoUtil {


    private static final String familyOneName = "李王张刘陈杨黄赵周吴徐孙朱马胡郭林何高梁郑罗宋谢唐韩曹许邓萧冯曾程蔡彭潘袁于董余苏叶吕魏蒋田杜丁沈姜范江傅钟卢汪戴崔任陆廖姚方金" +
            "邱夏谭韦贾邹石熊孟秦阎薛侯雷白龙段郝孔邵史毛常万顾赖武康贺严尹钱施牛洪龚" +
            "曾关楚";

    private static final String boyName = "伟刚勇毅俊峰强军平保东文辉力明永健世广志义兴良海山仁波宁贵福生龙元全国胜学祥才发武新利清飞彬富顺信子杰涛昌成康星光天达" +
            "安岩中茂进林有坚和彪博诚先敬震振壮会思群豪心邦承乐绍功松善厚庆磊民友裕河哲江超浩亮政谦亨奇固之轮翰朗伯宏言若鸣朋斌梁栋维启克伦翔旭鹏泽" +
            "晨辰士以建家致树炎德行时泰盛雄琛钧冠策腾楠榕风航弘";

    private static final String girlName = "秀娟英华慧巧美娜静淑惠珠翠雅芝玉萍红娥玲芬芳燕彩春菊兰凤洁梅琳素云莲真环雪荣爱妹霞香月莺媛艳瑞凡佳嘉琼勤珍贞莉桂娣叶璧" +
            "璐娅琦晶妍茜秋珊莎锦黛青倩婷姣婉娴瑾颖露瑶怡婵雁蓓纨仪荷丹蓉眉君琴蕊薇菁梦岚苑婕馨瑗琰韵融园艺咏卿聪澜纯毓悦昭冰爽琬茗羽希宁欣飘育滢馥" +
            "筠柔竹霭凝晓欢霄枫芸菲寒伊亚宜可姬舒影荔枝思丽";

    private static final String phoneTwoNum = "3578";



    /**
     * 获取随机男生姓名
     */
    public static String getRandomBoyName() {
        int bodNameIndexOne = RandomCodeUtil.randomInt(boyName.length());
        int bodNameIndexTwo = RandomCodeUtil.randomInt(boyName.length());
        int familyOneNameIndex = RandomCodeUtil.randomInt(familyOneName.length());
        if (familyOneNameIndex % 2 == 0) {
            return familyOneName.charAt(familyOneNameIndex) + boyName.substring(bodNameIndexOne, bodNameIndexOne + 1) + boyName.charAt(bodNameIndexTwo);
        } else {
            return familyOneName.charAt(familyOneNameIndex) + boyName.substring(bodNameIndexOne, bodNameIndexOne + 1);
        }
    }

    /**
     * 获取女生姓名
     */
    public static String getRandomGirlName() {
        int bodNameIndexOne = RandomCodeUtil.randomInt(girlName.length());
        int bodNameIndexTwo = RandomCodeUtil.randomInt(girlName.length());
        int familyOneNameIndex = RandomCodeUtil.randomInt(familyOneName.length());
        if (familyOneNameIndex % 2 == 0) {
            return familyOneName.charAt(familyOneNameIndex) + girlName.substring(bodNameIndexOne, bodNameIndexOne + 1);
        } else {
            return familyOneName.charAt(familyOneNameIndex) + girlName.substring(bodNameIndexOne, bodNameIndexOne + 1) + girlName.charAt(bodNameIndexTwo);
        }
    }

    /**
     * 获取随机手机号
     */
    public static String getRandomPhone() {
        int phoneTwoRandomIndex = RandomCodeUtil.randomInt(4);
        return "1" + phoneTwoNum.charAt(phoneTwoRandomIndex) + (100000000 + RandomCodeUtil.randomInt(899999999));
    }

    public static void main(String[] args) {
        System.out.println(getRandomPhone());
    }
}

