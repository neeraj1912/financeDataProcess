package com.finance.dashboard.dto.dashboard;

import java.math.BigDecimal;
import java.util.List;

public record DashboardSummaryResponse(
        BigDecimal totalIncome,
        BigDecimal totalExpenses,
        BigDecimal netBalance,
        List<CategoryTotalResponse> categoryTotals,
        List<TrendPointResponse> monthlyTrends,
        List<RecentActivityResponse> recentActivity
) {
}
