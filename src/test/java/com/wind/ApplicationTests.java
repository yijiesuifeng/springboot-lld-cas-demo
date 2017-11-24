package com.wind;

import com.wind.common.redis.IRedisService;
import com.wind.person.domain.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

    @Autowired
    IRedisService redisService;
	@Test
	public void contextLoads() {
        /*Person person = new Person();
        person.setName("李兰东");
        person.setAge(25);
        redisService.setObject("lld",person);*/

        Person lld = redisService.getObject("lld", new Person());
        System.out.println(lld.toString());

    }

}
