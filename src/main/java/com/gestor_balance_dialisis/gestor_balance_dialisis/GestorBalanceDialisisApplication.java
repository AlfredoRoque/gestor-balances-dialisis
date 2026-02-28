package com.gestor_balance_dialisis.gestor_balance_dialisis;

import com.gestor_balance_dialisis.gestor_balance_dialisis.config.KafkaSslInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class GestorBalanceDialisisApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(GestorBalanceDialisisApplication.class)
				.initializers(new KafkaSslInitializer())
				.run(args);
	}

}
