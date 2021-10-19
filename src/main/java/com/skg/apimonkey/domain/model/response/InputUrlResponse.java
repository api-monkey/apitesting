package com.skg.apimonkey.domain.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class InputUrlResponse implements Serializable {
    private String hashId;
    private String passedUrl;
    private String errorMessage;
    @JsonProperty(value = "success")
    private boolean isSuccess;
}
