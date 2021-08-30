package com.skg.apimonkey.service.impl;

import com.skg.apimonkey.service.SwaggerParserService;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class SwaggerParserServiceImpl implements SwaggerParserService {

    private static final String JSON_SUFFIX = ".json";

    @Override
    public SwaggerParseResult getSwaggerRestApi(String swaggerUrl) {
        String jsonUrl = swaggerUrl;

        if (StringUtils.isNotEmpty(swaggerUrl) && !StringUtils.endsWithIgnoreCase(swaggerUrl, JSON_SUFFIX)) {
            jsonUrl = getJsonUrlFromSwaggerPage(swaggerUrl);
        }

        SwaggerParseResult result = null;
        try {
            result = new OpenAPIParser().readLocation(jsonUrl, null, null);

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