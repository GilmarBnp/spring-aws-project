package br.com.gil.integrationtests.controller.withyaml;

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
import com.fasterxml.jackson.databind.JsonMappingException;

import br.com.gil.configs.TestConfigs;
import br.com.gil.integrationtests.controller.withyaml.mapper.YmlMapper;
import br.com.gil.integrationtests.pagedmodels.PagedModelPerson;
import br.com.gil.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.gil.integrationtests.vo.AccountCredentialsVO;
import br.com.gil.integrationtests.vo.PersonVO;
import br.com.gil.integrationtests.vo.TokenVO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
public class PersonControllerYamlTest extends AbstractIntegrationTest {
	
	private static RequestSpecification specification;
	private static YmlMapper objectMapper;
	
	private static PersonVO person;	

	@BeforeAll
	public static void setup() {
		objectMapper = new YmlMapper();
		person = new PersonVO();
	}
		
	@Test
	@Order(0)
    public void authorization() throws JsonMappingException, JsonProcessingException {
	
		AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");
		
		var acessToken = given()
			.config(
			  RestAssuredConfig
				.config()
				.encoderConfig(EncoderConfig.encoderConfig()
				.encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
			.basePath("/auth/signin")
			  .port(TestConfigs.SERVER_PORT)
			  .contentType(TestConfigs.CONTENT_TYPE_YAML)
			  .accept(TestConfigs.CONTENT_TYPE_YAML)
			.body(user, objectMapper)	
			  .when()
			.post()
			  .then()
				.statusCode(200)
				  .extract()
				  .body()
				    .as(TokenVO.class, objectMapper)	
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
	
	var persistedPerson = given().spec(specification)
	  .config(
		RestAssuredConfig
		  .config()
		  .encoderConfig(EncoderConfig.encoderConfig()
		  .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
	  .contentType(TestConfigs.CONTENT_TYPE_YAML)
	  .accept(TestConfigs.CONTENT_TYPE_YAML)
		.port(TestConfigs.SERVER_PORT)
		.body(person, objectMapper)
		.when()
	  .post()
	  .then()
		  .statusCode(200)
		    .extract()
		    .body()
		      .as(PersonVO.class, objectMapper);
	
		person = persistedPerson;
		
		assertNotNull(persistedPerson);
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		
		assertEquals("Nelson",persistedPerson.getFirstName());
		assertEquals("Piquet", persistedPerson.getLastName());
		assertEquals("Brasília - DF - Brasil", persistedPerson.getAddress());
		assertEquals("Male", persistedPerson.getGender());
		
		Assertions.assertTrue(persistedPerson.getId() > 0);
	}
	
	@Test
	@Order(2)
    public void testUpdate() throws JsonMappingException, JsonProcessingException {
		
	person.setLastName("Piquet Souto Maior");
							
	var persistedPerson = given().spec(specification)
	  .config(
		RestAssuredConfig
		  .config()
		  .encoderConfig(EncoderConfig.encoderConfig()
		  .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
	  .contentType(TestConfigs.CONTENT_TYPE_YAML)
	  .accept(TestConfigs.CONTENT_TYPE_YAML)
		.port(TestConfigs.SERVER_PORT)
		.body(person, objectMapper)
		.when()
	  .post()
	  .then()
		  .statusCode(200)
		    .extract()
		    .body()
		      .as(PersonVO.class, objectMapper);
	
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
							
		var persistedPerson = given().spec(specification)
		.config(
		  RestAssuredConfig
		    .config()
			.encoderConfig(EncoderConfig.encoderConfig()
			.encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
		.contentType(TestConfigs.CONTENT_TYPE_YAML)
		.accept(TestConfigs.CONTENT_TYPE_YAML)
		  .header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
	    .pathParam("id", person.getId())
	    .when()
		.get("{id}")
	  .then()
		.statusCode(200)
	      .extract()
	       .body()
	        .as(PersonVO.class, objectMapper);
	
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
	   //.contentType(TestConfigs.CONTENT_TYPE_YAML)
	     .pathParam("id", person.getId())
	     .when()
		 .delete("{id}")
	   .then()
		.statusCode(204);
	}
	
	@Test
	@Order(5)
    public void testFindAll() throws JsonMappingException, JsonProcessingException {
	
	 var wrapper = given().spec(specification)
	  .config(
		RestAssuredConfig
		 .config()
		 .encoderConfig(EncoderConfig.encoderConfig()
		 .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
	  .contentType(TestConfigs.CONTENT_TYPE_YAML)
	  .queryParams("page", 3, "size", 10, "direction", "asc")
	  .accept(TestConfigs.CONTENT_TYPE_YAML)
		.port(TestConfigs.SERVER_PORT)
		.body(person, objectMapper)	
		.when()
	  .get()		
	  .then()
		  .statusCode(200)
		    .extract()
		    .body()
		      .as(PagedModelPerson.class, objectMapper);	
		      //.as(new TypeRef<List<PersonVO>>(){
			//});
	
		var people = wrapper.getContent();

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
    public void testFindAllWithoutToken() throws JsonMappingException, JsonProcessingException {
		RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
				.setBasePath("/api/person/v1")
				.setPort(TestConfigs.SERVER_PORT)
					.addFilter(new RequestLoggingFilter(LogDetail.ALL))
					.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
			
			given().spec(specificationWithoutToken)
				.config(
					RestAssuredConfig
						.config()
						.encoderConfig(EncoderConfig.encoderConfig()
							.encodeContentTypeAs(
								TestConfigs.CONTENT_TYPE_YAML,
								ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_TYPE_YAML)
				.accept(TestConfigs.CONTENT_TYPE_YAML)
					.when()
					.get()
				.then()
					.statusCode(403);
	}
	
	@Test
	@Order(7)
    public void testHATEOAS() throws JsonMappingException, JsonProcessingException {
	
	 var content = given().spec(specification)
	  .config(
		RestAssuredConfig
		 .config()
		 .encoderConfig(EncoderConfig.encoderConfig()
		 .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
	  .contentType(TestConfigs.CONTENT_TYPE_YAML)
	  .queryParams("page", 3, "size", 10, "direction", "asc")
	  .accept(TestConfigs.CONTENT_TYPE_YAML)
		.port(TestConfigs.SERVER_PORT)
		.body(person, objectMapper)	
		.when()	
	  .get()		
	  .then()
		  .statusCode(200)
		    .extract()
		    .body()
		      .asString();
	
		System.out.println(content);
	 
	    assertTrue(content.contains("rel: \"self\"\n"
	    		+ "    href: \"http://localhost:8080/api/person/v1/686\""));
		
	    assertTrue(content.contains("rel: \"self\"\n"
				+ "    href: \"http://localhost:8080/api/person/v1/199\""));
		
		assertTrue(content.contains("rel: \"self\"\n"
				+ "    href: \"http://localhost:8080/api/person/v1/797\""));
		
		assertTrue(content.contains("rel: \"next\"\n"
				+ "  href: \"http://localhost:8080/api/person/v1?direction=asc&page=4&size=10&sort=firstName,asc\""));
		
		assertTrue(content.contains("rel: \"prev\"\n"
				+ "  href: \"http://localhost:8080/api/person/v1?direction=asc&page=2&size=10&sort=firstName,asc\""));
		
		assertTrue(content.contains("rel: \"last\"\n"
				+ "  href: \"http://localhost:8080/api/person/v1?direction=asc&page=103&size=10&sort=firstName,asc\""));
		
	}

	private void mockPerson() {
		person.setFirstName("Nelson");
		person.setLastName("Piquet");
		person.setAddress("Brasília - DF - Brasil");
		person.setGender("Male");
		person.setEnabled(true);
	}
}
