package io.github.talelin.knowledge.spider;

import io.github.talelin.knowledge.dto.course.CreateOrUpdateCourseDTO;
import io.github.talelin.knowledge.service.CourseService;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

public class ArticlePipeline implements Pipeline {

    private final CourseService courseService;

    public ArticlePipeline(CourseService courseService) {
        this.courseService = courseService;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        CreateOrUpdateCourseDTO courseDTO = resultItems.get("courseDTO");
        if (courseDTO != null) {
            courseService.createCourse(courseDTO);
        }
    }
}