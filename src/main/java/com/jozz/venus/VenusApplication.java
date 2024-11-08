package com.jozz.venus;

import com.jozz.venus.annotation.DaoScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@DaoScan("com.jozz.venus.dao")
@SpringBootApplication
@MapperScan("com.jozz.venus.mapper")
public class VenusApplication {

	public static void main(String[] args) {
		SpringApplication.run(VenusApplication.class, args);
	}

}
