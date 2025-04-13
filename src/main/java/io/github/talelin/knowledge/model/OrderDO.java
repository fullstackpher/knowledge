package io.github.talelin.knowledge.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("order")
public class OrderDO implements Serializable {
    private static final long serialVersionUID = -6158485976609186776L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Integer userId;
    private Long courseId;
    private String status;
    private Date createTime;
    @JsonIgnore
    private Date updateTime;
}
