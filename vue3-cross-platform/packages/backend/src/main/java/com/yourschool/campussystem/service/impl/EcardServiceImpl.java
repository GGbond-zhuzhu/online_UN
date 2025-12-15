package com.yourschool.campussystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yourschool.campussystem.dto.EcardConsumeDTO;
import com.yourschool.campussystem.dto.VisitorCardApplyDTO;
import com.yourschool.campussystem.entity.ConsumeRecord;
import com.yourschool.campussystem.entity.Ecard;
import com.yourschool.campussystem.entity.User;
import com.yourschool.campussystem.entity.University;
import com.yourschool.campussystem.enums.CardStatusEnum;
import com.yourschool.campussystem.enums.ConsumeTypeEnum;
import com.yourschool.campussystem.common.ErrorCode;
import com.yourschool.campussystem.exception.BusinessException;
import com.yourschool.campussystem.mapper.ConsumeRecordMapper;
import com.yourschool.campussystem.mapper.EcardMapper;
import com.yourschool.campussystem.mapper.UserMapper;
import com.yourschool.campussystem.mapper.UniversityMapper;
import com.yourschool.campussystem.service.EcardService;
import com.yourschool.campussystem.vo.ConsumeRecordVO;
import com.yourschool.campussystem.vo.EcardVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 校园卡服务实现类
 */
@Service
@RequiredArgsConstructor
public class EcardServiceImpl extends ServiceImpl<EcardMapper, Ecard> implements EcardService {

    private final EcardMapper ecardMapper;
    private final ConsumeRecordMapper consumeRecordMapper;
    private final UserMapper userMapper;
    private final UniversityMapper universityMapper;

    @Override
    public EcardVO getCardInfo(Long userId) {
        // 查询用户的校园卡
        LambdaQueryWrapper<Ecard> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Ecard::getUserId, userId);
        Ecard ecard = ecardMapper.selectOne(queryWrapper);

        if (ecard == null) {
            throw new BusinessException(ErrorCode.ECARD_NOT_EXIST);
        }

        // 查询用户信息
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }

        // 查询今日消费统计
        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<ConsumeRecord> todayQuery = new LambdaQueryWrapper<>();
        todayQuery.eq(ConsumeRecord::getCardNo, ecard.getCardNo())
                .ge(ConsumeRecord::getConsumeTime, today.atStartOfDay())
                .lt(ConsumeRecord::getConsumeTime, today.plusDays(1).atStartOfDay());
        List<ConsumeRecord> todayRecords = consumeRecordMapper.selectList(todayQuery);

        int todayConsumeCount = todayRecords.size();
        BigDecimal todayConsumeAmount = todayRecords.stream()
                .map(ConsumeRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 查询最后消费时间
        LambdaQueryWrapper<ConsumeRecord> lastQuery = new LambdaQueryWrapper<>();
        lastQuery.eq(ConsumeRecord::getCardNo, ecard.getCardNo())
                .orderByDesc(ConsumeRecord::getConsumeTime)
                .last("LIMIT 1");
        ConsumeRecord lastRecord = consumeRecordMapper.selectOne(lastQuery);

        // 构建返回对象
        EcardVO ecardVO = new EcardVO();
        ecardVO.setCardNo(ecard.getCardNo());
        ecardVO.setUserId(ecard.getUserId());
        ecardVO.setUserName(user.getNickname() != null ? user.getNickname() : user.getUsername());
        ecardVO.setBalance(ecard.getBalance());
        ecardVO.setStatus(ecard.getStatus());
        ecardVO.setIsVisitorCard(ecard.getIsVisitorCard());
        ecardVO.setVisitorExpireTime(ecard.getVisitorExpireTime());
        ecardVO.setTodayConsumeCount(todayConsumeCount);
        ecardVO.setTodayConsumeAmount(todayConsumeAmount);
        ecardVO.setLastConsumeTime(lastRecord != null ? lastRecord.getConsumeTime() : null);
        ecardVO.setCreateTime(ecard.getCreateTime());

        return ecardVO;
    }

    @Override
    @Transactional
    public ConsumeRecordVO consume(Long userId, EcardConsumeDTO consumeDTO) {
        // 查询用户的校园卡
        LambdaQueryWrapper<Ecard> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Ecard::getUserId, userId);
        Ecard ecard = ecardMapper.selectOne(queryWrapper);

        if (ecard == null) {
            throw new BusinessException(ErrorCode.ECARD_NOT_EXIST);
        }

        // 检查卡片状态
        if (ecard.getStatus() != CardStatusEnum.NORMAL) {
            throw new BusinessException(ErrorCode.ECARD_STATUS_ERROR);
        }

        // 检查余额
        if (ecard.getBalance().compareTo(consumeDTO.getAmount()) < 0) {
            throw new BusinessException(ErrorCode.ECARD_BALANCE_INSUFFICIENT);
        }

        // 扣款
        BigDecimal newBalance = ecard.getBalance().subtract(consumeDTO.getAmount());
        ecard.setBalance(newBalance);
        ecardMapper.updateById(ecard);

        // 创建消费记录
        ConsumeRecord record = new ConsumeRecord();
        record.setCardNo(ecard.getCardNo());
        record.setAmount(consumeDTO.getAmount());
        record.setBalanceAfter(newBalance);
        record.setMerchantId(consumeDTO.getMerchantId());
        record.setMerchantName(consumeDTO.getMerchantName());
        record.setConsumeType(consumeDTO.getConsumeType());
        record.setDescription(consumeDTO.getDescription());
        record.setLongitude(consumeDTO.getLongitude() != null ? 
                BigDecimal.valueOf(consumeDTO.getLongitude()) : null);
        record.setLatitude(consumeDTO.getLatitude() != null ? 
                BigDecimal.valueOf(consumeDTO.getLatitude()) : null);
        // 获取用户的学校ID
        User user = userMapper.selectById(userId);
        Long schoolId = user != null ? user.getSchoolId() : null;
        record.setIsInCampus(checkLocation(schoolId, record.getLongitude(), record.getLatitude()));
        record.setPayMethod("CARD");
        record.setConsumeTime(LocalDateTime.now());
        consumeRecordMapper.insert(record);

        // 构建返回对象
        ConsumeRecordVO recordVO = new ConsumeRecordVO();
        recordVO.setId(record.getId());
        recordVO.setCardNo(record.getCardNo());
        recordVO.setAmount(record.getAmount());
        recordVO.setBalanceAfter(record.getBalanceAfter());
        recordVO.setMerchantId(record.getMerchantId());
        recordVO.setMerchantName(record.getMerchantName());
        recordVO.setConsumeType(record.getConsumeType());
        recordVO.setDescription(record.getDescription());
        recordVO.setLongitude(record.getLongitude() != null ? record.getLongitude().doubleValue() : null);
        recordVO.setLatitude(record.getLatitude() != null ? record.getLatitude().doubleValue() : null);
        recordVO.setIsInCampus(record.getIsInCampus());
        recordVO.setConsumeTime(record.getConsumeTime());
        recordVO.setPayMethod(record.getPayMethod());

        return recordVO;
    }

    @Override
    public List<ConsumeRecordVO> getConsumeRecords(Long userId, String startDate, String endDate,
                                                    String consumeType, Integer page, Integer size) {
        // 查询用户的校园卡
        LambdaQueryWrapper<Ecard> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Ecard::getUserId, userId);
        Ecard ecard = ecardMapper.selectOne(queryWrapper);

        if (ecard == null) {
            throw new BusinessException(ErrorCode.ECARD_NOT_EXIST);
        }

        // 构建查询条件
        LambdaQueryWrapper<ConsumeRecord> recordQuery = new LambdaQueryWrapper<>();
        recordQuery.eq(ConsumeRecord::getCardNo, ecard.getCardNo());

        if (startDate != null && !startDate.isEmpty()) {
            LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
            recordQuery.ge(ConsumeRecord::getConsumeTime, start.atStartOfDay());
        }

        if (endDate != null && !endDate.isEmpty()) {
            LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
            recordQuery.lt(ConsumeRecord::getConsumeTime, end.plusDays(1).atStartOfDay());
        }

        if (consumeType != null && !consumeType.isEmpty()) {
            recordQuery.eq(ConsumeRecord::getConsumeType, ConsumeTypeEnum.valueOf(consumeType));
        }

        recordQuery.orderByDesc(ConsumeRecord::getConsumeTime);

        // 分页查询
        Page<ConsumeRecord> pageObj = new Page<>(page, size);
        Page<ConsumeRecord> result = consumeRecordMapper.selectPage(pageObj, recordQuery);

        // 转换为VO
        return result.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EcardVO applyVisitorCard(VisitorCardApplyDTO applyDTO) {
        // 生成游客卡号
        String cardNo = "VISITOR_" + System.currentTimeMillis();

        // 创建游客卡（不关联用户ID）
        Ecard visitorCard = new Ecard();
        visitorCard.setCardNo(cardNo);
        visitorCard.setUserId(null);
        visitorCard.setBalance(new BigDecimal("100.00")); // 初始余额
        visitorCard.setStatus(CardStatusEnum.NORMAL);
        visitorCard.setIsVisitorCard(true);
        visitorCard.setVisitorExpireTime(LocalDateTime.now().plusDays(7)); // 7天有效期
        ecardMapper.insert(visitorCard);

        // 构建返回对象
        EcardVO ecardVO = new EcardVO();
        ecardVO.setCardNo(visitorCard.getCardNo());
        ecardVO.setUserId(null);
        ecardVO.setUserName(applyDTO.getName());
        ecardVO.setBalance(visitorCard.getBalance());
        ecardVO.setStatus(visitorCard.getStatus());
        ecardVO.setIsVisitorCard(true);
        ecardVO.setVisitorExpireTime(visitorCard.getVisitorExpireTime());
        ecardVO.setCreateTime(visitorCard.getCreateTime());

        return ecardVO;
    }

    @Override
    @Transactional
    public void reportLoss(Long userId) {
        LambdaQueryWrapper<Ecard> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Ecard::getUserId, userId);
        Ecard ecard = ecardMapper.selectOne(queryWrapper);

        if (ecard == null) {
            throw new BusinessException(ErrorCode.ECARD_NOT_EXIST);
        }

        ecard.setStatus(CardStatusEnum.LOST);
        ecardMapper.updateById(ecard);
    }

    @Override
    @Transactional
    public void cancelLoss(Long userId) {
        LambdaQueryWrapper<Ecard> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Ecard::getUserId, userId);
        Ecard ecard = ecardMapper.selectOne(queryWrapper);

        if (ecard == null) {
            throw new BusinessException(ErrorCode.ECARD_NOT_EXIST);
        }

        if (ecard.getStatus() != CardStatusEnum.LOST) {
            throw new BusinessException(ErrorCode.ECARD_STATUS_ERROR);
        }

        ecard.setStatus(CardStatusEnum.NORMAL);
        ecardMapper.updateById(ecard);
    }

    @Override
    @Transactional
    public EcardVO recharge(Long userId, BigDecimal amount) {
        LambdaQueryWrapper<Ecard> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Ecard::getUserId, userId);
        Ecard ecard = ecardMapper.selectOne(queryWrapper);

        if (ecard == null) {
            throw new BusinessException(ErrorCode.ECARD_NOT_EXIST);
        }

        ecard.setBalance(ecard.getBalance().add(amount));
        ecardMapper.updateById(ecard);

        return getCardInfo(userId);
    }

    @Override
    public boolean checkLocation(Long schoolId, BigDecimal longitude, BigDecimal latitude) {
        if (schoolId == null || longitude == null || latitude == null) {
            return false;
        }

        // 查询高校信息
        University university = universityMapper.selectById(schoolId);
        if (university == null || university.getLongitude() == null || university.getLatitude() == null) {
            return false;
        }

        // 计算距离（使用Haversine公式计算地球表面两点间距离）
        double lat1 = university.getLatitude().doubleValue();
        double lon1 = university.getLongitude().doubleValue();
        double lat2 = latitude.doubleValue();
        double lon2 = longitude.doubleValue();

        final int R = 6371000; // 地球半径（米）
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;

        return distance <= university.getCampusRadius();
    }

    @Override
    public Object getTodayStatistics(Long userId) {
        EcardVO ecardVO = getCardInfo(userId);
        
        // 查询今日消费记录
        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<ConsumeRecord> query = new LambdaQueryWrapper<>();
        query.eq(ConsumeRecord::getCardNo, ecardVO.getCardNo())
                .ge(ConsumeRecord::getConsumeTime, today.atStartOfDay())
                .lt(ConsumeRecord::getConsumeTime, today.plusDays(1).atStartOfDay());
        List<ConsumeRecord> records = consumeRecordMapper.selectList(query);

        int consumeCount = records.size();
        BigDecimal consumeAmount = records.stream()
                .map(ConsumeRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal averageConsume = consumeCount > 0 ? 
                consumeAmount.divide(BigDecimal.valueOf(consumeCount), 2, RoundingMode.HALF_UP) : 
                BigDecimal.ZERO;

        // 统计最频繁的消费类型
        Map<ConsumeTypeEnum, Long> typeCount = records.stream()
                .collect(Collectors.groupingBy(ConsumeRecord::getConsumeType, Collectors.counting()));
        
        ConsumeTypeEnum mostFrequentType = typeCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("consumeCount", consumeCount);
        statistics.put("consumeAmount", consumeAmount);
        statistics.put("averageConsume", averageConsume);
        statistics.put("mostFrequentType", mostFrequentType != null ? mostFrequentType.name() : null);
        statistics.put("mostFrequentCount", mostFrequentType != null ? 
                typeCount.get(mostFrequentType).intValue() : 0);

        return statistics;
    }

    @Override
    public Object generateDynamicCode(Long userId) {
        // 生成动态码（实际应该使用更复杂的算法）
        String code = "STU" + userId + "_" + System.currentTimeMillis();
        String qrCodeUrl = "https://example.com/qrcode/" + code;
        long expireTime = System.currentTimeMillis() + 300000; // 5分钟有效期

        Map<String, Object> codeInfo = new HashMap<>();
        codeInfo.put("code", code);
        codeInfo.put("qrCodeUrl", qrCodeUrl);
        codeInfo.put("expireTime", expireTime);
        codeInfo.put("message", "动态码5分钟内有效，可用于门禁、图书馆等系统");

        return codeInfo;
    }

    /**
     * 转换消费记录为VO
     */
    private ConsumeRecordVO convertToVO(ConsumeRecord record) {
        ConsumeRecordVO vo = new ConsumeRecordVO();
        vo.setId(record.getId());
        vo.setCardNo(record.getCardNo());
        vo.setAmount(record.getAmount());
        vo.setBalanceAfter(record.getBalanceAfter());
        vo.setMerchantId(record.getMerchantId());
        vo.setMerchantName(record.getMerchantName());
        vo.setConsumeType(record.getConsumeType());
        vo.setDescription(record.getDescription());
        vo.setLongitude(record.getLongitude() != null ? record.getLongitude().doubleValue() : null);
        vo.setLatitude(record.getLatitude() != null ? record.getLatitude().doubleValue() : null);
        vo.setIsInCampus(record.getIsInCampus());
        vo.setConsumeTime(record.getConsumeTime());
        vo.setPayMethod(record.getPayMethod());
        return vo;
    }
}
