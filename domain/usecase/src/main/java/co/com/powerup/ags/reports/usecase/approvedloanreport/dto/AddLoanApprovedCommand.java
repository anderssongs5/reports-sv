package co.com.powerup.ags.reports.usecase.approvedloanreport.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record AddLoanApprovedCommand (String loanId, BigDecimal amount, Instant approvedDate) {
}
