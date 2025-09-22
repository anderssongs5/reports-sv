package co.com.powerup.ags.reports.dynamodb;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

/* Enhanced DynamoDB annotations are incompatible with Lombok #1932
         https://github.com/aws/aws-sdk-java-v2/issues/1932*/
@DynamoDbBean
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovedLoanRecord {

    private String pk;
    private String gsi1PK;
    private String gsi1SK;
    private String loanId;
    private BigDecimal amount;
    private Instant approvedDate;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("PK")
    public String getPk() {
        return pk;
    }

    @DynamoDbAttribute("amount")
    public BigDecimal getAmount() {
        return amount;
    }
    
    @DynamoDbSecondaryPartitionKey(indexNames = "GSI1")
    @DynamoDbAttribute("GSI1PK")
    public String getGsi1PK() {
        return gsi1PK;
    }
    
    @DynamoDbSecondarySortKey(indexNames = "GSI1")
    @DynamoDbAttribute("GSI1SK")
    public String getGsi1SK() {
        return gsi1SK;
    }
    
    @DynamoDbAttribute("loanId")
    public String getLoanId() {
        return loanId;
    }
    
    @DynamoDbAttribute("approvedDate")
    public Instant getApprovedDate() {
        return approvedDate;
    }
    
    public static ApprovedLoanRecord fromMessage(String loanId, BigDecimal amount, Instant approvedDate) {
        String dateKey = LocalDate.ofInstant(approvedDate, ZoneOffset.UTC).toString();
        String amountKey = String.format("AMOUNT#%012d", amount.longValue());
        
        return ApprovedLoanRecord.builder()
                .pk("LOAN#" + loanId)
                .gsi1PK("DATE#" + dateKey)
                .gsi1SK(amountKey)
                .loanId(loanId)
                .amount(amount)
                .approvedDate(approvedDate)
                .build();
    }
}
