package com.skg.apimonkey.util;

import org.apache.commons.validator.routines.EmailValidator;

public class StringUtil {

    public static boolean isValidEmail(String email) {
        EmailValidator validator = EmailValidator.getInstance();
        return validator.isValid(email);
    }
}
