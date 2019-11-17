package com.forte.demo.api;

import com.baidu.aip.speech.AipSpeech;
import com.baidu.aip.speech.TtsResponse;
import com.baidu.aip.util.Util;
import com.forte.demo.util.CleanData;
import com.forte.demo.util.HttpclientUtil;
import com.forte.qqrobot.utils.CQCodeUtil;
import net.pwall.html.HTML;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class BaiduApi {

    public static String bingBaidu(String key){
        String param = HttpclientUtil.getURLEncoderString(key);
        String s = HttpclientUtil.doGet("https://baike.baidu.com/item/"+param);
        Document doc = Jsoup.parse(s);
        Elements select = doc.select("[class=lemma-summary][label-module=lemmaSummary]");
        s = select.toString();
        s = HTML.unescape(s);
        s = CleanData.delHTMLTag(s);
        return s.replaceAll("\\s*", "");
    }

    public static String say(String param, Map<String,String> params,boolean isMan){
        AipSpeech client = new AipSpeech(params.get("APP_ID"), params.get("API_KEY"), params.get("SECRET_KEY"));
        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        System.setProperty("aip.log4j.conf", "path/to/your/log4j.properties");
        // 设置可选参数
        HashMap<String, Object> options = new HashMap<String, Object>();
        if (!isMan) {
            options.put("spd", "5");
            options.put("pit", "6");
            options.put("per", "4");
        }else {
            options.put("spd", "5");
            options.put("pit", "6");
            options.put("per", "1");
        }
        TtsResponse res = client.synthesis(param, "zh", 1, options);
        byte[] data = res.getData();
        if (data != null) {
            try {
                Util.writeBytesToFileSystem(data, "/home/lxl/coolq/data/record/output.silk");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return CQCodeUtil.build().getCQCode_Record("output.silk",false)+"";
    }
}
