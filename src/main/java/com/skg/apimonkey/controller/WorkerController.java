package com.skg.apimonkey.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.skg.apimonkey.domain.model.RequestType;
import com.skg.apimonkey.domain.model.TestDataCase;
import com.skg.apimonkey.domain.model.response.InputUrlResponse;
import com.skg.apimonkey.domain.model.response.RunCaseRequest;
import com.skg.apimonkey.domain.model.response.RunCaseResponse;
import com.skg.apimonkey.service.DataCreationService;
import com.skg.apimonkey.service.SwaggerParserService;
import com.skg.apimonkey.service.impl.CaseRunnerManager;
import com.skg.apimonkey.service.util.Response;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.skg.apimonkey.service.util.StringUtil.isJson;

@Slf4j
@RestController
public class WorkerController {

    @Autowired
    private SwaggerParserService parserService;
    @Autowired
    private DataCreationService dataCreationService;
    @Autowired
    private CaseRunnerManager caseRunnerManager;


    @GetMapping("/test")
    public Object home(@RequestParam String param) {

        return parserService.getSwaggerRestApi(param);
    }

    @GetMapping("/rest/parseSwaggerUrl")
    public InputUrlResponse parseSwaggerUrl(@RequestParam(value = "url") String url,
                                            @RequestParam(value = "variantNumber", required = false, defaultValue = "1") Integer variantNumber) {

        SwaggerParseResult result = null;
        List<TestDataCase> cases = null;
        String errorMessage = null;

        try {
            result = parserService.getSwaggerRestApi(url);

        } catch (Exception e) {
            log.error("getSwaggerRestApi error: ", e);
            errorMessage = "Sorry we only support the REST API’s which have Swagger / Open APi definitions.";
        }

        if (Objects.nonNull(result) && Objects.nonNull(result.getOpenAPI())) {

            try {
                cases = dataCreationService.generateTestDataCases(result, variantNumber);

            } catch (Exception e) {
                log.error("generateTestDataCases error: ", e);
                errorMessage = "Error creating test cases from swagger specification. Current URL is not supported.";
            }
        }

        return InputUrlResponse.builder()
                .passedUrl(url)
                .errorMessage(errorMessage)
                .isSuccess(CollectionUtils.isNotEmpty(cases))
                .cases(cases)
                .build();
    }

    @PostMapping("/rest/runCase")
    public RunCaseResponse runCase(@RequestBody RunCaseRequest data) throws JsonProcessingException {

        Response response = caseRunnerManager.runDataCase(data.getDataCase());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        String responseStr = "";

        if(Objects.equals(data.getDataCase().getRequestType(), RequestType.POST)) {
            responseStr = response.getBody() == null ? "empty" : objectMapper.writeValueAsString(objectMapper.readValue(new String(response.getBody(), StandardCharsets.UTF_8), HashMap.class));

        } else if (Objects.equals(data.getDataCase().getRequestType(), RequestType.GET)) {
            if (response.getBody() != null && isJson(new String(response.getBody(), StandardCharsets.UTF_8)) ) {
                responseStr = objectMapper.writeValueAsString(objectMapper.readValue(new String(response.getBody(), StandardCharsets.UTF_8), Object.class));

            } else if (response.getBody() != null) {
                responseStr = new String(response.getBody(), StandardCharsets.UTF_8);

            }
        }

        return RunCaseResponse.builder()
                .responseCode(response.getStatusLine().getStatusCode())
                .responseBody(responseStr)
                .build();
    }
}