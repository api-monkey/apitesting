package com.skg.apimonkey.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.skg.apimonkey.domain.data.ErrorMessageLog;
import com.skg.apimonkey.domain.data.UserDataCase;
import com.skg.apimonkey.domain.model.RequestType;
import com.skg.apimonkey.domain.model.TestDataCase;
import com.skg.apimonkey.domain.model.response.InputUrlResponse;
import com.skg.apimonkey.domain.model.response.RunCaseRequest;
import com.skg.apimonkey.domain.model.response.RunCaseResponse;
import com.skg.apimonkey.repository.ErrorMessageLogRepository;
import com.skg.apimonkey.repository.SwaggerDataRepository;
import com.skg.apimonkey.repository.UserDataCaseRepository;
import com.skg.apimonkey.service.SwaggerParserService;
import com.skg.apimonkey.service.impl.CaseRunnerManager;
import com.skg.apimonkey.service.util.MappingUtil;
import com.skg.apimonkey.service.util.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.skg.apimonkey.service.util.StringUtil.isJson;

@Slf4j
@RestController
public class WorkerController {

    @Autowired
    private SwaggerParserService parserService;
    @Autowired
    private CaseRunnerManager caseRunnerManager;
    @Autowired
    private UserDataCaseRepository userDataCaseRepository;
    @Autowired
    private SwaggerDataRepository swaggerDataRepository;
    @Autowired
    private ErrorMessageLogRepository errorMessageLogRepository;

    @GetMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        return Collections.singletonMap("name", principal.getAttribute("name"));
    }

    @GetMapping("/rest/parseSwaggerUrl")
    public InputUrlResponse parseSwaggerUrl(@RequestParam(value = "url") String url,
                                            @RequestParam(value = "variantNumber", required = false, defaultValue = "4") Integer variantNumber) {

        String hashId = null;
        String errorMessage = null;

        try {
            hashId = parserService.getSwaggerDataHashId(url);

        } catch (Exception e) {
            log.error("getSwaggerRestApi error: ", e);
            errorMessage = "Sorry we only support the REST API’s which have Swagger / Open APi definitions. Example: https://petstore.swagger.io/v2/swagger.json";

            ErrorMessageLog errorLog = ErrorMessageLog.builder()
                    .url(url)
                    .errorMessage(e.getMessage())
                    .stackTrace(ExceptionUtils.getStackTrace(e))
                    .createdDate(new Date())
                    .build();
            errorMessageLogRepository.save(errorLog);
        }

        return InputUrlResponse.builder()
                .passedUrl(url)
                .errorMessage(errorMessage)
                .isSuccess(StringUtils.isNotEmpty(hashId))
                .hashId(hashId)
                .build();
    }

    @PostMapping("/rest/runCase")
    public RunCaseResponse runCase(@RequestBody RunCaseRequest data) throws JsonProcessingException {

        // update in database
        if (data.getDataCase() != null) {
            int countBodies = CollectionUtils.isNotEmpty(data.getDataCase().getRequestBodyVariants()) ? data.getDataCase().getRequestBodyVariants().size() : 0;
            int countParams = CollectionUtils.isNotEmpty(data.getDataCase().getRequestParamsVariants()) ? data.getDataCase().getRequestParamsVariants().size() : 0;
            int countHParams = CollectionUtils.isNotEmpty(data.getDataCase().getInHeaderParameters()) ? data.getDataCase().getInHeaderParameters().size() : 0;
            int casesSize = Math.max(countBodies, Math.max(countHParams, countParams));

            if ((casesSize - 1) == data.getDataCase().getExecuteNumber()) {
                updateTestDataInDatabase(data);
            }
        }

        Response response = caseRunnerManager.runDataCase(data.getDataCase());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        String responseStr = "";

        if(Objects.equals(data.getDataCase().getRequestType(), RequestType.POST) ||
                Objects.equals(data.getDataCase().getRequestType(), RequestType.PUT)) {

            if(response.getBody() == null) {
                responseStr = "empty";
            } else {

                try{
                    responseStr = objectMapper.writeValueAsString(objectMapper.readValue(new String(response.getBody(), StandardCharsets.UTF_8), HashMap.class));
                } catch (Exception ignored) {}

                if(StringUtils.isEmpty(responseStr)) {
                    responseStr = new String(response.getBody(), StandardCharsets.UTF_8);
                }
            }

        } else if (Objects.equals(data.getDataCase().getRequestType(), RequestType.GET)) {
            if (response.getBody() != null && isJson(new String(response.getBody(), StandardCharsets.UTF_8)) ) {
                responseStr = objectMapper.writeValueAsString(objectMapper.readValue(new String(response.getBody(), StandardCharsets.UTF_8), Object.class));

            } else if (response.getBody() != null) {
                responseStr = new String(response.getBody(), StandardCharsets.UTF_8);

            }
        }

        return RunCaseResponse.builder()
                .responseCode(response.getStatusLine().getStatusCode())
                .responseBody(responseStr)
                .build();
    }

    private synchronized void updateTestDataInDatabase(RunCaseRequest data) {
        TestDataCase testDataCase = data.getDataCase();
        UserDataCase userDataCase = userDataCaseRepository.findFirstByDataIdAndDataName(testDataCase.getDataId(), testDataCase.getSummary());
        UserDataCase toSave = MappingUtil.toUserDataCase(testDataCase);

        if (userDataCase != null) {
            userDataCase.setDataCaseContent(toSave.getDataCaseContent());
            userDataCase.setUpdatedDate(new Date());

        } else {

            userDataCase = toSave;
            userDataCase.setSwaggerData(swaggerDataRepository.findFirstByHashId(data.getSwaggerDataHashId()));
        }
        userDataCaseRepository.save(userDataCase);
    }
}