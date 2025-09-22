package co.com.powerup.ags.reports.model.approvedloanreport;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class ApprovedLoanReport {
    
    private String loanId;
    private BigDecimal amount;
    private Instant approvedDate;
}
