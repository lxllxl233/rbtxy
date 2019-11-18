package com.forte.demo.msg;

import com.alibaba.fastjson.JSON;
import com.forte.demo.api.*;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.sender.MsgSender;
import com.forte.qqrobot.utils.CQCodeUtil;
import org.mx.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

//判定类
public class Judge {
    public static String mainQQ;
    public static String rbtQQ;
    public static List<String> groupList = null;
    public static Map<String,String> map;
    private static final ResourceBundle resourceBundle;

    static{
        System.out.println("装载配置文件ing......................");
        resourceBundle = ResourceBundle.getBundle("config");
        mainQQ = resourceBundle.getString("mainQQ");
        rbtQQ = resourceBundle.getString("rbtQQ");
        String lists = resourceBundle.getString("groupList");
        System.out.println(lists);
        groupList = JSON.parseObject(lists, List.class);
        map = new HashMap<>();
        map.put("APP_ID",resourceBundle.getString("APP_ID"));
        map.put("API_KEY",resourceBundle.getString("API_KEY"));
        map.put("SECRET_KEY",resourceBundle.getString("SECRET_KEY"));
        System.out.println(map.toString());
        System.out.println("配置文件已装载!");
    }

    public static String priJudgeMsg(MsgSender sender,String qq,String msg) {System.out.println(mainQQ);
        if(msg.startsWith("#签到-")) {
            String param = msg.replaceAll("#签到-", "");
            GroupApi.setGroupSign(sender, param);
            return "";
        }else if (qq.equals(mainQQ)) {
            //操作监控
            String[] split = msg.split("#");
            if (split[1].startsWith("say")) {
                msg = split[1];
                boolean isMan = false;
                if (msg.startsWith("say-少年")){
                    msg = msg.replaceAll("say-少年", "");
                    isMan = true;
                }else {
                    msg = msg.replaceAll("say-", "");
                }
                sender.SENDER.sendPrivateMsg(split[0].replaceAll("say", ""), BaiduApi.say(msg,map,isMan));
            } else {
                sender.SENDER.sendPrivateMsg(split[0], split[1]);
            }
            return "";
        } else{
            String priMsg = judgeMsg(msg,null, sender);
            if (StringUtils.isBlank(priMsg)){
                sender.SENDER.sendPrivateMsg(mainQQ, qq + "#" + msg);
                return "";
            }else {
                sender.SENDER.sendPrivateMsg(mainQQ, qq + "#" + msg);
                return priMsg;
            }
        }


    }
    public static String judgeMsg(String msg, GroupMsg groupMsg, MsgSender sender){
        if(CQCodeUtil.build().isAt(msg,rbtQQ)){
            if (groupMsg.getQQ().equals(mainQQ)){
                return "天大地大，主人最大~";
            }else {
                return "诗茵还小，不要老艾特人家了~";
            }
        }

        if (msg.startsWith("#say-")){
            boolean isMan = false;
            if (msg.startsWith("#say-少年")){
                msg = msg.replaceAll("#say-少年", "");
                isMan = true;
            }else {
                msg = msg.replaceAll("#say-", "");
            }
                if (msg.startsWith("#")) {
                    return BaiduApi.say(judge(msg), map,isMan);
                } else {
                    return BaiduApi.say(msg, map,isMan);
                }
        }//一些群管理功能
        else if (msg.startsWith("##")){
            useGroupApi(msg,groupMsg,sender);
            return "";
        }else {
            return judge(msg);
        }
    }

    public static void useGroupApi(String msg,GroupMsg groupMsg,MsgSender sender){
        if (msg.startsWith("##自闭")){
            GroupApi.StopTalk(sender,groupMsg.getGroup(),groupMsg.getQQ());
        }else if (msg.startsWith("##我是")){
            msg = msg.replaceAll("##我是","");
            msg = msg.replaceAll("爷爷","孙子");
            msg = msg.replaceAll("爸爸","儿子");
            msg = msg.replaceAll("妈妈","闺女");
            msg = msg.replaceAll("哥","弟");
            String groupQQ = groupMsg.getGroup();
            GroupApi.changeCard(sender,groupQQ,groupMsg.getQQ(),msg);
            sender.SENDER.sendGroupMsg(groupQQ,"好好好,你是"+msg);
        }else if (msg.startsWith("##修改昵称")){
            sender.SENDER.sendGroupMsg(groupMsg.getGroup(),
                    GroupApi.printAllGroupMemberNickmameIsNULL(GroupApi.getGroupMemberList(sender,groupMsg.getGroup())));
        }
    }

    public static String judge(String msg) {
        if (msg.startsWith("#question")) {
            msg = msg.replaceAll("#question", "");
            try {
                Integer num = Integer.valueOf(msg);
                num += 1001;
                if (num >= 1002 && num <= 1274) {
                    String question = QuestionApi.getQuestion("" + num);
                    return "问题来了:\n" + question + "\n试试看";
                } else {
                    return "哎呀，不要这样子啦~";
                }
            } catch (Exception e) {
                return "调戏，是不可以的~";
            }
        } else if (msg.startsWith("#百度-")) {
            String param = msg.replaceAll("#百度-", "");
            String bd = (StringUtils.isBlank(bd = BaiduApi.bingBaidu(param))) ? "\n啊~百度也没有啊，小茵没办法了" : bd;
            return "这个小茵也不知道,我去百度一下就回," + bd;
        } else if (msg.startsWith("#电影-")) {
            String param = msg.replaceAll("#电影-", "");
            return VideoApi.getVideo(param);
        } else if (msg.startsWith("#哔哩哔哩-")) {
            String param = msg.replaceAll("#哔哩哔哩-", "");
            return VideoApi.getByBiliBili(param);
        } else if (msg.startsWith("#MP3-")) {
            String param = msg.replaceAll("#MP3-", "");
            return MusicApi.getMusic(param);
        } else if (msg.startsWith("#王者攻略-")) {
            String param = msg.replaceAll("#王者攻略-", "");
            return GameApi.hero(param);
        } else if (msg.startsWith("#我不开心")) {
            String param = msg.replaceAll("#我不开心", "");
            return HappyApi.happy();
        } else if (msg.startsWith("#晚安")) {
            return "哈呼~\n晚安";
        } else if (msg.startsWith("#睡了吗")) {
            return "小茵还不困~";
        } else if (msg.startsWith("#翻译成")) {
            if (msg.startsWith("#翻译成英语")) {
                msg = msg.replaceAll("#翻译成英语", "");
                return TranslateApi.toEnglish(msg);
            } else if (msg.startsWith("#翻译成汉语")) {
                msg = msg.replaceAll("#翻译成汉语", "");
                return TranslateApi.toChinese(msg);
            } else if (msg.startsWith("#翻译成日语")) {
                msg = msg.replaceAll("#翻译成日语", "");
                return TranslateApi.toJapainese(msg);
            } else {
                return "什么鸟语，小茵看不懂~";
            }
        }else if (msg.startsWith("#美图")) {
            return ImgApi.getBeautiful();
        }else if(msg.startsWith("#help")){
            return "小茵的功能列表:\n注：所有()里的参数均为可选\n#say-(少年):播放后面的内容,可嵌套其他命令\n#百度-(百度的内容)\n#哔哩哔哩-(在bilibili搜索的内容)\n#翻译成(汉语/英语/日语)(翻译内容):支持英译汉，汉译英，汉译日\n" +
                    "#电影-(电影名字)\n#王者攻略-(英雄名字)：获取英雄攻略\n#我不开心:小茵给你讲笑话\n##自闭:随机禁言\n更多请参考源码...";
        } else {
            return "";
        }
    }

    //判断是否在某个群聊开启机器人
    public static boolean inGroup(String groupQQ){
        //判断是不是在所有群聊开启qq机器人
        if (groupList.get(0).equals("-1")){
            return true;
        }
        for (String s : groupList) {
            if (s.equals(groupQQ))
                return true;
        }
        return false;
    }

}
