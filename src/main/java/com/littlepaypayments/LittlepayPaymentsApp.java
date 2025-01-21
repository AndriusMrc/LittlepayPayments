package com.littlepaypayments;

import com.littlepaypayments.model.Tap;
import com.littlepaypayments.model.Trip;

import java.io.IOException;
import java.util.List;

public class LittlepayPaymentsApp {
    private static final String TAPS_CSV_FILE = "src/main/resources/taps.csv";
    private static final String TRIPS_CSV_FILE = "src/main/resources/trips.csv";

    public static void main(String[] args) {
        try {
            CsvReader csvReader = new CsvReader();
            TripProcessor tripProcessor = new TripProcessor();
            CsvWriter csvWriter = new CsvWriter();

            List<Tap> taps = csvReader.readTaps(TAPS_CSV_FILE);
            List<Trip> trips = tripProcessor.processTrips(taps);
            csvWriter.writeTrips(TRIPS_CSV_FILE, trips);

        } catch (IOException e) {
            throw new RuntimeException("Littlepay application failure. " + e.getMessage());
        }
    }
}
