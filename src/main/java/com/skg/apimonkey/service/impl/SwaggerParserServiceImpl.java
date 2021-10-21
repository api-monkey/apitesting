package com.skg.apimonkey.service.impl;

import com.skg.apimonkey.domain.data.SwaggerData;
import com.skg.apimonkey.repository.SwaggerDataRepository;
import com.skg.apimonkey.service.SwaggerParserService;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.skg.apimonkey.service.util.WebUtil.downloadSwaggerJson;
import static com.skg.apimonkey.service.util.WebUtil.getCleanStartPage;

@Service
@Slf4j
public class SwaggerParserServiceImpl implements SwaggerParserService {

    private static final String JSON_SUFFIX = ".json";

    @Autowired
    private SwaggerDataRepository swaggerDataRepository;

    @Override
    public String getSwaggerDataHashId(String swaggerUrl) {
        String jsonUrl = swaggerUrl;
        String htmlBody;
        boolean updated = false;

        if (StringUtils.isNotEmpty(swaggerUrl) && !StringUtils.endsWithIgnoreCase(swaggerUrl, JSON_SUFFIX)) {
            jsonUrl = getJsonUrlFromSwaggerPage(swaggerUrl);
        }
        jsonUrl = getCleanStartPage(jsonUrl);

        SwaggerData swaggerData = swaggerDataRepository.findFirstByUrl(jsonUrl);
        if (swaggerData != null && swaggerData.getUpdatedDate().after(DateUtils.addDays(new Date(), -1))) {
            htmlBody = swaggerData.getPageContent();

        } else {

            htmlBody = downloadSwaggerJson(jsonUrl);
            updated = true;
        }

        if(updated && StringUtils.isNotEmpty(htmlBody)) {
            boolean isExist = swaggerData != null;
            swaggerData = isExist ? swaggerData : new SwaggerData();
            swaggerData.setUrl(jsonUrl);
            swaggerData.setPassedUrl(swaggerUrl);
            swaggerData.setHashId(new HmacUtils(HmacAlgorithms.HMAC_SHA_1, "api-monkey").hmacHex(jsonUrl + new Date()).substring(0, 16));
            swaggerData.setPageContent(htmlBody);
            swaggerData.setCreatedDate(isExist ? swaggerData.getCreatedDate() : new Date());
            swaggerData.setUpdatedDate(new Date());
            swaggerDataRepository.save(swaggerData);
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

        } catch (Exception e) {
            log.error("Error parsing swagger url: {}", e);
        }
        return result;
    }

    private static String getJsonUrlFromSwaggerPage(String path) {

        String result = null;

        try {

            Document doc = Jsoup.parse(new URL(path), 30000);
            List<String> list = new ArrayList<>();
            Matcher matcher = Pattern.compile("[\"'](.*.json)[\"']", Pattern.CASE_INSENSITIVE).matcher(doc.html());
            while (matcher.find()){
                list.add(matcher.group());
            }

            String endPart = "";
            if (CollectionUtils.isNotEmpty(list)) {
                String[] parts = list.get(0).split("\"");
                endPart = Arrays.stream(parts)
                        .filter(StringUtils::isNotEmpty)
                        .filter(i -> StringUtils.containsIgnoreCase(i, JSON_SUFFIX))
                        .findFirst()
                        .orElse(null);
            }

            if(!StringUtils.containsIgnoreCase(endPart, "http")) {
                URL sourceUrl = new URL(path);
                result = sourceUrl.getProtocol() + "://" + sourceUrl.getHost() + (StringUtils.startsWith(endPart, "/") ? "" : "/") + endPart;

            } else {
                result = endPart;

            }

        } catch (Exception e) {
            log.error("Error crawling .json link from the page [{}]: ", path, e);
        }

        return result;
    }
}