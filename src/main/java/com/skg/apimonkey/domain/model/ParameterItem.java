package com.skg.apimonkey.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class ParameterItem implements Serializable {
    private String name;
    private String value;
    private boolean required;
    @JsonIgnore
    private boolean inPath;
}
