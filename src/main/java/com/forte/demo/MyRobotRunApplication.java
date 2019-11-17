package com.forte.demo;

import com.forte.demo.api.BaiduApi;
import com.forte.demo.msg.Judge;
import com.forte.qqrobot.component.forhttpapi.HttpApp;
import com.forte.qqrobot.component.forhttpapi.HttpApplication;
import com.forte.qqrobot.component.forhttpapi.HttpConfiguration;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.utils.CQCodeUtil;


public class MyRobotRunApplication implements HttpApp {


    public static void main(String[] args) {
        MyRobotRunApplication myRobotRunApplication = new MyRobotRunApplication();
        HttpApplication httpApplication = new HttpApplication();
        httpApplication.run(myRobotRunApplication);
    }

    @Override
    public void before(HttpConfiguration configuration) {
        configuration
                .setIp("127.0.0.1")
                .setJavaPort(15514)
                .setServerPath("/coolq")
                .setServerPort(9999)
                .setScannerPackage("com.forte.demo");

        //这俩行不要删除，相当于我手动加载了一下配置文件
        Judge judge = new Judge();
        BaiduApi baiduApi = new BaiduApi();
    }
    @Override
    public void after(CQCodeUtil cqCodeUtil, MsgSender sender) {
        sender.SENDER.sendPrivateMsg("你的qq","成功了");
    }
}
