package com.skg.apimonkey.service.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skg.apimonkey.domain.data.UserDataCase;
import com.skg.apimonkey.domain.model.TestDataCase;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class MappingUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static TestDataCase toTestDataCase(UserDataCase userDataCase) {
        TestDataCase testDataCase = null;
        if(userDataCase != null) {
            try {
                testDataCase = objectMapper.readValue(userDataCase.getDataCaseContent(), TestDataCase.class);
            } catch (JsonProcessingException e) {
                log.error("Error parsing UserDataCase to TestDataCase");
            }
        }
        return testDataCase;
    }

    public static UserDataCase toUserDataCase(TestDataCase testDataCase) {
        UserDataCase userDataCase = null;
        if(testDataCase != null) {

            userDataCase = new UserDataCase();
            userDataCase.setDataId(testDataCase.getDataId());
            userDataCase.setDataName(testDataCase.getSummary());

            try {
                userDataCase.setDataCaseContent(objectMapper.writeValueAsString(testDataCase));
            } catch (JsonProcessingException e) {
                log.error("Error writing TestDataCase to UserDataCase");
            }
            userDataCase.setUpdatedDate(new Date());
            userDataCase.setCreatedDate(new Date());
        }
        return userDataCase;
    }
}