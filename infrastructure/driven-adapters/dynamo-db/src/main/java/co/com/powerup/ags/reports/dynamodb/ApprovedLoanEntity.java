package co.com.powerup.ags.reports.dynamodb;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

/* Enhanced DynamoDB annotations are incompatible with Lombok #1932
         https://github.com/aws/aws-sdk-java-v2/issues/1932*/
@DynamoDbBean
public class ApprovedLoanEntity {

    private String pk;
    private String gsi1PK;
    private String gsi1SK;
    private String loanId;
    private BigDecimal amount;
    private Instant approvedDate;
    
    public ApprovedLoanEntity() {
        super();
    }
    
    public ApprovedLoanEntity(String pk, String gsi1PK, String gsi1SK, String loanId, BigDecimal amount,
                              Instant approvedDate) {
        this.pk = pk;
        this.gsi1PK = gsi1PK;
        this.gsi1SK = gsi1SK;
        this.loanId = loanId;
        this.amount = amount;
        this.approvedDate = approvedDate;
    }
    
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
    
    public void setPk(String pk) {
        this.pk = pk;
    }
    
    public void setGsi1PK(String gsi1PK) {
        this.gsi1PK = gsi1PK;
    }
    
    public void setGsi1SK(String gsi1SK) {
        this.gsi1SK = gsi1SK;
    }
    
    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public void setApprovedDate(Instant approvedDate) {
        this.approvedDate = approvedDate;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String pk;
        private String gsi1PK;
        private String gsi1SK;
        private String loanId;
        private BigDecimal amount;
        private Instant approvedDate;
        
        public Builder pk(String pk) {
            this.pk = pk;
            return this;
        }
        
        public Builder gsi1PK(String gsi1PK) {
            this.gsi1PK = gsi1PK;
            return this;
        }
        
        public Builder gsi1SK(String gsi1SK) {
            this.gsi1SK = gsi1SK;
            return this;
        }
        
        public Builder loanId(String loanId) {
            this.loanId = loanId;
            return this;
        }
        
        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }
        
        public Builder approvedDate(Instant approvedDate) {
            this.approvedDate = approvedDate;
            return this;
        }
        
        public ApprovedLoanEntity build() {
            return new ApprovedLoanEntity(pk, gsi1PK, gsi1SK, loanId, amount, approvedDate);
        }
    }
    
    public static ApprovedLoanEntity fromMessage(String loanId, BigDecimal amount, Instant approvedDate) {
        String dateKey = LocalDate.ofInstant(approvedDate, ZoneOffset.UTC).toString();
        String amountKey = String.format("AMOUNT#%012d", amount.longValue());
        
        return ApprovedLoanEntity.builder()
                .pk("LOAN#" + loanId)
                .gsi1PK("DATE#" + dateKey)
                .gsi1SK(amountKey)
                .loanId(loanId)
                .amount(amount)
                .approvedDate(approvedDate)
                .build();
    }
}
