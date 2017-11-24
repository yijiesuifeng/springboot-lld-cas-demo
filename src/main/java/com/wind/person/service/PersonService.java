package com.wind.person.service;

import com.wind.person.domain.Person;

/**
 * @author Lilandong
 * @date 2017/11/12
 */
public interface PersonService {
    Person getPersonByName(String name);
    String savePerson(Person person);
    Person addPersonByName(String name,boolean ex);
}
