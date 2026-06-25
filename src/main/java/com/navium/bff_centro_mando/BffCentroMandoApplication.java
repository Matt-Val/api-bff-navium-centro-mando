package com.navium.bff_centro_mando;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.navium")
public class BffCentroMandoApplication {

	public static void main(String[] args) {
		SpringApplication.run(BffCentroMandoApplication.class, args);
	}
}