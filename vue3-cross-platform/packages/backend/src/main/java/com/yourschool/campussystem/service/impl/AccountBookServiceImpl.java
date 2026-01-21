package com.yourschool.campussystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yourschool.campussystem.common.ErrorCode;
import com.yourschool.campussystem.dto.AccountBookDTO;
import com.yourschool.campussystem.entity.AccountBook;
import com.yourschool.campussystem.entity.ConsumeRecord;
import com.yourschool.campussystem.entity.Ecard;
import com.yourschool.campussystem.enums.ConsumeTypeEnum;
import com.yourschool.campussystem.exception.BusinessException;
import com.yourschool.campussystem.mapper.AccountBookMapper;
import com.yourschool.campussystem.mapper.ConsumeRecordMapper;
import com.yourschool.campussystem.mapper.EcardMapper;
import com.yourschool.campussystem.service.AccountBookService;
import com.yourschool.campussystem.vo.AccountBookVO;
import com.yourschool.campussystem.vo.MonthlyReportVO;
import com.yourschool.campussystem.vo.WeeklyReportVO;
import com.yourschool.campussystem.vo.YearlyReportVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 记账本服务实现类
 */
@Service
@RequiredArgsConstructor
public class AccountBookServiceImpl extends ServiceImpl<AccountBookMapper, AccountBook> implements AccountBookService {

    private final AccountBookMapper accountBookMapper;
    private final ConsumeRecordMapper consumeRecordMapper;
    private final EcardMapper ecardMapper;

    @Override
    @Transactional
    public AccountBookVO addRecord(Long userId, AccountBookDTO dto) {
        AccountBook record = new AccountBook();
        BeanUtils.copyProperties(dto, record);
        record.setUserId(userId);
        record.setConsumeDate(dto.getConsumeDate() != null ? dto.getConsumeDate() : LocalDate.now());
        record.setIsAutoImport(false);
        if (!StringUtils.hasText(record.getPayMethod())) {
            record.setPayMethod("CARD");
        }
        accountBookMapper.insert(record);
        return convertToVO(record);
    }

    @Override
    @Transactional
    public void deleteRecord(Long userId, Long recordId) {
        AccountBook record = accountBookMapper.selectById(recordId);
        if (record == null || !record.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "记录不存在");
        }
        accountBookMapper.deleteById(recordId);
    }

    @Override
    @Transactional
    public AccountBookVO updateRecord(Long userId, Long recordId, AccountBookDTO dto) {
        AccountBook record = accountBookMapper.selectById(recordId);
        if (record == null || !record.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "记录不存在");
        }

        // 仅更新可编辑字段（保留自动导入标识/关联ID等）
        record.setAmount(dto.getAmount());
        record.setCategory(dto.getCategory());
        record.setDescription(dto.getDescription());
        record.setConsumeDate(dto.getConsumeDate() != null ? dto.getConsumeDate() : record.getConsumeDate());
        record.setLocation(dto.getLocation());
        if (StringUtils.hasText(dto.getPayMethod())) {
            record.setPayMethod(dto.getPayMethod());
        }

        accountBookMapper.updateById(record);
        return convertToVO(record);
    }

    @Override
    public List<AccountBookVO> getRecords(Long userId, LocalDate startDate, LocalDate endDate, String category, Integer page, Integer size) {
        LambdaQueryWrapper<AccountBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountBook::getUserId, userId);
        if (startDate != null) {
            queryWrapper.ge(AccountBook::getConsumeDate, startDate);
        }
        if (endDate != null) {
            queryWrapper.le(AccountBook::getConsumeDate, endDate);
        }
        if (StringUtils.hasText(category)) {
            queryWrapper.eq(AccountBook::getCategory, category);
        }

        queryWrapper.orderByDesc(AccountBook::getConsumeDate);
        queryWrapper.orderByDesc(AccountBook::getCreateTime);

        Page<AccountBook> pageObj = new Page<>(page, size);
        Page<AccountBook> result = accountBookMapper.selectPage(pageObj, queryWrapper);

        return result.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
    }

    @Override
    public WeeklyReportVO getWeeklyReport(Long userId, LocalDate weekStartDate) {
        LocalDate start = weekStartDate != null
                ? weekStartDate
                : LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate end = start.plusDays(6);

        List<AccountBook> records = queryRecords(userId, start, end);

        WeeklyReportVO vo = new WeeklyReportVO();
        vo.setWeekRange(formatRange(start, end));
        fillCommonReportStats(vo, records, start, end);
        return vo;
    }

    @Override
    @Transactional
    public void autoImportFromEcard(Long userId, LocalDate startDate, LocalDate endDate) {
        // 查询用户校园卡
        Ecard ecard = ecardMapper.selectOne(new LambdaQueryWrapper<Ecard>().eq(Ecard::getUserId, userId));
        if (ecard == null) {
            return;
        }

        LocalDateTime startTime = (startDate != null) ? startDate.atStartOfDay() : LocalDate.now().minusDays(30).atStartOfDay();
        LocalDateTime endTime = (endDate != null) ? endDate.plusDays(1).atStartOfDay() : LocalDate.now().plusDays(1).atStartOfDay();

        LambdaQueryWrapper<ConsumeRecord> query = new LambdaQueryWrapper<>();
        query.eq(ConsumeRecord::getCardNo, ecard.getCardNo())
                .ge(ConsumeRecord::getConsumeTime, startTime)
                .lt(ConsumeRecord::getConsumeTime, endTime);

        List<ConsumeRecord> consumeRecords = consumeRecordMapper.selectList(query);
        for (ConsumeRecord cr : consumeRecords) {
            // 是否已导入
            AccountBook existing = accountBookMapper.selectOne(
                    new LambdaQueryWrapper<AccountBook>()
                            .eq(AccountBook::getUserId, userId)
                            .eq(AccountBook::getEcardRecordId, cr.getId())
            );
            if (existing != null) {
                continue;
            }

            AccountBook record = new AccountBook();
            record.setUserId(userId);
            record.setAmount(cr.getAmount());
            record.setConsumeDate(cr.getConsumeTime() != null ? cr.getConsumeTime().toLocalDate() : LocalDate.now());
            record.setIsAutoImport(true);
            record.setEcardRecordId(cr.getId());
            record.setLocation(cr.getMerchantName());
            record.setPayMethod(StringUtils.hasText(cr.getPayMethod()) ? cr.getPayMethod() : "CARD");
            record.setDescription(cr.getDescription());
            record.setCategory(mapCategoryFromConsumeType(cr.getConsumeType()));

            accountBookMapper.insert(record);
        }
    }

    @Override
    public MonthlyReportVO getMonthlyReport(Long userId, LocalDate monthStartDate) {
        LocalDate anyDate = monthStartDate != null ? monthStartDate : LocalDate.now();
        YearMonth ym = YearMonth.of(anyDate.getYear(), anyDate.getMonth());
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        List<AccountBook> records = queryRecords(userId, start, end);

        MonthlyReportVO vo = new MonthlyReportVO();
        vo.setMonthRange(formatRange(start, end));
        BigDecimal totalAmount = sumAmount(records);
        vo.setTotalAmount(totalAmount);
        vo.setTotalCount(records.size());
        vo.setAvgDailyAmount(divideSafe(totalAmount, BigDecimal.valueOf(ym.lengthOfMonth()), 2));
        vo.setCategoryStats(buildCategoryStatsMonthly(records));
        vo.setLocationStats(buildLocationStatsMonthly(records));
        vo.setDailyStats(buildDailyStatsMonthly(records, start, end));

        // comparison：与上月对比
        YearMonth lastYm = ym.minusMonths(1);
        LocalDate lastStart = lastYm.atDay(1);
        LocalDate lastEnd = lastYm.atEndOfMonth();
        List<AccountBook> lastRecords = queryRecords(userId, lastStart, lastEnd);
        BigDecimal lastAmount = sumAmount(lastRecords);

        MonthlyReportVO.ComparisonVO cmp = new MonthlyReportVO.ComparisonVO();
        cmp.setLastMonthAmount(lastAmount);
        cmp.setLastMonthCount(lastRecords.size());
        cmp.setAmountChange(totalAmount.subtract(lastAmount));
        cmp.setAmountChangePercent(calcChangePercent(lastAmount, totalAmount));
        vo.setComparison(cmp);

        return vo;
    }

    @Override
    public YearlyReportVO getYearlyReport(Long userId, LocalDate yearStartDate) {
        LocalDate anyDate = yearStartDate != null ? yearStartDate : LocalDate.now();
        LocalDate start = LocalDate.of(anyDate.getYear(), 1, 1);
        LocalDate end = LocalDate.of(anyDate.getYear(), 12, 31);

        List<AccountBook> records = queryRecords(userId, start, end);

        YearlyReportVO vo = new YearlyReportVO();
        vo.setYearRange(formatRange(start, end));

        BigDecimal totalAmount = sumAmount(records);
        vo.setTotalAmount(totalAmount);
        vo.setTotalCount(records.size());
        vo.setAvgMonthlyAmount(divideSafe(totalAmount, BigDecimal.valueOf(12), 2));

        vo.setCategoryStats(buildCategoryStatsYearly(records));
        vo.setLocationStats(buildLocationStatsYearly(records));
        vo.setMonthlyStats(buildMonthlyStats(records, anyDate.getYear()));

        // comparison：与去年对比
        LocalDate lastStart = start.minusYears(1);
        LocalDate lastEnd = end.minusYears(1);
        List<AccountBook> lastRecords = queryRecords(userId, lastStart, lastEnd);
        BigDecimal lastAmount = sumAmount(lastRecords);

        YearlyReportVO.ComparisonVO cmp = new YearlyReportVO.ComparisonVO();
        cmp.setLastYearAmount(lastAmount);
        cmp.setLastYearCount(lastRecords.size());
        cmp.setAmountChange(totalAmount.subtract(lastAmount));
        cmp.setAmountChangePercent(calcChangePercent(lastAmount, totalAmount));
        vo.setComparison(cmp);

        return vo;
    }

    @Override
    public Object getStatistics(Long userId, LocalDate startDate, LocalDate endDate) {
        List<AccountBook> records = queryRecords(userId, startDate, endDate);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalAmount", sumAmount(records));
        stats.put("totalCount", records.size());

        Map<String, Long> categoryCount = records.stream()
                .collect(Collectors.groupingBy(r -> safeText(r.getCategory(), "其他"), Collectors.counting()));
        stats.put("categoryCount", categoryCount);

        return stats;
    }

    // ==================== 内部辅助 ====================

    private List<AccountBook> queryRecords(Long userId, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<AccountBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AccountBook::getUserId, userId);
        if (startDate != null) {
            wrapper.ge(AccountBook::getConsumeDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(AccountBook::getConsumeDate, endDate);
        }
        return accountBookMapper.selectList(wrapper);
    }

    private void fillCommonReportStats(WeeklyReportVO vo, List<AccountBook> records, LocalDate start, LocalDate end) {
        BigDecimal totalAmount = sumAmount(records);
        vo.setTotalAmount(totalAmount);
        vo.setTotalCount(records.size());
        long days = Math.max(1, start.datesUntil(end.plusDays(1)).count());
        vo.setAvgDailyAmount(divideSafe(totalAmount, BigDecimal.valueOf(days), 2));
        vo.setCategoryStats(buildCategoryStatsWeekly(records));
        vo.setLocationStats(buildLocationStatsWeekly(records));
        vo.setDailyStats(buildDailyStatsWeekly(records, start, end));
    }

    private BigDecimal sumAmount(List<AccountBook> records) {
        return records.stream()
                .map(r -> r.getAmount() != null ? r.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal divideSafe(BigDecimal numerator, BigDecimal denominator, int scale) {
        if (denominator == null || denominator.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return numerator.divide(denominator, scale, RoundingMode.HALF_UP);
    }

    private Double calcChangePercent(BigDecimal lastAmount, BigDecimal currentAmount) {
        if (lastAmount == null || lastAmount.compareTo(BigDecimal.ZERO) == 0) {
            return currentAmount != null && currentAmount.compareTo(BigDecimal.ZERO) != 0 ? 100.0 : 0.0;
        }
        BigDecimal change = currentAmount.subtract(lastAmount);
        BigDecimal percent = change.multiply(BigDecimal.valueOf(100)).divide(lastAmount, 2, RoundingMode.HALF_UP);
        return percent.doubleValue();
    }

    private String formatRange(LocalDate start, LocalDate end) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return start.format(fmt) + " ~ " + end.format(fmt);
    }

    private String safeText(String text, String defaultValue) {
        return StringUtils.hasText(text) ? text : defaultValue;
    }

    private String mapCategoryFromConsumeType(ConsumeTypeEnum type) {
        if (type == null) return "其他";
        if (type == ConsumeTypeEnum.CANTEEN) return "餐饮";
        // 其他类型暂时统一归为“其他”，后续可细分
        return "其他";
    }

    private List<WeeklyReportVO.CategoryStatVO> buildCategoryStatsWeekly(List<AccountBook> records) {
        BigDecimal total = sumAmount(records);
        Map<String, List<AccountBook>> grouped = records.stream()
                .collect(Collectors.groupingBy(r -> safeText(r.getCategory(), "其他")));

        return grouped.entrySet().stream()
                .map(e -> {
                    WeeklyReportVO.CategoryStatVO vo = new WeeklyReportVO.CategoryStatVO();
                    vo.setCategory(e.getKey());
                    BigDecimal amount = sumAmount(e.getValue());
                    vo.setAmount(amount);
                    vo.setCount(e.getValue().size());
                    vo.setPercentage(calcPercent(total, amount));
                    return vo;
                })
                .sorted(Comparator.comparing(WeeklyReportVO.CategoryStatVO::getAmount).reversed())
                .collect(Collectors.toList());
    }

    private List<WeeklyReportVO.LocationStatVO> buildLocationStatsWeekly(List<AccountBook> records) {
        BigDecimal total = sumAmount(records);
        Map<String, List<AccountBook>> grouped = records.stream()
                .collect(Collectors.groupingBy(r -> safeText(r.getLocation(), "未知")));

        return grouped.entrySet().stream()
                .map(e -> {
                    WeeklyReportVO.LocationStatVO vo = new WeeklyReportVO.LocationStatVO();
                    vo.setLocation(e.getKey());
                    BigDecimal amount = sumAmount(e.getValue());
                    vo.setAmount(amount);
                    vo.setCount(e.getValue().size());
                    vo.setPercentage(calcPercent(total, amount));
                    return vo;
                })
                .sorted(Comparator.comparing(WeeklyReportVO.LocationStatVO::getAmount).reversed())
                .collect(Collectors.toList());
    }

    private List<WeeklyReportVO.DailyStatVO> buildDailyStatsWeekly(List<AccountBook> records, LocalDate start, LocalDate end) {
        Map<LocalDate, List<AccountBook>> grouped = records.stream()
                .collect(Collectors.groupingBy(AccountBook::getConsumeDate));
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<WeeklyReportVO.DailyStatVO> list = new ArrayList<>();
        LocalDate cur = start;
        while (!cur.isAfter(end)) {
            List<AccountBook> dayRecords = grouped.getOrDefault(cur, Collections.emptyList());
            WeeklyReportVO.DailyStatVO vo = new WeeklyReportVO.DailyStatVO();
            vo.setDate(cur.format(fmt));
            vo.setAmount(sumAmount(dayRecords));
            vo.setCount(dayRecords.size());
            list.add(vo);
            cur = cur.plusDays(1);
        }
        return list;
    }

    private Double calcPercent(BigDecimal total, BigDecimal part) {
        if (total == null || total.compareTo(BigDecimal.ZERO) == 0) return 0.0;
        BigDecimal p = part.multiply(BigDecimal.valueOf(100)).divide(total, 2, RoundingMode.HALF_UP);
        return p.doubleValue();
    }

    private List<MonthlyReportVO.CategoryStatVO> buildCategoryStatsMonthly(List<AccountBook> records) {
        BigDecimal total = sumAmount(records);
        Map<String, List<AccountBook>> grouped = records.stream()
                .collect(Collectors.groupingBy(r -> safeText(r.getCategory(), "其他")));

        return grouped.entrySet().stream()
                .map(e -> {
                    MonthlyReportVO.CategoryStatVO vo = new MonthlyReportVO.CategoryStatVO();
                    vo.setCategory(e.getKey());
                    BigDecimal amount = sumAmount(e.getValue());
                    vo.setAmount(amount);
                    vo.setCount(e.getValue().size());
                    vo.setPercentage(calcPercent(total, amount));
                    return vo;
                })
                .sorted(Comparator.comparing(MonthlyReportVO.CategoryStatVO::getAmount).reversed())
                .collect(Collectors.toList());
    }

    private List<MonthlyReportVO.LocationStatVO> buildLocationStatsMonthly(List<AccountBook> records) {
        BigDecimal total = sumAmount(records);
        Map<String, List<AccountBook>> grouped = records.stream()
                .collect(Collectors.groupingBy(r -> safeText(r.getLocation(), "未知")));

        return grouped.entrySet().stream()
                .map(e -> {
                    MonthlyReportVO.LocationStatVO vo = new MonthlyReportVO.LocationStatVO();
                    vo.setLocation(e.getKey());
                    BigDecimal amount = sumAmount(e.getValue());
                    vo.setAmount(amount);
                    vo.setCount(e.getValue().size());
                    vo.setPercentage(calcPercent(total, amount));
                    return vo;
                })
                .sorted(Comparator.comparing(MonthlyReportVO.LocationStatVO::getAmount).reversed())
                .collect(Collectors.toList());
    }

    private List<MonthlyReportVO.DailyStatVO> buildDailyStatsMonthly(List<AccountBook> records, LocalDate start, LocalDate end) {
        Map<LocalDate, List<AccountBook>> grouped = records.stream()
                .collect(Collectors.groupingBy(AccountBook::getConsumeDate));
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        List<MonthlyReportVO.DailyStatVO> list = new ArrayList<>();
        LocalDate cur = start;
        while (!cur.isAfter(end)) {
            List<AccountBook> dayRecords = grouped.getOrDefault(cur, Collections.emptyList());
            MonthlyReportVO.DailyStatVO vo = new MonthlyReportVO.DailyStatVO();
            vo.setDate(cur.format(fmt));
            vo.setAmount(sumAmount(dayRecords));
            vo.setCount(dayRecords.size());
            list.add(vo);
            cur = cur.plusDays(1);
        }
        return list;
    }

    private List<YearlyReportVO.CategoryStatVO> buildCategoryStatsYearly(List<AccountBook> records) {
        BigDecimal total = sumAmount(records);
        Map<String, List<AccountBook>> grouped = records.stream()
                .collect(Collectors.groupingBy(r -> safeText(r.getCategory(), "其他")));

        return grouped.entrySet().stream()
                .map(e -> {
                    YearlyReportVO.CategoryStatVO vo = new YearlyReportVO.CategoryStatVO();
                    vo.setCategory(e.getKey());
                    BigDecimal amount = sumAmount(e.getValue());
                    vo.setAmount(amount);
                    vo.setCount(e.getValue().size());
                    vo.setPercentage(calcPercent(total, amount));
                    return vo;
                })
                .sorted(Comparator.comparing(YearlyReportVO.CategoryStatVO::getAmount).reversed())
                .collect(Collectors.toList());
    }

    private List<YearlyReportVO.LocationStatVO> buildLocationStatsYearly(List<AccountBook> records) {
        BigDecimal total = sumAmount(records);
        Map<String, List<AccountBook>> grouped = records.stream()
                .collect(Collectors.groupingBy(r -> safeText(r.getLocation(), "未知")));

        return grouped.entrySet().stream()
                .map(e -> {
                    YearlyReportVO.LocationStatVO vo = new YearlyReportVO.LocationStatVO();
                    vo.setLocation(e.getKey());
                    BigDecimal amount = sumAmount(e.getValue());
                    vo.setAmount(amount);
                    vo.setCount(e.getValue().size());
                    vo.setPercentage(calcPercent(total, amount));
                    return vo;
                })
                .sorted(Comparator.comparing(YearlyReportVO.LocationStatVO::getAmount).reversed())
                .collect(Collectors.toList());
    }

    private List<YearlyReportVO.MonthlyStatVO> buildMonthlyStats(List<AccountBook> records, int year) {
        Map<Integer, List<AccountBook>> grouped = records.stream()
                .filter(r -> r.getConsumeDate() != null)
                .collect(Collectors.groupingBy(r -> r.getConsumeDate().getMonthValue()));

        List<YearlyReportVO.MonthlyStatVO> list = new ArrayList<>();
        for (int m = 1; m <= 12; m++) {
            List<AccountBook> monthRecords = grouped.getOrDefault(m, Collections.emptyList());
            YearlyReportVO.MonthlyStatVO vo = new YearlyReportVO.MonthlyStatVO();
            vo.setMonth(String.format("%04d-%02d", year, m));
            vo.setAmount(sumAmount(monthRecords));
            vo.setCount(monthRecords.size());
            list.add(vo);
        }
        return list;
    }

    private AccountBookVO convertToVO(AccountBook record) {
        AccountBookVO vo = new AccountBookVO();
        BeanUtils.copyProperties(record, vo);
        return vo;
    }
}

