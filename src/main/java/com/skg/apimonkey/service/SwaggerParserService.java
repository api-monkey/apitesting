package com.skg.apimonkey.service;

import com.skg.apimonkey.domain.data.SwaggerData;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

public interface SwaggerParserService {
    String getSwaggerDataHashId(String swaggerUrl);

    SwaggerParseResult getSwaggerData(String hashId);

    SwaggerParseResult getSwaggerData(SwaggerData swaggerData);
}