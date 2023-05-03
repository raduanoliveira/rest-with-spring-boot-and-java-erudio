package br.com.erudio.integrationtests.controller.withYaml;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import br.com.erudio.configs.TestConfigs;
import br.com.erudio.data.vo.v1.security.TokenVO;
import br.com.erudio.integrationtests.controller.withYaml.mapper.YMLMapper;
import br.com.erudio.integrationtests.testcontainer.AbstractIntegrationTest;
import br.com.erudio.integrationtests.vo.AccountCredentialsVO;
import br.com.erudio.integrationtests.vo.PersonVO;
import br.com.erudio.integrationtests.vo.pagedmodels.PagedModelPerson;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class PersonControllerYamlTest extends AbstractIntegrationTest{

	private static RequestSpecification specification;
	private static YMLMapper objectMapper;
	
	private static PersonVO person;
	
	@BeforeAll
	public static void setup() {
		objectMapper = new YMLMapper();
		person = new PersonVO();
	}
	@Test
	@Order(0)
	public void authorization() throws JsonMappingException, JsonProcessingException {
		AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin1234");
		
		var accessToken = given()
				.config(RestAssuredConfig.config().
						encoderConfig(EncoderConfig.encoderConfig().
								encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
				.basePath("/auth/signin")
				.port(TestConfigs.SERVER_PORT)
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
					.body(user,objectMapper)
					.when()
					.post()
				.then()
					.statusCode(200)
				.extract()
					.body().as(TokenVO.class,objectMapper)
					.getAccessToken();
		
		specification = new RequestSpecBuilder()
				.addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken)
				.setBasePath("/api/person/v1")
				.setPort(TestConfigs.SERVER_PORT)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
	}
	
	@Test
	@Order(1)
	public void testCreate() throws JsonMappingException, JsonProcessingException {
		mockPerson();
		
		var createdPerson = given().spec(specification)
				.config(RestAssuredConfig.config().
						encoderConfig(EncoderConfig.encoderConfig().
								encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))	
			.contentType(TestConfigs.CONTENT_TYPE_YML)
			.accept(TestConfigs.CONTENT_TYPE_YML)
			.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
			.body(person,objectMapper)
			.when()
			.post()
			.then()
				.statusCode(200)
			.extract()
				.body().as(PersonVO.class, objectMapper);
		
		
		person = createdPerson;
		
		assertNotNull(createdPerson);
		assertNotNull(createdPerson.getId());
		assertNotNull(createdPerson.getFirstName());
		assertNotNull(createdPerson.getLastName());
		assertNotNull(createdPerson.getAddress());
		assertNotNull(createdPerson.getGender());
		assertTrue(createdPerson.getId() > 0);
		assertTrue(createdPerson.getEnabled());
		
		
		assertEquals("Richard", createdPerson.getFirstName());
		assertEquals("Stallman",createdPerson.getLastName());
		assertEquals("New York",createdPerson.getAddress());
		assertEquals("Male",createdPerson.getGender());
	}

	@Test
	@Order(2)
	public void testUpdate() throws JsonMappingException, JsonProcessingException {
		person.setLastName("Piquet Souto Maior");
		
		var persistedPerson = given().spec(specification)
				.config(RestAssuredConfig.config().
						encoderConfig(EncoderConfig.encoderConfig().
								encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
				.body(person, objectMapper)
				.when()
				.post()
			.then()
				.statusCode(200)
			.extract()
			.body().as(PersonVO.class, objectMapper);
		
		
		person = persistedPerson;
		
		assertNotNull(persistedPerson);
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		assertEquals(person.getId() ,persistedPerson.getId());
		assertTrue(persistedPerson.getEnabled());
		
		assertEquals("Richard", persistedPerson.getFirstName());
		assertEquals("Piquet Souto Maior",persistedPerson.getLastName());
		assertEquals("New York",persistedPerson.getAddress());
		assertEquals("Male",persistedPerson.getGender());
		
	}
	
	@Test
	@Order(3)
	public void testDisablePersonById() throws JsonMappingException, JsonProcessingException {
		mockPerson();
		
		var persistedPerson = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_YML)
					.accept(TestConfigs.CONTENT_TYPE_YML)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
				.pathParam("id", person.getId())
				.when()
				.patch("{id}")
			.then()
				.statusCode(200)
			.extract()
				.body().as(PersonVO.class, objectMapper);
		
		person = persistedPerson;
		
		assertNotNull(persistedPerson);
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		assertFalse(persistedPerson.getEnabled());
		assertEquals(person.getId(),persistedPerson.getId());
		
		assertEquals("Richard", persistedPerson.getFirstName());
		assertEquals("Piquet Souto Maior",persistedPerson.getLastName());
		assertEquals("New York",persistedPerson.getAddress());
		assertEquals("Male",persistedPerson.getGender());
	}
	
	
	@Test
	@Order(4)
	public void testFindById() throws JsonMappingException, JsonProcessingException {
		mockPerson();
		
		var persistedPerson = given().spec(specification)
				.config(RestAssuredConfig.config().
						encoderConfig(EncoderConfig.encoderConfig().
								encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
				.pathParam("id", person.getId())
				.when()
				.get("{id}")
			.then()
				.statusCode(200)
			.extract()
				.body().as(PersonVO.class, objectMapper);
		
		
		person = persistedPerson;
		
		assertNotNull(persistedPerson);
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		assertFalse(persistedPerson.getEnabled());
		assertTrue(persistedPerson.getId() > 0);
		
		assertEquals("Richard", persistedPerson.getFirstName());
		assertEquals("Piquet Souto Maior",persistedPerson.getLastName());
		assertEquals("New York",persistedPerson.getAddress());
		assertEquals("Male",persistedPerson.getGender());
	}
	
	
	@Test
	@Order(5)
	public void testDelete() throws JsonMappingException, JsonProcessingException {

		
		given().spec(specification)
		.config(RestAssuredConfig.config().
				encoderConfig(EncoderConfig.encoderConfig().
						encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
		.contentType(TestConfigs.CONTENT_TYPE_YML)
		.accept(TestConfigs.CONTENT_TYPE_YML)
				.pathParam("id", person.getId())
				.when()
				.delete("{id}")
			.then()
				.statusCode(204);
	}
	
	@Test
	@Order(6)
	public void testFindAll() throws JsonMappingException, JsonProcessingException {
		
		var wrapper = given().spec(specification)
				.config(RestAssuredConfig.config().
						encoderConfig(EncoderConfig.encoderConfig().
								encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
				.queryParams("page",3,"size",10,"direction","asc")
				.when()
				.get()
			.then()
				.statusCode(200)
			.extract()
			.body().as(PagedModelPerson.class, objectMapper);
		
		var people = wrapper.getContent();
		
		PersonVO foundPersonOne = people.get(0);
		
		assertNotNull(foundPersonOne.getId());
		assertNotNull(foundPersonOne.getFirstName());
		assertNotNull(foundPersonOne.getLastName());
		assertNotNull(foundPersonOne.getAddress());
		assertNotNull(foundPersonOne.getGender());
		assertEquals(673, foundPersonOne.getId());
		
		assertEquals("Alic", foundPersonOne.getFirstName());
		assertEquals("Terbrug",foundPersonOne.getLastName());
		assertEquals("3 Eagle Crest Court",foundPersonOne.getAddress());
		assertEquals("Male",foundPersonOne.getGender());
		assertTrue(foundPersonOne.getEnabled());
		
		PersonVO foundPersonTwo = people.get(1);
		 
		assertNotNull(foundPersonTwo.getId());
		assertNotNull(foundPersonTwo.getFirstName());
		assertNotNull(foundPersonTwo.getLastName());
		assertNotNull(foundPersonTwo.getAddress());
		assertNotNull(foundPersonTwo.getGender());
		assertEquals(410, foundPersonTwo.getId());
		assertFalse(foundPersonTwo.getEnabled());
		
		assertEquals("Alie", foundPersonTwo.getFirstName());
		assertEquals("Yeld",foundPersonTwo.getLastName());
		assertEquals("42 Messerschmidt Crossing",foundPersonTwo.getAddress());
		assertEquals("Female",foundPersonTwo.getGender());
	}
	
	@Test
	@Order(7)
	public void testFindAllWithoutToken() throws JsonMappingException, JsonProcessingException {
		
		RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
				.setBasePath("/api/person/v1")
				.setPort(TestConfigs.SERVER_PORT)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
		
		given().spec(specificationWithoutToken)
		.config(RestAssuredConfig.config().
				encoderConfig(EncoderConfig.encoderConfig().
						encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
		.contentType(TestConfigs.CONTENT_TYPE_YML)
		.accept(TestConfigs.CONTENT_TYPE_YML)
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
				.when()
				.get()
			.then()
				.statusCode(403);
	}
	
	@Test
	@Order(8)
	public void testFindByName() throws JsonMappingException, JsonProcessingException {
		
		var wrapper = given().spec(specification)
				.config(RestAssuredConfig.config().
						encoderConfig(EncoderConfig.encoderConfig().
								encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
			.contentType(TestConfigs.CONTENT_TYPE_YML)
			.accept(TestConfigs.CONTENT_TYPE_YML)
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
				.pathParam("firstName", "lean")
				.queryParams("page",0,"size",6,"direction","asc")
				.when()
				.get("findPersonsByName/{firstName}")
			.then()
				.statusCode(200)
			.extract()
			.body().as(PagedModelPerson.class, objectMapper);
		
		var people = wrapper.getContent();
		
		PersonVO foundPersonOne = people.get(0);
		
		assertNotNull(foundPersonOne.getId());
		assertNotNull(foundPersonOne.getFirstName());
		assertNotNull(foundPersonOne.getLastName());
		assertNotNull(foundPersonOne.getAddress());
		assertNotNull(foundPersonOne.getGender());
		
		assertEquals(3, foundPersonOne.getId());
		assertEquals("Leandro", foundPersonOne.getFirstName());
		assertEquals("Almeida",foundPersonOne.getLastName());
		assertEquals("Rua das Algarobas, 1000",foundPersonOne.getAddress());
		assertEquals("Female",foundPersonOne.getGender());
		assertFalse(foundPersonOne.getEnabled());
		
	}
	
	@Test
	@Order(9)
	public void testHATEOAS() throws JsonMappingException, JsonProcessingException {
		
		var unthreatedContent = given().spec(specification)
				.config(RestAssuredConfig.config().
						encoderConfig(EncoderConfig.encoderConfig().
								encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
				.queryParams("page",3,"size",10,"direction","asc")
				.when()
				.get()
			.then()
				.statusCode(200)
			.extract()
			.body().asString();
		
		var content = unthreatedContent.replace("\n", "").replace("\r", "");
		
		assertTrue(content.contains("rel: \"self\"    href: \"http://localhost:8888/api/person/v1/673\""));
		assertTrue(content.contains("rel: \"self\"    href: \"http://localhost:8888/api/person/v1/410\""));
		assertTrue(content.contains("rel: \"self\"    href: \"http://localhost:8888/api/person/v1/842\""));
		
		
		assertTrue(content.contains("rel: \"first\"  href: \"http://localhost:8888/api/person/v1?direction=asc&page=0&size=10&sort=firstName,asc\""));
		assertTrue(content.contains("rel: \"prev\"  href: \"http://localhost:8888/api/person/v1?direction=asc&page=2&size=10&sort=firstName,asc\""));
		assertTrue(content.contains("rel: \"self\"  href: \"http://localhost:8888/api/person/v1?page=3&size=10&direction=asc\""));
		assertTrue(content.contains("rel: \"next\"  href: \"http://localhost:8888/api/person/v1?direction=asc&page=4&size=10&sort=firstName,asc\""));
		assertTrue(content.contains("rel: \"last\"  href: \"http://localhost:8888/api/person/v1?direction=asc&page=100&size=10&sort=firstName,asc\""));
		
		assertTrue(content.contains("page:  size: 10  totalElements: 1004  totalPages: 101  number: 3"));
	}
	
	private void mockPerson() {
		person.setFirstName("Richard");
		person.setLastName("Stallman");
		person.setAddress("New York");
		person.setGender("Male");
		person.setEnabled(true);
	}

}
