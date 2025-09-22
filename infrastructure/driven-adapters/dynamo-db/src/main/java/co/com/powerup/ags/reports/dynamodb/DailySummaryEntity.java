package co.com.powerup.ags.reports.dynamodb;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@DynamoDbBean
public class DailySummaryEntity {
    
    private String pk;
    private String gsi1PK;
    private String gsi1SK;
    private Integer totalCount;
    private BigDecimal totalAmount;
    private BigDecimal averageAmount;
    private Instant lastUpdated;
    
    public DailySummaryEntity() {
        super();
    }
    
    public DailySummaryEntity(String pk, String gsi1PK, String gsi1SK, Integer totalCount, BigDecimal totalAmount,
                              BigDecimal averageAmount, Instant lastUpdated) {
        this.pk = pk;
        this.gsi1PK = gsi1PK;
        this.gsi1SK = gsi1SK;
        this.totalCount = totalCount;
        this.totalAmount = totalAmount;
        this.averageAmount = averageAmount;
        this.lastUpdated = lastUpdated;
    }
    
    @DynamoDbPartitionKey
    @DynamoDbAttribute("PK")
    public String getPk() {
        return pk;
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
    
    @DynamoDbAttribute("totalCount")
    public Integer getTotalCount() {
        return totalCount;
    }
    
    @DynamoDbAttribute("totalAmount")
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    @DynamoDbAttribute("averageAmount")
    public BigDecimal getAverageAmount() {
        return averageAmount;
    }
    
    @DynamoDbAttribute("lastUpdated")
    public Instant getLastUpdated() {
        return lastUpdated;
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
    
    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public void setAverageAmount(BigDecimal averageAmount) {
        this.averageAmount = averageAmount;
    }
    
    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private String pk;
        private String gsi1PK;
        private String gsi1SK;
        private Integer totalCount;
        private BigDecimal totalAmount;
        private BigDecimal averageAmount;
        private Instant lastUpdated;
        
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
        
        public Builder totalCount(Integer totalCount) {
            this.totalCount = totalCount;
            return this;
        }
        
        public Builder totalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }
        
        public Builder averageAmount(BigDecimal averageAmount) {
            this.averageAmount = averageAmount;
            return this;
        }
        
        public Builder lastUpdated(Instant lastUpdated) {
            this.lastUpdated = lastUpdated;
            return this;
        }
        
        public DailySummaryEntity build() {
            return new DailySummaryEntity(pk, gsi1PK, gsi1SK, totalCount, totalAmount, averageAmount, lastUpdated);
        }
    }
    
    public static DailySummaryEntity forDate(LocalDate date) {
        String dateKey = date.toString();
        String monthKey = date.toString().substring(0, 7); // YYYY-MM
        String dayKey = String.format("%02d", date.getDayOfMonth());
        
        return DailySummaryEntity.builder()
                .pk("DATE#" + dateKey)
                .gsi1PK("MONTH#" + monthKey)
                .gsi1SK("DAY#" + dayKey)
                .totalCount(0)
                .totalAmount(BigDecimal.ZERO)
                .averageAmount(BigDecimal.ZERO)
                .lastUpdated(Instant.now())
                .build();
    }
}
