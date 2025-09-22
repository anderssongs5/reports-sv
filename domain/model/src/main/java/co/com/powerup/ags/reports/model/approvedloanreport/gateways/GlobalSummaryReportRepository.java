package co.com.powerup.ags.reports.model.approvedloanreport.gateways;

import co.com.powerup.ags.reports.model.approvedloanreport.GlobalSummaryReport;
import reactor.core.publisher.Mono;

public interface GlobalSummaryReportRepository {
    
    Mono<GlobalSummaryReport> getGlobalSummary(String loanStatusName);
}
