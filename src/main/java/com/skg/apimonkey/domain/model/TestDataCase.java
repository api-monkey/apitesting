package com.skg.apimonkey.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.skg.apimonkey.domain.model.response.AuthHeader;
import io.swagger.v3.oas.models.PathItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestDataCase implements Serializable {
    private String dataId;
    private String summary;
    private RequestType requestType;
    @JsonIgnore
    private PathItem pathItem;
    private String methodName;
    private String contentType;
    private int executeNumber;
    private List<String> serverApiPathes;
    private List<AuthHeader> authHeaders;
    private List<ParametersDataCase> inHeaderParameters;

    private boolean isBroken;
    @JsonIgnore
    private String errorMessage;

    private List<Object> requestBodyVariants;
    private List<ParametersDataCase> requestParamsVariants;
}
