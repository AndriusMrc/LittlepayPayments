package com.littlepaypayments;

import com.littlepaypayments.model.Trip;
import com.littlepaypayments.model.TripStatus;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDateTime;
import java.util.List;

import static com.littlepaypayments.CsvWriter.TRIPS_CSV_FILE_HEADERS;
import static org.junit.jupiter.api.Assertions.*;

class CsvWriterTest {
    private final CsvWriter csvWriter = new CsvWriter();

    @Test
    void writeTripsCreatesCsvFileWithTripInformation() throws IOException {
        // Initialize test objects
        File tempTripsFile = File.createTempFile("trips", ".csv");
        tempTripsFile.deleteOnExit();

        Trip trip1 = Trip.builder()
                .started(LocalDateTime.of(2025, 1, 21, 8, 0, 0))
                .finished(LocalDateTime.of(2025, 1, 21, 8, 5, 0))
                .durationSecs(900)
                .fromStopId("Stop1")
                .toStopId("Stop2")
                .chargeAmount("$3.25")
                .companyId("Company1")
                .busId("Bus3")
                .pan("4111111111111111")
                .status(TripStatus.COMPLETED)
                .build();

        Trip trip2 = Trip.builder()
                .started(LocalDateTime.of(2025, 1, 22, 9, 0, 0))
                .finished(LocalDateTime.of(2025, 1, 22, 10, 5, 0))
                .durationSecs(600)
                .fromStopId("Stop3")
                .toStopId("Stop1")
                .chargeAmount("$7.30")
                .companyId("Company1")
                .busId("Bus9")
                .pan("4111111111111111")
                .status(TripStatus.INCOMPLETE)
                .build();

        List<Trip> trips = List.of(trip1, trip2);

        // Act
        csvWriter.writeTrips(tempTripsFile.getAbsolutePath(), trips);

        // Validate results
        try(Reader reader = new FileReader(tempTripsFile);
            CSVParser csvParser = CSVFormat.DEFAULT.builder()
                    .setHeader(TRIPS_CSV_FILE_HEADERS)
                    .setSkipHeaderRecord(true)
                    .build()
                    .parse(reader)) {

            List<CSVRecord> records = csvParser.getRecords();
            assertEquals(2, records.size());

            CSVRecord trip1Record = records.get(0);
            assertEquals("2025-01-21T08:00", trip1Record.get("Started"));
            assertEquals("2025-01-21T08:05", trip1Record.get("Finished"));
            assertEquals("900", trip1Record.get("DurationSecs"));
            assertEquals("Stop1", trip1Record.get("FromStopId"));
            assertEquals("Stop2", trip1Record.get("ToStopId"));
            assertEquals("$3.25", trip1Record.get("ChargeAmount"));
            assertEquals("Company1", trip1Record.get("CompanyId"));
            assertEquals("Bus3", trip1Record.get("BusID"));
            assertEquals("4111111111111111", trip1Record.get("PAN"));
            assertEquals(TripStatus.COMPLETED.name(), trip1Record.get("Status"));

            CSVRecord trip2Record = records.get(1);
            assertEquals("2025-01-22T09:00", trip2Record.get("Started"));
            assertEquals("2025-01-22T10:05", trip2Record.get("Finished"));
            assertEquals("600", trip2Record.get("DurationSecs"));
            assertEquals("Stop3", trip2Record.get("FromStopId"));
            assertEquals("Stop1", trip2Record.get("ToStopId"));
            assertEquals("$7.30", trip2Record.get("ChargeAmount"));
            assertEquals("Company1", trip2Record.get("CompanyId"));
            assertEquals("Bus9", trip2Record.get("BusID"));
            assertEquals("4111111111111111", trip2Record.get("PAN"));
            assertEquals(TripStatus.INCOMPLETE.name(), trip2Record.get("Status"));
        }
    }

    @Test
    void writeTripsShouldReturnEmptyFileWhenNoTrips() throws IOException {
        // Initialize test objects
        File tempTripsFile = File.createTempFile("trips", ".csv");
        tempTripsFile.deleteOnExit();

        // Act
        csvWriter.writeTrips(tempTripsFile.getAbsolutePath(), List.of());

        // Validate results
        try(Reader reader = new FileReader(tempTripsFile);
            CSVParser csvParser = CSVFormat.DEFAULT.builder()
                    .setHeader(TRIPS_CSV_FILE_HEADERS)
                    .setSkipHeaderRecord(true)
                    .build()
                    .parse(reader)) {

            List<CSVRecord> records = csvParser.getRecords();
            assertEquals(0, records.size());
        }
    }
}