package com.yourschool.campussystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yourschool.campussystem.common.ErrorCode;
import com.yourschool.campussystem.dto.CourseTableDTO;
import com.yourschool.campussystem.entity.CourseTable;
import com.yourschool.campussystem.exception.BusinessException;
import com.yourschool.campussystem.mapper.CourseTableMapper;
import com.yourschool.campussystem.service.CourseTableService;
import com.yourschool.campussystem.vo.CourseTableVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 课程表服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseTableServiceImpl extends ServiceImpl<CourseTableMapper, CourseTable> implements CourseTableService {

    private final CourseTableMapper courseTableMapper;

    @Override
    @Transactional
    public CourseTableVO createCourse(Long userId, CourseTableDTO courseDTO) {
        CourseTable course = new CourseTable();
        BeanUtils.copyProperties(courseDTO, course);
        course.setUserId(userId);
        course.setSource(courseDTO.getSource() != null ? courseDTO.getSource() : "MANUAL");
        
        courseTableMapper.insert(course);
        return convertToVO(course);
    }

    @Override
    public List<CourseTableVO> getCoursesByUserId(Long userId) {
        LambdaQueryWrapper<CourseTable> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTable::getUserId, userId)
                .eq(CourseTable::getIsDeleted, 0)
                .orderByAsc(CourseTable::getDayOfWeek)
                .orderByAsc(CourseTable::getStartTime);
        
        List<CourseTable> courses = courseTableMapper.selectList(queryWrapper);
        return courses.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public List<CourseTableVO> getCoursesByDayOfWeek(Long userId, String dayOfWeek) {
        LambdaQueryWrapper<CourseTable> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CourseTable::getUserId, userId)
                .eq(CourseTable::getDayOfWeek, dayOfWeek)
                .eq(CourseTable::getIsDeleted, 0)
                .orderByAsc(CourseTable::getStartTime);
        
        List<CourseTable> courses = courseTableMapper.selectList(queryWrapper);
        return courses.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public CourseTableVO getCourseDetail(Long userId, Long courseId) {
        CourseTable course = courseTableMapper.selectOne(
                new LambdaQueryWrapper<CourseTable>()
                        .eq(CourseTable::getId, courseId)
                        .eq(CourseTable::getUserId, userId)
                        .eq(CourseTable::getIsDeleted, 0)
        );
        
        if (course == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "课程不存在");
        }
        
        return convertToVO(course);
    }

    @Override
    @Transactional
    public CourseTableVO updateCourse(Long userId, Long courseId, CourseTableDTO courseDTO) {
        CourseTable course = courseTableMapper.selectOne(
                new LambdaQueryWrapper<CourseTable>()
                        .eq(CourseTable::getId, courseId)
                        .eq(CourseTable::getUserId, userId)
                        .eq(CourseTable::getIsDeleted, 0)
        );
        
        if (course == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "课程不存在");
        }
        
        BeanUtils.copyProperties(courseDTO, course);
        courseTableMapper.updateById(course);
        
        return convertToVO(course);
    }

    @Override
    @Transactional
    public void deleteCourse(Long userId, Long courseId) {
        CourseTable course = courseTableMapper.selectOne(
                new LambdaQueryWrapper<CourseTable>()
                        .eq(CourseTable::getId, courseId)
                        .eq(CourseTable::getUserId, userId)
                        .eq(CourseTable::getIsDeleted, 0)
        );
        
        if (course == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "课程不存在");
        }
        
        courseTableMapper.deleteById(courseId);
    }

    @Override
    @Transactional
    public List<CourseTableVO> batchImportCourses(Long userId, List<CourseTableDTO> courseList) {
        List<CourseTable> courses = courseList.stream().map(dto -> {
            CourseTable course = new CourseTable();
            BeanUtils.copyProperties(dto, course);
            course.setUserId(userId);
            course.setSource("AUTO");
            return course;
        }).collect(Collectors.toList());
        
        for (CourseTable course : courses) {
            courseTableMapper.insert(course);
        }
        
        return courses.stream().map(this::convertToVO).collect(Collectors.toList());
    }

    /**
     * 转换为VO
     */
    private CourseTableVO convertToVO(CourseTable course) {
        CourseTableVO vo = new CourseTableVO();
        BeanUtils.copyProperties(course, vo);
        return vo;
    }
}

