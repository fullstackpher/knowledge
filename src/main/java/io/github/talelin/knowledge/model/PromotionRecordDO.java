package io.github.talelin.knowledge.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("promotion_record")
public class PromotionRecordDO {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Integer userId;
    private Integer referrerId;
    private BigDecimal commission;
    private Date createTime;
    private Date updateTime;
}
