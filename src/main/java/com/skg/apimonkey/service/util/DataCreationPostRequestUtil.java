package com.skg.apimonkey.service.util;

import com.skg.apimonkey.domain.model.TestDataCase;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
public class DataCreationPostRequestUtil {

    //supported media type
    private final static String MEDIA_TYPE = "application/json";

    public static void generatePostBody(TestDataCase dataCase, OpenAPI openApi, int variantNumber) {
        generateBody(dataCase, openApi, variantNumber);
    }

    public static void generateBody(TestDataCase dataCase, OpenAPI openApi, int variantNumber) {

//        log.info("generate body for POST [{}]", dataCase.getMethodName());
        PathItem pathItem = dataCase.getPathItem();

        RequestBody requestBody = pathItem.getPost().getRequestBody();

        dataCase.setContentType(MEDIA_TYPE);
        dataCase.setSummary(StringUtils.isEmpty(pathItem.getPost().getSummary()) ? pathItem.getPost().getDescription() : pathItem.getPost().getSummary());
        dataCase.setServerApiPathes(openApi.getServers().stream().map(Server::getUrl).collect(Collectors.toList()));

        if ( Objects.isNull(requestBody) ) {
            log.warn("RequestBody empty for dataCase name: {}, method: {}", dataCase.getMethodName(), dataCase.getRequestType().name());
            dataCase.setErrorMessage(String.format("RequestBody empty for dataCase name: %s, method: %s", dataCase.getMethodName(), dataCase.getRequestType().name()));
            return;
        }

        MediaType mediaType = requestBody.getContent().get(MEDIA_TYPE);

        if(Objects.isNull(mediaType)) {
            log.warn("MediaType not found for dataCase name: {}, method: {}", dataCase.getMethodName(), dataCase.getRequestType().name());
            dataCase.setErrorMessage(String.format("MediaType not found for dataCase name: %s, method: %s", dataCase.getMethodName(), dataCase.getRequestType().name()));
            dataCase.setBroken(true);
            return;
        }

        //create request body
        List<Object> bodyObjectVariants = buildBodyVariantsFromSchema(mediaType.getSchema(), openApi.getComponents(), variantNumber);
        dataCase.setRequestBodyVariants(bodyObjectVariants);
    }

    private static List<Object> buildBodyVariantsFromSchema(Schema schema, Components companents, int variants) {

        List<Object> resultList = new ArrayList<>();
        for (int i = 0; i < variants; i++) {

            Object bodyItem;
            if (schema instanceof ArraySchema) {
                List<Object> arrayList = new ArrayList<>();
                Schema inArraySchema = ((ArraySchema) schema).getItems();
                arrayList.add(RequestValuesUtil.isObject(inArraySchema) ? buildBodyFromSchema(inArraySchema, companents, i) : buildBodyFromSchema(schema, companents, i));
                bodyItem = arrayList;

            } else {
                bodyItem = buildBodyFromSchema(schema, companents, i);
            }

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
                    arrayList.add(RequestValuesUtil.isObject(inArraySchema) ? buildBodyFromSchema(inArraySchema, companents, variantNumber) : RequestValuesUtil.getValueByType(inArraySchema, variantNumber));
                    object.put(entry.getKey(), arrayList);

                } else {
                    object.put(entry.getKey(), RequestValuesUtil.getValueByType(entry.getValue(), variantNumber));
                }
            }
        }
        return object;
    }
}