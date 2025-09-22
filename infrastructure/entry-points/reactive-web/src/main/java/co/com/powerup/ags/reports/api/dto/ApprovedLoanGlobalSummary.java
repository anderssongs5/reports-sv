package co.com.powerup.ags.reports.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Loan approved summary response")
public class ApprovedLoanGlobalSummary {
    
    @Schema(description = "Total number of approved loans", example = "150")
    public Long totalCount;
    
    @Schema(description = "Total amount of all approved loans", example = "2500000.50")
    public BigDecimal totalAmount;
}
