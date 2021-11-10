package com.skg.apimonkey.service.util;

import com.skg.apimonkey.domain.model.ParametersDataCase;
import com.skg.apimonkey.domain.model.TestDataCase;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
public class WebUtil {

    public static Response executeRequest(HttpUriRequest request) {

        Response result = new Response();

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(request)){

            int responseCode = response.getStatusLine().getStatusCode();
            HttpEntity entity = response.getEntity();
            result.setBody(EntityUtils.toByteArray(entity));
            log.info("<-- {}", response);
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

    public static void setHeaderParamsToRequest(TestDataCase dataCase, HttpRequestBase request) {
        ParametersDataCase inHeaderParams = dataCase.getInHeaderParameters().get(dataCase.getExecuteNumber());
        if(Objects.nonNull(inHeaderParams) && CollectionUtils.isNotEmpty(inHeaderParams.getParameterItems())) {
            inHeaderParams.getParameterItems().forEach(i -> {
                if (i.isRequired() || StringUtils.isNotEmpty(i.getValue())) {
                    request.addHeader(i.getName(), i.getValue());
                }
            });
        }
    }

    public static String getDomainUrl(String url) {
        URL uri = null;
        String domain = "";
        try {
            uri = new URL(url);
            domain = new URL(uri.getProtocol(), uri.getHost(), "").toString();
        } catch (MalformedURLException ignored) {}

        return domain;
    }

    public static void fixServerUrl(SwaggerParseResult swagger, String passedUrl) {

        if(Objects.nonNull(swagger) && CollectionUtils.isNotEmpty(swagger.getOpenAPI().getServers()) ) {

            for (Server server: swagger.getOpenAPI().getServers()) {

                if (StringUtils.startsWithIgnoreCase(server.getUrl(), "//") && !StringUtils.containsIgnoreCase(server.getUrl(), "http")) {
                    server.setUrl("https:" + server.getUrl());
                }

                if (!StringUtils.containsIgnoreCase(server.getUrl(), "http")) {

                    String updPassedUrl = getDomainUrl(passedUrl);
                    if(updPassedUrl.endsWith("/")) {
                        updPassedUrl = updPassedUrl.substring(0, updPassedUrl.length() - 1);
                    }
                    String updServerUrl = server.getUrl();
                    if(updServerUrl.startsWith("//")) {
                        updServerUrl = updServerUrl.replaceFirst("//", "");
                    }
                    if(updServerUrl.startsWith("/")) {
                        updServerUrl = updServerUrl.replaceFirst("/", "");
                    }

                    server.setUrl(updPassedUrl + "/" + updServerUrl);
                }
            }
        }
    }

    public static String response404(HttpServletResponse response) {
        response.setStatus(org.springframework.http.HttpStatus.NOT_FOUND.value()); // for HTTP response
        return "error/404";
    }
}