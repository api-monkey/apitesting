package com.skg.apimonkey.domain.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skg.apimonkey.domain.model.TestDataCase;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class InputUrlResponse implements Serializable {
    private List<TestDataCase> cases;
    private String passedUrl;
    private String errorMessage;
    @JsonProperty(value = "success")
    private boolean isSuccess;
}
