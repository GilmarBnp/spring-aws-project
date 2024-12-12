package br.com.gil.integrationtests.swagger;

import static io.restassured.RestAssured.given;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import br.com.gil.configs.TestConfigs;
import br.com.gil.integrationtests.testcontainers.AbstractIntegrationTest;

@SuppressWarnings("unused")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class SwaggerIntegrationTest extends AbstractIntegrationTest {

	@Test
    public void shouldDisplaySwaggerUiPage() {
		
	var content =	
		given()
		.basePath("/swagger-ui/index.html")
		.port(TestConfigs.SERVER_PORT)
		.when()
		  .get()
		.then()
		  .statusCode(200)
		.extract()
		  .body()
		    .asString();
		
		Assertions.assertTrue(content.contains("Swagger UI"));
	}
}
