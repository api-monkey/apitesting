package com.skg.apimonkey;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.skg.apimonkey.domain.model.TestDataCase;
import com.skg.apimonkey.service.SwaggerParserService;
import com.skg.apimonkey.service.DataCreationService;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Objects;

@SpringBootTest
@Slf4j
class TestDataCreationServiceTests {

	@Autowired
	private SwaggerParserService parserService;

	@Autowired
	private DataCreationService dataCreationService;

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

		SwaggerParseResult result = parserService.getSwaggerRestApi("https://petstore.swagger.io/#/pet/addPet");
		List<TestDataCase> cases = dataCreationService.generateTestDataCases(result);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);

		for (TestDataCase dataCase: cases) {
			log.info("Method: {}, path: {}", dataCase.getRequestType().name(), dataCase.getMethodName());
			if (Objects.nonNull(dataCase.getRequestParams())) {
				log.info("Request params: {}", dataCase.getRequestParams());
			}
			if (Objects.nonNull(dataCase.getRequestBody())) {
				log.info("Request body:{}{}", System.lineSeparator(), objectMapper.writeValueAsString(dataCase.getRequestBody()));
			} else {
				log.info("Request body: empty");
			}
		}

		Assert.assertNotNull(result);
	}

}
