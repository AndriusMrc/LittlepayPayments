package com.littlepaypayments;

import com.littlepaypayments.model.TapType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.stream.Collectors;

public class DataFormatter {

    // Remove leading and trailing spaces for headers and data
    // Making all headers uppercase
    public static Map<String, String> cleanHeadersAndData(Map<String, String> headerToDataMap) {
        return headerToDataMap.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().trim().toUpperCase(),
                        entry -> entry.getValue().trim()));
    }

    public static String getStringValue(Map<String, String> headerToDataMap, String key) {
        return headerToDataMap.get(key.toUpperCase());
    }

    public static LocalDateTime getLocalDateTime(Map<String, String> headerToDataMap, String key, DateTimeFormatter formatter) {
        return LocalDateTime.parse(getStringValue(headerToDataMap, key), formatter);
    }

    public static TapType getTapType(Map<String, String> headerToDataMap, String key) {
        return TapType.valueOf(getStringValue(headerToDataMap, key).toUpperCase());
    }

    public static String formatChargeAmount(BigDecimal amount) {
        return "$" + amount.setScale(2, RoundingMode.CEILING);
    }
}
