package com.jpsite.utils;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * @author jiangpeng
 * @date 2020/11/617:04
 */
public class BusinessUtils {
    /**
     * 随机返回过期时间，毫秒值
     *
     * @return
     * @throws ParseException
     */
    private LocalDateTime getRandomEndTime() {
        Random random = new Random();
        final LocalDateTime fromDate = LocalDateTime.now();
        // 3~24小时之后的时间
        return fromDate.plusHours(random.nextInt(22) + 3);
    }

    /**
     * 随机生成手机号
     *
     * @return
     */
    private String getRandomPhone() {
        Random random = new Random();
        String[] telFirst = "134,135,136,137,138,139,150,151,152,157,158,159,130,131,132,155,156,133,153".split(",");
        String first = telFirst[random.nextInt(telFirst.length - 1)];
        String second = String.valueOf(random.nextInt(888) + 10000).substring(1);
        String third = String.valueOf(random.nextInt(9100) + 10000).substring(1);
        return first + second + third;
    }

    /**
     * 随机生成姓名
     *
     * @return
     */
    private String getRandomName() {
        Random random = new Random();
        String[] lastNames = {"赵", "钱", "孙", "李", "周", "吴", "郑", "王", "冯", "陈", "褚", "卫", "蒋", "沈", "韩", "杨", "朱", "秦", "尤", "许",
                "何", "吕", "施", "张", "孔", "曹", "严", "华", "金", "魏", "陶", "姜", "戚", "谢", "邹", "喻", "柏", "水", "窦", "章", "云", "苏", "潘", "葛", "奚", "范",
                "彭", "郎",
                "鲁", "韦", "昌", "马", "苗", "凤", "花", "方", "俞", "任", "袁", "柳", "酆", "鲍", "史", "唐", "费", "廉", "岑", "薛", "雷", "贺", "倪", "汤", "滕", "殷",
                "罗", "毕", "郝", "邬", "安", "常", "乐", "于", "时", "傅", "皮", "卞", "齐", "康", "伍", "余", "元", "卜", "顾", "孟", "平", "黄", "和",
                "穆", "萧", "尹", "姚", "邵", "湛", "汪", "祁", "毛", "禹", "狄", "米", "贝", "明", "臧", "计", "伏", "成", "戴", "谈", "宋", "茅", "庞", "熊", "纪", "舒",
                "屈", "项", "祝", "董", "梁", "杜", "阮", "蓝", "闵", "席", "季"};
        String girl =
                "秀娟英华慧巧美娜静淑惠珠翠雅芝玉萍红娥玲芬芳燕彩春菊兰凤洁梅琳素云莲真环雪荣爱妹霞香月莺媛艳瑞凡佳嘉琼勤珍贞莉桂娣叶璧璐娅琦晶妍茜秋珊莎锦黛青倩婷姣婉娴瑾颖露瑶怡婵雁蓓纨仪荷丹蓉眉君琴蕊薇菁梦岚苑婕馨瑗琰韵融园艺咏卿聪澜纯毓悦昭冰爽琬茗羽希宁欣飘育滢馥筠柔竹霭凝晓欢霄枫芸菲寒伊亚宜可姬舒影荔枝思丽 ";
        String boy =
                "伟刚勇毅俊峰强军平保东文辉力明永健世广志义兴良海山仁波宁贵福生龙元全国胜学祥才发武新利清飞彬富顺信子杰涛昌成康星光天达安岩中茂进林有坚和彪博诚先敬震振壮会思群豪心邦承乐绍功松善厚庆磊民友裕河哲江超浩亮政谦亨奇固之轮翰朗伯宏言若鸣朋斌梁栋维启克伦翔旭鹏泽晨辰士以建家致树炎德行时泰盛雄琛钧冠策腾楠榕风航弘";
        //获得一个随机的姓氏
        String name = lastNames[random.nextInt(lastNames.length - 1)];
        //可以根据这个数设置产生的男女比例
        int j = random.nextInt(girl.length() - 2);
        if (random.nextInt(3) == 2) {
            name += j % 2 == 0 ? girl.substring(j, j + 2) : girl.substring(j, j + 1);
        } else {
            name += j % 2 == 0 ? boy.substring(j, j + 2) : boy.substring(j, j + 1);
        }

        return name;
    }
}
