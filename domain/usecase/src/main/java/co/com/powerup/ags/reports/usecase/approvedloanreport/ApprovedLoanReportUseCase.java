package co.com.powerup.ags.reports.usecase.approvedloanreport;

import co.com.powerup.ags.reports.model.approvedloanreport.ApprovedLoanReport;
import co.com.powerup.ags.reports.model.approvedloanreport.gateways.ApprovedLoanTransactionRepository;
import co.com.powerup.ags.reports.model.approvedloanreport.gateways.DailySummaryReportRepository;
import co.com.powerup.ags.reports.model.approvedloanreport.gateways.GlobalSummaryReportRepository;
import co.com.powerup.ags.reports.usecase.approvedloanreport.dto.AddLoanApprovedCommand;
import co.com.powerup.ags.reports.usecase.approvedloanreport.dto.ApprovedLoanSummary;
import co.com.powerup.ags.reports.usecase.approvedloanreport.dto.SummaryReport;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class ApprovedLoanReportUseCase {
    
    private static final String APPROVED = "APPROVED";
    private final ApprovedLoanTransactionRepository transactionRepository;
    private final GlobalSummaryReportRepository globalSummaryReportRepository;
    private final DailySummaryReportRepository dailySummaryReportRepository;

    public Mono<Void> processApprovedLoan(AddLoanApprovedCommand command) {
        ApprovedLoanReport loanReport = ApprovedLoanReport.builder()
                .loanId(command.loanId())
                .amount(command.amount())
                .approvedDate(command.approvedDate())
                .build();
        
        return transactionRepository.saveApprovedLoanWithSummaryUpdate(loanReport);
    }
    
    public Mono<ApprovedLoanSummary> getApprovedLoanGlobalSummary() {
        return globalSummaryReportRepository.getGlobalSummary(APPROVED)
                .map(summary -> new ApprovedLoanSummary(summary.totalCount(), summary.totalAmount()));
    }
    
    /*public Mono<SummaryReport> getSummaryReport() {
        return globalSummaryReportRepository.getGlobalSummary(APPROVED)
                .map(summary -> new ApprovedLoanSummary(summary.totalCount(), summary.totalAmount()))
                .flatMap(globalSummary -> {
                    String currentDate = LocalDate.now().toString();
                    return dailySummaryReportRepository.getDailySummary(currentDate)
                            .map(dailySummary -> {
                                var dailySum = new ApprovedLoanSummary(dailySummary.getTotalCount(), dailySummary.getTotalAmount());
                                return new SummaryReport(globalSummary, dailySum);
                            })
                            .defaultIfEmpty(new SummaryReport(globalSummary, new ApprovedLoanSummary(0L, BigDecimal.ZERO)));
                });
    }*/
}
