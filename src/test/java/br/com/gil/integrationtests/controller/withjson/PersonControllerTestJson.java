package br.com.gil.integrationtests.controller.withjson;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.gil.configs.TestConfigs;
import br.com.gil.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.gil.integrationtests.vo.AccountCredentialsVO;
import br.com.gil.integrationtests.vo.PersonVO;
import br.com.gil.integrationtests.vo.TokenVO;
import br.com.gil.integrationtests.vo.wrappers.WrapperPersonVO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
public class PersonControllerTestJson extends AbstractIntegrationTest {
	private static RequestSpecification specification;
	private static ObjectMapper objectMapper;
	private static PersonVO person;
	
	@BeforeAll
	public static void setup() {
		objectMapper = new ObjectMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		
		person = new PersonVO();
	}
		
	@Test
	@Order(0)
	public void authorization() throws JsonMappingException, JsonProcessingException {
		AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");
	
	var acessToken = given()
		.basePath("/api/auth/signin")
		  .port(TestConfigs.SERVER_PORT)
			.contentType(TestConfigs.CONTENT_TYPE_JSON)
			  .body(user)
			   .when()
			.post()
			  .then()
			   .statusCode(200)
				 .extract()
				  .body()
				   .as(TokenVO.class)
				    .getAcessToken();
	
	specification = new RequestSpecBuilder()
		.addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + acessToken)
		.setBasePath("/api/person/v1")
		.setPort(TestConfigs.SERVER_PORT)
		.addFilter(new RequestLoggingFilter(LogDetail.ALL))
		.addFilter(new RequestLoggingFilter(LogDetail.ALL))
		.build();
		}
	
	@Test
	@Order(1)
    public void testCreate() throws JsonMappingException, JsonProcessingException {
	mockPerson();					
	
	var content = given().spec(specification)
	  .contentType(TestConfigs.CONTENT_TYPE_JSON)
		.port(TestConfigs.SERVER_PORT)
		.body(person)
		.when()
	  .post()
	  .then()
		.statusCode(200)
		  .extract()
		   .body()
		    .asString();
	
		PersonVO createdPerson = objectMapper.readValue(content, PersonVO.class);
		person = createdPerson;
		
		assertNotNull(createdPerson);
		assertNotNull(createdPerson.getId());
		assertNotNull(createdPerson.getFirstName());
		assertNotNull(createdPerson.getLastName());
		assertNotNull(createdPerson.getAddress());
		assertNotNull(createdPerson.getGender());
		
		assertEquals("Nelson",createdPerson.getFirstName());
		assertEquals("Piquet", createdPerson.getLastName());
		assertEquals("Brasília - DF - Brasil", createdPerson.getAddress());
		assertEquals("Male", createdPerson.getGender());
		
		Assertions.assertTrue(createdPerson.getId() > 0);
	}
	
	@Test
	@Order(2)
    public void testUpdate() throws JsonMappingException, JsonProcessingException {
		
	person.setLastName("Piquet Souto Maior");
							
	var content = given().spec(specification)
	  .contentType(TestConfigs.CONTENT_TYPE_JSON)
		.port(TestConfigs.SERVER_PORT)
		.body(person)
		.when()
	  .post()
	  .then()
		.statusCode(200)
		  .extract()
		   .body()
		    .asString();
	
		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
		person = persistedPerson;
		
		assertNotNull(persistedPerson);
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		
		assertEquals("Nelson",persistedPerson.getFirstName());
		assertEquals("Piquet Souto Maior", persistedPerson.getLastName());
		assertEquals("Brasília - DF - Brasil", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
		
		Assertions.assertEquals(person.getId(), persistedPerson.getId() );
	}
	
	@Test
	@Order(3)
    public void testFindById() throws JsonMappingException, JsonProcessingException {
		mockPerson();
							
		var content = given().spec(specification)
		.contentType(TestConfigs.CONTENT_TYPE_JSON)
		  .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
	    .pathParam("id", person.getId())
	    .when()
		.get("{id}")
	  .then()
		.statusCode(200)
	      .extract()
		    .body()
		      .asString();
	
		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
		person = persistedPerson;
		
		assertNotNull(persistedPerson);
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		
		Assertions.assertEquals(person.getId(), persistedPerson.getId() );
		
		assertEquals("Nelson",persistedPerson.getFirstName());
		assertEquals("Piquet Souto Maior", persistedPerson.getLastName());
		assertEquals("Brasília - DF - Brasil", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
		
		Assertions.assertTrue(persistedPerson.getId() > 0);
	}
	
	
	@Test
	@Order(4)
    public void testDelete() throws JsonMappingException, JsonProcessingException {						
	  given().spec(specification)
	   //.contentType(TestConfigs.CONTENT_TYPE_JSON)
	   .pathParam("id", person.getId())
	     .when()
		 .delete("{id}")
	   .then()
		.statusCode(204);
	}
	
	@Test
	@Order(5)
    public void testFindAll() throws JsonMappingException, JsonProcessingException {
	 var content = given().spec(specification)
	  .contentType(TestConfigs.CONTENT_TYPE_JSON)
	  .queryParams("page", 3, "size", 10, "direction", "asc")
	   .port(TestConfigs.SERVER_PORT)
		.body(person)
		.when()
	  .get()	
	  .then()
		.statusCode(200)
		  .extract()
		   .body()
		     .asString();

		WrapperPersonVO embedded = objectMapper.readValue(content, WrapperPersonVO.class);
		var people = embedded.getEmbedded().getPersons();
		
	    PersonVO foundPersonOne = people.get(0);
		person = foundPersonOne; 
		
		assertNotNull(foundPersonOne.getId());
		assertNotNull(foundPersonOne.getFirstName());
		assertNotNull(foundPersonOne.getLastName());
		assertNotNull(foundPersonOne.getAddress());
		assertNotNull(foundPersonOne.getGender());
		
		assertEquals("Alic",foundPersonOne.getFirstName());
		assertEquals("Terbrug", foundPersonOne.getLastName());
		assertEquals("3 Eagle Crest Court", foundPersonOne.getAddress());
		assertEquals("Male", foundPersonOne.getGender());
		
		Assertions.assertEquals(677, foundPersonOne.getId());
		
		PersonVO foundPersonSix = people.get(5);
		person = foundPersonSix;  
		
		assertNotNull(foundPersonSix.getId());
		assertNotNull(foundPersonSix.getFirstName());
		assertNotNull(foundPersonSix.getLastName());
		assertNotNull(foundPersonSix.getAddress());
		assertNotNull(foundPersonSix.getGender());
		
		assertEquals("Allegra",foundPersonSix.getFirstName());
		assertEquals("Dome", foundPersonSix.getLastName());
		assertEquals("57 Roxbury Pass", foundPersonSix.getAddress());
		assertEquals("Female", foundPersonSix.getGender());
		
		Assertions.assertEquals(911, foundPersonSix.getId());
	}
	
	@Test
	@Order(6)
    public void testFindByName() throws JsonMappingException, JsonProcessingException {
	 var content = given().spec(specification)
	  .contentType(TestConfigs.CONTENT_TYPE_JSON)
	  .pathParam("firstName", "ayr")
	  .queryParams("page", 0, "size", 15, "direction", "asc")
	   .port(TestConfigs.SERVER_PORT)
		.body(person)
		.when()
	  .get("/findPersonByName/{firstName}")	
	  .then()
		.statusCode(200)
		  .extract()
		    .body()
		      .asString();
	
		WrapperPersonVO embedded = objectMapper.readValue(content, WrapperPersonVO.class);
		var people = embedded.getEmbedded().getPersons();
		
	    PersonVO foundPersonOne = people.get(0);
		person = foundPersonOne;
		
		assertTrue(foundPersonOne.getEnabled());
		
		assertNotNull(foundPersonOne.getId());
		assertNotNull(foundPersonOne.getFirstName());
		assertNotNull(foundPersonOne.getLastName());
		assertNotNull(foundPersonOne.getAddress());
		assertNotNull(foundPersonOne.getGender());
		
		assertEquals("Ayrton",foundPersonOne.getFirstName());
		assertEquals("Senna", foundPersonOne.getLastName());
		assertEquals("São Paulo", foundPersonOne.getAddress());
		assertEquals("Male", foundPersonOne.getGender());
		
		Assertions.assertEquals(1, foundPersonOne.getId());
	}
	
	@Test
	@Order(7)
    public void testFindAllWithoutToken() throws JsonMappingException, JsonProcessingException {
	 RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
		//.addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + acessToken)
		.setBasePath("/api/person/v1")
		.setPort(TestConfigs.SERVER_PORT)
		.addFilter(new RequestLoggingFilter(LogDetail.ALL))
		.addFilter(new RequestLoggingFilter(LogDetail.ALL))
		.build();	
	 
	given().spec(specificationWithoutToken)
	  .contentType(TestConfigs.CONTENT_TYPE_JSON)
		.port(TestConfigs.SERVER_PORT)
		.body(person)
		.when()
	  .get()	
	  .then()
		.statusCode(403)
		 .extract()
		  .body()
		   .asString();
	}	
	
	/*
	 * @Test
	 * 
	 * @Order(8) public void testHATEOAS() throws JsonMappingException,
	 * JsonProcessingException { var content = given().spec(specification)
	 * .contentType(TestConfigs.CONTENT_TYPE_JSON) .queryParams("page", 3, "size",
	 * 10, "direction", "asc") .port(TestConfigs.SERVER_PORT) .body(person) .when()
	 * .get() .then() .statusCode(200) .extract() .body() .asString();
	 * 
	 * System.out.println("HATEOAS Test Response Content: " + content);
	 * 
	 * assertTrue(content.contains(
	 * "\"_links\":{\"self\":{\"href\":\"http://localhost:8080/api/person/v1/677\"}}}"
	 * )); assertTrue(content.contains(
	 * "\"_links\":{\"self\":{\"href\":\"http://localhost:8080/api/person/v1/414\"}}}"
	 * )); assertTrue(content.contains(
	 * "\"_links\":{\"self\":{\"href\":\"http://localhost:8080/api/person/v1/199\"}}}"
	 * ));
	 * 
	 * 
	 * //assertTrue(content.contains(
	 * "\"page\":{\"size\":10,\"totalElements\":1025,\"totalPages\":102,\"number\":3}"
	 * )); //assertTrue(content.contains(
	 * "\"last\":{\"href\":\"http://localhost:8080/api/person/v1?direction=asc&page=102&size=10&sort=firstName,asc\"}"
	 * )); //assertTrue(content.contains(
	 * "\"next\":{\"href\":\"http://localhost:8080/api/person/v1?direction=asc&page=4&size=10&sort=firstName,asc\"}"
	 * )); //assertTrue(content.contains(
	 * "\"prev\":{\"href\":\"http://localhost:8080/api/person/v1?direction=asc&page=2&size=10&sort=firstName,asc\"}"
	 * )); }
	 */

	private void mockPerson() {
		person.setFirstName("Nelson");
		person.setLastName("Piquet");
		person.setAddress("Brasília - DF - Brasil");
		person.setGender("Male");
		person.setEnabled(true);
	}
}
