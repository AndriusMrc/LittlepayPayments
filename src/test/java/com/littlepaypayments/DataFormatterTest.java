package com.littlepaypayments;

import com.littlepaypayments.model.TapType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static com.littlepaypayments.DataFormatter.formatChargeAmount;
import static org.junit.jupiter.api.Assertions.*;

class DataFormatterTest {

    @Test
    void cleanHeadersAndDataShouldRemoveAllSpacesAndMakeHeadersUpercase() {
        // Map with extra spaces and mixed case headers
        Map<String, String> headerToDataMap = Map.of(
                " ID ", " 123 ",
                " DateTimeUTC ", " 2025-01-21T10:15:30 ",
                " TapType ", " on ");

        Map<String, String> cleanedHeaderToDataMap = DataFormatter.cleanHeadersAndData(headerToDataMap);

        assertTrue(cleanedHeaderToDataMap.containsKey("ID"));
        assertTrue(cleanedHeaderToDataMap.containsKey("DATETIMEUTC"));
        assertTrue(cleanedHeaderToDataMap.containsKey("TAPTYPE"));
        assertEquals("123", cleanedHeaderToDataMap.get("ID"));
        assertEquals("2025-01-21T10:15:30", cleanedHeaderToDataMap.get("DATETIMEUTC"));
        assertEquals("on", cleanedHeaderToDataMap.get("TAPTYPE"));
    }

    @Test
    void getStringValueShouldReturnValue() {
        Map<String, String> headerToDataMap = Map.of(
                "ID", "123",
                "DATETIMEUTC", "2025-01-21T10:15:30"
        );

        String idValue = DataFormatter.getStringValue(headerToDataMap, "id");
        String dateTimeValue = DataFormatter.getStringValue(headerToDataMap, "DateTimeUTC");

        assertEquals("123", idValue);
        assertEquals("2025-01-21T10:15:30", dateTimeValue);
    }

    @Test
    void getLocalDateTimeShouldReturnValue() {
        Map<String, String> headerToDataMap = Map.of("DATETIMEUTC", "2025-01-21T10:15:30");
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        LocalDateTime dateTime = DataFormatter.getLocalDateTime(headerToDataMap, "DateTIMEuTc", formatter);

        assertEquals(LocalDateTime.of(2025, 1, 21, 10, 15, 30, 0), dateTime);
    }

    @Test
    void getTapTypeShouldReturnValue() {
        // Given a cleaned map with a valid TapType
        Map<String, String> headerToDataMap = Map.of("TAPTYPE", "oN");

        // When calling getTapType
        TapType tapType = DataFormatter.getTapType(headerToDataMap, "TaPtYPE");

        // Then the correct TapType enum value should be returned
        assertEquals(TapType.ON, tapType);
    }

    @Test
    void getTapTypeWithInvalidValue() {
        Map<String, String> headerToDataMap = Map.of("TAPTYPE", "invalidType");

        assertThrows(IllegalArgumentException.class, () -> {
            DataFormatter.getTapType(headerToDataMap, "TAPTYPE");
        });
    }

    @Test
    void formatChargeAmountAdds$BeforeAmount() {
        String formattedAmount = formatChargeAmount(BigDecimal.valueOf(10.55));
        assertEquals("$10.55", formattedAmount);
    }

}