package com.forte.demo.api;

import com.alibaba.fastjson.JSON;
import com.forte.demo.util.HttpclientUtil;
import com.forte.qqrobot.beans.cqcode.CQCode;
import com.forte.qqrobot.utils.CQCodeUtil;

public class ImgApi {
    public static String getBeautiful(){
        String s = HttpclientUtil.doGet("https://api.ooopn.com/image/beauty/api.php?type=json");
        s = JSON.parseObject(s).get("imgurl")+"";
        return CQCodeUtil.build().getCQCode_Image(s)+"";
    }
}
