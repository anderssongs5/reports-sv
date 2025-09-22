package co.com.powerup.ags.reports.usecase.approvedloanreport.dto;

import java.math.BigDecimal;

public record ApprovedLoanGlobalSummary(Long totalCount, BigDecimal totalAmount) {
}
