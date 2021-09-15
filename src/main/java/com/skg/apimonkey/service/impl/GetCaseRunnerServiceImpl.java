package com.skg.apimonkey.service.impl;

import com.skg.apimonkey.domain.model.TestDataCase;
import com.skg.apimonkey.service.CaseRunnerService;
import com.skg.apimonkey.service.util.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GetCaseRunnerServiceImpl implements CaseRunnerService {

    @Override
    public Response executeCase(TestDataCase dataCase) {
        return null;
    }
}