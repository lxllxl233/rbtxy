package com.forte.demo.service;

import com.forte.demo.bean.LqbQuestion;

public interface LqbQuestionService {
    void save(LqbQuestion lqbQuestion);
    LqbQuestion getByPk(String pk);
}
