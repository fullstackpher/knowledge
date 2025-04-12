package io.github.talelin.knowledge.dto.course;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
public class CreateOrUpdateCourseDTO {
    private Long id;
    private String title;
    private String description;
    private String coverImage;
    private BigDecimal price;
    private String category;
    private String tag;
    private Date createTime;
    private CreateOrUpdateCourseDetailDTO courseDetail;
}
