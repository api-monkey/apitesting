package com.skg.apimonkey.service;

import com.skg.apimonkey.domain.model.TestDataCase;
import com.skg.apimonkey.service.util.Response;

public interface CaseRunnerService {

    Response executeCase(TestDataCase dataCase);
}