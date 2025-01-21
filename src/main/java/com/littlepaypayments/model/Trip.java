package com.littlepaypayments.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class Trip {
    LocalDateTime started;
    LocalDateTime finished;
    int durationSecs;
    String fromStopId;
    String toStopId;
    String chargeAmount;
    String companyId;
    String busId;
    String pan;
    TripStatus status;
}
