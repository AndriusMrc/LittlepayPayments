package com.littlepaypayments;

import com.littlepaypayments.model.Tap;
import com.littlepaypayments.model.TapType;
import com.littlepaypayments.model.Trip;
import com.littlepaypayments.model.TripStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TripProcessorTest {
    private TripProcessor tripProcessor;

    @BeforeEach
    void setUp() {
        tripProcessor = new TripProcessor();
    }

    @Test
    void processTripsShouldReturnCompletedTrip() {
        Tap tapOn = Tap.builder()
                .id(1L)
                .dateTimeUTC(LocalDateTime.of(2025, 1, 21, 8, 0, 0))
                .tapType(TapType.ON)
                .stopId("Stop1")
                .companyId("Company1")
                .busId("Bus1")
                .pan("5500005555555559")
                .build();
        Tap tapOff = Tap.builder()
                .id(1L)
                .dateTimeUTC(LocalDateTime.of(2025, 1, 21, 8, 15, 0))
                .tapType(TapType.OFF)
                .stopId("Stop2")
                .companyId("Company1")
                .busId("Bus1")
                .pan("5500005555555559")
                .build();
        List<Tap> taps = Arrays.asList(tapOn, tapOff);

        List<Trip> trips = tripProcessor.processTrips(taps);

        assertEquals(1, trips.size());
        Trip trip = trips.getFirst();
        assertEquals(LocalDateTime.parse("2025-01-21T08:00"), trip.getStarted());
        assertEquals(LocalDateTime.parse("2025-01-21T08:15"), trip.getFinished());
        assertEquals(900, trip.getDurationSecs());
        assertEquals("Stop1", trip.getFromStopId());
        assertEquals("Stop2", trip.getToStopId());
        assertEquals("$3.25", trip.getChargeAmount());
        assertEquals("Company1", trip.getCompanyId());
        assertEquals("Bus1", trip.getBusId());
        assertEquals("5500005555555559", trip.getPan());
        assertEquals(TripStatus.COMPLETED, trip.getStatus());
    }

    @Test
    void processTripsShouldReturnIncompleteTrip() {
        Tap tapOn = Tap.builder()
                .id(1L)
                .dateTimeUTC(LocalDateTime.of(2025, 1, 21, 8, 0, 0))
                .tapType(TapType.ON)
                .stopId("Stop1")
                .companyId("Company1")
                .busId("Bus1")
                .pan("5500005555555559")
                .build();
        List<Tap> taps = Arrays.asList(tapOn);

        List<Trip> trips = tripProcessor.processTrips(taps);

        assertEquals(1, trips.size());
        Trip trip = trips.getFirst();
        assertEquals(LocalDateTime.parse("2025-01-21T08:00"), trip.getStarted());
        assertEquals(LocalDateTime.parse("2025-01-21T23:59:59"), trip.getFinished());
        assertEquals(57599, trip.getDurationSecs());
        assertEquals("Stop1", trip.getFromStopId());
        assertEquals("Stop3", trip.getToStopId());
        assertEquals("$7.30", trip.getChargeAmount());
        assertEquals("Company1", trip.getCompanyId());
        assertEquals("Bus1", trip.getBusId());
        assertEquals("5500005555555559", trip.getPan());
        assertEquals(TripStatus.INCOMPLETE, trip.getStatus());
    }

    @Test
    void processTripsShouldReturnIncompleteTripsWhenTappedOnOnly() {
        Tap tapOn = Tap.builder()
                .id(1L)
                .dateTimeUTC(LocalDateTime.of(2025, 1, 21, 8, 0, 0))
                .tapType(TapType.ON)
                .stopId("Stop3")
                .companyId("Company1")
                .busId("Bus1")
                .pan("5500005555555559")
                .build();
        Tap tapOff = Tap.builder()
                .id(1L)
                .dateTimeUTC(LocalDateTime.of(2025, 1, 21, 8, 15, 0))
                .tapType(TapType.ON)
                .stopId("Stop2")
                .companyId("Company1")
                .busId("Bus2")
                .pan("5500005555555559")
                .build();
        List<Tap> taps = Arrays.asList(tapOn, tapOff);

        List<Trip> trips = tripProcessor.processTrips(taps);

        assertEquals(2, trips.size());
        Trip trip1 = trips.get(0);
        assertEquals(LocalDateTime.parse("2025-01-21T08:15"), trip1.getStarted());
        assertEquals(LocalDateTime.parse("2025-01-21T23:59:59"), trip1.getFinished());
        assertEquals(56699, trip1.getDurationSecs());
        assertEquals("Stop2", trip1.getFromStopId());
        assertEquals("Stop3", trip1.getToStopId());
        assertEquals("$5.50", trip1.getChargeAmount());
        assertEquals("Company1", trip1.getCompanyId());
        assertEquals("Bus2", trip1.getBusId());
        assertEquals("5500005555555559", trip1.getPan());
        assertEquals(TripStatus.INCOMPLETE, trip1.getStatus());

        Trip trip2 = trips.get(1);
        assertEquals(LocalDateTime.parse("2025-01-21T08:00"), trip2.getStarted());
        assertEquals(LocalDateTime.parse("2025-01-21T23:59:59"), trip2.getFinished());
        assertEquals(57599, trip2.getDurationSecs());
        assertEquals("Stop3", trip2.getFromStopId());
        assertEquals("Stop1", trip2.getToStopId());
        assertEquals("$7.30", trip2.getChargeAmount());
        assertEquals("Company1", trip2.getCompanyId());
        assertEquals("Bus1", trip2.getBusId());
        assertEquals("5500005555555559", trip2.getPan());
        assertEquals(TripStatus.INCOMPLETE, trip2.getStatus());
    }

    @Test
    void processTripsShouldReturnIncompleteTripsWhenTappedOffOnly() {
        Tap tapOn = Tap.builder()
                .id(1L)
                .dateTimeUTC(LocalDateTime.of(2025, 1, 21, 8, 0, 0))
                .tapType(TapType.OFF)
                .stopId("Stop3")
                .companyId("Company1")
                .busId("Bus1")
                .pan("5500005555555559")
                .build();
        Tap tapOff = Tap.builder()
                .id(1L)
                .dateTimeUTC(LocalDateTime.of(2025, 1, 21, 8, 15, 0))
                .tapType(TapType.OFF)
                .stopId("Stop2")
                .companyId("Company1")
                .busId("Bus2")
                .pan("5500005555555559")
                .build();
        List<Tap> taps = Arrays.asList(tapOn, tapOff);

        List<Trip> trips = tripProcessor.processTrips(taps);

        assertEquals(2, trips.size());
        Trip trip1 = trips.get(0);
        assertEquals(LocalDateTime.parse("2025-01-21T08:15"), trip1.getStarted());
        assertEquals(LocalDateTime.parse("2025-01-21T23:59:59"), trip1.getFinished());
        assertEquals(56699, trip1.getDurationSecs());
        assertEquals("Stop2", trip1.getFromStopId());
        assertEquals("Stop3", trip1.getToStopId());
        assertEquals("$5.50", trip1.getChargeAmount());
        assertEquals("Company1", trip1.getCompanyId());
        assertEquals("Bus2", trip1.getBusId());
        assertEquals("5500005555555559", trip1.getPan());
        assertEquals(TripStatus.INCOMPLETE, trip1.getStatus());

        Trip trip2 = trips.get(1);
        assertEquals(LocalDateTime.parse("2025-01-21T08:00"), trip2.getStarted());
        assertEquals(LocalDateTime.parse("2025-01-21T23:59:59"), trip2.getFinished());
        assertEquals(57599, trip2.getDurationSecs());
        assertEquals("Stop3", trip2.getFromStopId());
        assertEquals("Stop1", trip2.getToStopId());
        assertEquals("$7.30", trip2.getChargeAmount());
        assertEquals("Company1", trip2.getCompanyId());
        assertEquals("Bus1", trip2.getBusId());
        assertEquals("5500005555555559", trip2.getPan());
        assertEquals(TripStatus.INCOMPLETE, trip2.getStatus());
    }

    @Test
    void processTripsShouldReturnCancelledTrip() {
        Tap tapOn = Tap.builder()
                .id(1L)
                .dateTimeUTC(LocalDateTime.of(2025, 1, 21, 8, 0, 0))
                .tapType(TapType.ON)
                .stopId("Stop1")
                .companyId("Company1")
                .busId("Bus1")
                .pan("5500005555555559")
                .build();
        Tap tapOff = Tap.builder()
                .id(1L)
                .dateTimeUTC(LocalDateTime.of(2025, 1, 21, 8, 5, 0))
                .tapType(TapType.OFF)
                .stopId("Stop1")
                .companyId("Company1")
                .busId("Bus1")
                .pan("5500005555555559")
                .build();
        List<Tap> taps = Arrays.asList(tapOn, tapOff);

        List<Trip> trips = tripProcessor.processTrips(taps);

        assertEquals(1, trips.size());
        Trip trip = trips.getFirst();
        assertEquals(LocalDateTime.parse("2025-01-21T08:00"), trip.getStarted());
        assertEquals(LocalDateTime.parse("2025-01-21T08:05"), trip.getFinished());
        assertEquals(300, trip.getDurationSecs());
        assertEquals("Stop1", trip.getFromStopId());
        assertEquals("Stop1", trip.getToStopId());
        assertEquals("$0.00", trip.getChargeAmount());
        assertEquals("Company1", trip.getCompanyId());
        assertEquals("Bus1", trip.getBusId());
        assertEquals("5500005555555559", trip.getPan());
        assertEquals(TripStatus.CANCELLED, trip.getStatus());
    }

    @Test
    void processTripsShouldReturnNoTripsWhenNoTaps() {
        List<Trip> trips = tripProcessor.processTrips(List.of());
        assertEquals(0, trips.size());
    }

}