package com.forte.demo.api;

import com.forte.demo.bean.LqbQuestion;
import com.forte.demo.service.impl.LqbQuestionServiceImpl;
import com.forte.demo.util.CleanData;
import net.pwall.html.HTML;

public class QuestionApi {
    public static String getQuestion(String num){
        LqbQuestionServiceImpl lqbQuestionService = new LqbQuestionServiceImpl();
        LqbQuestion lqbQuestion = lqbQuestionService.getByPk(num);
        String question = lqbQuestion.getModel()+"\n";
        question+=(lqbQuestion.getFields()+"\n");
        question+=(lqbQuestion.getFields().getTitle()+"\n");
        question+=(lqbQuestion.getFields().getDescription()+"\n");
        question+=(lqbQuestion.getFields().getInput_description()+"\n");
        question+=(lqbQuestion.getFields().getOutput_description()+"\n");
        question+=(lqbQuestion.getFields().getHint()+"\n");
        question+=(lqbQuestion.getFields().getSource()+"\n");
        question = HTML.unescape(question);

        question = CleanData.delHTMLTag(question);
        return question;
    }
}
