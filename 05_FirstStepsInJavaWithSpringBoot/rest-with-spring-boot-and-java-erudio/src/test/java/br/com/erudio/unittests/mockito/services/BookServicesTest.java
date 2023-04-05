package br.com.erudio.unittests.mockito.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.erudio.Book;
import br.com.erudio.Person;
import br.com.erudio.data.vo.v1.BookVO;
import br.com.erudio.data.vo.v1.PersonVO;
import br.com.erudio.exceptions.RequiredObjectsIsNullException;
import br.com.erudio.repositories.BookRepository;
import br.com.erudio.services.BookServices;
import br.com.erudio.unittests.mapper.mocks.MockBook;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class BookServicesTest {
	
	MockBook input;
	
	@InjectMocks
	private BookServices service;
	
	@Mock
	BookRepository repository;
	
	@BeforeEach
	void setUpMocks() throws Exception{
		input = new MockBook();
		MockitoAnnotations.openMocks(this);
	}
	
	
	
	@Test
	void testFindAll() {
		List<Book> list = input.mockEntityList();
		when(repository.findAll()).thenReturn(list);
		
		var books = service.findAll();
		assertNotNull(books);
		assertEquals(14, books.size());
		
		var bookOne = books.get(1);
		

		assertNotNull(bookOne);
		assertNotNull(bookOne.getKey());
		assertNotNull(bookOne.getLinks());
		assertTrue(bookOne.toString().contains("links: [</api/book/v1/1>;rel=\"self\"]"));
		assertEquals("Author1", bookOne.getAuthor());
		assertEquals(1.0,bookOne.getPrice());
		assertEquals("Title1",bookOne.getTitle());
		assertEquals(1, bookOne.getKey());
	}
	
	@Test
	void testFindById() {
		Book entity = input.mockEntity(1);
		entity.setId(1);
		when(repository.findById(1)).thenReturn(Optional.of(entity));
		
		var result = service.findById(1);
		assertNotNull(result);
		assertNotNull(result.getKey());
		assertNotNull(result.getLinks());
		assertTrue(result.toString().contains("links: [</api/book/v1/1>;rel=\"self\"]"));
		assertEquals("Author1", result.getAuthor());
		assertEquals(1.0,result.getPrice());
		assertEquals("Title1",result.getTitle());
		assertEquals(1, result.getKey());
	}
	
	@Test
	void testCreate() {
		Book entity = input.mockEntity(1);
		Book persisted = entity;
		persisted.setId(1);
		
		BookVO vo = input.mockVO(1);
		vo.setKey(1);
		
		when(repository.save(entity)).thenReturn(persisted);
		
		var result = service.create(vo);
		assertNotNull(result);
		assertNotNull(result.getKey());
		assertNotNull(result.getLinks());
		assertTrue(result.toString().contains("links: [</api/book/v1/1>;rel=\"self\"]"));
		assertEquals("Author1", result.getAuthor());
		assertEquals(1.0,result.getPrice());
		assertEquals("Title1",result.getTitle());
		assertEquals(1, result.getKey());
	}
	
	@Test
	void testCreateWithNullPerson() {

		Exception exception = assertThrows(RequiredObjectsIsNullException.class, () -> {
			service.create(null);
		});
		
		String expectedMessage = "It is not allowed to persist a null object";
		String actualMessage = exception.getMessage();
		assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void testUpdate() {
		Book entity = input.mockEntity(1);
		entity.setId(1);
		
		Book persisted = entity;
		persisted.setId(1);
		
		BookVO vo = input.mockVO(1);
		vo.setKey(1);
		
		when(repository.findById(1)).thenReturn(Optional.of(entity));
		when(repository.save(entity)).thenReturn(persisted);
		
		var result = service.update(vo);
		assertNotNull(result);
		assertNotNull(result.getKey());
		assertNotNull(result.getLinks());
		assertTrue(result.toString().contains("links: [</api/book/v1/1>;rel=\"self\"]"));
		assertEquals("Author1", result.getAuthor());
		assertEquals(1.0,result.getPrice());
		assertEquals("Title1",result.getTitle());
	}
	
	@Test
	void testUpdateWithNullPerson() {

		Exception exception = assertThrows(RequiredObjectsIsNullException.class, () -> {
			service.update(null);
		});
		
		String expectedMessage = "It is not allowed to persist a null object";
		String actualMessage = exception.getMessage();
		assertTrue(actualMessage.contains(expectedMessage));
	}
	
	@Test
	void testDelete() {
		Book entity = input.mockEntity(1);
		entity.setId(1);
		when(repository.findById(1)).thenReturn(Optional.of(entity));
		
		service.delete(1);
	}
	
}
