package com.forte.demo.api;

import com.forte.demo.util.HttpclientUtil;

public class MusicApi {
    public static String getMusic(String name){

        String param = HttpclientUtil.getURLEncoderString(name);
        String url = "https://api.imjad.cn/cloudmusic/?type=search&s="+param;
        String data = HttpclientUtil.doGet(url);
        try {
            data = data.substring(0,100);
        }catch (Exception e){

        }
        System.out.println(data);

        String[] musicData = data.split(",");
        String id = musicData[1].replaceAll("\"id\":","");

        url = "https://api.imjad.cn/cloudmusic/?type=song&id="+id+"&search_type=1";
        data = HttpclientUtil.doGet(url);
        data = data.substring(1, 200);
        musicData = data.split(",");
        data = musicData[1].replaceAll("\"url\":","");
        data = data.substring(1,data.length()-1);
        data = data.replaceAll("\\\\","");
        return data;
    }
}
