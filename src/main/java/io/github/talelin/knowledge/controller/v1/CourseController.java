package io.github.talelin.knowledge.controller.v1;

import io.github.talelin.knowledge.dto.course.CreateOrUpdateCourseDTO;
import io.github.talelin.knowledge.model.CourseDO;
import io.github.talelin.knowledge.model.CourseDetailDO;
import io.github.talelin.knowledge.service.CourseService;
import io.github.talelin.knowledge.vo.CreatedVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/course")
@Validated
public class CourseController {
    @Autowired
    private CourseService courseService;

    @PostMapping("/create")
    public CreatedVO createCourse(@RequestBody @Validated CreateOrUpdateCourseDTO validator) {
        courseService.createCourse(validator);
        return new CreatedVO("创建课程成功");
    }

    @GetMapping("/list")
    public List<CourseDO> getCourseList() {
        return courseService.findAll();
    }

    @GetMapping("/details")
    public List<CourseDetailDO> getCourseDetailList() {
        return courseService.findDetailAll();
    }

    @GetMapping("/detail/{id}")
    public CourseDetailDO getCourseDetail(@PathVariable Integer id) {
        return courseService.findDetailById(id);
    }
}
