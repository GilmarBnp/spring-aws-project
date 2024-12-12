package br.com.gil.integrationtests.controller.withyaml;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertNotNull;

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
import br.com.gil.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.gil.integrationtests.vo.AccountCredentialsVO;
import br.com.gil.integrationtests.vo.TokenVO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
public class AuthControllerYamlTest extends AbstractIntegrationTest{
	
	private static YmlMapper objectMapper;
	
	private static TokenVO tokenVO;

	@BeforeAll
	public static void setup(){
		objectMapper = new YmlMapper();
	}
	
	@Test
	@Order(1)
    public void testSigning() throws JsonMappingException, JsonProcessingException {
		
		RequestSpecification specification = new RequestSpecBuilder()
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.build();
		
		AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");
		
		tokenVO = given().spec(specification)
				.config(
					RestAssuredConfig
						.config()
						.encoderConfig(EncoderConfig.encoderConfig()
								 .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
				.accept(TestConfigs.CONTENT_TYPE_YAML)
				.body(user, objectMapper)
				.basePath("/api/auth/signin")
				  .port(TestConfigs.SERVER_PORT)
				  .contentType(TestConfigs.CONTENT_TYPE_YAML)
				  .when()
				.post()
				  .then()
				  .log().all() 
				  .statusCode(200)
					  .extract()
					  .body()
					    .as(TokenVO.class, objectMapper);

		assertNotNull(tokenVO.getAcessToken());
		assertNotNull(tokenVO.getRefreshToken());	
	}
	
	@Test
	@Order(2)
    public void testRefresh() throws JsonMappingException, JsonProcessingException {
		
		@SuppressWarnings("unused")
		AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");
		
		var newtokenVO = given()
				.config(RestAssuredConfig
					.config()
					.encoderConfig(EncoderConfig.encoderConfig()
							 .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YAML, ContentType.TEXT)))
				.accept(TestConfigs.CONTENT_TYPE_YAML)
				.basePath("/api/auth/refresh")
				  .port(TestConfigs.SERVER_PORT)
				  .contentType(TestConfigs.CONTENT_TYPE_XML)
				.pathParam("username", tokenVO.getUsername())
				.header(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + tokenVO.getRefreshToken())
				  .when()
				    .put("{username}")
				  .then()
					.statusCode(200)
					  .extract()
					  .body()
					    .as(TokenVO.class, objectMapper);

		assertNotNull(newtokenVO.getAcessToken());
		assertNotNull(newtokenVO.getRefreshToken());	
	}
	
}
