package com.skg.apimonkey.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skg.apimonkey.domain.model.TestDataCase;
import com.skg.apimonkey.service.CaseRunnerService;
import com.skg.apimonkey.service.util.Response;
import com.skg.apimonkey.service.util.WebUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class PostCaseRunnerServiceImpl implements CaseRunnerService {

    @Override
    public Response executeCase(TestDataCase dataCase) {

        HttpUriRequest postRequest = createRequest(dataCase);
        Response response = WebUtil.executeRequest(postRequest);

        return response;
    }

    @SneakyThrows
    public static HttpUriRequest createRequest(TestDataCase dataCase) {

        Object requestObj = dataCase.getRequestBodyVariants().get(0);
        String json = new ObjectMapper().writeValueAsString(requestObj);

        log.info(String.format("--> POST request body: %s", json));

        HttpPost request = new HttpPost(dataCase.getServerApiPathes().get(0) + dataCase.getMethodName());
        request.addHeader("Content-Type", dataCase.getContentType());
//        request.setHeader("Authorization", TOKEN);

        StringEntity entity = new StringEntity(json, "UTF-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType(dataCase.getContentType());
        request.setEntity(entity);

        return request;
    }
}