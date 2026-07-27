package com.tramo.backend.subscription.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SubscriptionStatusDTO {
    private boolean supporter;
    private long storageUsedBytes;
    private long storageQuotaBytes;
    private long publishesPerWeek;  // -1 = unlimited
}
