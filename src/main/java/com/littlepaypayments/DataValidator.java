package com.littlepaypayments;

import org.apache.commons.validator.routines.checkdigit.LuhnCheckDigit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static com.littlepaypayments.DataFormatter.getStringValue;

public class DataValidator {

    public static void validateRecord(Map<String, String> record, DateTimeFormatter dateTimeFormatter) {
        String pan = record.get("PAN");
        // Validates a string representing a card number using the Luhn algorithm
        if (!LuhnCheckDigit.LUHN_CHECK_DIGIT.isValid(pan)) {
            throw new IllegalArgumentException("Invalid PAN: " + pan);
        }

        String dateTimeUtc = getStringValue(record, "DateTimeUTC");
        if (!isValidDateTime(dateTimeUtc, dateTimeFormatter)) {
            throw new IllegalArgumentException("Invalid DateTimeUTC: " + dateTimeUtc);
        }
    }

    public static boolean isValidDateTime(String dateTime, DateTimeFormatter formatter) {
        try {
            LocalDateTime.parse(dateTime, formatter);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
