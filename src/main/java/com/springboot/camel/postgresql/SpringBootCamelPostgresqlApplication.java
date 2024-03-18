package com.springboot.camel.postgresql;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.camel.spi.annotations.Component;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.springboot.camel.postgresql.entity.Book;

@SpringBootApplication
@Component("com.springboot.camel.postgresql.service")
public class SpringBootCamelPostgresqlApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootCamelPostgresqlApplication.class, args);
		
	}	
}
