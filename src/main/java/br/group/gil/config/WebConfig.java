package br.group.gil.config;


import java.util.List;
import br.group.gil.serialization.converter.YamalJackson2HttpMessageConverter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	private static final MediaType MEDIA_TYPE_APPLICATION_YML = MediaType.valueOf("application/x-yaml");
	
	@Value("${cors.originPatterns:default}")
	private String corsOriginPatters = "";
	
	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(new YamalJackson2HttpMessageConverter());
	}
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		var allowedOrigins = corsOriginPatters.split(",");
	
	registry.addMapping("/**")
		//.allowedMethods("Get", "Post", "Put")
		.allowedMethods("*")
		.allowedOrigins(allowedOrigins)
	  .allowCredentials(true);
	}

	/*Content negotiaton via query param, escolhe o tipo de dado que será retornado na response
	http://localhost:8080/api/person?mediaType=xml
	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {	
	    configurer.favorParameter(true)
        .parameterName("mediaType")
        .ignoreAcceptHeader(true)
        .useRegisteredExtensionsOnly(false)
        .defaultContentType(MediaType.APPLICATION_JSON)
        .mediaType("json", MediaType.APPLICATION_JSON)
        .mediaType("xml", MediaType.APPLICATION_XML);
	}*/
	
	/*Content negotiaton via header param
	http://localhost:8080/api/person?mediaType=xml
		@Override
		public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {	
		    configurer.favorParameter(true)
	        .ignoreAcceptHeader(false)
	        .useRegisteredExtensionsOnly(false)
	        .defaultContentType(MediaType.APPLICATION_JSON)
	        .mediaType("json", MediaType.APPLICATION_JSON)
	        .mediaType("xml", MediaType.APPLICATION_XML);
		}*/
	
	
	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {	
	    configurer.favorParameter(true)
        .ignoreAcceptHeader(false)
        .useRegisteredExtensionsOnly(false)
        .defaultContentType(MediaType.APPLICATION_JSON)
        .mediaType("json", MediaType.APPLICATION_JSON)
        .mediaType("xml", MediaType.APPLICATION_XML)
        .mediaType("x-yamal", MEDIA_TYPE_APPLICATION_YML);
	}
   }
	