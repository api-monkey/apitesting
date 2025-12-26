package com.skg.apimonkey.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skg.apimonkey.domain.model.TestDataCase;
import com.skg.apimonkey.service.CaseRunnerService;
import com.skg.apimonkey.service.util.Response;
import com.skg.apimonkey.service.util.WebUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.springframework.stereotype.Service;

import static com.skg.apimonkey.service.util.WebUtil.setHeaderParamsToRequest;

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

        Object requestObj = dataCase.getRequestBodyVariants().get(dataCase.getExecuteNumber());
        String json = new ObjectMapper().writeValueAsString(requestObj);

        HttpPost request = new HttpPost(dataCase.getServerApiPathes().get(0) + dataCase.getMethodName());
        request.addHeader("Content-Type", dataCase.getContentType());

        if (CollectionUtils.isNotEmpty(dataCase.getAuthHeaders())) {
            dataCase.getAuthHeaders().forEach(i -> {
                request.addHeader(i.getKey(), i.getValue());
            });
        }
//        request.setHeader("Authorization", TOKEN);

        // set header params
        setHeaderParamsToRequest(dataCase, request);

        log.info("--> {} request body: {}, headers: {}", request, json, request.getAllHeaders());

        if (!StringUtils.equalsIgnoreCase(json, "{}")) {
            StringEntity entity = new StringEntity(json, "UTF-8");
            request.setEntity(entity);
            entity.setContentEncoding("UTF-8");
            entity.setContentType(dataCase.getContentType());
        }

        return request;
    }
}