package com.skg.apimonkey.domain.model;

import io.swagger.v3.oas.models.PathItem;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestDataCase {
    private RequestType requestType;
    private PathItem pathItem;
    private String methodName;
    private boolean isBroken;
    private Object requestBody;
    private String requestParams;
}
