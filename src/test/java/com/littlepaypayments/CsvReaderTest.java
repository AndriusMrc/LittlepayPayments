package com.littlepaypayments;

import com.littlepaypayments.model.Tap;
import com.littlepaypayments.model.TapType;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class CsvReaderTest {

    private final CsvReader csvReader = new CsvReader();
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @Test
    void readTapsShouldReturnListOfTaps() throws URISyntaxException, IOException {
        File file = Paths.get(Objects.requireNonNull(getClass().getResource("/taps.csv"))
                .toURI())
                .toFile();

        List<Tap> taps = csvReader.readTaps(file.getAbsolutePath(), DATE_TIME_FORMATTER);

        assertEquals(2, taps.size());
        Tap tap1 = taps.get(0);
        assertEquals(1L, tap1.getId());
        assertEquals(LocalDateTime.parse("2023-01-22T13:00"), tap1.getDateTimeUTC());
        assertEquals(TapType.ON, tap1.getTapType());
        assertEquals("Stop1", tap1.getStopId());
        assertEquals("Company1", tap1.getCompanyId());
        assertEquals("Bus37", tap1.getBusId());
        assertEquals("5500005555555559", tap1.getPan());

        Tap tap2 = taps.get(1);
        assertEquals(2L, tap2.getId());
        assertEquals(LocalDateTime.parse("2023-01-23T08:02"), tap2.getDateTimeUTC());
        assertEquals(TapType.OFF, tap2.getTapType());
        assertEquals("Stop2", tap2.getStopId());
        assertEquals("Company2", tap2.getCompanyId());
        assertEquals("Bus11", tap2.getBusId());
        assertEquals("4111111111111111", tap2.getPan());
    }

    @Test
    void readTapsShouldReturnEmptyListOfTaps() throws URISyntaxException, IOException {
        File file = Paths.get(Objects.requireNonNull(getClass().getResource("/taps_no_data.csv"))
                .toURI())
                .toFile();

        List<Tap> taps = csvReader.readTaps(file.getAbsolutePath(), DATE_TIME_FORMATTER);

        assertEquals(0, taps.size());
    }
}