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

    public List<Tap> readTaps(String filePath, DateTimeFormatter dateTimeFormatter) throws IOException {
        List<Tap> taps = new ArrayList<>();

        try(Reader reader = new FileReader(filePath);
            CSVParser csvParser = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build()
                    .parse(reader)) {

            for (CSVRecord record : csvParser) {
                Tap tap = createTap(record, dateTimeFormatter);
                taps.add(tap);
            }
        }
        return taps;
    }

    private Tap createTap(CSVRecord record, DateTimeFormatter dateTimeFormatter) {
        Map<String, String> cleanedRecord = cleanRecord(record);

        return Tap.builder()
                .id(Long.valueOf(getStringValue(cleanedRecord, "ID")))
                .dateTimeUTC(getLocalDateTime(cleanedRecord, "DateTimeUTC", dateTimeFormatter))
                .tapType(getTapType(cleanedRecord, "TapType"))
                .stopId(getStringValue(cleanedRecord, "StopId"))
                .companyId(getStringValue(cleanedRecord, "CompanyId"))
                .busId(getStringValue(cleanedRecord, "BusID"))
                .pan(getStringValue(cleanedRecord, "PAN"))
                .build();
    }

    // Remove leading and trailing spaces for headers and data
    // Making all headers uppercase
    private Map<String, String> cleanRecord(CSVRecord record) {
        return record.toMap().entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().trim().toUpperCase(),
                        entry -> entry.getValue().trim()));
    }

    private String getStringValue(Map<String, String> cleanedRecord, String key) {
        return cleanedRecord.get(key.toUpperCase());
    }

    private LocalDateTime getLocalDateTime(Map<String, String> cleanedRecord, String key, DateTimeFormatter formatter) {
        return LocalDateTime.parse(getStringValue(cleanedRecord, key), formatter);
    }

    private TapType getTapType(Map<String, String> cleanedRecord, String key) {
        return TapType.valueOf(getStringValue(cleanedRecord, key).toUpperCase());
    }
}
