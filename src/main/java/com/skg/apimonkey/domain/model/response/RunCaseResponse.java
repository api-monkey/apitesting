package com.skg.apimonkey.domain.model.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class RunCaseResponse implements Serializable {
    private Integer responseCode;
    private String responseBody;
}
