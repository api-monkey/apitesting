package com.skg.apimonkey.service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class WebUtil {

    public HttpUriRequest createPostRequest() throws JsonProcessingException {

        Object requestObj = new Object();

        String json = new ObjectMapper().writeValueAsString(requestObj);

        log.info(String.format("--> POST request body: %s", json));

        HttpPost request = new HttpPost("URL");
        request.addHeader("Content-Type", "application/json");
//        request.setHeader("Authorization", TOKEN);
        StringEntity entity = new StringEntity(json, "UTF-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        request.setEntity(entity);
        return request;
    }

    public static Response executeRequest(HttpUriRequest request) {

        Response result = new Response();

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(request)){

            int responseCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            result.setBody(EntityUtils.toByteArray(entity));
            if (responseCode != HttpStatus.SC_OK) {
                log.error(response.getStatusLine().toString());
                log.info(new String(result.getBody(), StandardCharsets.UTF_8));
            }
            result.setStatusLine(response.getStatusLine());
            result.setContentType(ContentType.getOrDefault(entity));

        } catch (IOException e) {
            log.error("Error request execution: {}", e);
        }

        return result;
    }
}