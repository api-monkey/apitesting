package com.skg.apimonkey.service.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Slf4j
public class WebUtil {

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

    public static String getCleanStartPage(String url) {
        if (StringUtils.isNotEmpty(url)) {
            try {
                URL urlObj = new URL(url);
                return new URL(urlObj.getProtocol(), urlObj.getHost(), urlObj.getFile()).toString();
            } catch (Exception ignored) {}
        }
        return url;
    }

    public static String downloadSwaggerJson(String url) {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpResponse response = client.execute(new HttpGet(url));
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity, "UTF-8");
            client.close();
            return result;

        } catch (Exception e) {
            log.error("Error downloading json from url", e);
        }

        return null;
    }
}