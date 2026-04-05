package com.finance.dashboard.dto.record;

import com.finance.dashboard.model.RecordType;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record FinancialRecordResponse(
        Long id,
        BigDecimal amount,
        RecordType type,
        String category,
        LocalDate recordDate,
        String notes,
        Instant createdAt,
        String createdByName
) {
}
