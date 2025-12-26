package com.skg.apimonkey.service.impl;

import com.skg.apimonkey.domain.model.TestDataCase;
import com.skg.apimonkey.service.util.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class CaseRunnerManager {

    private final PostCaseRunnerServiceImpl postCaseRunnerService;
    private final GetCaseRunnerServiceImpl getCaseRunnerService;

    public Response runDataCase(TestDataCase dataCase) {

        if (Objects.isNull(dataCase) || Objects.isNull(dataCase.getRequestType())) {
            log.warn("Test data case is null or request method not defined!");
        }

        switch (dataCase.getRequestType()) {
            case POST:
                return postCaseRunnerService.executeCase(dataCase);

            case GET:
                return getCaseRunnerService.executeCase(dataCase);

            case PUT:
                return postCaseRunnerService.executeCase(dataCase);

            case DELETE:
            case HEAD:
                log.warn("Data generation for method {} not implemented yet!", dataCase.getRequestType().name());
                dataCase.setBroken(true);
                return null;

            default:
                log.warn("Unknown RequestType [{}]! It has not implemented yet!", dataCase.getRequestType().name());
                dataCase.setBroken(true);
                return null;
        }
    }
}