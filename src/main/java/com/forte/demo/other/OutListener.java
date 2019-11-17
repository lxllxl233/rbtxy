package com.forte.demo.other;

import com.forte.demo.api.*;
import com.forte.demo.msg.Judge;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.anno.depend.Beans;
import com.forte.qqrobot.beans.messages.msgget.*;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.sender.MsgSender;


@Beans
public class OutListener {
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
            sender.SENDER.sendGroupMsg(groupMsg.getGroup(), Judge.judgeMsg(msg, groupMsg,sender));
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
