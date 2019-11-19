package com.forte.demo.other;

import com.forte.demo.api.*;
import com.forte.demo.msg.Judge;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.anno.depend.Beans;
import com.forte.qqrobot.beans.messages.msgget.*;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.sender.MsgSender;

import java.util.HashMap;
import java.util.Map;


@Beans
public class OutListener {
    public static Map<String,String> map;
    static{
        map = new HashMap<>();
    };
    @Listen(MsgGetTypes.privateMsg)
    public void listenPri(PrivateMsg privateMsg, MsgSender sender){
        String msg = privateMsg.getMsg();
        String qq = privateMsg.getQQ();
        System.out.println(privateMsg.getQQ()+":"+msg);

        sender.SENDER.sendPrivateMsg(qq,Judge.priJudgeMsg(sender,qq,msg));
    }

    @Listen(MsgGetTypes.groupMsg)
    public void listenGroup(GroupMsg groupMsg,MsgSender sender){
        //判断是否在该群聊里开启了机器人
        if (Judge.inGroup(groupMsg.getGroup())) {

            String msg = groupMsg.getMsg();
            String location = null;
            if (null != (location = Judge.isImg(msg))){
                map.put(groupMsg.getQQ(),Judge.getFace(location));
            }
            if (msg.startsWith("#结果-")){
                msg = msg.replaceAll("#结果-","");
                sender.SENDER.sendGroupMsg(groupMsg.getGroup(), map.get(msg));
            }else if (msg.startsWith("#say-#结果-") || msg.startsWith("#say-少年#结果-")){
                msg = msg.replaceAll("#say-","");
                msg = msg.replaceAll("#结果-","");
                boolean isMan = msg.startsWith("少年");
                if (isMan){
                    msg = msg.replaceAll("少年","");
                }
                sender.SENDER.sendGroupMsg(groupMsg.getGroup(), Judge.useBaiduApi(map.get(msg),isMan));
            }else {
                sender.SENDER.sendGroupMsg(groupMsg.getGroup(), Judge.judgeMsg(msg, groupMsg,sender));
            }
        }
    }

    @Listen(MsgGetTypes.groupMemberIncrease)
    public void groupAddRequest(GroupMemberIncrease groupMemberIncrease, MsgSender sender){
        String qq = groupMemberIncrease.getBeOperatedQQ();
        sender.SENDER.sendGroupMsg(groupMemberIncrease.getGroup(),"欢迎"+qq+"大佬加入");
    }

    @Listen(MsgGetTypes.groupMemberReduce)
    public void groupAddRequest(GroupMemberReduce groupMemberReduce, MsgSender sender){
        String qq = groupMemberReduce.getBeOperatedQQ();
        sender.SENDER.sendGroupMsg(groupMemberReduce.getGroup(),"刚刚"+qq+"永远离开了我们");
    }

    @Listen(MsgGetTypes.groupFileUpload)
    public void groupFileUpload(GroupFileUpload groupFileUpload,MsgSender sender){
        String msg = GroupApi.getUploadFileInfo(groupFileUpload);
        String qq = groupFileUpload.getGroup();
        sender.SENDER.sendGroupMsg(qq,msg);
    }

}
