package com.littlepaypayments;

import com.littlepaypayments.model.Tap;
import com.littlepaypayments.model.TapType;
import com.littlepaypayments.model.Trip;
import com.littlepaypayments.model.TripStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class TripProcessor {
    private final Map<String, Map<String, BigDecimal>> tripCosts;

    public TripProcessor() {
        this.tripCosts = initializeTripCosts();
    }

    public List<Trip> processTrips(List<Tap> taps) {
        Map<String, List<Tap>> tapsByPan = groupTapsByPan(taps);
        List<Trip> trips = new ArrayList<>();

        for (Map.Entry<String, List<Tap>> entry : tapsByPan.entrySet()) {
            List<Tap> sortedTaps = entry.getValue();
            sortedTaps.sort(Comparator.comparing(Tap::getDateTimeUTC));

            trips.addAll(processTripsForPan(sortedTaps));
        }
        return trips;
    }

    private Map<String, List<Tap>> groupTapsByPan(List<Tap> taps) {
        return taps.stream().collect(Collectors.groupingBy(Tap::getPan));
    }

    private List<Trip> processTripsForPan(List<Tap> taps) {
        List<Trip> trips = new ArrayList<>();
        Stack<Tap> tapStack = new Stack<>();

        for (Tap tap : taps) {
            if (tap.getTapType() == TapType.ON) {
                tapStack.push(tap);
            } else if (tap.getTapType() == TapType.OFF) {
                if (!tapStack.isEmpty()) {
                    Tap previousTap = tapStack.pop();
                    if (previousTap.getTapType() == TapType.ON) {
                        if (!tap.getStopId().equals(previousTap.getStopId())) {
                            trips.add(createCompletedTrip(previousTap, tap));
                        } else {
                            trips.add(createdCancelledTrip(previousTap, tap));
                        }
                    } else {
                        // To handle cases when passenger taps OFF without tapping ON
                        tapStack.push(previousTap);
                        tapStack.push(tap);
                    }
                } else {
                    tapStack.push(tap);
                }
            }
        }

        while (!tapStack.isEmpty()) {
            Tap incompleteTap = tapStack.pop();
            trips.add(createIncompleteTrip(incompleteTap));
        }

        return trips;
    }

    private Trip createCompletedTrip(Tap previousTap, Tap tap) {
        BigDecimal cost = tripCosts.get(previousTap.getStopId()).get(tap.getStopId());
        return buildTrip(previousTap, tap.getStopId(), tap.getDateTimeUTC(), cost, TripStatus.COMPLETED);
    }

    private Trip createdCancelledTrip(Tap previousTap, Tap tap) {
        return buildTrip(previousTap, tap.getStopId(), tap.getDateTimeUTC(), BigDecimal.ZERO, TripStatus.CANCELLED);
    }

    private Trip createIncompleteTrip(Tap tap) {
        BigDecimal maxCost = BigDecimal.ZERO;
        String stopIdWithMaxCost = null;

        for (Map.Entry<String, BigDecimal> entry : tripCosts.get(tap.getStopId()).entrySet()) {
            if (maxCost.compareTo(entry.getValue()) <= 0) {
                maxCost = entry.getValue();
                stopIdWithMaxCost = entry.getKey();
            }
        }

        // Calculate the finished time as the end of the day (23:59:59)
        LocalDateTime finishedTime = tap.getDateTimeUTC().toLocalDate().atTime(23, 59, 59);

        return buildTrip(tap, stopIdWithMaxCost, finishedTime, maxCost, TripStatus.INCOMPLETE);
    }

    private Trip buildTrip(Tap tap, String finishStopId,
                           LocalDateTime finishedDateTime, BigDecimal cost, TripStatus status) {
        return Trip.builder()
                .started(tap.getDateTimeUTC())
                .finished(finishedDateTime)
                .durationSecs(finishedDateTime != null ? getDurationSecs(tap.getDateTimeUTC(), finishedDateTime) : 0)
                .fromStopId(tap.getStopId())
                .toStopId(finishStopId)
                .chargeAmount(formatChargeAmount(cost))
                .companyId(tap.getCompanyId())
                .busId(tap.getBusId())
                .pan(tap.getPan())
                .status(status)
                .build();
    }

    private static int getDurationSecs(LocalDateTime start, LocalDateTime end) {
        return (int) start.until(end, ChronoUnit.SECONDS);
    }

    private static String formatChargeAmount(BigDecimal amount) {
        return "$" + amount.setScale(2, RoundingMode.CEILING);
    }

    private Map<String, Map<String, BigDecimal>> initializeTripCosts() {
        Map<String, Map<String, BigDecimal>> tripCosts = new HashMap<>();
        tripCosts.put("Stop1", Map.of("Stop2", BigDecimal.valueOf(3.25), "Stop3", BigDecimal.valueOf(7.30)));
        tripCosts.put("Stop2", Map.of("Stop1", BigDecimal.valueOf(3.25), "Stop3", BigDecimal.valueOf(5.50)));
        tripCosts.put("Stop3", Map.of("Stop1", BigDecimal.valueOf(7.30), "Stop2", BigDecimal.valueOf(5.50)));
        return tripCosts;
    }

}
