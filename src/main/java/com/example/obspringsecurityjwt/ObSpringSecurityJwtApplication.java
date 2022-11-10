package com.example.obspringsecurityjwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Clase punto de entrada app Spring Boot
 */
@SpringBootApplication
//@EnableWebMvc
public class ObSpringSecurityJwtApplication {

	public static void main(String[] args) {
		// Contenedor de beans
				SpringApplication.run(ObSpringSecurityJwtApplication.class, args);
	}

}
