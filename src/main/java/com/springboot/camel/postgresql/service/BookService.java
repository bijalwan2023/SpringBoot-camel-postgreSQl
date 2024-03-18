package com.springboot.camel.postgresql.service;

import com.springboot.camel.postgresql.entity.Book;
import com.springboot.camel.postgresql.repository.BookRepository;

import javassist.NotFoundException;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

@Service
public class BookService {

	@Autowired
	private CamelContext camelContext;
	private final BookRepository bookRepository;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	//Book book=new Book();

	public BookService(BookRepository bookRepository) {
		this.bookRepository = bookRepository;
	}


	//This method retrieves a list of all books from the BookRepository.
	public List<Book> findAllBooks() {
		return bookRepository.findAll();
	}

	//This method retrieves a book by its name from the BookRepository.
	public Book findBookByName(String name) {
		return bookRepository.findBookByName(name);
	}

	//This method updates a book in the BookRepository.
	public Book updateBook(Book books)  {
		return bookRepository.save(books);
	}

	//This method adds a new book to the BookRepository.

	public Book addBook(Book book) {
		return bookRepository.save(book);
	}

	// This method removes a book from the BookRepository by its ID.
	public void removeBook(int bookId) {
		bookRepository.deleteById(bookId);
	}

	//It reads CSV data, creates Book objects, and saves them to the database.
	public void  save() {
		Properties properties = new Properties();
		String csvFilePath = properties.getProperty("csvFilePath");
		List<Book> books =  readBooksFromCSV("C:\\csv\\csvFiles.csv");

		for(Book book:books) {
			System.out.println(book);
			bookRepository.save(book);
		}
	}


	// This method reads books from a CSV file and returns them as a list of Book objects.
	private  List<Book> readBooksFromCSV(String fileName) {
		List<Book> books = new ArrayList<>();
		Path pathToFile = Path.of(fileName);
		try (BufferedReader br = Files.newBufferedReader(pathToFile,StandardCharsets.US_ASCII))
		{
			String line =br.readLine();
			while(line != null) {
				String [] attributes = line.split(",");
				Book book;
				book = createBook(attributes);
				books.add(book);
				//System.out.println(books.get(1));
				line = br.readLine();
			}
		}

		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return books;
	}
	
	
	//This  method creates a Book object from an array of metadata, likely representing the attributes of a book.
	public   Book createBook(String[] metadata) {

		int id=Integer.parseInt(metadata[0]);
		System.out.println(id);
		String name=metadata[1];
		String author = metadata[2];
		double price = Double.parseDouble(metadata[3]);
		return new Book(id , author, name,price);
	}

}
