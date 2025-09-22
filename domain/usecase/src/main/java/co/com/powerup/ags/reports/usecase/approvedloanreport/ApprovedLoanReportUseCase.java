package co.com.powerup.ags.reports.usecase.approvedloanreport;

import co.com.powerup.ags.reports.model.approvedloanreport.ApprovedLoanReport;
import co.com.powerup.ags.reports.model.approvedloanreport.gateways.ApprovedLoanTransactionRepository;
import co.com.powerup.ags.reports.model.approvedloanreport.gateways.GlobalSummaryReportRepository;
import co.com.powerup.ags.reports.usecase.approvedloanreport.dto.AddLoanApprovedCommand;
import co.com.powerup.ags.reports.usecase.approvedloanreport.dto.ApprovedLoanGlobalSummary;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ApprovedLoanReportUseCase {
    
    private static final String APPROVED = "APPROVED";
    private final ApprovedLoanTransactionRepository transactionRepository;
    private final GlobalSummaryReportRepository globalSummaryReportRepository;

    public Mono<Void> processApprovedLoan(AddLoanApprovedCommand command) {
        ApprovedLoanReport loanReport = ApprovedLoanReport.builder()
                .loanId(command.loanId())
                .amount(command.amount())
                .approvedDate(command.approvedDate())
                .build();
        
        return transactionRepository.saveApprovedLoanWithSummaryUpdate(loanReport);
    }
    
    public Mono<ApprovedLoanGlobalSummary> getApprovedLoanGlobalSummary() {
        return globalSummaryReportRepository.getGlobalSummary(APPROVED)
                .map(summary -> new ApprovedLoanGlobalSummary(summary.totalCount(), summary.totalAmount()));
    }
}
