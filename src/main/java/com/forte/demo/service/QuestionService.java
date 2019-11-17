package com.forte.demo.service;

import com.forte.demo.bean.Question;

public interface QuestionService {
    void save(Question question);
    String getQuestionById(Integer id);
    String getAnswerById(Integer id);
}
