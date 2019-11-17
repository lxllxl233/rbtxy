package com.forte.demo.api;

import com.forte.qqrobot.beans.messages.msgget.GroupFileUpload;
import com.forte.qqrobot.beans.messages.result.GroupInfo;
import com.forte.qqrobot.beans.messages.result.GroupMemberInfo;
import com.forte.qqrobot.beans.messages.result.GroupMemberList;
import com.forte.qqrobot.beans.messages.result.inner.GroupMember;
import com.forte.qqrobot.sender.MsgSender;

import java.util.Random;

public class GroupApi {

    //获取群成员列表
    public static GroupMemberList getGroupMemberList(MsgSender sender,String groupQQ){
        GroupMemberList groupMemberList = sender.GETTER.getGroupMemberList(groupQQ);
        return groupMemberList;
    }

    //传入一个qq号，判断是不是管理员
    public static boolean isManger(MsgSender sender,String groupQQ,String qq){
        GroupInfo groupInfo = sender.GETTER.getGroupInfo(groupQQ);
        if(groupInfo.getOwnerQQ().equals(qq)){
            return true;
        }
        String[] adminList = groupInfo.getAdminList();
        for (String s : adminList) {
            System.out.println(s);
            if (qq.equals(s)){
                return true;
            }
        }
        return false;
    }

    //获取群成员信息
    public static GroupMemberInfo getGroupMemberInfo(MsgSender sender,String groupQQ,String qq){
        GroupMemberInfo groupMemberInfo = sender.GETTER.getGroupMemberInfo(groupQQ, qq);
        return groupMemberInfo;
    }



    //打印群成员列表
    public static String printAllGroupMember(GroupMemberList groupMemberList){
        String msg = "猫窝里有:\n";
        for (GroupMember groupMember : groupMemberList) {
            msg+=(groupMember.getName()+":"+groupMember.getNickName()+"\n");
        }
        return msg+"谢谢你们!";
    }

    //打印没修改昵称的群成员
    public static String printAllGroupMemberNickmameIsNULL(GroupMemberList groupMemberList){
        String msg = "没修改群昵称的有:\n";
        for (GroupMember groupMember : groupMemberList) {
            if (null == groupMember.getNickName()){
                msg+=groupMember.getName()+":"+groupMember.getQQ()+"\n";
            }
        }
        return msg+"请尽快修改欧~";
    }

    //获取群成员上传文件的信息
    public static String getUploadFileInfo(GroupFileUpload groupFileUpload){
        String msg = "刚刚"+groupFileUpload.getQQCode()+"上传了("+groupFileUpload.getFileName()
                +")\n"+"大小:"+String.format("%.2f",groupFileUpload.getFileSize()/1024.0/1024.0)+"MB";
        return msg;
    }

    //随机禁言某些人
    public static boolean StopTalk(MsgSender sender,String groupQQ,String qq){
        Random random = new Random();
        long num = random.nextLong()%(60*60);
        if (num < 0){
            num = -num;
        }
        if (num % 2==0){
            num*=2;
            sender.SENDER.sendGroupMsg(groupQQ,"恭喜你抽中超级加倍!!!");
        }
        return sender.SETTER.setGroupBan(groupQQ,qq,num);
    }

    //设置群成员名片
    public static void changeCard(MsgSender sender,String groupQQ,String qq,String name){
        sender.SETTER.setGroupCard(groupQQ,qq,name);
    }

    //设置群成员的专属头衔
    public static void setHeadName(MsgSender sender,String groupQQ,String qq,String msg){
        long time = 1024*1024;
        sender.SETTER.setGroupExclusiveTitle(groupQQ,qq,msg,time);
    }

    //群签到
    public static void setGroupSign(MsgSender sender,String groupQQ){
        sender.SETTER.setGroupSign(groupQQ);
    }

}






















