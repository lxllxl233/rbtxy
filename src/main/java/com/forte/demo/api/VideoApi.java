package com.forte.demo.api;

import com.forte.demo.util.CleanData;
import com.forte.demo.util.HttpclientUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class VideoApi {
    public static String getVideo(String name){
        String rtText = "";
        String param = HttpclientUtil.getURLEncoderString(name);
        String url = "http://v.d9y.net/seacher.php?wd="+param;

        String data = HttpclientUtil.doGet(url);
        //正则表达式清理
        String apUrl = "http://v.d9y.net/";
        Document doc = Jsoup.parse(data);
        Element document = doc.body();
        Elements select = document.select("a[href][title][class]");
        int i = 0;
        for (Element element : select) {
            rtText+=(element.attr("title")+":"+apUrl+element.attr("href")+"\n");
            if (++i == 5){
                break;
            }
        }
        return rtText;
    }

    //爬bilibili
    public static String getByBiliBili(String param){

        param = HttpclientUtil.getURLEncoderString(param);
        String url = "https://search.bilibili.com/all?keyword="+param;
        String data = HttpclientUtil.doGet(url);

        Document document = Jsoup.parse(data);
        Elements elementsByClass = document.getElementsByClass("video-item matrix");
        String returnStr = "";
        int i = 0;
        for (Element byClass : elementsByClass) {
            Elements select = byClass.select("a[href][title]");

            returnStr+=(select.attr("title")+":http:"+select.attr("href"))+"\n";
            Elements elementsByClass1 = byClass.getElementsByClass("so-imgTag_rb");
            returnStr+="播放时长："+ CleanData.delHTMLTag(elementsByClass1.toString())+"\n";

            Elements select1 = byClass.select("span[title][class]");
            for (Element element : select1) {
                returnStr+=(element.attr("title")+":"+CleanData.delHTMLTag(element.toString()))+"\n";
            }
            if (++i == 3){
                break;
            }
        }

        return returnStr;
    }
}
