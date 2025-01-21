package com.littlepaypayments;

import org.junit.jupiter.api.Test;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DataValidatorTest {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    public void validateRecordPassesWithValidPanAndValidDateTime() {
        Map<String, String> validRecord = new HashMap<>();
        validRecord.put("PAN", "630495060000000000");
        validRecord.put("DATETIMEUTC", "2025-01-21 12:30:45");

        assertDoesNotThrow(() -> DataValidator.validateRecord(validRecord, DATE_TIME_FORMATTER));
    }

    @Test
    public void validateRecordThrowsExceptionWhenInvalidPAN() {
        Map<String, String> invalidRecord = new HashMap<>();
        invalidRecord.put("PAN", "1234567890123456");
        invalidRecord.put("DATETIMEUTC", "2025-01-21 12:30:45");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> DataValidator.validateRecord(invalidRecord, DATE_TIME_FORMATTER));

        assertEquals("Invalid PAN: 1234567890123456", exception.getMessage());
    }

    @Test
    public void validateRecordThrowsExceptionWhenInvalidDateTime() {
        Map<String, String> invalidRecord = new HashMap<>();
        invalidRecord.put("PAN", "630495060000000000");
        invalidRecord.put("DATETIMEUTC", "2025-01-32 12:30:45");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> DataValidator.validateRecord(invalidRecord, DATE_TIME_FORMATTER));

        assertEquals("Invalid DateTimeUTC: 2025-01-32 12:30:45", exception.getMessage());
    }

    @Test
    public void isValidDateTimeReturnsTrue() {
        String validDateTime = "2025-01-21 12:30:45";
        boolean isValid = DataValidator.isValidDateTime(validDateTime, DATE_TIME_FORMATTER);
        assertTrue(isValid);
    }

    @Test
    public void isValidDateTimeReturnsFalse() {
        String invalidDateTime = "2025-01-32 12:30:45";
        boolean isValid = DataValidator.isValidDateTime(invalidDateTime, DATE_TIME_FORMATTER);
        assertFalse(isValid);
    }
}