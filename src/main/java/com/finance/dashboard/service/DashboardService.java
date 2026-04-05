package com.finance.dashboard.service;

import com.finance.dashboard.dto.dashboard.CategoryTotalResponse;
import com.finance.dashboard.dto.dashboard.DashboardSummaryResponse;
import com.finance.dashboard.dto.dashboard.RecentActivityResponse;
import com.finance.dashboard.dto.dashboard.TrendPointResponse;
import com.finance.dashboard.dto.record.RecordFilter;
import com.finance.dashboard.model.FinancialRecord;
import com.finance.dashboard.model.RecordType;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {

    private final FinancialRecordService financialRecordService;

    public DashboardService(FinancialRecordService financialRecordService) {
        this.financialRecordService = financialRecordService;
    }

    @Transactional(readOnly = true)
    public DashboardSummaryResponse getSummary(RecordFilter filter) {
        List<FinancialRecord> records = financialRecordService.getRecordsForSummary(filter);

        BigDecimal income = totalForType(records, RecordType.INCOME);
        BigDecimal expenses = totalForType(records, RecordType.EXPENSE);
        BigDecimal net = income.subtract(expenses);

        List<CategoryTotalResponse> categoryTotals = records.stream()
                .collect(Collectors.groupingBy(FinancialRecord::getCategory,
                        Collectors.reducing(BigDecimal.ZERO, FinancialRecord::getAmount, BigDecimal::add)))
                .entrySet()
                .stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue(Comparator.reverseOrder()))
                .map(entry -> new CategoryTotalResponse(entry.getKey(), entry.getValue()))
                .toList();

        List<TrendPointResponse> monthlyTrends = records.stream()
                .collect(Collectors.groupingBy(record -> YearMonth.from(record.getRecordDate())))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    BigDecimal trendIncome = totalForType(entry.getValue(), RecordType.INCOME);
                    BigDecimal trendExpense = totalForType(entry.getValue(), RecordType.EXPENSE);
                    return new TrendPointResponse(
                            entry.getKey().toString(),
                            trendIncome,
                            trendExpense,
                            trendIncome.subtract(trendExpense)
                    );
                })
                .toList();

        List<RecentActivityResponse> recentActivity = records.stream()
                .sorted(Comparator.comparing(FinancialRecord::getCreatedAt).reversed())
                .limit(5)
                .map(record -> new RecentActivityResponse(
                        record.getId(),
                        record.getType(),
                        record.getCategory(),
                        record.getAmount(),
                        record.getRecordDate(),
                        record.getNotes()
                ))
                .toList();

        return new DashboardSummaryResponse(income, expenses, net, categoryTotals, monthlyTrends, recentActivity);
    }

    private BigDecimal totalForType(List<FinancialRecord> records, RecordType type) {
        return records.stream()
                .filter(record -> record.getType() == type)
                .map(FinancialRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
