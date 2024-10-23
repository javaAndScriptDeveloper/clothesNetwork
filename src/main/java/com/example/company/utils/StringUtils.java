package com.example.company.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @SneakyThrows
    public static <T> String toJson(T object) {
        return OBJECT_MAPPER.writeValueAsString(object);
    }

    public String decodeBase64(String input) {
        return new String(Base64.getDecoder().decode(input), StandardCharsets.UTF_8);
    }

    public String encodeBase64(String input) {
        return Base64.getEncoder().encodeToString(input.getBytes());
    }
}
