package com.forte.demo.test;


import com.forte.demo.util.HttpclientUtil;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AboutQue {
    public static void main(String[] args) throws IOException {
        // 连接地址（通过阅读html源代码获得，即为登陆表单提交的URL）
        String url = "http://oj.hhzzss.cn:81/api/login";
        Map map = new HashMap();

        map.put("password","rootroot");
        map.put("username","root");
        String s = HttpclientUtil.doPost(url,map);
        System.out.println(s);

    }
}
