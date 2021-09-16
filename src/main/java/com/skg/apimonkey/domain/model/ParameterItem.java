package com.skg.apimonkey.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ParameterItem {
    private String key;
    private String value;
    private boolean inPath;
}
