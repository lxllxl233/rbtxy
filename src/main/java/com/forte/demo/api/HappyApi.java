package com.forte.demo.api;

import com.forte.demo.util.CleanData;
import com.forte.demo.util.HttpclientUtil;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.util.Random;

public class HappyApi {
    public static String happy(){
        Random random = new Random();
        int num = random.nextInt(160)+1;

        String url = "http://duanziwang.com/page/"+num+"/";
        String data = HttpclientUtil.doGet(url);
        Elements elements = Jsoup.parse(data).body().select("div[class=post-content]");
        num = random.nextInt(elements.size()-1)+1;
        return "小主莫慌(>_<、),小茵给你讲个笑话,\n"+CleanData.delHTMLTag(elements.get(num).toString())+"\n怎么样，开心起来了吗(≖ v ≖)✧";
    }
}
