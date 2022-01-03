package com.skg.apimonkey.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringUtil {

    public static boolean isValidEmail(String email) {
        EmailValidator validator = EmailValidator.getInstance();
        return validator.isValid(email);
    }

    public static String getFirstLastName(String name, boolean isFirstName) {
        if (StringUtils.isNotEmpty(name)) {
            List<String> nameParts = Arrays.stream(name.trim().split(" "))
                    .filter(StringUtils::isNotEmpty)
                    .collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(nameParts)) {

                if (isFirstName) {
                    return nameParts.get(0);
                } else {
                    return nameParts.size() > 1 ? nameParts.get(1) : nameParts.get(0);
                }
            }
        }
        return name;
    }
}
