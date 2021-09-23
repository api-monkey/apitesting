package com.skg.apimonkey.controller;

import com.skg.apimonkey.domain.model.TestDataCase;
import com.skg.apimonkey.service.DataCreationService;
import com.skg.apimonkey.service.SwaggerParserService;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public Object parseSwaggerUrl(@RequestParam(value = "url") String url,
                                  @RequestParam(value = "limit", required = false, defaultValue = "1") Integer variantNumber) {

        SwaggerParseResult result = parserService.getSwaggerRestApi("https://petstore.swagger.io/v2/swagger.json");
        List<TestDataCase> cases = dataCreationService.generateTestDataCases(result, variantNumber);
        return cases;
    }
}