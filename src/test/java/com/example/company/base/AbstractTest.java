package com.example.company.base;

import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;

public class AbstractTest {

    protected static final EnhancedRandom random = EnhancedRandomBuilder.aNewEnhancedRandomBuilder()
            .seed(123L)
            .objectPoolSize(100)
            .randomizationDepth(3)
            .charset(StandardCharsets.UTF_8)
            .dateRange(LocalDate.now().minusDays(1), LocalDate.now().plusDays(1))
            .stringLengthRange(5, 50)
            .collectionSizeRange(1, 10)
            .scanClasspathForConcreteTypes(true)
            .overrideDefaultInitialization(false)
            .build();

    protected static String getRandomString() {
        return random.nextObject(String.class);
    }

    protected static int getPositiveInteger() {
        return random.nextInt(0, Integer.MAX_VALUE);
    }

    protected static <T extends Enum<T>> T getRandomEnumValue(Class<T> enumClass) {
        var enumValues = Arrays.stream(enumClass.getEnumConstants()).toList();
        if (enumValues.size() == 1) {
            return enumValues.get(0);
        }
        return enumValues.get(random.nextInt(enumValues.size() - 1));
    }
}
