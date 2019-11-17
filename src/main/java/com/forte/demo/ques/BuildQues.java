package com.forte.demo.ques;

import com.forte.demo.bean.Question;
import com.forte.demo.service.impl.QuestionServiceImpl;

import java.io.*;

public class BuildQues {
    public static void main(String[] args) throws IOException {
        FileReader reader = null;
        BufferedReader in = null;
        File inFile = new File("/home/lxl/IdeaProjects/gmail/rbt/src/main/resources/cx.txt");
        reader = new FileReader(inFile);
        in = new BufferedReader(reader);
        String read = "题目";

        String que = "";
        String ans = "";
        QuestionServiceImpl questionService = new QuestionServiceImpl();
        Question question = new Question();
        while (null != read){
            if (read.contains("题目：")){
                do {
                    que+=read;
                    read = in.readLine();
                }while (!read.contains("程序源代码："));
            }
            if (read.contains("程序源代码：")){
                do {
                    ans+=read;
                    read = in.readLine();
                    if (null == read){
                        break;
                    }
                }while (!read.contains("题目："));
            }else {
                read = in.readLine();
            }
            question.setTitle(que);
            question.setAnswer(ans);
            questionService.saveOne(question);
            que = "";
            ans = "";
        }
    }

}
