package io.github.talelin.knowledge.service;

import io.github.talelin.knowledge.dto.course.CreateOrUpdateCourseDTO;
import io.github.talelin.knowledge.model.CourseDO;
import io.github.talelin.knowledge.model.CourseDetailDO;

import java.util.List;

public interface CourseService {
    Boolean createCourse(CreateOrUpdateCourseDTO validator);

    List<CourseDO> findAll();

    List<CourseDetailDO> findDetailAll();

    CourseDetailDO findDetailById(Integer id);
}
