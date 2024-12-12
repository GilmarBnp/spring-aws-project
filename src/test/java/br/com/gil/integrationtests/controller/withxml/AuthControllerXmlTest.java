package br.com.gil.integrationtests.controller.withxml;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import br.com.gil.configs.TestConfigs;
import br.com.gil.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.gil.integrationtests.vo.AccountCredentialsVO;
import br.com.gil.integrationtests.vo.TokenVO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
public class AuthControllerXmlTest extends AbstractIntegrationTest{
	
	private static TokenVO tokenVO;

	@Test
	@Order(1)
    public void testSigning() throws JsonMappingException, JsonProcessingException {
		
		AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");
		
		tokenVO = given()
				.basePath("/api/auth/signin")
				  .port(TestConfigs.SERVER_PORT)
				  .contentType(TestConfigs.CONTENT_TYPE_XML)
				.body(user)
				  .when()
				.post()
				  .then()
					.statusCode(200)
					  .extract()
					  .body()
					    .as(TokenVO.class);

		assertNotNull(tokenVO.getAcessToken());
		assertNotNull(tokenVO.getRefreshToken());	
	}
	
	@Test
	@Order(2)
    public void testRefresh() throws JsonMappingException, JsonProcessingException {
		
		@SuppressWarnings("unused")
		AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");
		
		var newtokenVO = given()
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
					    .as(TokenVO.class);

		assertNotNull(newtokenVO.getAcessToken());
		assertNotNull(newtokenVO.getRefreshToken());	
	}
	
}
