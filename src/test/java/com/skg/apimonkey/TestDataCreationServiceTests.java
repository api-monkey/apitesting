package com.skg.apimonkey;

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
	void testGetSwaggerRestApi() {

		SwaggerParseResult result = parserService.getSwaggerRestApi("https://petstore.swagger.io/#/pet/addPet");

		Assert.assertNotNull(result);
	}

	@Test
	@Ignore
	void testGenerateTestDataForSwagger() {

		SwaggerParseResult result = parserService.getSwaggerRestApi("https://petstore.swagger.io/#/pet/addPet");
		List<TestDataCase> cases = dataCreationService.generateTestDataCases(result);

		cases.forEach(i -> {
			log.info("Method: {}, path: {}", i.getRequestType().name(), i.getMethodName());
			if (Objects.nonNull(i.getRequestParams())) {
				log.info("Request params: {}", i.getRequestParams());
			}
			if (Objects.nonNull(i.getRequestBody())) {
				log.info("Request body: {}", i.getRequestBody());
			}
		});

		Assert.assertNotNull(result);
	}

}
