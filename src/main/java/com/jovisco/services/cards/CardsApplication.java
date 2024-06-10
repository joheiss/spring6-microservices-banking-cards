package com.jovisco.services.cards;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition(info = @Info(title = "Cards Microservice - REST API Documentation", description = "Learning how to develop, build, document and deploy microservices with Spring Boot", version = "1.0.0", contact = @Contact(name = "Josef Heiss", email = "contact@jovisco.de", url = "https://www.jovisco.de"), license = @License(name = "Apache 2.0", url = "https://www.jovisco.de")), externalDocs = @ExternalDocumentation(description = "Spring Microservices Course: Cards Microservice - REST API Documentation", url = "https://www.example.com"))
@SpringBootApplication
public class CardsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CardsApplication.class, args);
	}

}
