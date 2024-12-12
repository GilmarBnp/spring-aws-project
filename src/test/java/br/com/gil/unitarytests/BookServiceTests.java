package br.com.gil.unitarytests;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import br.com.gil.configs.TestConfigs;
import br.com.gil.integrationtests.vo.AccountCredentialsVO;
import br.com.gil.integrationtests.vo.TokenVO;
import br.group.gil.Startup;
import br.group.gil.data.vo.v1.BookVO;
import br.group.gil.exceptions.RequiredObjetNullException;
import br.group.gil.models.Book;
import br.group.gil.repository.BookRepository;
import br.group.gil.services.BookService;
import br.group.gil.unitytest.mapper.mocks.MockBook;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;

@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(OrderAnnotation.class)
@ContextConfiguration(classes = Startup.class)
class BookServiceTests {
	
	/*
	 * @SpyBean MockBook input;
	 * 
	 * @Autowired private BookService service;
	 * 
	 * @MockBean BookRepository repository;
	 * 
	 * @BeforeEach void setUpMockes() throws Exception { System.out.println(); input
	 * = new MockBook(); MockitoAnnotations.openMocks(this);
	 * 
	 * }
	 * 
	 * @Test
	 * 
	 * @Order(0) public void authorization() throws JsonMappingException,
	 * JsonProcessingException { AccountCredentialsVO user = new
	 * AccountCredentialsVO("leandro", "admin123");
	 * 
	 * var acessToken = given() .basePath("/api/auth/signin")
	 * .port(TestConfigs.SERVER_PORT) .contentType(TestConfigs.CONTENT_TYPE_JSON)
	 * .body(user) .when() .post() .then() .statusCode(200) .extract() .body()
	 * .as(TokenVO.class) .getAcessToken(); }
	 * 
	 * 
	 * @Test
	 * 
	 * @Order(1) void testFindById() throws Exception { Book book =
	 * input.mockEntity((double) 2);
	 * 
	 * book.setId(1L); System.out.println("REPOSITORYYYYYYYYYY" + repository);
	 * when(repository.findById(1L)).thenReturn(Optional.of(book));
	 * 
	 * BookVO result = service.findById(book.getId());
	 * 
	 * assertNotNull(result); assertNotNull(result.getId());
	 * assertNotNull(result.getLinks());
	 * 
	 * assertTrue(result.toString().
	 * contains("links: [<http://localhost/api/book/1>;rel=\"self\"]"));
	 * 
	 * System.out.println("RESULT BOOK NUMBER 2 TITLE "+ result.getTitle());
	 * System.out.println("RESULT BOOK NUMBER 2 AUTHOR "+ result.getAuthor());
	 * System.out.println("RESULT BOOK NUMBER 2 PRICE "+ result.getPrice());
	 * System.out.println("RESULT BOOK NUMBER 2 LAUNCHDATE"+
	 * result.getLaunchDate());
	 * 
	 * assertEquals("Title2.0", result.getTitle()); assertEquals("Author2.0",
	 * result.getAuthor()); assertEquals(2.0, result.getPrice());
	 * assertEquals(result.getLaunchDate(), result.getLaunchDate()); }
	 * 
	 * @Test
	 * 
	 * @Order(2) void testCreate() throws Exception { Book entity =
	 * input.mockEntity((double) 2); Book persisted = entity; entity.setId(1L);
	 * 
	 * BookVO vo = input.mockVO((double) 2); vo.setId(1L);
	 * 
	 * when(repository.save(entity)).thenReturn(persisted);
	 * 
	 * BookVO result = service.create(vo);
	 * 
	 * assertNotNull(result); assertNotNull(result.getId());
	 * assertNotNull(result.getLinks());
	 * 
	 * System.out.println("TEST CREATE RESULT TO STRING " + result.toString());
	 * assertTrue(result.toString().
	 * contains("links: [<http://localhost/api/book/1>;rel=\"self\"]"));
	 * 
	 * assertEquals("Title2.0", result.getTitle()); assertEquals("Author2.0",
	 * result.getAuthor()); assertEquals(2.0, result.getPrice());
	 * assertEquals(result.getLaunchDate(), result.getLaunchDate()); }
	 * 
	 * @Test
	 * 
	 * @Order(3) void testCreateWithNullBook() throws Exception {
	 * 
	 * Exception exception = assertThrows(RequiredObjetNullException.class, () -> {
	 * service.create(null); });
	 * 
	 * String expectedMessage = "It is not allowed to persist a null object!";
	 * String messageReceived = exception.getMessage();
	 * 
	 * assertTrue(messageReceived.contains(expectedMessage)); }
	 * 
	 * @Test
	 * 
	 * @Order(4) void testUpdate() throws Exception { Book entity =
	 * input.mockEntity((double) 2); entity.setId(1L);
	 * 
	 * Book persisted = entity; entity.setId(1L);
	 * 
	 * BookVO vo = input.mockVO((double) 2); vo.setId(1L);
	 * 
	 * when(repository.findById(1L)).thenReturn(Optional.of(entity));
	 * when(repository.save(entity)).thenReturn(persisted);
	 * 
	 * BookVO result = service.update(vo);
	 * 
	 * assertNotNull(result); assertNotNull(result.getId());
	 * assertNotNull(result.getLinks());
	 * 
	 * assertTrue(result.toString().
	 * contains("links: [<http://localhost/api/book/1>;rel=\"self\"]"));
	 * 
	 * assertEquals("Title2.0", result.getTitle()); assertEquals("Author2.0",
	 * result.getAuthor()); assertEquals(2.0, result.getPrice());
	 * assertEquals(result.getLaunchDate(), result.getLaunchDate()); }
	 * 
	 * @Test
	 * 
	 * @Order(5) void testUpdateWithNullBook() throws Exception {
	 * 
	 * Exception exception = assertThrows(NullPointerException.class, () -> {
	 * service.update(null); });
	 * 
	 * String expectedMessage = ""; String messageReceived = exception.getMessage();
	 * 
	 * assertTrue(messageReceived.contains(expectedMessage)); }
	 * 
	 * @Test void testDelete() { Book entity = input.mockEntity((double) 2);
	 * entity.setId(1L);
	 * 
	 * Book persisted = entity; persisted.setId(1L);
	 * 
	 * BookVO vo = input.mockVO((double) 2); vo.setId(1L);
	 * 
	 * when(repository.findById(1L)).thenReturn(Optional.of(persisted)); return; }
	 * 
	 * @Test
	 * 
	 * @Order(6) void testFindAll() throws Exception {
	 * 
	 * Integer page = 0; Integer size = 10; String sortDirection = "asc";
	 * 
	 * Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection,
	 * "author"));
	 * 
	 * PagedModel<EntityModel<BookVO>> bookPage = (PagedModel<EntityModel<BookVO>>)
	 * service.findAll(pageable);
	 * 
	 * for (EntityModel<BookVO> bookEntity : bookPage) { BookVO book =
	 * bookEntity.getContent(); if (book != null) {
	 * System.out.println(book.getAuthor() + " " + book.getAuthor()); } }
	 * 
	 * List<EntityModel<BookVO>> bookPageList = new
	 * ArrayList<>(bookPage.getContent());
	 * 
	 * 
	 * if (bookPageList.size() > 0) {
	 * System.out.println(bookPageList.get(0).getContent().author +
	 * bookPageList.get(0).getContent().title);
	 * 
	 * assertNotNull(bookPageList); assertEquals(14, bookPageList.size());
	 * 
	 * BookVO bookTwo = bookPageList.get(2).getContent();
	 * 
	 * assertNotNull(bookTwo); assertNotNull(bookTwo.getId());
	 * assertNotNull(bookTwo.getLinks());
	 * 
	 * assertTrue(bookTwo.toString().
	 * contains("links: [<http://localhost/api/book/2>;rel=\"self\"]"));
	 * 
	 * assertEquals("Title2.0", bookTwo.getTitle()); assertEquals("Author2.0",
	 * bookTwo.getAuthor()); assertEquals(2.0, bookTwo.getPrice());
	 * assertEquals(bookTwo.getLaunchDate(), bookTwo.getLaunchDate());
	 * 
	 * 
	 * BookVO bookEight = bookPageList.get(8).getContent();
	 * System.out.println("FINDALL BOOK 8 TITLE " +
	 * bookPageList.get(8).getContent().getTitle());
	 * 
	 * assertNotNull(bookEight); assertNotNull(bookEight.getId());
	 * assertNotNull(bookEight.getLinks());
	 * 
	 * assertTrue(bookEight.toString().
	 * contains("links: [<http://localhost/api/book/8>;rel=\"self\"]"));
	 * 
	 * assertEquals("Title8.0", bookEight.getTitle()); assertEquals("Author8.0",
	 * bookEight.getAuthor()); assertEquals(8.0, bookEight.getPrice());
	 * assertEquals(bookEight.getLaunchDate(), bookTwo.getLaunchDate());
	 * 
	 * BookVO bookEleven = bookPageList.get(11).getContent();
	 * 
	 * assertNotNull(bookEleven); assertNotNull(bookEleven.getId());
	 * assertNotNull(bookEleven.getLinks());
	 * 
	 * assertTrue(bookEleven.toString().
	 * contains("links: [<http://localhost/api/book/11>;rel=\"self\"]"));
	 * 
	 * assertEquals("Title11.0", bookEleven.getTitle()); assertEquals("Author11.0",
	 * bookEleven.getAuthor()); assertEquals(11.0, bookEleven.getPrice());
	 * assertEquals(bookEleven.getLaunchDate(), bookEleven.getLaunchDate()); }
	 * 
	 * }
	 */
}
