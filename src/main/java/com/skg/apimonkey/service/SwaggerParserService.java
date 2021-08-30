package com.skg.apimonkey.service;

import io.swagger.v3.parser.core.models.SwaggerParseResult;

public interface SwaggerParserService {
    SwaggerParseResult getSwaggerRestApi(String swaggerUrl);
}