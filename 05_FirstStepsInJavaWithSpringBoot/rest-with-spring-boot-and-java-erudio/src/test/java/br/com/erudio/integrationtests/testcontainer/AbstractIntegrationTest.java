package br.com.erudio.integrationtests.testcontainer;

import java.util.Map;
import java.util.stream.Stream;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.lifecycle.Startables;

@ContextConfiguration(initializers = AbstractIntegrationTest.Initializer.class)
public class AbstractIntegrationTest {

	static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
		
		static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.29");
		
		private static void startContainers() {
			Startables.deepStart(Stream.of(mysql)).join();
		}

		private static Map<String, String> createConnectionConfiguration() {
			
			System.out.println("getJdbcUrl");
			System.out.println(mysql.getJdbcUrl());
			System.out.println("get UserName");
			System.out.println(mysql.getUsername());
			System.out.println("get Password");
			System.out.println(mysql.getPassword());
			
			return Map.of("spring.datasource.url", mysql.getJdbcUrl(),
					"spring.datasource.username", mysql.getUsername(),
					"spring.datasource.password", mysql.getPassword());
		}
		
		@SuppressWarnings({"unchecked", "rawtypes"})
		@Override
		public void initialize(ConfigurableApplicationContext applicationContext) {
			startContainers();
			ConfigurableEnvironment enviroment = applicationContext.getEnvironment();
			
			MapPropertySource testcontainers = new MapPropertySource("testcontainers", (Map) createConnectionConfiguration());
			
			enviroment.getPropertySources().addFirst(testcontainers);
		}

	}

}
