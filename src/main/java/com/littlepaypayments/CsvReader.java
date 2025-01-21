package com.littlepaypayments;

import com.littlepaypayments.model.Tap;
import com.littlepaypayments.model.TapType;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CsvReader {
    private final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public List<Tap> readTaps(String filePath) throws IOException {
        List<Tap> taps = new ArrayList<>();

        try(Reader reader = new FileReader(filePath);
            CSVParser csvParser = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build()
                    .parse(reader)) {

            for (CSVRecord record : csvParser) {
                Tap tap = createTap(record);
                taps.add(tap);
            }
        }
        return taps;
    }

    private Tap createTap(CSVRecord record) {
        // Remove leading and trailing spaces for headers and data
        Map<String, String> trimmedRecord = record.toMap().entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().trim(),
                        entry -> entry.getValue().trim()));

        return Tap.builder()
                .id(Long.valueOf(trimmedRecord.get("ID")))
                .dateTimeUTC(LocalDateTime.parse(trimmedRecord.get("DateTimeUTC"), DATE_TIME_FORMATTER))
                .tapType(TapType.valueOf(trimmedRecord.get("TapType").toUpperCase()))
                .stopId(trimmedRecord.get("StopId"))
                .companyId(trimmedRecord.get("CompanyId"))
                .busId(trimmedRecord.get("BusID"))
                .pan(trimmedRecord.get("PAN"))
                .build();
    }
}
