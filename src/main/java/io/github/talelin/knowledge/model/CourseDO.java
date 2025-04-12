package io.github.talelin.knowledge.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("course")
public class CourseDO implements Serializable {
    private static final long serialVersionUID = -5585159202942077159L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String title;
    private String description;
    private String category;
    private String tag;
    private BigDecimal price;
    private String coverImage;
    @JsonIgnore
    private Date createTime;

    @JsonIgnore
    private Date updateTime;
}
