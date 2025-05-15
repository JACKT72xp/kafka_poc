package com.caixabank.absis.apps.dataservice.poc.fastdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class PocFastdataApplication {
	public static void main(String[] args) {
		System.out.println("Hola Mundo");
		SpringApplication.run(PocFastdataApplication.class);
	}
}
