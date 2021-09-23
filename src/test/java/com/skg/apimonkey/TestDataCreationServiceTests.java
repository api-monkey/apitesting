package com.skg.apimonkey;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.skg.apimonkey.domain.model.ParametersDataCase;
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
import java.util.stream.Collectors;

import static com.skg.apimonkey.service.util.StringUtil.isJson;

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
		List<TestDataCase> cases = dataCreationService.generateTestDataCases(result, 3);

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);

		for (TestDataCase dataCase: cases) {
			log.info("Method: {}, path: {}", dataCase.getRequestType().name(), dataCase.getMethodName());

			if (Objects.nonNull(dataCase.getRequestParamsVariants())) {
				for (int i = 0; i < dataCase.getRequestParamsVariants().size(); i++) {

					ParametersDataCase parameterItem = dataCase.getRequestParamsVariants().get(i);
					long pathParamsCount = parameterItem.isNoParams() ? 0 : parameterItem.getParameterItems().stream().filter(j -> !j.isInPath()).count();
					boolean isNoParams = parameterItem.isNoParams() || pathParamsCount == 0;

					log.info("Request param variant [{}, {}] {}:{}{}",
							dataCase.getRequestType().name(),
							objectMapper.writeValueAsString(parameterItem.getModifiedPath()),
							i + 1,
							System.lineSeparator(),
							isNoParams ?
									"<no params>" :
									parameterItem.getParameterItems().stream()
											.filter(j -> !j.isInPath())
											.map(j -> String.format("%s = %s", j.getKey(), j.getValue()))
											.collect(Collectors.joining(System.lineSeparator()))
					);
				}
			}
			if (CollectionUtils.isNotEmpty(dataCase.getRequestBodyVariants())) {
				for (int i = 0; i < dataCase.getRequestBodyVariants().size(); i++) {
					log.info("Request body variant [{}, {}] {}:{}{}",
							dataCase.getRequestType().name(),
							dataCase.getMethodName(),
							i + 1,
							System.lineSeparator(),
							objectMapper.writeValueAsString(dataCase.getRequestBodyVariants().get(i)));
				}
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

    @Test
    @Ignore
    void testCaseRunnerManagerGet() throws JsonProcessingException {

        SwaggerParseResult result = parserService.getSwaggerRestApi("https://petstore.swagger.io/");
        List<TestDataCase> cases = dataCreationService.generateTestDataCases(result, 2).stream()
                .filter(i -> i.getRequestType().equals(RequestType.GET))
//                .limit(1)
                .collect(Collectors.toList());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        for (TestDataCase dataCase: cases) {
            Response response = caseRunnerManager.runDataCase(dataCase);

            log.info("Response: {}", response.getStatusLine().toString());
            log.info("Response body: ");
            if (response.getBody() == null) {
            	System.out.println("empty");

			} else if (isJson(new String(response.getBody(), StandardCharsets.UTF_8))) {
            	System.out.println(objectMapper.writeValueAsString(objectMapper.readValue(new String(response.getBody(), StandardCharsets.UTF_8), Object.class)));

			} else {
            	System.out.println(new String(response.getBody(), StandardCharsets.UTF_8));
			}
        }
        Assert.assertNotNull(result);
    }
}
