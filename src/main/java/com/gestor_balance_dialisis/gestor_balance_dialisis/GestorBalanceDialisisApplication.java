package com.gestor_balance_dialisis.gestor_balance_dialisis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class GestorBalanceDialisisApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestorBalanceDialisisApplication.class, args);
	}

}
