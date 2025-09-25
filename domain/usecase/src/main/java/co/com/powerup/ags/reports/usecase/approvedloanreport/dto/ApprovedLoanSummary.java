package co.com.powerup.ags.reports.usecase.approvedloanreport.dto;

import java.math.BigDecimal;

public record ApprovedLoanSummary(Long totalCount, BigDecimal totalAmount) {
}
