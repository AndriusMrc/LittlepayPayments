package com.littlepaypayments;

import com.littlepaypayments.model.Tap;
import com.littlepaypayments.model.Trip;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class LittlepayPaymentsApp {
    private static final String TAPS_CSV_FILE = "/taps.csv";

    public static void main(String[] args) {
        try {
            CsvReader csvReader = new CsvReader();
            TripProcessor tripProcessor = new TripProcessor();
            File file = Paths.get(Objects.requireNonNull(LittlepayPaymentsApp.class.getResource(TAPS_CSV_FILE))
                    .toURI())
                    .toFile();

            List<Tap> taps = csvReader.readTaps(file.getAbsolutePath());
            List<Trip> trips = tripProcessor.processTrips(taps);
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Littlepay application failure. " + e.getMessage());
        }
    }
}
