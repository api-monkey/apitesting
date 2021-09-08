package com.skg.apimonkey.service.util;

import com.skg.apimonkey.domain.model.TestDataCase;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;


@Slf4j
public class DataCreationUtil {

    //supported media type
    private final static String MEDIA_TYPE = "application/json";

    public static void generatePostBody(TestDataCase dataCase, OpenAPI openApi) {

        log.info("generate body for POST [{}]", dataCase.getMethodName());
        PathItem pathItem = dataCase.getPathItem();

        MediaType mediaType = pathItem.getPost().getRequestBody().getContent().get(MEDIA_TYPE);

        if(Objects.isNull(mediaType)) {
            log.warn("MediaType not found for dataCase name: {}, method: {}", dataCase.getMethodName(), dataCase.getRequestType().name());
            return;
        }

        Map bodyObject = buildBodyFromSchema(mediaType.getSchema(), openApi.getComponents());
        dataCase.setRequestBody(bodyObject);
    }

    private static Map<String, Object> buildBodyFromSchema(Schema schema, Components companents) {

        Map<String, Object> object = new HashMap<>();

        if (StringUtils.isNotEmpty(schema.get$ref())) {

            String schemaKey = Arrays.stream(schema.get$ref().split("/")).filter(StringUtils::isNotEmpty).reduce((first, second) -> second).orElse(null);

            if (StringUtils.isNotEmpty(schemaKey)) {
                Schema innerSchema = companents.getSchemas().get(schemaKey);
                object = buildBodyFromSchema(innerSchema, companents);

            } else {
                log.warn("Schema key not found for ref: {}", schema.get$ref());
            }

        } else if (MapUtils.isNotEmpty(schema.getProperties())) {

            Map<String, Schema> props = (Map<String, Schema>) schema.getProperties();

            for (Map.Entry<String, Schema> entry: props.entrySet()) {

                if(entry.getValue() instanceof ObjectSchema || StringUtils.isNotEmpty(entry.getValue().get$ref())) {
                    object.put(entry.getKey(), buildBodyFromSchema(entry.getValue(), companents));

                } else if (entry.getValue() instanceof ArraySchema) {
                    List<Object> arrayList = new ArrayList<>();
                    arrayList.add(buildBodyFromSchema(((ArraySchema) entry.getValue()).getItems(), companents));
                    object.put(entry.getKey(), arrayList);

                } else {
                    object.put(entry.getKey(), getValueByType(entry.getValue()));
                }
            }
        }
        return object;
    }

    private static Object getValueByType(Schema schema) {

        if(Objects.isNull(schema) || StringUtils.isEmpty(schema.getType())) {
            log.warn("Error creating value from scheme type!");
            return null;
        }
        Object result;
        switch (schema.getType()) {
            case "integer":
                result = Objects.isNull(schema.getExample()) ? 0 : schema.getExample();
                break;
            case "string":
                if (StringUtils.equalsIgnoreCase(schema.getFormat(), "email")) {
                    result = Objects.isNull(schema.getExample()) ? "test@gmail.com" : schema.getExample();

                } else if(StringUtils.equalsIgnoreCase(schema.getFormat(), "date-time")) {
                    result = Objects.isNull(schema.getExample()) ? "2024-09-08T14:00:41.246Z" : schema.getExample();

                } else if(StringUtils.equalsIgnoreCase(schema.getFormat(), "date")) {
                    result = Objects.isNull(schema.getExample()) ? "2024-09-08" : schema.getExample();

                } else {
                result = Objects.isNull(schema.getExample()) ? "testString" : schema.getExample();
            }
                break;
            case "number":
                result = Objects.isNull(schema.getExample()) ? "0.0" : schema.getExample();
                break;
            case "boolean":
                result = Objects.isNull(schema.getExample()) ? "false" : schema.getExample();
                break;
            default:
                log.warn("Unrecognized type: {} !", schema.getType());
                result = null;
                break;
        }
        return result;
    }
}