package com.springboot.camel.postgresql.route;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.camel.postgresql.entity.Book;
import com.springboot.camel.postgresql.repository.BookRepository;
import com.springboot.camel.postgresql.service.BookService;

import com.sun.rowset.internal.Row;

import java.sql.RowId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import javax.sql.DataSource;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.model.dataformat.BindyType;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.spi.DataFormat;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class BookRoute extends RouteBuilder {
	Logger logger=org.slf4j.LoggerFactory.getLogger(BookRoute.class);
	//EmployeeRepository employeeRepository;
	private final Environment env;

	@Autowired
	private BookRepository repo;


	public BookRoute(Environment env) {
		this.env = env;
	}

	public void configure() throws Exception {

		restConfiguration()
		.contextPath(env.getProperty("camel.component.servlet.mapping.contextPath", "/rest/*"))
		.apiContextPath("/api-doc")
		.apiProperty("api.title", "Spring Boot Camel Postgres Rest API.")
		.apiProperty("api.version", "1.0")
		.apiProperty("cors", "true")
		.apiContextRouteId("doc-api")
		.port(env.getProperty("server.port", "8080"))
		.bindingMode(RestBindingMode.json);

		//retrieves a book by its name from the BookRepository.

		rest("/getBookbyName")
		.consumes(MediaType.APPLICATION_JSON_VALUE)
		.produces(MediaType.APPLICATION_JSON_VALUE)
		.get("/{name}")
		.route()
		.to("{{route.findBookByName}}")
		.endRest();
		
		//retrieves a list of all books from the BookRepository
		rest("/getBook")
		.consumes(MediaType.APPLICATION_JSON_VALUE)
		.produces(MediaType.APPLICATION_JSON_VALUE)
		.get("/")
		.route()
		.to("{{route.findAllBooks}}")
		.endRest();

      //adds a new book to the BookRepository.
		rest("/addBook")
		.consumes(MediaType.APPLICATION_JSON_VALUE)
		.produces(MediaType.APPLICATION_JSON_VALUE)
		.post("/")
		.route()
		.marshal().json()
		.unmarshal(getJacksonDataFormat(Book.class))
		.to("{{route.saveBook}}")
		.endRest();
		//delete a book from the BookRepository by its ID.
		rest("/delete")
		.consumes(MediaType.APPLICATION_JSON_VALUE)
		.produces(MediaType.APPLICATION_JSON_VALUE)
		.delete("/{bookId}")
		.route()
		.to("{{route.removeBook}}")
		.end();

		//reads CSV data
		rest("/readCsv")
		.consumes(MediaType.APPLICATION_JSON_VALUE)
		.produces(MediaType.APPLICATION_JSON_VALUE).get()
		.route()
		.to("{{route.save}}")
		.endRest();

		//updates a book in the BookRepository.
		rest("/update")
		.consumes(MediaType.APPLICATION_JSON_VALUE)
		.produces(MediaType.APPLICATION_JSON_VALUE)
		.put("/{bookId}")
		.type(Book.class) 
		.route()
		.to("{{route.updateBook}}").process(exchange -> { 
			exchange.getIn().setBody("Book Details updated successfully");
		});; 

		from("{{route.updateBook}}")
		.log("Received body : ${body}")
		.bean(BookService.class, "updateBook"); 

		from("{{route.findBookByName}}")
		.log("Received header : ${header.name}")
		.bean(BookService.class, "findBookByName(${header.name})");

		from("{{route.findAllBooks}}")
		.bean(BookService.class, "findAllBooks");


		from("{{route.saveBook}}")
		.log("Received Body ${body}")
		.bean(BookService.class, "addBook(${body})") .process(exchange -> {
			exchange.getIn().setBody("Book Details Saved successfully");
		});

		from("{{route.save}}")
		.bean(BookService.class, "save()")
		.setBody(constant("Data from File has been read successfully"));


		from("{{route.removeBook}}")
		.log("Received header : ${header.bookId}")
		.bean(BookService.class, "removeBook(${header.bookId})").process(exchange -> {
	     
	        String deletedBookData = exchange.getIn().getBody(String.class);
	      
	        System.out.println("Deleted Book Data: " + deletedBookData);
	       
	        exchange.getMessage().setBody(deletedBookData);
	        
	        exchange.getMessage().setHeader("Content-Type", "application/json");
	    }) ;

	}

	private JacksonDataFormat getJacksonDataFormat(Class<?> unmarshalType) {
		JacksonDataFormat format = new JacksonDataFormat();
		format.setUnmarshalType(unmarshalType);
		return format;
	}
}
