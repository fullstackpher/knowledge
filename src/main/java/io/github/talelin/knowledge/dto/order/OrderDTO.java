package io.github.talelin.knowledge.dto.order;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class OrderDTO {
    private Integer userId;
    private Long courseId;
    private String status;
    private Date createTime;
}
