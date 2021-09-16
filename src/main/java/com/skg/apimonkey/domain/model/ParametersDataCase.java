package com.skg.apimonkey.domain.model;

import io.swagger.v3.oas.models.parameters.Parameter;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ParametersDataCase {
    private String modifiedPath;
    private List<Parameter> parameters;
    private List<ParameterItem> parameterItems;
    private boolean isNoParams;
}
