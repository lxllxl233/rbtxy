package com.forte.demo.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.forte.demo.bean.Fields;
import com.forte.demo.bean.LqbQuestion;
import com.forte.demo.service.impl.LqbQuestionServiceImpl;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class LqbQue {
    public static void main(String[] args) throws IOException {

        File file=new File("/home/lxl/IdeaProjects/gmail/rbt/src/main/resources/lqbtk.txt");
        String content= FileUtils.readFileToString(file,"UTF-8");
        List<LqbQuestion> lqbQuestions = JSON.parseArray(content, LqbQuestion.class);
        LqbQuestionServiceImpl lqbQuestionService = new LqbQuestionServiceImpl();
        Fields fields = null;
        for (LqbQuestion lqbQuestion : lqbQuestions) {
          System.out.println(lqbQuestionService.getByPk(lqbQuestion.getPk()).getFields());
        }
    }
}
