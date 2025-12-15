package com.yourschool.campussystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourschool.campussystem.dto.ParttimeApplyDTO;
import com.yourschool.campussystem.dto.ParttimePublishDTO;
import com.yourschool.campussystem.dto.ParttimeQueryDTO;
import com.yourschool.campussystem.entity.BrowseHistory;
import com.yourschool.campussystem.entity.Favorite;
import com.yourschool.campussystem.entity.Parttime;
import com.yourschool.campussystem.entity.ParttimeApply;
import com.yourschool.campussystem.entity.User;
import com.yourschool.campussystem.entity.University;
import com.yourschool.campussystem.enums.ApplyStatusEnum;
import com.yourschool.campussystem.enums.ParttimeStatusEnum;
import com.yourschool.campussystem.common.ErrorCode;
import com.yourschool.campussystem.exception.BusinessException;
import com.yourschool.campussystem.mapper.BrowseHistoryMapper;
import com.yourschool.campussystem.mapper.FavoriteMapper;
import com.yourschool.campussystem.mapper.ParttimeApplyMapper;
import com.yourschool.campussystem.mapper.ParttimeMapper;
import com.yourschool.campussystem.mapper.UserMapper;
import com.yourschool.campussystem.mapper.UniversityMapper;
import com.yourschool.campussystem.service.ParttimeService;
import com.yourschool.campussystem.vo.ApplyRecordVO;
import com.yourschool.campussystem.vo.ParttimeDetailVO;
import com.yourschool.campussystem.vo.ParttimeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 兼职服务实现类
 */
@Service
@RequiredArgsConstructor
public class ParttimeServiceImpl extends ServiceImpl<ParttimeMapper, Parttime> implements ParttimeService {

    private final ParttimeMapper parttimeMapper;
    private final ParttimeApplyMapper applyMapper;
    private final FavoriteMapper favoriteMapper;
    private final BrowseHistoryMapper browseHistoryMapper;
    private final UserMapper userMapper;
    private final UniversityMapper universityMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public ParttimeVO publishParttime(Long userId, ParttimePublishDTO publishDTO) {
        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 创建兼职
        Parttime parttime = new Parttime();
        parttime.setTitle(publishDTO.getTitle());
        parttime.setDescription(publishDTO.getDescription());
        parttime.setSalaryPerHour(publishDTO.getSalaryPerHour());
        parttime.setRecruitCount(publishDTO.getRecruitCount());
        parttime.setAppliedCount(0);
        parttime.setWorkStartTime(publishDTO.getWorkStartTime());
        parttime.setWorkEndTime(publishDTO.getWorkEndTime());
        parttime.setLocation(publishDTO.getLocation());
        parttime.setRequirements(publishDTO.getRequirements());
        parttime.setStatus(publishDTO.getStatus() != null ? publishDTO.getStatus() : ParttimeStatusEnum.RECRUITING);
        parttime.setPublisherId(userId);
        parttime.setSchoolId(user.getSchoolId());
        parttime.setContactName(publishDTO.getContactName());
        parttime.setContactPhone(publishDTO.getContactPhone());

        // 转换图片URL列表为JSON字符串
        if (publishDTO.getImageUrls() != null && !publishDTO.getImageUrls().isEmpty()) {
            try {
                parttime.setImageUrls(objectMapper.writeValueAsString(publishDTO.getImageUrls()));
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.BAD_REQUEST);
            }
        }

        parttimeMapper.insert(parttime);

        return convertToVO(parttime, user, null, null);
    }

    @Override
    public Map<String, Object> getParttimeList(ParttimeQueryDTO queryDTO, Long currentUserId) {
        // 构建查询条件
        LambdaQueryWrapper<Parttime> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Parttime::getIsDeleted, 0);

        // 学校筛选（同校可见）
        if (queryDTO.getSchoolId() != null) {
            queryWrapper.eq(Parttime::getSchoolId, queryDTO.getSchoolId());
        } else if (currentUserId != null) {
            // 如果未指定学校，游客只能看到同校的
            User currentUser = userMapper.selectById(currentUserId);
            if (currentUser != null && currentUser.getSchoolId() != null) {
                queryWrapper.eq(Parttime::getSchoolId, currentUser.getSchoolId());
            }
        }

        // 状态筛选
        if (queryDTO.getStatus() != null) {
            queryWrapper.eq(Parttime::getStatus, queryDTO.getStatus());
        } else {
            // 默认只显示招聘中的
            queryWrapper.eq(Parttime::getStatus, ParttimeStatusEnum.RECRUITING);
        }

        // 关键词搜索
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            queryWrapper.and(wrapper -> wrapper
                    .like(Parttime::getTitle, queryDTO.getKeyword())
                    .or()
                    .like(Parttime::getDescription, queryDTO.getKeyword())
            );
        }

        // 薪资范围筛选
        if (queryDTO.getMinSalary() != null) {
            queryWrapper.ge(Parttime::getSalaryPerHour, queryDTO.getMinSalary());
        }
        if (queryDTO.getMaxSalary() != null) {
            queryWrapper.le(Parttime::getSalaryPerHour, queryDTO.getMaxSalary());
        }

        // 工作时间筛选
        if (queryDTO.getWorkStartAfter() != null) {
            queryWrapper.ge(Parttime::getWorkStartTime, queryDTO.getWorkStartAfter());
        }
        if (queryDTO.getWorkEndBefore() != null) {
            queryWrapper.le(Parttime::getWorkEndTime, queryDTO.getWorkEndBefore());
        }

        // 排序
        if ("salaryPerHour".equals(queryDTO.getSortBy())) {
            if ("ASC".equals(queryDTO.getSortOrder())) {
                queryWrapper.orderByAsc(Parttime::getSalaryPerHour);
            } else {
                queryWrapper.orderByDesc(Parttime::getSalaryPerHour);
            }
        } else if ("workStartTime".equals(queryDTO.getSortBy())) {
            if ("ASC".equals(queryDTO.getSortOrder())) {
                queryWrapper.orderByAsc(Parttime::getWorkStartTime);
            } else {
                queryWrapper.orderByDesc(Parttime::getWorkStartTime);
            }
        } else {
            // 默认按创建时间倒序
            queryWrapper.orderByDesc(Parttime::getCreateTime);
        }

        // 分页查询
        Page<Parttime> pageObj = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        Page<Parttime> result = parttimeMapper.selectPage(pageObj, queryWrapper);

        // 转换为VO
        List<ParttimeVO> voList = result.getRecords().stream()
                .map(parttime -> {
                    User publisher = userMapper.selectById(parttime.getPublisherId());
                    University university = parttime.getSchoolId() != null ? 
                            universityMapper.selectById(parttime.getSchoolId()) : null;
                    
                    // 检查当前用户是否已报名
                    Boolean hasApplied = false;
                    if (currentUserId != null) {
                        LambdaQueryWrapper<ParttimeApply> applyQuery = new LambdaQueryWrapper<>();
                        applyQuery.eq(ParttimeApply::getParttimeId, parttime.getId())
                                .eq(ParttimeApply::getApplicantId, currentUserId);
                        hasApplied = applyMapper.selectCount(applyQuery) > 0;
                    }
                    
                    return convertToVO(parttime, publisher, university, hasApplied);
                })
                .collect(Collectors.toList());

        // 构建返回结果
        Map<String, Object> response = new HashMap<>();
        response.put("list", voList);
        response.put("page", queryDTO.getPage());
        response.put("size", queryDTO.getSize());
        response.put("total", result.getTotal());
        response.put("totalPages", result.getPages());

        return response;
    }

    @Override
    public ParttimeDetailVO getParttimeDetail(Long parttimeId, Long currentUserId) {
        // 查询兼职
        Parttime parttime = parttimeMapper.selectById(parttimeId);
        if (parttime == null || parttime.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.PARTTIME_NOT_EXIST);
        }

        // 查询发布者信息
        User publisher = userMapper.selectById(parttime.getPublisherId());
        if (publisher == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 查询学校信息
        University university = parttime.getSchoolId() != null ? 
                universityMapper.selectById(parttime.getSchoolId()) : null;

        // 查询当前用户是否已报名
        Boolean hasApplied = false;
        if (currentUserId != null) {
            LambdaQueryWrapper<ParttimeApply> applyQuery = new LambdaQueryWrapper<>();
            applyQuery.eq(ParttimeApply::getParttimeId, parttimeId)
                    .eq(ParttimeApply::getApplicantId, currentUserId);
            hasApplied = applyMapper.selectCount(applyQuery) > 0;
        }

        // 判断是否同校
        boolean isSameSchool = false;
        boolean isPublisher = false;
        if (currentUserId != null) {
            User currentUser = userMapper.selectById(currentUserId);
            isSameSchool = currentUser != null && 
                    currentUser.getSchoolId() != null && 
                    currentUser.getSchoolId().equals(parttime.getSchoolId());
            isPublisher = currentUserId.equals(parttime.getPublisherId());
        }

        // 解析图片URL列表
        List<String> imageUrls = new ArrayList<>();
        if (StringUtils.hasText(parttime.getImageUrls())) {
            try {
                imageUrls = objectMapper.readValue(parttime.getImageUrls(), new TypeReference<List<String>>() {});
            } catch (Exception e) {
                // 解析失败，使用空列表
            }
        }

        // 构建详情VO
        ParttimeDetailVO detailVO = new ParttimeDetailVO();
        detailVO.setId(parttime.getId());
        detailVO.setTitle(parttime.getTitle());
        detailVO.setDescription(parttime.getDescription());
        detailVO.setSalaryPerHour(parttime.getSalaryPerHour());
        detailVO.setRecruitCount(parttime.getRecruitCount());
        detailVO.setAppliedCount(parttime.getAppliedCount());
        detailVO.setWorkStartTime(parttime.getWorkStartTime());
        detailVO.setWorkEndTime(parttime.getWorkEndTime());
        detailVO.setLocation(parttime.getLocation());
        detailVO.setRequirements(parttime.getRequirements());
        detailVO.setStatus(parttime.getStatus());
        detailVO.setPublisherId(parttime.getPublisherId());
        detailVO.setPublisherName(publisher.getNickname() != null ? publisher.getNickname() : publisher.getUsername());
        detailVO.setPublisherAvatar(publisher.getAvatarUrl());
        detailVO.setSchoolId(parttime.getSchoolId());
        detailVO.setSchoolName(university != null ? university.getName() : null);
        detailVO.setContactName(parttime.getContactName());
        detailVO.setImageUrls(imageUrls);
        detailVO.setHasApplied(hasApplied);
        detailVO.setIsSameSchool(isSameSchool);
        detailVO.setIsPublisher(isPublisher);
        detailVO.setCreateTime(parttime.getCreateTime());
        detailVO.setUpdateTime(parttime.getUpdateTime());

        // 同校可见联系方式
        if (isSameSchool || isPublisher) {
            detailVO.setContactPhone(parttime.getContactPhone());
        }

        return detailVO;
    }

    @Override
    @Transactional
    public void applyParttime(Long userId, ParttimeApplyDTO applyDTO) {
        // 查询兼职
        Parttime parttime = parttimeMapper.selectById(applyDTO.getParttimeId());
        if (parttime == null || parttime.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.PARTTIME_NOT_EXIST);
        }

        // 检查状态
        if (parttime.getStatus() != ParttimeStatusEnum.RECRUITING) {
            throw new BusinessException(ErrorCode.PARTTIME_STATUS_ERROR);
        }

        // 检查是否已满员
        if (parttime.getAppliedCount() >= parttime.getRecruitCount()) {
            throw new BusinessException(ErrorCode.PARTTIME_RECRUIT_FULL);
        }

        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 检查是否同校
        if (user.getSchoolId() == null || !user.getSchoolId().equals(parttime.getSchoolId())) {
            throw new BusinessException(ErrorCode.PARTTIME_NOT_SAME_SCHOOL);
        }

        // 检查是否已报名
        LambdaQueryWrapper<ParttimeApply> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ParttimeApply::getParttimeId, applyDTO.getParttimeId())
                .eq(ParttimeApply::getApplicantId, userId);
        if (applyMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException(ErrorCode.PARTTIME_APPLY_DUPLICATE);
        }

        // 创建报名记录
        ParttimeApply apply = new ParttimeApply();
        apply.setParttimeId(applyDTO.getParttimeId());
        apply.setApplicantId(userId);
        apply.setApplicationNote(applyDTO.getApplicationNote());
        apply.setAvailableTime(applyDTO.getAvailableTime());
        apply.setStatus(ApplyStatusEnum.PENDING);
        applyMapper.insert(apply);

        // 更新报名人数
        parttime.setAppliedCount(parttime.getAppliedCount() + 1);
        parttimeMapper.updateById(parttime);
    }

    @Override
    @Transactional
    public void cancelApply(Long userId, Long applyId) {
        // 查询报名记录
        ParttimeApply apply = applyMapper.selectById(applyId);
        if (apply == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }

        // 检查权限
        if (!apply.getApplicantId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        // 只能取消待审核的报名
        if (apply.getStatus() != ApplyStatusEnum.PENDING) {
            throw new BusinessException(ErrorCode.PARTTIME_STATUS_ERROR);
        }

        // 删除报名记录
        applyMapper.deleteById(applyId);

        // 更新兼职报名人数
        Parttime parttime = parttimeMapper.selectById(apply.getParttimeId());
        if (parttime != null && parttime.getAppliedCount() > 0) {
            parttime.setAppliedCount(parttime.getAppliedCount() - 1);
            parttimeMapper.updateById(parttime);
        }
    }

    @Override
    public Map<String, Object> getMyApplications(Long userId, ApplyStatusEnum status, Integer page, Integer size) {
        LambdaQueryWrapper<ParttimeApply> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ParttimeApply::getApplicantId, userId);

        if (status != null) {
            queryWrapper.eq(ParttimeApply::getStatus, status);
        }

        queryWrapper.orderByDesc(ParttimeApply::getApplyTime);

        Page<ParttimeApply> pageObj = new Page<>(page, size);
        Page<ParttimeApply> result = applyMapper.selectPage(pageObj, queryWrapper);

        List<ApplyRecordVO> voList = result.getRecords().stream()
                .map(apply -> {
                    Parttime parttime = parttimeMapper.selectById(apply.getParttimeId());
                    User applicant = userMapper.selectById(apply.getApplicantId());
                    User processor = apply.getProcessorId() != null ? 
                            userMapper.selectById(apply.getProcessorId()) : null;

                    ApplyRecordVO vo = new ApplyRecordVO();
                    vo.setId(apply.getId());
                    vo.setParttimeId(apply.getParttimeId());
                    vo.setParttimeTitle(parttime != null ? parttime.getTitle() : null);
                    vo.setApplicantId(apply.getApplicantId());
                    vo.setApplicantName(applicant != null ? 
                            (applicant.getNickname() != null ? applicant.getNickname() : applicant.getUsername()) : null);
                    vo.setApplicantAvatar(applicant != null ? applicant.getAvatarUrl() : null);
                    vo.setApplicantSchoolId(applicant != null ? applicant.getSchoolId() : null);
                    vo.setApplicationNote(apply.getApplicationNote());
                    vo.setAvailableTime(apply.getAvailableTime());
                    vo.setStatus(apply.getStatus());
                    vo.setProcessorId(apply.getProcessorId());
                    vo.setProcessorName(processor != null ? 
                            (processor.getNickname() != null ? processor.getNickname() : processor.getUsername()) : null);
                    vo.setProcessNote(apply.getProcessNote());
                    vo.setApplyTime(apply.getApplyTime());
                    vo.setProcessTime(apply.getProcessTime());

                    return vo;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("list", voList);
        response.put("total", result.getTotal());

        return response;
    }

    @Override
    public Map<String, Object> getMyPublished(Long userId, ParttimeStatusEnum status, Integer page, Integer size) {
        LambdaQueryWrapper<Parttime> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Parttime::getPublisherId, userId)
                .eq(Parttime::getIsDeleted, 0);

        if (status != null) {
            queryWrapper.eq(Parttime::getStatus, status);
        }

        queryWrapper.orderByDesc(Parttime::getCreateTime);

        Page<Parttime> pageObj = new Page<>(page, size);
        Page<Parttime> result = parttimeMapper.selectPage(pageObj, queryWrapper);

        List<ParttimeVO> voList = result.getRecords().stream()
                .map(parttime -> {
                    User publisher = userMapper.selectById(parttime.getPublisherId());
                    University university = parttime.getSchoolId() != null ? 
                            universityMapper.selectById(parttime.getSchoolId()) : null;
                    return convertToVO(parttime, publisher, university, false);
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("list", voList);
        response.put("total", result.getTotal());

        return response;
    }

    @Override
    public Map<String, Object> getParttimeApplications(Long userId, Long parttimeId, ApplyStatusEnum status, Integer page, Integer size) {
        // 查询兼职
        Parttime parttime = parttimeMapper.selectById(parttimeId);
        if (parttime == null || parttime.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.PARTTIME_NOT_EXIST);
        }

        // 检查权限（只有发布者可以查看）
        if (!parttime.getPublisherId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        LambdaQueryWrapper<ParttimeApply> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ParttimeApply::getParttimeId, parttimeId);

        if (status != null) {
            queryWrapper.eq(ParttimeApply::getStatus, status);
        }

        queryWrapper.orderByDesc(ParttimeApply::getApplyTime);

        Page<ParttimeApply> pageObj = new Page<>(page, size);
        Page<ParttimeApply> result = applyMapper.selectPage(pageObj, queryWrapper);

        List<ApplyRecordVO> voList = result.getRecords().stream()
                .map(apply -> {
                    User applicant = userMapper.selectById(apply.getApplicantId());
                    User processor = apply.getProcessorId() != null ? 
                            userMapper.selectById(apply.getProcessorId()) : null;

                    ApplyRecordVO vo = new ApplyRecordVO();
                    vo.setId(apply.getId());
                    vo.setParttimeId(apply.getParttimeId());
                    vo.setParttimeTitle(parttime.getTitle());
                    vo.setApplicantId(apply.getApplicantId());
                    vo.setApplicantName(applicant != null ? 
                            (applicant.getNickname() != null ? applicant.getNickname() : applicant.getUsername()) : null);
                    vo.setApplicantAvatar(applicant != null ? applicant.getAvatarUrl() : null);
                    vo.setApplicantSchoolId(applicant != null ? applicant.getSchoolId() : null);
                    vo.setApplicationNote(apply.getApplicationNote());
                    vo.setAvailableTime(apply.getAvailableTime());
                    vo.setStatus(apply.getStatus());
                    vo.setProcessorId(apply.getProcessorId());
                    vo.setProcessorName(processor != null ? 
                            (processor.getNickname() != null ? processor.getNickname() : processor.getUsername()) : null);
                    vo.setProcessNote(apply.getProcessNote());
                    vo.setApplyTime(apply.getApplyTime());
                    vo.setProcessTime(apply.getProcessTime());

                    return vo;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("list", voList);
        response.put("total", result.getTotal());

        return response;
    }

    @Override
    @Transactional
    public void processApplication(Long userId, Long applicationId, ApplyStatusEnum result, String processNote) {
        // 查询报名记录
        ParttimeApply apply = applyMapper.selectById(applicationId);
        if (apply == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }

        // 查询兼职
        Parttime parttime = parttimeMapper.selectById(apply.getParttimeId());
        if (parttime == null || parttime.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.PARTTIME_NOT_EXIST);
        }

        // 检查权限（只有发布者可以处理）
        if (!parttime.getPublisherId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        // 只能处理待审核的报名
        if (apply.getStatus() != ApplyStatusEnum.PENDING) {
            throw new BusinessException(ErrorCode.PARTTIME_STATUS_ERROR);
        }

        // 更新报名状态
        apply.setStatus(result);
        apply.setProcessorId(userId);
        apply.setProcessNote(processNote);
        apply.setProcessTime(LocalDateTime.now());
        applyMapper.updateById(apply);
    }

    @Override
    @Transactional
    public ParttimeVO updateParttime(Long userId, Long parttimeId, ParttimePublishDTO updateDTO) {
        // 查询兼职
        Parttime parttime = parttimeMapper.selectById(parttimeId);
        if (parttime == null || parttime.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.PARTTIME_NOT_EXIST);
        }

        // 检查权限
        if (!parttime.getPublisherId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        // 更新兼职信息
        parttime.setTitle(updateDTO.getTitle());
        parttime.setDescription(updateDTO.getDescription());
        parttime.setSalaryPerHour(updateDTO.getSalaryPerHour());
        parttime.setRecruitCount(updateDTO.getRecruitCount());
        parttime.setWorkStartTime(updateDTO.getWorkStartTime());
        parttime.setWorkEndTime(updateDTO.getWorkEndTime());
        parttime.setLocation(updateDTO.getLocation());
        parttime.setRequirements(updateDTO.getRequirements());
        parttime.setContactName(updateDTO.getContactName());
        parttime.setContactPhone(updateDTO.getContactPhone());

        // 更新图片
        if (updateDTO.getImageUrls() != null) {
            try {
                parttime.setImageUrls(objectMapper.writeValueAsString(updateDTO.getImageUrls()));
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.BAD_REQUEST);
            }
        }

        parttimeMapper.updateById(parttime);

        User publisher = userMapper.selectById(userId);
        University university = parttime.getSchoolId() != null ? 
                universityMapper.selectById(parttime.getSchoolId()) : null;
        return convertToVO(parttime, publisher, university, null);
    }

    @Override
    @Transactional
    public void updateParttimeStatus(Long userId, Long parttimeId, ParttimeStatusEnum status) {
        Parttime parttime = parttimeMapper.selectById(parttimeId);
        if (parttime == null || parttime.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.PARTTIME_NOT_EXIST);
        }

        if (!parttime.getPublisherId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        parttime.setStatus(status);
        parttimeMapper.updateById(parttime);
    }

    @Override
    @Transactional
    public void deleteParttime(Long userId, Long parttimeId) {
        Parttime parttime = parttimeMapper.selectById(parttimeId);
        if (parttime == null || parttime.getIsDeleted() == 1) {
            throw new BusinessException(ErrorCode.PARTTIME_NOT_EXIST);
        }

        if (!parttime.getPublisherId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        // 检查是否有进行中的报名
        LambdaQueryWrapper<ParttimeApply> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ParttimeApply::getParttimeId, parttimeId)
                .in(ParttimeApply::getStatus, Arrays.asList(ApplyStatusEnum.PENDING, ApplyStatusEnum.APPROVED));
        if (applyMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException(ErrorCode.PARTTIME_STATUS_ERROR);
        }

        // 逻辑删除
        parttime.setIsDeleted(1);
        parttimeMapper.updateById(parttime);
    }

    /**
     * 转换兼职实体为VO
     */
    private ParttimeVO convertToVO(Parttime parttime, User publisher, University university, Boolean hasApplied) {
        ParttimeVO vo = new ParttimeVO();
        vo.setId(parttime.getId());
        vo.setTitle(parttime.getTitle());
        vo.setDescription(parttime.getDescription());
        vo.setSalaryPerHour(parttime.getSalaryPerHour());
        vo.setRecruitCount(parttime.getRecruitCount());
        vo.setAppliedCount(parttime.getAppliedCount());
        vo.setWorkStartTime(parttime.getWorkStartTime());
        vo.setWorkEndTime(parttime.getWorkEndTime());
        vo.setLocation(parttime.getLocation());
        vo.setRequirements(parttime.getRequirements());
        vo.setStatus(parttime.getStatus());
        vo.setPublisherId(parttime.getPublisherId());
        vo.setPublisherName(publisher != null ? 
                (publisher.getNickname() != null ? publisher.getNickname() : publisher.getUsername()) : null);
        vo.setPublisherAvatar(publisher != null ? publisher.getAvatarUrl() : null);
        vo.setSchoolId(parttime.getSchoolId());
        vo.setSchoolName(university != null ? university.getName() : null);
        vo.setContactName(parttime.getContactName());
        vo.setHasApplied(hasApplied != null ? hasApplied : false);
        vo.setCreateTime(parttime.getCreateTime());
        vo.setUpdateTime(parttime.getUpdateTime());

        // 解析图片URL列表
        if (StringUtils.hasText(parttime.getImageUrls())) {
            try {
                vo.setImageUrls(objectMapper.readValue(parttime.getImageUrls(), new TypeReference<List<String>>() {}));
            } catch (Exception e) {
                vo.setImageUrls(new ArrayList<>());
            }
        } else {
            vo.setImageUrls(new ArrayList<>());
        }

        return vo;
    }

    @Override
    @Transactional
    public void favoriteParttime(Long userId, Long parttimeId) {
        // 检查兼职是否存在
        Parttime parttime = parttimeMapper.selectById(parttimeId);
        if (parttime == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "兼职不存在");
        }

        // 检查是否已收藏
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getTargetType, "PARTTIME")
                .eq(Favorite::getTargetId, parttimeId);
        
        if (favoriteMapper.selectCount(queryWrapper) > 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "已收藏过此兼职");
        }

        // 创建收藏记录
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setTargetType("PARTTIME");
        favorite.setTargetId(parttimeId);
        favoriteMapper.insert(favorite);
    }

    @Override
    @Transactional
    public void cancelFavoriteParttime(Long userId, Long parttimeId) {
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getTargetType, "PARTTIME")
                .eq(Favorite::getTargetId, parttimeId);
        
        Favorite favorite = favoriteMapper.selectOne(queryWrapper);
        if (favorite == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "未收藏此兼职");
        }
        
        favoriteMapper.deleteById(favorite.getId());
    }

    @Override
    public Map<String, Object> getParttimeFavorites(Long userId, Integer page, Integer size) {
        // 查询收藏记录
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getTargetType, "PARTTIME")
                .orderByDesc(Favorite::getCreateTime);

        Page<Favorite> pageObj = new Page<>(page, size);
        Page<Favorite> result = favoriteMapper.selectPage(pageObj, queryWrapper);

        // 获取兼职ID列表
        List<Long> parttimeIds = result.getRecords().stream()
                .map(Favorite::getTargetId)
                .distinct()
                .collect(Collectors.toList());

        if (parttimeIds.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("records", new ArrayList<>());
            response.put("total", 0L);
            response.put("page", page);
            response.put("size", size);
            return response;
        }

        // 批量查询兼职信息（使用in查询替代已弃用的selectBatchIds）
        LambdaQueryWrapper<Parttime> parttimeQuery = new LambdaQueryWrapper<>();
        parttimeQuery.in(Parttime::getId, parttimeIds);
        List<Parttime> parttimeList = parttimeMapper.selectList(parttimeQuery);
        Map<Long, Parttime> parttimeMap = parttimeList.stream()
                .collect(Collectors.toMap(Parttime::getId, p -> p));

        // 批量查询发布者信息
        List<Long> publisherIds = parttimeList.stream()
                .map(Parttime::getPublisherId)
                .distinct()
                .collect(Collectors.toList());
        // 批量查询发布者信息（使用in查询替代已弃用的selectBatchIds）
        LambdaQueryWrapper<User> publisherQuery = new LambdaQueryWrapper<>();
        publisherQuery.in(User::getId, publisherIds);
        List<User> publishers = userMapper.selectList(publisherQuery);
        Map<Long, User> publisherMap = publishers.stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        // 构建返回数据
        List<Map<String, Object>> records = new ArrayList<>();
        for (Favorite favorite : result.getRecords()) {
            Parttime parttime = parttimeMap.get(favorite.getTargetId());
            if (parttime != null) {
                Map<String, Object> record = new HashMap<>();
                record.put("id", favorite.getId());
                record.put("parttimeId", parttime.getId());
                record.put("title", parttime.getTitle());
                
                User publisher = publisherMap.get(parttime.getPublisherId());
                record.put("companyName", publisher != null ? 
                        (publisher.getNickname() != null ? publisher.getNickname() : publisher.getUsername()) : null);
                
                record.put("salary", parttime.getSalaryPerHour() != null ? 
                        parttime.getSalaryPerHour() + "元/小时" : null);
                record.put("location", parttime.getLocation());
                record.put("favoriteTime", favorite.getCreateTime());
                records.add(record);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("records", records);
        response.put("total", result.getTotal());
        response.put("page", page);
        response.put("size", size);

        return response;
    }

    @Override
    @Transactional
    public void clearParttimeFavorites(Long userId) {
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getTargetType, "PARTTIME");
        favoriteMapper.delete(queryWrapper);
    }

    @Override
    @Transactional
    public void recordBrowse(Long userId, Long parttimeId) {
        // 检查兼职是否存在
        Parttime parttime = parttimeMapper.selectById(parttimeId);
        if (parttime == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "兼职不存在");
        }

        // 检查是否已有浏览记录（同一天内不重复记录）
        LambdaQueryWrapper<BrowseHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BrowseHistory::getUserId, userId)
                .eq(BrowseHistory::getContentType, "PARTTIME")
                .eq(BrowseHistory::getContentId, parttimeId)
                .ge(BrowseHistory::getViewTime, LocalDateTime.now().toLocalDate().atStartOfDay())
                .orderByDesc(BrowseHistory::getViewTime)
                .last("LIMIT 1");
        
        BrowseHistory existingHistory = browseHistoryMapper.selectOne(queryWrapper);
        
        if (existingHistory == null) {
            // 创建新的浏览记录
            BrowseHistory history = new BrowseHistory();
            history.setUserId(userId);
            history.setContentType("PARTTIME");
            history.setContentId(parttimeId);
            browseHistoryMapper.insert(history);
        } else {
            // 更新浏览时间
            existingHistory.setViewTime(LocalDateTime.now());
            browseHistoryMapper.updateById(existingHistory);
        }
    }

    @Override
    public Map<String, Object> getBrowseHistory(Long userId, Integer page, Integer size) {
        // 查询浏览记录
        LambdaQueryWrapper<BrowseHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BrowseHistory::getUserId, userId)
                .eq(BrowseHistory::getContentType, "PARTTIME")
                .orderByDesc(BrowseHistory::getViewTime);

        Page<BrowseHistory> pageObj = new Page<>(page, size);
        Page<BrowseHistory> result = browseHistoryMapper.selectPage(pageObj, queryWrapper);

        // 获取兼职ID列表
        List<Long> parttimeIds = result.getRecords().stream()
                .map(BrowseHistory::getContentId)
                .distinct()
                .collect(Collectors.toList());

        if (parttimeIds.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("records", new ArrayList<>());
            response.put("total", 0L);
            response.put("page", page);
            response.put("size", size);
            return response;
        }

        // 批量查询兼职信息（使用in查询替代已弃用的selectBatchIds）
        LambdaQueryWrapper<Parttime> parttimeQuery = new LambdaQueryWrapper<>();
        parttimeQuery.in(Parttime::getId, parttimeIds);
        List<Parttime> parttimeList = parttimeMapper.selectList(parttimeQuery);
        Map<Long, Parttime> parttimeMap = parttimeList.stream()
                .collect(Collectors.toMap(Parttime::getId, p -> p));

        // 批量查询发布者信息
        List<Long> publisherIds = parttimeList.stream()
                .map(Parttime::getPublisherId)
                .distinct()
                .collect(Collectors.toList());
        // 批量查询发布者信息（使用in查询替代已弃用的selectBatchIds）
        LambdaQueryWrapper<User> publisherQuery = new LambdaQueryWrapper<>();
        publisherQuery.in(User::getId, publisherIds);
        List<User> publishers = userMapper.selectList(publisherQuery);
        Map<Long, User> publisherMap = publishers.stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        // 构建返回数据
        List<Map<String, Object>> records = new ArrayList<>();
        for (BrowseHistory history : result.getRecords()) {
            Parttime parttime = parttimeMap.get(history.getContentId());
            if (parttime != null) {
                Map<String, Object> record = new HashMap<>();
                record.put("id", history.getId());
                record.put("parttimeId", parttime.getId());
                record.put("title", parttime.getTitle());
                
                User publisher = publisherMap.get(parttime.getPublisherId());
                record.put("companyName", publisher != null ? 
                        (publisher.getNickname() != null ? publisher.getNickname() : publisher.getUsername()) : null);
                
                record.put("salary", parttime.getSalaryPerHour() != null ? 
                        parttime.getSalaryPerHour() + "元/小时" : null);
                record.put("location", parttime.getLocation());
                record.put("viewTime", history.getViewTime());
                records.add(record);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("records", records);
        response.put("total", result.getTotal());
        response.put("page", page);
        response.put("size", size);

        return response;
    }

    @Override
    @Transactional
    public void deleteBrowseHistory(Long userId, Long historyId) {
        // 验证浏览记录是否属于当前用户
        BrowseHistory history = browseHistoryMapper.selectById(historyId);
        if (history == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "浏览记录不存在");
        }
        if (!history.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权删除此浏览记录");
        }
        browseHistoryMapper.deleteById(historyId);
    }

    @Override
    @Transactional
    public void clearBrowseHistory(Long userId) {
        LambdaQueryWrapper<BrowseHistory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BrowseHistory::getUserId, userId)
                .eq(BrowseHistory::getContentType, "PARTTIME");
        browseHistoryMapper.delete(queryWrapper);
    }
}
