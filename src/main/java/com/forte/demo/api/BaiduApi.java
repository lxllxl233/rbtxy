package com.forte.demo.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.aip.speech.AipSpeech;
import com.baidu.aip.speech.TtsResponse;
import com.baidu.aip.util.Util;
import com.forte.demo.util.*;
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
    //颜值测试angry:愤怒 disgust:厌恶 fear:恐惧 happy:高兴 sad:伤心 surprise:惊讶 neutral:无表情 pouty: 撅嘴 grimace:鬼脸
    public static String faceDetect(String fileName,String accessToken) {
        String params = "{\"grimace\":\"调皮\",\"surprise\":\"惊讶\",\"pouty\":\"可爱\",\"happy\":\"开心\",\"sad\":\"伤心\",\"neutral\":\"安静\",\"angry\":\"生气\",\"disgust\":\"郁闷\",\"female\":\"小姐姐\",\"male\":\"小哥哥\",\"fear\":\"害怕\"}";
        Map<String,String> paramsMap = JSON.parseObject(params,Map.class);

        try {
            //获取百度的回调结果
            String result = getResult(fileName,accessToken);
            JSONObject jsonObject = JSON.parseObject(result);
            Object result1 = jsonObject.get("result");
            Object face_list = JSON.parseObject(JSON.toJSONString(result1)).get("face_list");
            String s = JSON.toJSONString(face_list);
            s = s.replaceAll("\\[","");
            s = s.replaceAll("]","");
            System.out.println(s);
            Map<String,Object> map1 = JSON.parseObject(s,Map.class);
            String emotion = map1.get("emotion").toString();
            String gender = map1.get("gender")+"";
            gender = gender.substring(gender.indexOf("\"type\":\"")+8,gender.indexOf("\"}"));
            emotion = emotion.substring(emotion.indexOf("\"type\":\"")+8,emotion.indexOf("\"}"));
            String score = map1.get("beauty")+"";
            Integer age = Integer.parseInt(map1.get("age")+"");

            gender = paramsMap.get(gender);
            emotion = paramsMap.get(emotion);
            if (age>30){
                gender = gender.replaceAll("小哥哥","大叔");
                gender = gender.replaceAll("小姐姐","妇女");
                if (age>50){
                    gender = gender.replaceAll("大叔","大爷");
                    gender = gender.replaceAll("妇女","大妈");
                }
            }

            return "哇！看起来是一位很"+emotion+"的"+gender+"呢！小茵给你打"+score+"分！";
        } catch (Exception e) {
            return "小茵还看不懂这个,请上传相册里的图片~";
        }
    }

    public static String getResult(String fileName,String accessToken) throws Exception {
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/detect";
        Map<String, Object> map = new HashMap<>();
        String imgUrl = FileUtil.readFileAsString("/home/lxl/coolq/data/image/"+fileName+".cqimg");
        System.out.println(imgUrl);
        imgUrl = imgUrl.substring(imgUrl.indexOf("url=")+4,imgUrl.indexOf("addtime"));
        System.out.println(imgUrl);
        long time = System.currentTimeMillis();
        ImgUtil.createImage(imgUrl,"/home/lxl/face/"+time+".jpg");
        byte[] bytes = FileUtil.readFileByBytes("/home/lxl/face/" + time + ".jpg");
        String encode = Base64Util.encode(bytes);
        map.put("image", encode);
        map.put("face_field", "age,beauty,emotion,gender");
        map.put("image_type", "BASE64");
        String param = GsonUtil.toJson(map);

        return HttpUtil.post(url, accessToken, "application/json", param);
    }
}
