package com.yourschool.campussystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yourschool.campussystem.common.ErrorCode;
import com.yourschool.campussystem.dto.DietRecordDTO;
import com.yourschool.campussystem.entity.DietRecord;
import com.yourschool.campussystem.entity.ConsumeRecord;
import com.yourschool.campussystem.entity.Ecard;
import com.yourschool.campussystem.enums.ConsumeTypeEnum;
import com.yourschool.campussystem.exception.BusinessException;
import com.yourschool.campussystem.mapper.DietRecordMapper;
import com.yourschool.campussystem.mapper.ConsumeRecordMapper;
import com.yourschool.campussystem.mapper.EcardMapper;
import com.yourschool.campussystem.service.DietRecordService;
import com.yourschool.campussystem.vo.DietRecordVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 饮食记录服务实现类
 */
@Service
@RequiredArgsConstructor
public class DietRecordServiceImpl extends ServiceImpl<DietRecordMapper, DietRecord> implements DietRecordService {

    private final DietRecordMapper dietRecordMapper;
    private final ConsumeRecordMapper consumeRecordMapper;
    private final EcardMapper ecardMapper;

    @Override
    @Transactional
    public DietRecordVO addRecord(Long userId, DietRecordDTO dto) {
        DietRecord record = new DietRecord();
        BeanUtils.copyProperties(dto, record);
        record.setUserId(userId);
        record.setDietDate(dto.getDietDate() != null ? dto.getDietDate() : LocalDate.now());
        record.setIsAutoImport(false);
        
        // 设置默认背景颜色
        if (record.getBackgroundColor() == null || record.getBackgroundColor().isEmpty()) {
            record.setBackgroundColor(getDefaultColorByMealType(dto.getMealType()));
        }
        
        dietRecordMapper.insert(record);
        return convertToVO(record);
    }

    @Override
    @Transactional
    public void deleteRecord(Long userId, Long recordId) {
        DietRecord record = dietRecordMapper.selectById(recordId);
        if (record == null || !record.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "记录不存在");
        }
        dietRecordMapper.deleteById(recordId);
    }

    @Override
    @Transactional
    public DietRecordVO updateRecord(Long userId, Long recordId, DietRecordDTO dto) {
        DietRecord record = dietRecordMapper.selectById(recordId);
        if (record == null || !record.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "记录不存在");
        }
        
        BeanUtils.copyProperties(dto, record);
        if (dto.getDietDate() != null) {
            record.setDietDate(dto.getDietDate());
        }
        dietRecordMapper.updateById(record);
        
        return convertToVO(record);
    }

    @Override
    public List<DietRecordVO> getRecords(Long userId, LocalDate startDate, LocalDate endDate, 
                                         String mealType, Integer page, Integer size) {
        LambdaQueryWrapper<DietRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DietRecord::getUserId, userId);
        
        if (startDate != null) {
            queryWrapper.ge(DietRecord::getDietDate, startDate);
        }
        if (endDate != null) {
            queryWrapper.le(DietRecord::getDietDate, endDate);
        }
        if (mealType != null && !mealType.isEmpty()) {
            queryWrapper.eq(DietRecord::getMealType, mealType);
        }
        
        queryWrapper.orderByDesc(DietRecord::getDietDate);
        queryWrapper.orderByDesc(DietRecord::getCreateTime);
        
        Page<DietRecord> pageObj = new Page<>(page, size);
        Page<DietRecord> result = dietRecordMapper.selectPage(pageObj, queryWrapper);
        
        return result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DietRecordVO> getRecordsByDate(Long userId, LocalDate date) {
        List<DietRecord> records = dietRecordMapper.selectList(
            new LambdaQueryWrapper<DietRecord>()
                .eq(DietRecord::getUserId, userId)
                .eq(DietRecord::getDietDate, date)
                .orderByAsc(DietRecord::getMealType)
        );
        
        return records.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public DietRecordVO getRecordById(Long userId, Long recordId) {
        DietRecord record = dietRecordMapper.selectById(recordId);
        if (record == null || !record.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "记录不存在");
        }
        return convertToVO(record);
    }

    @Override
    @Transactional
    public void autoImportFromEcard(Long userId, LocalDate startDate, LocalDate endDate) {
        // 查询用户的校园卡
        Ecard ecard = ecardMapper.selectOne(
            new LambdaQueryWrapper<Ecard>()
                .eq(Ecard::getUserId, userId)
        );
        
        if (ecard == null) {
            return;
        }
        
        // 查询E卡通食堂消费记录
        LambdaQueryWrapper<ConsumeRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ConsumeRecord::getCardNo, ecard.getCardNo());
        queryWrapper.eq(ConsumeRecord::getConsumeType, ConsumeTypeEnum.CANTEEN);
        if (startDate != null) {
            queryWrapper.ge(ConsumeRecord::getConsumeTime, startDate.atStartOfDay());
        }
        if (endDate != null) {
            queryWrapper.lt(ConsumeRecord::getConsumeTime, endDate.plusDays(1).atStartOfDay());
        }
        
        List<ConsumeRecord> consumeRecords = consumeRecordMapper.selectList(queryWrapper);
        
        // 转换为饮食记录
        for (ConsumeRecord consumeRecord : consumeRecords) {
            // 检查是否已导入
            DietRecord existing = dietRecordMapper.selectOne(
                new LambdaQueryWrapper<DietRecord>()
                    .eq(DietRecord::getUserId, userId)
                    .eq(DietRecord::getEcardRecordId, consumeRecord.getId())
            );
            
            if (existing == null) {
                DietRecord record = new DietRecord();
                record.setUserId(userId);
                record.setDietDate(consumeRecord.getConsumeTime().toLocalDate());
                record.setIsAutoImport(true);
                record.setEcardRecordId(consumeRecord.getId());
                record.setLocation(consumeRecord.getMerchantName());
                record.setSource("CANTEEN");
                record.setPrice(consumeRecord.getAmount());
                
                // 根据消费时间判断餐次
                LocalTime consumeTime = consumeRecord.getConsumeTime().toLocalTime();
                String mealType = determineMealType(consumeTime);
                record.setMealType(mealType);
                
                // 设置默认食物名称
                record.setFoodName("食堂用餐");
                record.setFoodDetail(consumeRecord.getDescription());
                record.setBackgroundColor(getDefaultColorByMealType(mealType));
                
                dietRecordMapper.insert(record);
            }
        }
    }

    @Override
    public Object getStatistics(Long userId, LocalDate startDate, LocalDate endDate) {
        List<DietRecord> records = dietRecordMapper.selectList(
            new LambdaQueryWrapper<DietRecord>()
                .eq(DietRecord::getUserId, userId)
                .ge(DietRecord::getDietDate, startDate)
                .le(DietRecord::getDietDate, endDate)
        );
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCount", records.size());
        
        // 餐次统计
        Map<String, Long> mealTypeStats = records.stream()
                .collect(Collectors.groupingBy(DietRecord::getMealType, Collectors.counting()));
        stats.put("mealTypeStats", mealTypeStats);
        
        // 来源统计
        Map<String, Long> sourceStats = records.stream()
                .collect(Collectors.groupingBy(DietRecord::getSource, Collectors.counting()));
        stats.put("sourceStats", sourceStats);
        
        return stats;
    }

    private DietRecordVO convertToVO(DietRecord record) {
        DietRecordVO vo = new DietRecordVO();
        BeanUtils.copyProperties(record, vo);
        return vo;
    }

    private String determineMealType(LocalTime time) {
        int hour = time.getHour();
        if (hour >= 6 && hour < 10) {
            return "BREAKFAST";
        } else if (hour >= 11 && hour < 14) {
            return "LUNCH";
        } else if (hour >= 17 && hour < 21) {
            return "DINNER";
        } else {
            return "SNACK";
        }
    }

    private String getDefaultColorByMealType(String mealType) {
        switch (mealType) {
            case "BREAKFAST":
                return "#FFE5B4"; // 淡橙色
            case "LUNCH":
                return "#FFB6C1"; // 淡粉色
            case "DINNER":
                return "#DDA0DD"; // 淡紫色
            case "SNACK":
                return "#98FB98"; // 淡绿色
            default:
                return "#E6E6FA"; // 淡紫色
        }
    }
}

