package com.forte.demo.service.impl;

import com.forte.demo.bean.Fields;
import com.forte.demo.bean.LqbQuestion;
import com.forte.demo.service.FieldsService;
import com.forte.demo.service.LqbQuestionService;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class LqbQuestionServiceImpl {
    public void save(LqbQuestion lqbQuestion){
        //创建输入流
        InputStream is = null;
        try {
            is = Resources.getResourceAsStream("SqlMapConfig.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //创建工厂
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory factory = builder.build(is);
        //创建SqlSession对象,并通过反射创对象
        SqlSession session = factory.openSession();
        LqbQuestionService lqbQuestionService = session.getMapper(LqbQuestionService.class);
        FieldsServiceImpl fieldsService = new FieldsServiceImpl();

        lqbQuestionService.save(lqbQuestion);
        fieldsService.save(lqbQuestion.getFields());
        session.close();
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public LqbQuestion getByPk(String pk){
        //创建输入流
        InputStream is = null;
        try {
            is = Resources.getResourceAsStream("SqlMapConfig.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //创建工厂
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory factory = builder.build(is);
        //创建SqlSession对象,并通过反射创对象
        SqlSession session = factory.openSession();
        LqbQuestionService lqbQuestionService = session.getMapper(LqbQuestionService.class);
        FieldsServiceImpl fieldsService = new FieldsServiceImpl();

        LqbQuestion lqbQuestion = lqbQuestionService.getByPk(pk);
        lqbQuestion.setFields(fieldsService.getByPk(pk));
        session.close();
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lqbQuestion;
    }
}
