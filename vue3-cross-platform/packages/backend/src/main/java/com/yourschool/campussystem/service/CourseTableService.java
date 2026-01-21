package com.yourschool.campussystem.service;

import com.yourschool.campussystem.dto.CourseTableDTO;
import com.yourschool.campussystem.vo.CourseTableVO;

import java.util.List;

/**
 * 课程表服务接口
 */
public interface CourseTableService {

    /**
     * 创建课程
     */
    CourseTableVO createCourse(Long userId, CourseTableDTO courseDTO);

    /**
     * 获取用户的课程列表
     */
    List<CourseTableVO> getCoursesByUserId(Long userId);

    /**
     * 根据星期获取课程列表
     */
    List<CourseTableVO> getCoursesByDayOfWeek(Long userId, String dayOfWeek);

    /**
     * 获取课程详情
     */
    CourseTableVO getCourseDetail(Long userId, Long courseId);

    /**
     * 更新课程
     */
    CourseTableVO updateCourse(Long userId, Long courseId, CourseTableDTO courseDTO);

    /**
     * 删除课程
     */
    void deleteCourse(Long userId, Long courseId);

    /**
     * 批量导入课程
     */
    List<CourseTableVO> batchImportCourses(Long userId, List<CourseTableDTO> courseList);
}

