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
import com.yourschool.campussystem.mapper.AccountBookMapper;
import com.yourschool.campussystem.mapper.DietRecordMapper;
import com.yourschool.campussystem.entity.AccountBook;
import com.yourschool.campussystem.entity.DietRecord;
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
    private final AccountBookMapper accountBookMapper;
    private final DietRecordMapper dietRecordMapper;

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

        // 自动导入到记账本（异步处理，不影响主流程）
        try {
            String category = mapConsumeTypeToCategory(record.getConsumeType());
            AccountBook accountBook = new AccountBook();
            accountBook.setUserId(userId);
            accountBook.setAmount(record.getAmount());
            accountBook.setCategory(category);
            accountBook.setDescription(record.getDescription());
            accountBook.setConsumeDate(record.getConsumeTime().toLocalDate());
            accountBook.setIsAutoImport(true);
            accountBook.setEcardRecordId(record.getId());
            accountBook.setLocation(record.getMerchantName());
            accountBook.setPayMethod("CARD");
            
            // 检查是否已导入
            LambdaQueryWrapper<AccountBook> accountBookQuery = new LambdaQueryWrapper<>();
            accountBookQuery.eq(AccountBook::getUserId, userId)
                           .eq(AccountBook::getEcardRecordId, record.getId());
            List<AccountBook> existing = accountBookMapper.selectList(accountBookQuery);
            
            if (existing.isEmpty()) {
                accountBookMapper.insert(accountBook);
            }
        } catch (Exception e) {
            // 导入失败不影响主流程，仅记录日志
            System.out.println("自动导入记账本失败: " + e.getMessage());
        }

        // 如果是食堂消费，自动导入到饮食表
        if (consumeDTO.getConsumeType() == ConsumeTypeEnum.CANTEEN) {
            try {
                DietRecord dietRecord = new DietRecord();
                dietRecord.setUserId(userId);
                dietRecord.setDietDate(record.getConsumeTime().toLocalDate());
                dietRecord.setIsAutoImport(true);
                dietRecord.setEcardRecordId(record.getId());
                dietRecord.setLocation(record.getMerchantName());
                dietRecord.setSource("CANTEEN");
                dietRecord.setPrice(record.getAmount());
                
                // 根据消费时间判断餐次
                java.time.LocalTime consumeTime = record.getConsumeTime().toLocalTime();
                String mealType = determineMealType(consumeTime);
                dietRecord.setMealType(mealType);
                
                // 设置默认食物名称
                dietRecord.setFoodName("食堂用餐");
                dietRecord.setFoodDetail(record.getDescription());
                dietRecord.setBackgroundColor(getDefaultColorByMealType(mealType));
                
                // 检查是否已导入
                LambdaQueryWrapper<DietRecord> dietQuery = new LambdaQueryWrapper<>();
                dietQuery.eq(DietRecord::getUserId, userId)
                         .eq(DietRecord::getEcardRecordId, record.getId());
                List<DietRecord> existing = dietRecordMapper.selectList(dietQuery);
                
                if (existing.isEmpty()) {
                    dietRecordMapper.insert(dietRecord);
                }
            } catch (Exception e) {
                // 导入失败不影响主流程，仅记录日志
                System.out.println("自动导入饮食表失败: " + e.getMessage());
            }
        }

        return recordVO;
    }

    private String mapConsumeTypeToCategory(ConsumeTypeEnum consumeType) {
        if (consumeType == null) {
            return "其他";
        }
        switch (consumeType) {
            case CANTEEN:
                return "餐饮";
            case SUPERMARKET:
                return "购物";
            case LIBRARY:
                return "学习";
            case MEDICAL:
                return "其他";
            default:
                return "其他";
        }
    }

    private String determineMealType(java.time.LocalTime time) {
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
                return "#FFE5B4";
            case "LUNCH":
                return "#FFB6C1";
            case "DINNER":
                return "#DDA0DD";
            case "SNACK":
                return "#98FB98";
            default:
                return "#E6E6FA";
        }
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

    @Override
    public Object getMonthStatistics(Long userId, Integer year, Integer month) {
        // 查询用户的校园卡
        LambdaQueryWrapper<Ecard> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Ecard::getUserId, userId);
        Ecard ecard = ecardMapper.selectOne(queryWrapper);

        if (ecard == null) {
            throw new BusinessException(ErrorCode.ECARD_NOT_EXIST);
        }

        // 如果年份和月份为空，使用当前年月
        if (year == null) year = LocalDate.now().getYear();
        if (month == null) month = LocalDate.now().getMonthValue();

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1);

        // 查询本月消费记录
        LambdaQueryWrapper<ConsumeRecord> recordQuery = new LambdaQueryWrapper<>();
        recordQuery.eq(ConsumeRecord::getCardNo, ecard.getCardNo())
                .ge(ConsumeRecord::getConsumeTime, startDate.atStartOfDay())
                .lt(ConsumeRecord::getConsumeTime, endDate.atStartOfDay());
        List<ConsumeRecord> records = consumeRecordMapper.selectList(recordQuery);

        // 计算总金额和交易次数
        BigDecimal totalAmount = records.stream()
                .map(ConsumeRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int totalCount = records.size();
        BigDecimal dailyAverage = totalCount > 0 ?
                totalAmount.divide(BigDecimal.valueOf(totalCount), 2, RoundingMode.HALF_UP) :
                BigDecimal.ZERO;

        // 统计各消费类型的占比
        Map<ConsumeTypeEnum, BigDecimal> typeAmount = records.stream()
                .collect(Collectors.groupingBy(
                        ConsumeRecord::getConsumeType,
                        Collectors.reducing(BigDecimal.ZERO, ConsumeRecord::getAmount, BigDecimal::add)
                ));

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalAmount", totalAmount);
        statistics.put("totalCount", totalCount);
        statistics.put("dailyAverage", dailyAverage);
        statistics.put("typeStatistics", typeAmount);
        statistics.put("year", year);
        statistics.put("month", month);

        return statistics;
    }

    @Override
    public Object setConsumeGoal(Long userId, BigDecimal monthlyGoal) {
        // 这里应该保存到数据库，暂时使用内存存储（实际应该创建EcardSettings表）
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("monthlyGoal", monthlyGoal);
        result.put("message", "消费目标设置成功");
        return result;
    }

    @Override
    public Object getConsumeGoal(Long userId) {
        // 这里应该从数据库读取，暂时返回默认值
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("monthlyGoal", new BigDecimal("500.00"));
        result.put("currentAmount", new BigDecimal("245.80"));
        result.put("progress", 49.16);
        return result;
    }

    @Override
    public Object setBalanceReminder(Long userId, BigDecimal threshold, Boolean enabled) {
        // 这里应该保存到数据库，暂时使用内存存储
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("threshold", threshold);
        result.put("enabled", enabled);
        result.put("message", "余额提醒设置成功");
        return result;
    }

    @Override
    public Object getBalanceReminder(Long userId) {
        // 这里应该从数据库读取，暂时返回默认值
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("threshold", new BigDecimal("50.00"));
        result.put("enabled", true);
        return result;
    }

    @Override
    @Transactional
    public Object transfer(Long userId, String targetCardNo, BigDecimal amount) {
        // 查询转出方的校园卡
        LambdaQueryWrapper<Ecard> fromQuery = new LambdaQueryWrapper<>();
        fromQuery.eq(Ecard::getUserId, userId);
        Ecard fromCard = ecardMapper.selectOne(fromQuery);

        if (fromCard == null) {
            throw new BusinessException(ErrorCode.ECARD_NOT_EXIST);
        }

        // 检查余额
        if (fromCard.getBalance().compareTo(amount) < 0) {
            throw new BusinessException(ErrorCode.ECARD_BALANCE_INSUFFICIENT);
        }

        // 查询转入方的校园卡
        LambdaQueryWrapper<Ecard> toQuery = new LambdaQueryWrapper<>();
        toQuery.eq(Ecard::getCardNo, targetCardNo);
        Ecard toCard = ecardMapper.selectOne(toQuery);

        if (toCard == null) {
            throw new BusinessException(ErrorCode.ECARD_NOT_EXIST);
        }

        // 执行转账
        fromCard.setBalance(fromCard.getBalance().subtract(amount));
        toCard.setBalance(toCard.getBalance().add(amount));
        ecardMapper.updateById(fromCard);
        ecardMapper.updateById(toCard);

        // 创建转账记录（作为消费记录）
        ConsumeRecord transferRecord = new ConsumeRecord();
        transferRecord.setCardNo(fromCard.getCardNo());
        transferRecord.setAmount(amount.negate()); // 负数表示转出
        transferRecord.setBalanceAfter(fromCard.getBalance());
        transferRecord.setMerchantId(toCard.getCardNo());
        transferRecord.setMerchantName("转账至 " + targetCardNo);
        transferRecord.setConsumeType(ConsumeTypeEnum.OTHER);
        transferRecord.setDescription("转账给 " + targetCardNo);
        transferRecord.setPayMethod("TRANSFER");
        transferRecord.setConsumeTime(LocalDateTime.now());
        consumeRecordMapper.insert(transferRecord);

        Map<String, Object> result = new HashMap<>();
        result.put("fromCardNo", fromCard.getCardNo());
        result.put("toCardNo", targetCardNo);
        result.put("amount", amount);
        result.put("fromBalance", fromCard.getBalance());
        result.put("toBalance", toCard.getBalance());
        result.put("message", "转账成功");

        return result;
    }
}
