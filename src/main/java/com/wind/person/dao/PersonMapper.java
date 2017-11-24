package com.wind.person.dao;

import com.wind.person.domain.Person;
import org.apache.ibatis.annotations.Param;

public interface PersonMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Person record);

    int insertSelective(Person record);

    Person selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Person record);

    int updateByPrimaryKey(Person record);

    Person selectPersonByName(@Param("name") String name);
}