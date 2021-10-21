package com.skg.apimonkey.service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.skg.apimonkey.domain.model.RequestType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;


@Slf4j
public class StringUtil {

    public static boolean isJson(String text) {
        try {
            new JSONObject(text);

        } catch (JSONException ex) {
            try {
                new JSONArray(text);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public static String generateDataId(RequestType requestType, String pathName) {

        if(requestType != null && StringUtils.isNotEmpty(pathName)) {
            return (requestType.name() + pathName)
                    .toLowerCase()
                    .replaceAll("[^a-z0-9 -]", " ")
                    .replaceAll(" {1,}", "-");
        }
        return "";
    }

    public static String beautify(Object object) throws JsonProcessingException {
        if(Objects.nonNull(object)) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            return objectMapper.writeValueAsString(object);
        }
        return null;
    }

    public static String beautify(String string) throws JsonProcessingException {
        if(Objects.nonNull(string)) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            return objectMapper.writeValueAsString(objectMapper.readValue(string, HashMap.class));
        }
        return null;
    }
}