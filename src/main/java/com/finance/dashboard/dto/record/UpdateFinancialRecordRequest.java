package com.finance.dashboard.dto.record;

import com.finance.dashboard.model.RecordType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateFinancialRecordRequest(
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
        @NotNull RecordType type,
        @NotBlank @Size(max = 60) String category,
        @NotNull LocalDate recordDate,
        @Size(max = 500) String notes
) {
}
