package com.finance.dashboard.dto.record;

import com.finance.dashboard.model.RecordType;
import java.time.LocalDate;

public record RecordFilter(
        RecordType type,
        String category,
        LocalDate from,
        LocalDate to
) {
}
