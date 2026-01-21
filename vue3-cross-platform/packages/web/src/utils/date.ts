import dayjs from 'dayjs';
import 'dayjs/locale/zh-cn';
dayjs.locale('zh-cn');

// 获取格式化的日期键 2026-01-11
export const getDateKey = (date: dayjs.Dayjs = dayjs()): string => {
  return date.format('YYYY-MM-DD');
};

// 获取日期中文格式 2026年1月11日 星期日
export const formatDateCN = (date: dayjs.Dayjs = dayjs()): string => {
  return date.format('YYYY年MM月DD日 dddd');
};

// 获取周开始日期（周一）
export const getWeekStart = (date: dayjs.Dayjs = dayjs()): dayjs.Dayjs => {
  const day = date.day();
  const diff = day === 0 ? -6 : 1 - day;
  return date.add(diff, 'day');
};

// 生成当日时间槽 6:00-23:00
export const generateTimeSlots = () => {
  const slots: string[] = [];
  for (let h = 6; h <= 23; h++) {
    slots.push(`${h}:00`);
  }
  return slots;
};

// 简化农历转换（可自行集成完整农历库）
export const getLunarDate = (date: dayjs.Dayjs): string => {
  const lunarDays = ['初一','初二','初三','初四','初五','初六','初七','初八','初九','初十',
    '十一','十二','十三','十四','十五','十六','十七','十八','十九','二十',
    '廿一','廿二','廿三','廿四','廿五','廿六','廿七','廿八','廿九','三十'];
  return lunarDays[(date.date()-1) % 30];
};

export default dayjs;
