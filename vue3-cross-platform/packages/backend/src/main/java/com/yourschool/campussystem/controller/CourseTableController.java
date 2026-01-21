package com.yourschool.campussystem.controller;

import com.yourschool.campussystem.common.ApiResponse;
import com.yourschool.campussystem.dto.CourseTableDTO;
import com.yourschool.campussystem.service.CourseTableService;
import com.yourschool.campussystem.util.UserContextUtils;
import com.yourschool.campussystem.vo.CourseTableVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程表管理控制器
 */
@RestController
@RequestMapping("/api/course-table")
@Tag(name = "课程表管理", description = "课程表的增删改查、导入导出等功能")
@RequiredArgsConstructor
public class CourseTableController {

    private final CourseTableService courseTableService;

    @Operation(summary = "创建课程", description = "创建新的课程记录")
    @PostMapping("/create")
    public ApiResponse<CourseTableVO> createCourse(
            HttpServletRequest request,
            @Parameter(description = "课程信息", required = true)
            @Valid @RequestBody CourseTableDTO courseDTO) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        CourseTableVO courseVO = courseTableService.createCourse(userId, courseDTO);
        return ApiResponse.success("课程创建成功", courseVO);
    }

    @Operation(summary = "获取用户的所有课程", description = "获取当前用户的所有课程列表")
    @GetMapping("/list")
    public ApiResponse<List<CourseTableVO>> getCourses(HttpServletRequest request) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        List<CourseTableVO> courses = courseTableService.getCoursesByUserId(userId);
        return ApiResponse.success("查询成功", courses);
    }

    @Operation(summary = "根据星期获取课程", description = "获取指定星期的课程列表")
    @GetMapping("/day/{dayOfWeek}")
    public ApiResponse<List<CourseTableVO>> getCoursesByDay(
            HttpServletRequest request,
            @Parameter(description = "星期（周一、周二...周日）", example = "周一", required = true)
            @PathVariable String dayOfWeek) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        List<CourseTableVO> courses = courseTableService.getCoursesByDayOfWeek(userId, dayOfWeek);
        return ApiResponse.success("查询成功", courses);
    }

    @Operation(summary = "获取课程详情", description = "获取指定课程的详细信息")
    @GetMapping("/detail/{id}")
    public ApiResponse<CourseTableVO> getCourseDetail(
            HttpServletRequest request,
            @Parameter(description = "课程ID", example = "1", required = true)
            @PathVariable Long id) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        CourseTableVO courseVO = courseTableService.getCourseDetail(userId, id);
        return ApiResponse.success("查询成功", courseVO);
    }

    @Operation(summary = "更新课程", description = "更新课程信息")
    @PutMapping("/update/{id}")
    public ApiResponse<CourseTableVO> updateCourse(
            HttpServletRequest request,
            @Parameter(description = "课程ID", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "课程信息", required = true)
            @Valid @RequestBody CourseTableDTO courseDTO) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        CourseTableVO courseVO = courseTableService.updateCourse(userId, id, courseDTO);
        return ApiResponse.success("课程更新成功", courseVO);
    }

    @Operation(summary = "删除课程", description = "删除指定课程")
    @DeleteMapping("/delete/{id}")
    public ApiResponse<String> deleteCourse(
            HttpServletRequest request,
            @Parameter(description = "课程ID", example = "1", required = true)
            @PathVariable Long id) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        courseTableService.deleteCourse(userId, id);
        return ApiResponse.success("课程删除成功");
    }

    @Operation(summary = "批量导入课程", description = "批量导入课程列表")
    @PostMapping("/batch-import")
    public ApiResponse<List<CourseTableVO>> batchImportCourses(
            HttpServletRequest request,
            @Parameter(description = "课程列表", required = true)
            @Valid @RequestBody List<CourseTableDTO> courseList) {
        Long userId = UserContextUtils.getUserIdRequired(request);
        List<CourseTableVO> courses = courseTableService.batchImportCourses(userId, courseList);
        return ApiResponse.success("批量导入成功", courses);
    }
}

