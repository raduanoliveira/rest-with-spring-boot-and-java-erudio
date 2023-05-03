package br.com.erudio.integrationtests.controller.withJson;

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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.erudio.configs.TestConfigs;
import br.com.erudio.data.vo.v1.security.TokenVO;
import br.com.erudio.integrationtests.testcontainer.AbstractIntegrationTest;
import br.com.erudio.integrationtests.vo.AccountCredentialsVO;
import br.com.erudio.integrationtests.vo.PersonVO;
import br.com.erudio.integrationtests.vo.wrappers.WrapperPersonVO;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class PersonControllerJsonTest extends AbstractIntegrationTest{

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
		AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin1234");
		
		var accessToken = given()
				.basePath("/auth/signin")
				.port(TestConfigs.SERVER_PORT)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.body(user)
					.when()
					.post()
				.then()
					.statusCode(200)
				.extract()
					.body().as(TokenVO.class)
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
		
		var content = given().spec(specification)
			.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
				.body(person)
				.when()
				.post()
			.then()
				.statusCode(200)
			.extract()
				.body().asString();
		
		PersonVO createdPerson = objectMapper.readValue(content, PersonVO.class);
		
		person = createdPerson;
		
		assertNotNull(createdPerson);
		assertNotNull(createdPerson.getId());
		assertNotNull(createdPerson.getFirstName());
		assertNotNull(createdPerson.getLastName());
		assertNotNull(createdPerson.getAddress());
		assertNotNull(createdPerson.getGender());
		assertTrue(createdPerson.getEnabled());
		
		assertTrue(createdPerson.getId() > 0);
		
		assertEquals("Richard", createdPerson.getFirstName());
		assertEquals("Stallman",createdPerson.getLastName());
		assertEquals("New York",createdPerson.getAddress());
		assertEquals("Male",createdPerson.getGender());
	}

	@Test
	@Order(2)
	public void testUpdate() throws JsonMappingException, JsonProcessingException {
		person.setLastName("Piquet Souto Maior");
		
		var content = given().spec(specification)
			.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
				.body(person)
				.when()
				.post()
			.then()
				.statusCode(200)
			.extract()
				.body().asString();
		
		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
		
		person = persistedPerson;
		
		assertNotNull(persistedPerson);
		assertNotNull(persistedPerson.getId());
		assertNotNull(persistedPerson.getFirstName());
		assertNotNull(persistedPerson.getLastName());
		assertNotNull(persistedPerson.getAddress());
		assertNotNull(persistedPerson.getGender());
		assertTrue(persistedPerson.getEnabled());
		
		assertEquals(person.getId() ,persistedPerson.getId());
		
		assertEquals("Richard", persistedPerson.getFirstName());
		assertEquals("Piquet Souto Maior",persistedPerson.getLastName());
		assertEquals("New York",persistedPerson.getAddress());
		assertEquals("Male",persistedPerson.getGender());
		
	}
	
	
	@Test
	@Order(3)
	public void testDisablePersonById() throws JsonMappingException, JsonProcessingException {
		mockPerson();
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
				.pathParam("id", person.getId())
				.when()
				.patch("{id}")
			.then()
				.statusCode(200)
			.extract()
				.body().asString();
		
		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
		
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
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
				.pathParam("id", person.getId())
				.when()
				.get("{id}")
			.then()
				.statusCode(200)
			.extract()
				.body().asString();
		
		PersonVO persistedPerson = objectMapper.readValue(content, PersonVO.class);
		
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
	@Order(5)
	public void testDelete() throws JsonMappingException, JsonProcessingException {

		
		given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.pathParam("id", person.getId())
				.when()
				.delete("{id}")
			.then()
				.statusCode(204);
	}
	
	@Test
	@Order(6)
	public void testFindAll() throws JsonMappingException, JsonProcessingException {
		
		var content = given().spec(specification)
			.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
				.queryParams("page",3,"size",10,"direction","asc")
				.when()
				.get()
			.then()
				.statusCode(200)
			.extract()
				.body()
				.asString();
		
		WrapperPersonVO wrapper = objectMapper.readValue(content, WrapperPersonVO.class);
		var people = wrapper.getEmbedded().getPersons();
		
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
	public void testFindByIdWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
		mockPerson();
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_SEMERU)
				.pathParam("id", person.getId())
				.when()
				.get("{id}")
			.then()
				.statusCode(403)
			.extract()
				.body().asString();
		
		
		assertNotNull(content);
		assertEquals("Invalid CORS request",content);
	}
	
	@Test
	@Order(8)
	public void testFindAllWithoutToken() throws JsonMappingException, JsonProcessingException {
		
		RequestSpecification specificationWithoutToken = new RequestSpecBuilder()
				.setBasePath("/api/person/v1")
				.setPort(TestConfigs.SERVER_PORT)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
		
		given().spec(specificationWithoutToken)
			.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
				.when()
				.get()
			.then()
				.statusCode(403);
	}
	
	@Test
	@Order(9)
	public void testCreateWithWrongOrigin() throws JsonMappingException, JsonProcessingException {
		mockPerson();
		
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_JSON)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_SEMERU)
				.body(person)
				.when()
				.post()
			.then()
				.statusCode(403)
			.extract()
				.body().asString();
		

		
		assertNotNull(content);
		assertEquals("Invalid CORS request",content);
	}
	
	@Test
	@Order(10)
	public void testFindByName() throws JsonMappingException, JsonProcessingException {
		
		var content = given().spec(specification)
			.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
				.pathParam("firstName", "lean")
				.queryParams("page",0,"size",6,"direction","asc")
				.when()
				.get("findPersonsByName/{firstName}")
			.then()
				.statusCode(200)
			.extract()
				.body()
				.asString();
		
		WrapperPersonVO wrapper = objectMapper.readValue(content, WrapperPersonVO.class);
		var people = wrapper.getEmbedded().getPersons();
		
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
	@Order(11)
	public void testHATEOAS() throws JsonMappingException, JsonProcessingException {
		
		var content = given().spec(specification)
			.contentType(TestConfigs.CONTENT_TYPE_JSON)
				.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
				.queryParams("page",3,"size",10,"direction","asc")
				.when()
				.get()
			.then()
				.statusCode(200)
			.extract()
				.body()
				.asString();
		
		
		assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/person/v1/673\"}}}"));
		assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/person/v1/410\"}}}"));
		assertTrue(content.contains("\"_links\":{\"self\":{\"href\":\"http://localhost:8888/api/person/v1/842\"}}}"));
		
		
		assertTrue(content.contains("\"first\":{\"href\":\"http://localhost:8888/api/person/v1?direction=asc&page=0&size=10&sort=firstName,asc\"}"));
		assertTrue(content.contains("\"prev\":{\"href\":\"http://localhost:8888/api/person/v1?direction=asc&page=2&size=10&sort=firstName,asc\"}"));
		assertTrue(content.contains("\"self\":{\"href\":\"http://localhost:8888/api/person/v1?page=3&size=10&direction=asc\"}"));
		assertTrue(content.contains("\"next\":{\"href\":\"http://localhost:8888/api/person/v1?direction=asc&page=4&size=10&sort=firstName,asc\"}"));
		assertTrue(content.contains("\"last\":{\"href\":\"http://localhost:8888/api/person/v1?direction=asc&page=100&size=10&sort=firstName,asc\"}}"));
		
		assertTrue(content.contains("\"page\":{\"size\":10,\"totalElements\":1004,\"totalPages\":101,\"number\":3}}"));
	}
	
	
	private void mockPerson() {
		person.setFirstName("Richard");
		person.setLastName("Stallman");
		person.setAddress("New York");
		person.setGender("Male");
		person.setEnabled(true);
		
	}

}
