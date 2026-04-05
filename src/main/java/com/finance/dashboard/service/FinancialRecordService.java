package com.finance.dashboard.service;

import com.finance.dashboard.dto.record.CreateFinancialRecordRequest;
import com.finance.dashboard.dto.record.FinancialRecordResponse;
import com.finance.dashboard.dto.record.RecordFilter;
import com.finance.dashboard.dto.record.UpdateFinancialRecordRequest;
import com.finance.dashboard.exception.BadRequestException;
import com.finance.dashboard.exception.NotFoundException;
import com.finance.dashboard.model.FinancialRecord;
import com.finance.dashboard.repository.FinancialRecordRepository;
import com.finance.dashboard.security.FinanceUserPrincipal;
import jakarta.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.ArrayList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FinancialRecordService {

    private final FinancialRecordRepository financialRecordRepository;

    public FinancialRecordService(FinancialRecordRepository financialRecordRepository) {
        this.financialRecordRepository = financialRecordRepository;
    }

    @Transactional(readOnly = true)
    public Page<FinancialRecordResponse> getRecords(RecordFilter filter, Pageable pageable) {
        validateFilter(filter);
        return financialRecordRepository.findAll(buildSpecification(filter), pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public java.util.List<FinancialRecord> getRecordsForSummary(RecordFilter filter) {
        validateFilter(filter);
        return financialRecordRepository.findAll(buildSpecification(filter));
    }

    @Transactional
    public FinancialRecordResponse createRecord(CreateFinancialRecordRequest request, FinanceUserPrincipal principal) {
        FinancialRecord record = new FinancialRecord(
                request.amount(),
                request.type(),
                request.category().trim(),
                request.recordDate(),
                normalizeNotes(request.notes()),
                Instant.now(),
                principal.getFullName()
        );
        return toResponse(financialRecordRepository.save(record));
    }

    @Transactional
    public FinancialRecordResponse updateRecord(Long id, UpdateFinancialRecordRequest request) {
        FinancialRecord record = financialRecordRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Financial record was not found"));

        record.setAmount(request.amount());
        record.setType(request.type());
        record.setCategory(request.category().trim());
        record.setRecordDate(request.recordDate());
        record.setNotes(normalizeNotes(request.notes()));
        return toResponse(record);
    }

    @Transactional
    public void deleteRecord(Long id) {
        if (!financialRecordRepository.existsById(id)) {
            throw new NotFoundException("Financial record was not found");
        }
        financialRecordRepository.deleteById(id);
    }

    private Specification<FinancialRecord> buildSpecification(RecordFilter filter) {
        return (root, query, criteriaBuilder) -> {
            java.util.List<Predicate> predicates = new ArrayList<>();
            if (filter.type() != null) {
                predicates.add(criteriaBuilder.equal(root.get("type"), filter.type()));
            }
            if (filter.category() != null && !filter.category().isBlank()) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.lower(root.get("category")),
                        filter.category().trim().toLowerCase()
                ));
            }
            if (filter.from() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("recordDate"), filter.from()));
            }
            if (filter.to() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("recordDate"), filter.to()));
            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private void validateFilter(RecordFilter filter) {
        if (filter.from() != null && filter.to() != null && filter.from().isAfter(filter.to())) {
            throw new BadRequestException("The from date cannot be after the to date");
        }
    }

    private String normalizeNotes(String notes) {
        return notes == null || notes.isBlank() ? null : notes.trim();
    }

    private FinancialRecordResponse toResponse(FinancialRecord record) {
        return new FinancialRecordResponse(
                record.getId(),
                record.getAmount(),
                record.getType(),
                record.getCategory(),
                record.getRecordDate(),
                record.getNotes(),
                record.getCreatedAt(),
                record.getCreatedByName()
        );
    }
}
