package com.skg.apimonkey.controller;

import com.skg.apimonkey.domain.model.TestDataCase;
import com.skg.apimonkey.domain.model.response.InputUrlResponse;
import com.skg.apimonkey.service.DataCreationService;
import com.skg.apimonkey.service.SwaggerParserService;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
public class WorkerController {

    @Autowired
    private SwaggerParserService parserService;

    @Autowired
    private DataCreationService dataCreationService;


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
}