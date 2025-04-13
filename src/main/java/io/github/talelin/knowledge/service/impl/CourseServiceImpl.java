package io.github.talelin.knowledge.service.impl;

import io.github.talelin.knowledge.common.util.BeanCopyUtil;
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
        CourseDO courseDO = BeanCopyUtil.copyProperties(validator, CourseDO::new);
        courseDO.setCreateTime(new Date());
        boolean courseCreated = courseMapper.insert(courseDO) > 0;

        if (courseCreated) {
            CourseDetailDO courseDetailDO = BeanCopyUtil.copyProperties(validator.getCourseDetail(), CourseDetailDO::new);
            courseDetailDO.setCourseId(courseDO.getId());
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

    @Override
    public CourseDetailDO findDetailById(Integer id) {
        return courseDetailMapper.selectById(id);
    }
}