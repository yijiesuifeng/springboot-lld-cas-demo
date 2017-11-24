package com.wind.person.controller;

import com.wind.person.domain.Person;
import com.wind.person.service.PersonService;
import com.wind.property.HomeProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lilandong
 * @date 2017/11/12
 */
@RestController
public class PersonController {
    @Autowired
    PersonService personService;
    @Autowired
    private HomeProperties homeProperties;

    /**
     * 用于测试redis和mybatis查询
     */
    @RequestMapping("getPersonByName")
    public Person getPersonByName(@RequestParam(value = "name") String name){
        return personService.getPersonByName(name);
    }
    /**
     * 用于测试redis保存
     */
    @RequestMapping("savePerson")
    public String savePerson(@RequestParam(value = "name") String name,@RequestParam(value = "age") int age){
        Person person = new Person();
        person.setName(name);
        person.setAge(age);
        return personService.savePerson(person);
    }

    /**
     * 加载配置文件
     */
    @RequestMapping("getPropertyText")
    public String getPropertyText(){
        return homeProperties.toString();
    }


    /**
     * 调用方法的第二个参数为true时，会导致异常，用于测试事务是否生效
     */
    @RequestMapping("testTransanction")
    public Person testTransanction(){
        return personService.addPersonByName("lld123",true);
    }
}
