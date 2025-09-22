package co.com.powerup.ags.reports.model.approvedloanreport.gateways;

import co.com.powerup.ags.reports.model.approvedloanreport.ApprovedLoanReport;
import co.com.powerup.ags.reports.model.approvedloanreport.DailySummaryReport;
import reactor.core.publisher.Mono;

public interface ApprovedLoanTransactionRepository {
    
    Mono<Void> saveApprovedLoanWithSummaryUpdate(ApprovedLoanReport approvedLoanReport);
}