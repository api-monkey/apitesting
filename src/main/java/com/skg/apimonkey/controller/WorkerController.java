package com.skg.apimonkey.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.skg.apimonkey.domain.data.UserDataCase;
import com.skg.apimonkey.domain.model.RequestType;
import com.skg.apimonkey.domain.model.TestDataCase;
import com.skg.apimonkey.domain.model.response.InputUrlResponse;
import com.skg.apimonkey.domain.model.response.RunCaseRequest;
import com.skg.apimonkey.domain.model.response.RunCaseResponse;
import com.skg.apimonkey.repository.SwaggerDataRepository;
import com.skg.apimonkey.repository.UserDataCaseRepository;
import com.skg.apimonkey.service.SwaggerParserService;
import com.skg.apimonkey.service.impl.CaseRunnerManager;
import com.skg.apimonkey.service.util.MappingUtil;
import com.skg.apimonkey.service.util.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

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

    @GetMapping("/rest/parseSwaggerUrl")
    public InputUrlResponse parseSwaggerUrl(@RequestParam(value = "url") String url,
                                            @RequestParam(value = "variantNumber", required = false, defaultValue = "4") Integer variantNumber) {

        String hashId = null;
        String errorMessage = null;

        try {
            hashId = parserService.getSwaggerDataHashId(url);

        } catch (Exception e) {
            log.error("getSwaggerRestApi error: ", e);
            errorMessage = "Sorry we only support the REST API’s which have Swagger / Open APi definitions.";
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
        if (data.getDataCase() != null && data.getDataCase().getExecuteNumber() == 0) {
            updateTestDataInDatabase(data);
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