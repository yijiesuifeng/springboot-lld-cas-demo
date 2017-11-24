package com.wind;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@ServletComponentScan
@MapperScan("com.wind.person.dao")// mapper 接口类扫描包配置
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
