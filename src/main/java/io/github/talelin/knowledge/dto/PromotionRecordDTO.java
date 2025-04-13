package io.github.talelin.knowledge.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
public class PromotionRecordDTO {
    private Integer userId;
    private Integer referrerId;
    private BigDecimal commission;
    private Date createTime;
}
