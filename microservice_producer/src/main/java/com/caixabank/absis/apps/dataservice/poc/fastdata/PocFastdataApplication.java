package com.caixabank.absis.apps.dataservice.poc.fastdata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;

@SpringBootApplication
@EnableConfigurationProperties
public class PocFastdataApplication {
	public static void main(String[] args) {
		SpringApplication.run(PocFastdataApplication.class);
	}
}
