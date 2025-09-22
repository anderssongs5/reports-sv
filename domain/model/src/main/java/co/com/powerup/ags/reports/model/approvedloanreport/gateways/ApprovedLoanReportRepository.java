package co.com.powerup.ags.reports.model.approvedloanreport.gateways;

import co.com.powerup.ags.reports.model.approvedloanreport.ApprovedLoanReport;
import reactor.core.publisher.Mono;

public interface ApprovedLoanReportRepository {
    
    Mono<ApprovedLoanReport> saveApprovedLoan(ApprovedLoanReport approvedLoanReport);
}
