package io.github.talelin.knowledge.service.impl;

import io.github.talelin.knowledge.dto.course.CreateOrUpdateCourseDetailDTO;
import io.github.talelin.knowledge.dto.course.CreateOrUpdateCourseDTO;
import io.github.talelin.knowledge.mapper.CourseMapper;
import io.github.talelin.knowledge.mapper.CourseDetailMapper;
import io.github.talelin.knowledge.model.CourseDO;
import io.github.talelin.knowledge.model.CourseDetailDO;
import io.github.talelin.knowledge.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CourseDetailMapper courseDetailMapper;

    @Transactional
    @Override
    public Boolean createCourse(CreateOrUpdateCourseDTO validator) {
        CourseDO courseDO = new CourseDO();
        courseDO.setId(validator.getId());
        courseDO.setTitle(validator.getTitle());
        courseDO.setDescription(validator.getDescription());
        courseDO.setPrice(validator.getPrice());
        courseDO.setCoverImage(validator.getCoverImage());
        courseDO.setCategory(validator.getCategory());
        courseDO.setTag(validator.getTag());
        courseDO.setCreateTime(new Date());
        boolean courseCreated = courseMapper.insert(courseDO) > 0;

        if (courseCreated) {
            CreateOrUpdateCourseDetailDTO detailDTO = validator.getCourseDetail();
            CourseDetailDO courseDetailDO = new CourseDetailDO();
            courseDetailDO.setId(courseDO.getId());
            courseDetailDO.setCourseId(courseDO.getId());
            courseDetailDO.setContent(detailDTO.getContent());
            courseDetailDO.setHiddenContent(detailDTO.getHiddenContent());
            courseDetailDO.setCreateTime(new Date());
            return courseDetailMapper.insert(courseDetailDO) > 0;
        }
        return false;
    }

    @Override
    public List<CourseDO> findAll() {
        return courseMapper.selectList(null);
    }

    @Override
    public List<CourseDetailDO> findDetailAll() {
        return courseDetailMapper.selectList(null);
    }
}