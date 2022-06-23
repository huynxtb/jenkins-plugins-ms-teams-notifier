package io.jenkins.plugins.util;

import jenkins.org.apache.commons.validator.routines.UrlValidator;

public class Validation {

    public static boolean isUrl(String input){

        String[] schemes = new String[]{"http", "https"};
                UrlValidator urlValidator = new UrlValidator(schemes);

        return urlValidator.isValid(input);
    }
}
