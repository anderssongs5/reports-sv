package co.com.powerup.ags.reports.model.approvedloanreport.gateways;

import co.com.powerup.ags.reports.model.approvedloanreport.DailySummaryReport;
import reactor.core.publisher.Mono;

public interface DailySummaryReportRepository {
    
    Mono<DailySummaryReport> getDailySummary(String date);
}
