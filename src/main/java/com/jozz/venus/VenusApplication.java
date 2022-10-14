package com.jozz.venus;

import com.jozz.venus.annotation.DaoScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@DaoScan("com.jozz.venus.dao")
@SpringBootApplication
public class VenusApplication {

	public static void main(String[] args) {
		SpringApplication.run(VenusApplication.class, args);
	}

}
