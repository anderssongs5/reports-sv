package co.com.powerup.ags.reports.dynamodb;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.math.BigDecimal;

/* Enhanced DynamoDB annotations are incompatible with Lombok #1932
         https://github.com/aws/aws-sdk-java-v2/issues/1932*/
@DynamoDbBean
public class GlobalSummaryEntity {

    private String pk;
    private BigDecimal totalAmount;
    private Long totalCount;
    
    public GlobalSummaryEntity() {
        super();
    }
    
    public GlobalSummaryEntity(String pk, BigDecimal totalAmount, Long totalCount) {
        this.pk = pk;
        this.totalAmount = totalAmount;
        this.totalCount = totalCount;
    }
    
    @DynamoDbPartitionKey
    @DynamoDbAttribute("PK")
    public String getPk() {
        return pk;
    }
    
    @DynamoDbAttribute("totalAmount")
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    @DynamoDbAttribute("totalCount")
    public Long getTotalCount() {
        return totalCount;
    }
    
    public void setPk(String pk) {
        this.pk = pk;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }
}
