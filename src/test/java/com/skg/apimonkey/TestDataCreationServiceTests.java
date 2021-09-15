package com.skg.apimonkey;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.skg.apimonkey.domain.model.RequestType;
import com.skg.apimonkey.domain.model.TestDataCase;
import com.skg.apimonkey.service.SwaggerParserService;
import com.skg.apimonkey.service.DataCreationService;
import com.skg.apimonkey.service.impl.CaseRunnerManager;
import com.skg.apimonkey.service.util.Response;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;
import java.util.*;

@SpringBootTest
@Slf4j
class TestDataCreationServiceTests {

	@Autowired
	private SwaggerParserService parserService;

	@Autowired
	private DataCreationService dataCreationService;

	@Autowired
	private CaseRunnerManager caseRunnerManager;

	@Test
	@Ignore
	void testGetSwaggerRestApi() throws JsonProcessingException {

		SwaggerParseResult result = parserService.getSwaggerRestApi("https://petstore.swagger.io/#/pet/addPet");

		log.info(new ObjectMapper().writeValueAsString(result));
		Assert.assertNotNull(result);
	}

	@Test
	@Ignore
	void testGenerateTestDataForSwagger() throws JsonProcessingException {

		SwaggerParseResult result = parserService.getSwaggerRestApi("https://petstore.swagger.io/v2/swagger.json");
		List<TestDataCase> cases = dataCreationService.generateTestDataCases(result);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);

		for (TestDataCase dataCase: cases) {
			log.info("Method: {}, path: {}", dataCase.getRequestType().name(), dataCase.getMethodName());
			if (Objects.nonNull(dataCase.getRequestParams())) {
				log.info("Request params: {}", dataCase.getRequestParams());
			}
			if (CollectionUtils.isNotEmpty(dataCase.getRequestBodyVariants())) {
				for (int i = 0; i < dataCase.getRequestBodyVariants().size(); i++) {
					log.info("Request body variant [{}, {}] {}:{}{}", dataCase.getRequestType().name(), dataCase.getMethodName(), i + 1, System.lineSeparator(), objectMapper.writeValueAsString(dataCase.getRequestBodyVariants().get(i)));
				}
			} else {
				log.info("Request body: empty");
			}
		}
		Assert.assertNotNull(result);
	}

	@Test
	@Ignore
	void testCaseRunnerManagerPost() throws JsonProcessingException {

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);

		String bodyStr = "{" +
				"  \"id\" : \"0\",\n" +
				"  \"photoUrls\" : [ \"photos url test\" ],\n" +
				"  \"name\" : \"doggie\",\n" +
				"  \"category\" : {\n" +
				"    \"name\" : \"test\",\n" +
				"    \"id\" : \"0\"\n" +
				"  },\n" +
				"  \"tags\" : [ {\n" +
				"    \"name\" : \"tag name\",\n" +
				"    \"id\" : \"0\"\n" +
				"  } ],\n" +
				"  \"status\" : \"pending\"\n" +
				"}";
		Map body = objectMapper.readValue(bodyStr, HashMap.class);

		TestDataCase dataCase = TestDataCase.builder()
				.requestType(RequestType.POST)
				.methodName("/pet")
				.contentType("application/json")
				.serverApiPathes(Collections.singletonList("https://petstore.swagger.io/v2"))
				.isBroken(false)
				.requestBodyVariants(Collections.singletonList(body))
				.build();

		Response response = caseRunnerManager.runDataCase(dataCase);

		log.info("Response: {}", response.getStatusLine().toString());
		log.info("Body: \n{}", response.getBody() == null ? "empty" : objectMapper.writeValueAsString(objectMapper.readValue(new String(response.getBody(), StandardCharsets.UTF_8), HashMap.class)));

		Assert.assertNotNull(response);
	}

}
