package com.example.company.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils {

    public String decodeBase64(String input) {
        return new String(Base64.getDecoder().decode(input), StandardCharsets.UTF_8);
    }

    public String encodeBase64(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes());
    }
}
