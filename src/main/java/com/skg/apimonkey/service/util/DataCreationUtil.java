package com.skg.apimonkey.service.util;

import com.skg.apimonkey.domain.model.TestDataCase;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.RequestBody;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


@Slf4j
public class DataCreationUtil {

    //supported media type
    private final static String MEDIA_TYPE = "application/json";
    private final static String EMAIL = "email";
    private final static String STRING = "string";
    private final static String INTEGER = "integer";
    private final static String NUMBER = "number";
    private final static String BOOLEAN = "boolean";
    private final static String DATE = "date";
    private final static String DATETIME = "date-time";

    private final static List<String> STRING_TYPE_VALUES = Arrays.asList("test", "", "null", "88888", "random test value");

    public static void generatePostBody(TestDataCase dataCase, OpenAPI openApi) {
        generatePostBody(dataCase, openApi, 5);
    }

    public static void generatePostBody(TestDataCase dataCase, OpenAPI openApi, int variantNumber) {

        log.info("generate body for POST [{}]", dataCase.getMethodName());
        PathItem pathItem = dataCase.getPathItem();

        RequestBody requestBody = pathItem.getPost().getRequestBody();
        if (Objects.isNull(requestBody)) {
            log.warn("RequestBody empty for dataCase name: {}, method: {}", dataCase.getMethodName(), dataCase.getRequestType().name());
            return;
        }

        MediaType mediaType = requestBody.getContent().get(MEDIA_TYPE);

        if(Objects.isNull(mediaType)) {
            log.warn("MediaType not found for dataCase name: {}, method: {}", dataCase.getMethodName(), dataCase.getRequestType().name());
            return;
        }

        //create request body
        List<Object> bodyObjectVariants = buildBodyVariantsFromSchema(mediaType.getSchema(), openApi.getComponents(), variantNumber);
        dataCase.setRequestBodyVariants(bodyObjectVariants);
    }

    private static List<Object> buildBodyVariantsFromSchema(Schema schema, Components companents, int variants) {

        List<Object> resultList = new ArrayList<>();
        for (int i = 0; i < variants; i++) {
            Object bodyItem = buildBodyFromSchema(schema, companents, i);
            if(Objects.nonNull(bodyItem)) {
                resultList.add(bodyItem);
            }
        }
        return resultList;
    }


    private static Map<String, Object> buildBodyFromSchema(Schema schema, Components companents, int variantNumber) {

        Map<String, Object> object = new HashMap<>();

        // ref to object
        if (StringUtils.isNotEmpty(schema.get$ref())) {

            String schemaKey = Arrays.stream(schema.get$ref().split("/")).filter(StringUtils::isNotEmpty).reduce((first, second) -> second).orElse(null);

            if (StringUtils.isNotEmpty(schemaKey)) {
                Schema innerSchema = companents.getSchemas().get(schemaKey);
                object = buildBodyFromSchema(innerSchema, companents, variantNumber);

            } else {
                log.warn("Schema key not found for ref: {}", schema.get$ref());
            }

        // real object
        } else if (MapUtils.isNotEmpty(schema.getProperties())) {

            Map<String, Schema> props = (Map<String, Schema>) schema.getProperties();

            for (Map.Entry<String, Schema> entry: props.entrySet()) {

                if(entry.getValue() instanceof ObjectSchema || StringUtils.isNotEmpty(entry.getValue().get$ref())) {
                    object.put(entry.getKey(), buildBodyFromSchema(entry.getValue(), companents, variantNumber));

                } else if (entry.getValue() instanceof ArraySchema) {
                    List<Object> arrayList = new ArrayList<>();
                    Schema inArraySchema = ((ArraySchema) entry.getValue()).getItems();
                    arrayList.add(isObject(inArraySchema) ? buildBodyFromSchema(inArraySchema, companents, variantNumber) : getValueByType(inArraySchema, variantNumber));
                    object.put(entry.getKey(), arrayList);

                } else {
                    object.put(entry.getKey(), getValueByType(entry.getValue(), variantNumber));
                }
            }
        }
        return object;
    }

    private static Object getValueByType(Schema schema, int variantNumber) {

        if(Objects.isNull(schema) || StringUtils.isEmpty(schema.getType())) {
            log.warn("Error creating value from scheme type!");
            return null;
        }
        Object result;
        switch (schema.getType()) {
            case INTEGER:
                result = Objects.isNull(schema.getExample()) ? 0 : schema.getExample();
                break;
            case STRING:
                if (StringUtils.equalsIgnoreCase(schema.getFormat(), EMAIL)) {
                    result = Objects.isNull(schema.getExample()) ? "test@gmail.com" : schema.getExample();

                } else if(StringUtils.equalsIgnoreCase(schema.getFormat(), DATETIME)) {
                    result = Objects.isNull(schema.getExample()) ? "2024-09-08T14:00:41.246Z" : schema.getExample();

                } else if(StringUtils.equalsIgnoreCase(schema.getFormat(), DATE)) {
                    result = Objects.isNull(schema.getExample()) ? "2024-09-08" : schema.getExample();

                } else {
                result = getStringVariant((StringSchema) schema, variantNumber);
            }
                break;
            case NUMBER:
                result = Objects.isNull(schema.getExample()) ? "0.0" : schema.getExample();
                break;
            case BOOLEAN:
                result = Objects.isNull(schema.getExample()) ? "false" : schema.getExample();
                break;
            default:
                log.warn("Unrecognized type: {} !", schema.getType());
                result = null;
                break;
        }
        return result;
    }

    private static String getStringVariant(StringSchema schema, int variantNumber) {

        if (CollectionUtils.isNotEmpty(schema.getEnum())) {
            return schema.getEnum().get(ThreadLocalRandom.current().nextInt(schema.getEnum().size()));
        }

        if (variantNumber == 0) {
            return Objects.isNull(schema.getExample()) ?
                    STRING_TYPE_VALUES.get(ThreadLocalRandom.current().nextInt(STRING_TYPE_VALUES.size())) :
                    String.valueOf(schema.getExample());
        }
        return STRING_TYPE_VALUES.get(ThreadLocalRandom.current().nextInt(STRING_TYPE_VALUES.size()));
    }

    private static boolean isObject(Schema schema) {
        return StringUtils.isEmpty(schema.getType()) ||
                StringUtils.equalsIgnoreCase(schema.getType(), "object") ||
                MapUtils.isNotEmpty(schema.getProperties()) ||
                StringUtils.isNotEmpty(schema.get$ref());
    }
}