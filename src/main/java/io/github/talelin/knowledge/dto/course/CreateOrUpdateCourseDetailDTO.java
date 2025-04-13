package io.github.talelin.knowledge.dto.course;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class CreateOrUpdateCourseDetailDTO {
    private String content;
    private String hiddenContent;
    private Date createTime;
}
