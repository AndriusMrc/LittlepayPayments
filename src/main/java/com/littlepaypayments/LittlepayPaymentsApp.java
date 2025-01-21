package com.littlepaypayments;

import com.littlepaypayments.model.Tap;
import com.littlepaypayments.model.Trip;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LittlepayPaymentsApp {
    private static final String TAPS_CSV_FILE = "src/main/resources/taps.csv";
    private static final String TRIPS_CSV_FILE = "src/main/resources/trips.csv";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public static void main(String[] args) {
        try {
            CsvReader csvReader = new CsvReader();
            TripProcessor tripProcessor = new TripProcessor(initializeTripCosts());
            CsvWriter csvWriter = new CsvWriter();

            List<Tap> taps = csvReader.readTaps(TAPS_CSV_FILE, DATE_TIME_FORMATTER);
            List<Trip> trips = tripProcessor.processTrips(taps);
            csvWriter.writeTrips(TRIPS_CSV_FILE, trips, DATE_TIME_FORMATTER);

        } catch (IOException e) {
            throw new RuntimeException("Littlepay application failure. " + e.getMessage());
        }
    }

    private static Map<String, Map<String, BigDecimal>> initializeTripCosts() {
        Map<String, Map<String, BigDecimal>> tripCosts = new HashMap<>();
        tripCosts.put("Stop1", Map.of("Stop2", BigDecimal.valueOf(3.25), "Stop3", BigDecimal.valueOf(7.30)));
        tripCosts.put("Stop2", Map.of("Stop1", BigDecimal.valueOf(3.25), "Stop3", BigDecimal.valueOf(5.50)));
        tripCosts.put("Stop3", Map.of("Stop1", BigDecimal.valueOf(7.30), "Stop2", BigDecimal.valueOf(5.50)));
        return tripCosts;
    }
}
