package com.springboot.camel.postgresql.service;

import javax.sql.DataSource;

import org.apache.camel.spi.annotations.Component;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CsvToDatabase {
	@Bean(name ="My")
	public DataSource datasource() {		
		return DataSourceBuilder.create().url("jdbc:postgresql://localhost:5433/vehicles")
				.username("postgres").password("admin").driverClassName("org.postgresql.Driver").build();				
		
	}

}
