package br.group.gil.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@SecurityScheme(
	    name = "Bearer Authentication",
	    type = SecuritySchemeType.HTTP,
	    bearerFormat = "JWT",
	    scheme = "bearer"
	)
@SecurityRequirement(name="Bearer Authentication")
@Configuration
public class OpenApiConfig {
			 
	@Bean
	public OpenAPI customOpenApi() {
		return new OpenAPI()
				.info(new Info()
						.title("RestFul Api with Springboot 3")
						.version("v1")
						.description("Some description")
						.termsOfService("http//www.licensetest.com")
						.license(
						    new License()
						    .name("Apache 2.0")
						    .url("http//www.licensetest.com")));
				
	}
}
