package com.jozz.venus;

import com.jozz.venus.handler.MyScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MyScan
//@MapperScan("com.jozz.venus.mapper")
@SpringBootApplication
public class VenusApplication {

	public static void main(String[] args) {
		SpringApplication.run(VenusApplication.class, args);
	}

}
