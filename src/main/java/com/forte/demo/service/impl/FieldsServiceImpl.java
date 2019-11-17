package com.forte.demo.service.impl;

import com.forte.demo.bean.Fields;
import com.forte.demo.service.FieldsService;
import com.forte.demo.service.LqbQuestionService;
import com.forte.demo.service.QuestionService;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class FieldsServiceImpl {
    public void save(Fields fields){
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
        FieldsService fieldsService = session.getMapper(FieldsService.class);
        fieldsService.save(fields);
        session.close();
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Fields getByPk(String pk){
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
        FieldsService fieldsService = session.getMapper(FieldsService.class);

        Fields fields = fieldsService.getByPk(pk);
        session.close();
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fields;
    }
}
