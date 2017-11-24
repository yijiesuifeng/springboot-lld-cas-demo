package com.wind.person.service.impl;

import com.wind.common.redis.IRedisService;
import com.wind.common.util.JSONUtil;
import com.wind.person.dao.PersonMapper;
import com.wind.person.domain.Person;
import com.wind.person.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lilandong
 * @date 2017/11/12
 */
@Service
public class PersonServiceImpl implements PersonService{

    @Autowired
    PersonMapper personMapper;
    @Autowired
    IRedisService redisService;

    @Override
    public Person getPersonByName(String name) {
        Person person;
        if (redisService.get(name)==null){
            person = personMapper.selectPersonByName(name);
            System.out.println("=================mysql");
        }else {
            String s = redisService.get(name);
            person = JSONUtil.toBean(s, Person.class);
            System.out.println("=================redis");
        }
        return person ;
    }

    @Override
    public String savePerson(Person person) {
        int i = personMapper.insertSelective(person);
        Map<String, String> resultMap = new HashMap<>();
        if (i!=0){
            resultMap.put("status","ok");
            redisService.set(person.getName(),JSONUtil.toJson(person));
        }else {
            resultMap.put("status","no-ok");
        }
        return JSONUtil.toJson(resultMap);
    }

    @Override
    public Person addPersonByName(String name, boolean ex) {
        Person person = new Person();
        person.setName(name);
        personMapper.insertSelective(person);
        if (ex){
            throw new NullPointerException();
        }
        return person;
    }
}
