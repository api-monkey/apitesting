package com.skg.apimonkey.service;

import com.skg.apimonkey.domain.model.TestDataCase;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

import java.util.List;

public interface DataCreationService {

    List<TestDataCase> generateTestDataCases(SwaggerParseResult swaggerConfig);
}