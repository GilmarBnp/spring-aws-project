package br.com.gil.integrationtests.testcontainers;

import java.util.Map;
import java.util.stream.Stream;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.lifecycle.Startables;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(initializers = AbstractIntegrationTest.Initializer.class)
public class AbstractIntegrationTest {

	@Configuration
	public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {	
		
		static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.4.1");
				
		private static Map<String, String> createConnectionConfiguration() {
			return Map.of(
				"spring.datasource.url", mysql.getJdbcUrl(),
				"spring.datasource.username", mysql.getUsername(),
				"spring.datasource.password", mysql.getPassword()
			);
		}	
		
		 private static void startContainers() {
		        Startables.deepStart(Stream.of(mysql)).join();
		    }
		
		@SuppressWarnings({"unchecked", "rawtypes"})
		@Override
		public void initialize(ConfigurableApplicationContext applicationContext) {
			startContainers();
			
			ConfigurableEnvironment environment = applicationContext.getEnvironment();
					
			MapPropertySource testcontainers = new MapPropertySource(
				"testcontainers",
				(Map) createConnectionConfiguration());

			environment.getPropertySources().addFirst(testcontainers);
		}	
	}
}
