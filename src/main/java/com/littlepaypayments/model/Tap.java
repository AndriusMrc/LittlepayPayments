package com.littlepaypayments.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class Tap {
    private Long id;
    private LocalDateTime dateTimeUTC;
    private TapType tapType;
    private String stopId;
    private String companyId;
    private String busId;
    private String pan;
}
