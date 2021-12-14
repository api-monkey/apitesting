package com.skg.apimonkey.domain.model;

import lombok.Data;

@Data
public class SwaggerJsonResult {
    private String jsonUrl;
    private String resultPage;
    private String errorMessage;
    private String errorTrace;
}