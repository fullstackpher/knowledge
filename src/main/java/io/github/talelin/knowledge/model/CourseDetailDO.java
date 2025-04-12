package io.github.talelin.knowledge.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("course_detail")
public class CourseDetailDO implements Serializable {
    private static final long serialVersionUID = -4843817652240904217L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long courseId;
    private String content;
    private String hiddenContent;
    @JsonIgnore
    private Date createTime;

    @JsonIgnore
    private Date updateTime;
}
