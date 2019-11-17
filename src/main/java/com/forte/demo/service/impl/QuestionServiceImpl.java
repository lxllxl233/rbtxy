package com.forte.demo.service.impl;

import com.forte.demo.bean.Question;
import com.forte.demo.service.QuestionService;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class QuestionServiceImpl {
    public void saveOne(Question question){
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
        QuestionService questionService = session.getMapper(QuestionService.class);

        questionService.save(question);
        session.close();
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String  getQuestionById(int id){
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
        QuestionService questionService = session.getMapper(QuestionService.class);
        String question = questionService.getQuestionById(id);
        session.close();
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return question;
    }

    public String getAnswerById(int id){
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
        QuestionService questionService = session.getMapper(QuestionService.class);
        String answer = questionService.getAnswerById(id);
        session.close();
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return answer;
    }
}
