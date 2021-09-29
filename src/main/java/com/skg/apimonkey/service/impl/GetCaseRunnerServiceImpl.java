package com.skg.apimonkey.service.impl;

import com.skg.apimonkey.domain.model.ParameterItem;
import com.skg.apimonkey.domain.model.ParametersDataCase;
import com.skg.apimonkey.domain.model.TestDataCase;
import com.skg.apimonkey.service.CaseRunnerService;
import com.skg.apimonkey.service.util.Response;
import com.skg.apimonkey.service.util.WebUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GetCaseRunnerServiceImpl implements CaseRunnerService {

    @Override
    public Response executeCase(TestDataCase dataCase) {
        HttpUriRequest getRequest = createGetRequest(dataCase);
        Response response = WebUtil.executeRequest(getRequest);

        return response;
    }

    @SneakyThrows
    public HttpUriRequest createGetRequest(TestDataCase dataCase) {

        ParametersDataCase getParamsObj = dataCase.getRequestParamsVariants().get(0);
        log.info(String.format("--> GET request: %s", getParamsObj.getModifiedPath()));

        long pathParamsCount = getParamsObj.isNoParams() ? 0 : getParamsObj.getParameterItems().stream().filter(j -> !j.isInPath()).count();
        boolean isNoParams = getParamsObj.isNoParams() || pathParamsCount == 0;

        URIBuilder builder = new URIBuilder(dataCase.getServerApiPathes().get(0) + getParamsObj.getModifiedPath());
        if (!isNoParams) {

            for (ParameterItem item : getParamsObj.getParameterItems()) {
                if (!item.isInPath()) {
                    builder.setParameter(item.getName(), item.getValue());
                }
            }
        }

        HttpGet request = new HttpGet(builder.build().toString());

        log.info(String.valueOf(request));

        request.addHeader("Accept", dataCase.getContentType());
        request.addHeader("Content-Type", dataCase.getContentType());

        return request;
    }
}