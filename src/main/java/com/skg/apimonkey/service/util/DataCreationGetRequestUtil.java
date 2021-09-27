package com.skg.apimonkey.service.util;

import com.skg.apimonkey.domain.model.ParameterItem;
import com.skg.apimonkey.domain.model.ParametersDataCase;
import com.skg.apimonkey.domain.model.TestDataCase;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
public class DataCreationGetRequestUtil {

    //supported media type
    private final static String MEDIA_TYPE = "application/json";

    public static void generateGetParameters(TestDataCase dataCase, OpenAPI openApi, Integer variantNumber) {
        generateParameters(dataCase, openApi, variantNumber);
    }

    public static void generateParameters(TestDataCase dataCase, OpenAPI openApi, int variantNumber) {

        log.info("generate body for GET [{}]", dataCase.getMethodName());
        PathItem pathItem = dataCase.getPathItem();

        List<Parameter> parameters = pathItem.getGet().getParameters();

        dataCase.setContentType(MEDIA_TYPE);
        dataCase.setSummary(StringUtils.isEmpty(pathItem.getGet().getSummary()) ? pathItem.getGet().getDescription() : pathItem.getGet().getSummary());
        dataCase.setServerApiPathes(openApi.getServers().stream().map(Server::getUrl).collect(Collectors.toList()));

        //create request params
        List<ParametersDataCase> paramsVariants = buildParamsVariantsFromSchema(parameters, dataCase.getMethodName(), variantNumber);
        dataCase.setRequestParamsVariants(paramsVariants);
    }

    private static List<ParametersDataCase> buildParamsVariantsFromSchema(List<Parameter> parameters, String query, int variants) {

        List<ParametersDataCase> resultList = new ArrayList<>();

        // empty params case
        if (CollectionUtils.isEmpty(parameters)) {
            ParametersDataCase paramsObject = new ParametersDataCase();
            paramsObject.setModifiedPath(query);
            paramsObject.setNoParams(true);
            return Collections.singletonList(paramsObject);
        }

        for (int i = 0; i < variants; i++) {

            ParametersDataCase paramsObject = new ParametersDataCase();
            paramsObject.setParameters(parameters);
            paramsObject.setParameterItems(new ArrayList<>());

            String modifiedQuery = query;

            for (Parameter param: parameters) {

                ParameterItem parameterItem = new ParameterItem();
                parameterItem.setKey(param.getName());

                //find value
                Schema schema;
                if (param.getSchema() instanceof ArraySchema) {
                    schema = ((ArraySchema) param.getSchema()).getItems();
                } else {
                    schema = param.getSchema();
                }

                String value = (String) RequestValuesUtil.getValueByType(schema, i);
                parameterItem.setValue(value);

                if(StringUtils.equalsIgnoreCase(param.getIn(), "path")) {
                    modifiedQuery = modifiedQuery.replaceAll("\\{" + param.getName() + "}", value);
                    parameterItem.setInPath(true);
                }
                paramsObject.getParameterItems().add(parameterItem);
            }

            paramsObject.setModifiedPath(modifiedQuery);

            resultList.add(paramsObject);
        }
        return resultList;
    }
}