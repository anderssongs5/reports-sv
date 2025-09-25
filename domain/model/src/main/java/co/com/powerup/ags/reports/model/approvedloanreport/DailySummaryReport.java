package co.com.powerup.ags.reports.model.approvedloanreport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class DailySummaryReport {

    private Long totalCount;
    private BigDecimal totalAmount;
    private BigDecimal averageAmount;
    private Instant lastUpdated;
}
