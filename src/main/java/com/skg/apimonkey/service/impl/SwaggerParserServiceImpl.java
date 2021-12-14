package com.skg.apimonkey.service.impl;

import com.skg.apimonkey.domain.data.ErrorMessageLog;
import com.skg.apimonkey.domain.data.SwaggerData;
import com.skg.apimonkey.domain.model.SwaggerJsonResult;
import com.skg.apimonkey.repository.ErrorMessageLogRepository;
import com.skg.apimonkey.repository.SwaggerDataRepository;
import com.skg.apimonkey.service.SwaggerParserService;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.skg.apimonkey.service.util.StringUtil.isJson;
import static com.skg.apimonkey.service.util.WebUtil.*;

@Service
@Slf4j
public class SwaggerParserServiceImpl implements SwaggerParserService {

    private static final String JSON_SUFFIX = ".json";

    @Autowired
    private SwaggerDataRepository swaggerDataRepository;
    @Autowired
    private ErrorMessageLogRepository errorMessageLogRepository;

    @Override
    public String getSwaggerDataHashId(String swaggerUrl) {
        String jsonUrl = swaggerUrl;
        String htmlBody;
        SwaggerJsonResult resultPage = null;
        boolean updated = false;

        if (StringUtils.isNotEmpty(swaggerUrl) &&
                !StringUtils.endsWithIgnoreCase(swaggerUrl, JSON_SUFFIX) &&
                !isJson(downloadSwaggerJson(swaggerUrl).getResultPage())) {

            resultPage = getJsonUrlFromSwaggerPage(swaggerUrl);
            jsonUrl = resultPage.getJsonUrl();
        }
        jsonUrl = getCleanStartPage(jsonUrl);

        SwaggerData swaggerData = swaggerDataRepository.findFirstByUrl(jsonUrl);
        if (swaggerData != null && swaggerData.getUpdatedDate().after(DateUtils.addDays(new Date(), -1))) {
            htmlBody = swaggerData.getPageContent();

        } else {

            resultPage = downloadSwaggerJson(jsonUrl);
            htmlBody = resultPage.getResultPage();
            updated = true;
        }

        boolean isExist = swaggerData != null;

        if(updated && StringUtils.isNotEmpty(htmlBody)) {

            swaggerData = isExist ?
                    swaggerData :
                    new SwaggerData();
            swaggerData.setUrl(jsonUrl);
            swaggerData.setPassedUrl(swaggerUrl);
            swaggerData.setHashId(isExist ?
                    swaggerData.getHashId() :
                    new HmacUtils(HmacAlgorithms.HMAC_SHA_1, "api-monkey").hmacHex(jsonUrl + new Date()).substring(0, 16));
            swaggerData.setPageContent(htmlBody);
            swaggerData.setCreatedDate(isExist ?
                    swaggerData.getCreatedDate() :
                    new Date());
            swaggerData.setUpdatedDate(new Date());
            swaggerDataRepository.save(swaggerData);
        }

        if (resultPage != null && StringUtils.isNotEmpty(resultPage.getErrorMessage())) {
            ErrorMessageLog errorLog = ErrorMessageLog.builder()
                    .url(jsonUrl)
                    .errorMessage(resultPage.getErrorMessage())
                    .stackTrace(resultPage.getErrorTrace())
                    .createdDate(new Date())
                    .build();
            errorMessageLogRepository.save(errorLog);
        }

        return swaggerData == null ? null : swaggerData.getHashId();
    }

    @Override
    public SwaggerParseResult getSwaggerData(String hashId) {

        SwaggerData swaggerData = swaggerDataRepository.findFirstByHashId(hashId);
        return getSwaggerData(swaggerData);
    }

    public SwaggerParseResult getSwaggerData(SwaggerData swaggerData) {

        SwaggerParseResult result = null;

        try {
            result = new OpenAPIParser().readContents(swaggerData.getPageContent(), null, null);

            //fix server url
            fixServerUrl(result, swaggerData.getPassedUrl());

        } catch (Exception e) {
            log.error("Error parsing swagger url: {}", e);
        }
        return result;
    }

    private static SwaggerJsonResult getJsonUrlFromSwaggerPage(String path) {

        SwaggerJsonResult result = new SwaggerJsonResult();

        try {

            URL sourceUrl = new URL(path);
            Document doc = Jsoup.parse(new URL(path), 30000);
            List<String> list = new ArrayList<>();
            Matcher matcher = Pattern.compile("[\"'](.*.json)[\"']", Pattern.CASE_INSENSITIVE).matcher(doc.html());
            while (matcher.find()){
                list.add(matcher.group());
            }

            // url has .json ending case
            if (CollectionUtils.isNotEmpty(list)) {

                String endPart = "";

                String[] parts = list.get(0).split("\"");
                endPart = Arrays.stream(parts)
                        .filter(StringUtils::isNotEmpty)
                        .filter(i -> StringUtils.containsIgnoreCase(i, JSON_SUFFIX))
                        .findFirst()
                        .orElse(null);

                if(!StringUtils.containsIgnoreCase(endPart, "http")) {
                    result.setJsonUrl(sourceUrl.getProtocol() + "://" + sourceUrl.getHost() + (StringUtils.startsWith(endPart, "/") ? "" : "/") + endPart);

                } else {
                    result.setJsonUrl(endPart);
                }

            // looking url with json response
            } else {

                Elements links = doc.select("a");
                for (Element link: links) {
                    String linkString = link.attr("abs:href");
                    if (StringUtils.isNotEmpty(linkString) && StringUtils.containsIgnoreCase(linkString, sourceUrl.getHost())) {

                        SwaggerJsonResult jsonStr = downloadSwaggerJson(linkString);

                        if(isJson(jsonStr.getResultPage())) {
                            result.setJsonUrl(linkString);
                            break;
                        }
                    }
                }
            }

        } catch (Exception e) {
            result.setErrorMessage(ExceptionUtils.getRootCauseMessage(e));
            result.setErrorTrace(ExceptionUtils.getStackTrace(e));
            log.error("Error crawling .json link from the page [{}]: ", path, e);
        }

        return result;
    }
}