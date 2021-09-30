package com.skg.apimonkey.domain.model.response;

import com.skg.apimonkey.domain.model.TestDataCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RunCaseRequest implements Serializable {
    private String dataId;
    private Integer number;
    private TestDataCase dataCase;
}
