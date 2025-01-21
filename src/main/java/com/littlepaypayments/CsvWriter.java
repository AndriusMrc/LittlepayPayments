package com.littlepaypayments;

import com.littlepaypayments.model.Trip;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class CsvWriter {
    public static final String[] TRIPS_CSV_FILE_HEADERS = {"Started", "Finished", "DurationSecs", "FromStopId",
            "ToStopId", "ChargeAmount", "CompanyId", "BusID", "PAN", "Status"};

    public void writeTrips(String filePath, List<Trip> trips) throws IOException {
        try(Writer writer = new FileWriter(filePath);
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
                    .setHeader(TRIPS_CSV_FILE_HEADERS).build())) {

            for (Trip trip : trips) {
                csvPrinter.printRecord(
                        trip.getStarted(),
                        trip.getFinished(),
                        trip.getDurationSecs(),
                        trip.getFromStopId(),
                        trip.getToStopId(),
                        trip.getChargeAmount(),
                        trip.getCompanyId(),
                        trip.getBusId(),
                        trip.getPan(),
                        trip.getStatus()
                );
            }
        }
    }
}
