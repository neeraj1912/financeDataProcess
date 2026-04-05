package com.finance.dashboard.controller;

import com.finance.dashboard.dto.record.CreateFinancialRecordRequest;
import com.finance.dashboard.dto.record.FinancialRecordResponse;
import com.finance.dashboard.dto.record.RecordFilter;
import com.finance.dashboard.dto.record.UpdateFinancialRecordRequest;
import com.finance.dashboard.model.RecordType;
import com.finance.dashboard.security.FinanceUserPrincipal;
import com.finance.dashboard.service.FinancialRecordService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/records")
public class FinancialRecordController {

    private final FinancialRecordService financialRecordService;

    public FinancialRecordController(FinancialRecordService financialRecordService) {
        this.financialRecordService = financialRecordService;
    }

    @GetMapping
    public Page<FinancialRecordResponse> getRecords(
            @RequestParam(required = false) RecordType type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @PageableDefault(size = 10, sort = "recordDate") Pageable pageable
    ) {
        return financialRecordService.getRecords(new RecordFilter(type, category, from, to), pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FinancialRecordResponse createRecord(
            @Valid @RequestBody CreateFinancialRecordRequest request,
            @AuthenticationPrincipal FinanceUserPrincipal principal
    ) {
        return financialRecordService.createRecord(request, principal);
    }

    @PutMapping("/{id}")
    public FinancialRecordResponse updateRecord(
            @PathVariable Long id,
            @Valid @RequestBody UpdateFinancialRecordRequest request
    ) {
        return financialRecordService.updateRecord(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecord(@PathVariable Long id) {
        financialRecordService.deleteRecord(id);
    }
}
