package com.forte.demo.api;

import com.alibaba.fastjson.JSON;
import com.forte.demo.util.CleanData;
import com.forte.demo.util.HttpclientUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Map;

public class GameApi {

    public static String hero(String param){

        String s = "{嬴政:110, 鲁班七号:112, 阿轲:116, 马可波罗:132, 吕布:123, 刘邦:149, 大乔:191, 东皇太一:187, 韩信:150, 武则天:136, 亚瑟:166, 黄忠:192," +
                " 芈月:121, 李元芳:173, 白起:120, 女娲:179, 貂蝉:141, 太乙真人:186, 娜可露露:162, 弈星:197, 夏侯惇:126, 关羽:140, 墨子:108, 程咬金:144," +
                " 扁鹊:119, 蔡文姬:184, 百里守约:196, 狄仁杰:133, 赵云:107, 老夫子:139, 小乔:106, 张飞:171, 诸葛亮:190, 孙悟空:167, 露娜:146, 姜子牙:148, " +
                "孙尚香:111, 杨玉环:176, 不知火舞:157, 张良:156, 司马懿:137, 钟无艳:117, 成吉思汗:177, 杨戬:178, 苏烈:194, 钟馗:175, 安琪拉:142, 花木兰:154, " +
                "高渐离:115, 廉颇:105, 孙膑:118, 公孙离:199, 牛魔:168, 虞姬:174, 铠:193, 兰陵王:153, 哪吒:180, 元歌:125, 李白:131, 典韦:129, 刘备:170, " +
                "甄姬:127, 刘禅:114, 王昭君:152, 橘右京:163, 雅典娜:183, " +
                "干将莫邪:182, 梦奇:198, 鬼谷子:189, 百里玄策:195, 庄周:113, 妲己:109, 宫本武藏:130, 项羽:135, 曹操:128, 达摩:134, 后羿:169, 周瑜:124}";
        Map<String,Integer> map = JSON.parseObject(s, Map.class);

        s = map.get(param)+"";
        String url = "https://pvp.qq.com/web201605/herodetail/"+s+".shtml";
        String data = HttpclientUtil.doGet(url,"gbk");
        Document document = Jsoup.parse(data);
        Element body = document.body();
        Elements elements = body.select("div[class=show-list]");

        elements.remove(elements.last());

        String returnData = "";
        for (Element element : elements) {
            Elements select1 = element.select("p[class=skill-name]");
            Elements select2 = element.select("p[class=skill-desc]");
            Elements select3 = element.select("div[class=skill-tips]");
            returnData+="==================\n";
            returnData+= CleanData.delHTMLTag(select1.toString())+"\n";
            returnData+="技能描述："+CleanData.delHTMLTag(select2.toString())+"\n";
            returnData+="Tip:"+CleanData.delHTMLTag(select3.toString())+"\n";
            returnData+="==================\n";
        }
        return returnData;
    }


}
