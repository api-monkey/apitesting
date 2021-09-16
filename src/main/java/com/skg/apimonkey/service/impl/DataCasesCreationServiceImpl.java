package com.skg.apimonkey.service.impl;

import com.skg.apimonkey.domain.model.RequestType;
import com.skg.apimonkey.domain.model.TestDataCase;
import com.skg.apimonkey.service.DataCreationService;
import com.skg.apimonkey.service.util.DataCreationGetRequestUtil;
import com.skg.apimonkey.service.util.DataCreationPostRequestUtil;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DataCasesCreationServiceImpl implements DataCreationService {

    @Override
    public List<TestDataCase> generateTestDataCases(SwaggerParseResult swaggerConfig) {

        if (Objects.isNull(swaggerConfig) || Objects.isNull(swaggerConfig.getOpenAPI()) || Objects.isNull(swaggerConfig.getOpenAPI().getPaths())) {
            log.warn("Swagger config is broken. Test data generation skipped");
            return null;
        }

        // split by request type
        List<TestDataCase> dataCaseList = splitByRequestType(swaggerConfig.getOpenAPI().getPaths());

        // generate data
        dataCaseList.forEach(i -> {
            updateWithTestCases(i, swaggerConfig.getOpenAPI());
        });

        return dataCaseList.stream()
                .filter(i -> !i.isBroken())
                .collect(Collectors.toList());
    }

    private void updateWithTestCases(TestDataCase dataCase, OpenAPI openApi) {

        if(CollectionUtils.isEmpty(openApi.getServers())) {
            log.warn("Server url not found");
            dataCase.setErrorMessage("Server url not found");
            dataCase.setBroken(true);
            return;
        }

        switch (dataCase.getRequestType()) {
            case POST:
                DataCreationPostRequestUtil.generatePostBody(dataCase, openApi);
                break;
            case GET:
                DataCreationGetRequestUtil.generateGetParameters(dataCase, openApi);
                break;
            case PUT:
            case DELETE:
            case HEAD:
                log.warn("Data generation for method {} not implemented yet!", dataCase.getRequestType().name());
                dataCase.setBroken(true);
                break;
        }
    }

    private List<TestDataCase> splitByRequestType(Paths endpoints) {

        List<TestDataCase> dataCaseList = new ArrayList<>();

        endpoints.forEach((k, v) -> {

            if(Objects.nonNull(v.getGet())) {
                dataCaseList.add(createDataCase(k, RequestType.GET, v));
            }
            if(Objects.nonNull(v.getPut())) {
                dataCaseList.add(createDataCase(k, RequestType.PUT, v));
            }
            if(Objects.nonNull(v.getPost())) {
                dataCaseList.add(createDataCase(k, RequestType.POST, v));
            }
            if(Objects.nonNull(v.getDelete())) {
                dataCaseList.add(createDataCase(k, RequestType.DELETE, v));
            }

        });

        return dataCaseList;
    }

    private TestDataCase createDataCase(String pathName, RequestType requestType, PathItem pathItem) {
        return TestDataCase.builder()
                .methodName(pathName)
                .requestType(requestType)
                .pathItem(pathItem)
                .build();
    }
}