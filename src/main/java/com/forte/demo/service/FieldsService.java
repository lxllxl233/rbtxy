package com.forte.demo.service;

import com.forte.demo.bean.Fields;
import com.forte.demo.bean.LqbQuestion;

public interface FieldsService {
    void save(Fields fields);
    Fields getByPk(String pk);
}
