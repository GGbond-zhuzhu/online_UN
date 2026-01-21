import * as XLSX from 'xlsx';
import { Schedule, ScheduleCategory, ScheduleStatus } from '../types';
import { getDateKey } from './date';
import dayjs from './date';

// 课程表模板表头
const TEMPLATE_HEADER = [
  '行程标题', '分类(课程/考试/社团/兼职/个人)', '开始时间', '结束时间',
  '日期(2026-01-11)', '地点', '描述'
];

// 行程分类映射
export const categoryMap: Record<string, ScheduleCategory> = {
  '课程': ScheduleCategory.COURSE,
  '考试': ScheduleCategory.EXAM,
  '社团': ScheduleCategory.CLUB,
  '兼职': ScheduleCategory.PART_TIME,
  '个人': ScheduleCategory.PERSONAL,
  'course': ScheduleCategory.COURSE,
  'exam': ScheduleCategory.EXAM,
  'club': ScheduleCategory.CLUB,
  'part-time': ScheduleCategory.PART_TIME,
  'personal': ScheduleCategory.PERSONAL
};

// 分类颜色映射
export const categoryColorMap: Record<ScheduleCategory, string> = {
  [ScheduleCategory.ALL]: '#D0D0D0',
  [ScheduleCategory.COURSE]: '#FFB6C1',
  [ScheduleCategory.EXAM]: '#B0E0E6',
  [ScheduleCategory.CLUB]: '#FFF8DC',
  [ScheduleCategory.PART_TIME]: '#98FB98',
  [ScheduleCategory.PERSONAL]: '#DDA0DD'
};

// 导出行程数据为Excel
export const exportSchedulesToExcel = (schedules: Schedule[]): void => {
  // 转换数据格式
  const exportData = schedules.map(schedule => [
    schedule.title,
    Object.keys(categoryMap).find(key => categoryMap[key] === schedule.category) || '个人',
    schedule.startTime,
    schedule.endTime,
    schedule.dateKey,
    schedule.location,
    schedule.description
  ]);

  // 创建工作表
  const ws = XLSX.utils.aoa_to_sheet([TEMPLATE_HEADER, ...exportData]);
  const wb = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(wb, ws, '行程表');

  // 设置列宽
  ws['!cols'] = [
    { wch: 20 }, // 行程标题
    { wch: 15 }, // 分类
    { wch: 10 }, // 开始时间
    { wch: 10 }, // 结束时间
    { wch: 15 }, // 日期
    { wch: 20 }, // 地点
    { wch: 30 }  // 描述
  ];

  // 导出文件
  XLSX.writeFile(wb, `行程表_${dayjs().format('YYYY-MM-DD')}.xlsx`);
};

// 从Excel文件导入行程数据
export const importSchedulesFromExcel = (file: File): Promise<Schedule[]> => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    
    reader.onload = (e) => {
      try {
        const data = new Uint8Array(e.target?.result as ArrayBuffer);
        const wb = XLSX.read(data, { type: 'array' });
        const ws = wb.Sheets[wb.SheetNames[0]];
        const jsonData = XLSX.utils.sheet_to_json(ws, { header: 1 });

        // 验证表头
        const header = jsonData[0];
        if (!Array.isArray(header) || header.length < 5) {
          reject(new Error('Excel文件格式不正确，缺少必要的表头'));
          return;
        }

        // 转换数据
        const schedules: Schedule[] = [];
        for (let i = 1; i < jsonData.length; i++) {
          const row = jsonData[i] as any[];
          if (!row[0] || !row[2] || !row[4]) continue; // 跳过空行

          const categoryStr = (row[1] || '').toString().trim();
          const category = categoryMap[categoryStr] || ScheduleCategory.PERSONAL;

          const schedule: Schedule = {
            id: crypto.randomUUID(),
            title: (row[0] || '').toString().trim(),
            category: category,
            startTime: (row[2] || '').toString().trim(),
            endTime: (row[3] || '').toString().trim(),
            dateKey: (row[4] || '').toString().trim(),
            location: (row[5] || '').toString().trim(),
            description: (row[6] || '').toString().trim(),
            status: ScheduleStatus.UPCOMING,
            sharedWith: [],
            color: categoryColorMap[category]
          };

          schedules.push(schedule);
        }

        resolve(schedules);
      } catch (error) {
        reject(new Error('解析Excel文件失败：' + (error as Error).message));
      }
    };

    reader.onerror = () => {
      reject(new Error('读取文件失败'));
    };

    reader.readAsArrayBuffer(file);
  });
};

// 生成行程模板Excel
export const generateScheduleTemplate = (): void => {
  // 创建示例数据
  const exampleData = [
    ['高等数学', '课程', '08:00', '09:40', '2026-01-13', '主教学楼101', '每周一、三、五'],
    ['大学英语', '课程', '10:00', '11:40', '2026-01-14', '东校区203', '每周二、四'],
    ['期末考试-高数', '考试', '09:00', '11:00', '2026-01-18', '主教学楼101', '闭卷考试'],
    ['社团活动', '社团', '14:30', '17:30', '2026-01-19', '学生活动中心', '技术分享会'],
    ['兼职工作', '兼职', '18:00', '21:00', '2026-01-20', '图书馆', '整理书籍']
  ];

  // 创建工作表
  const ws = XLSX.utils.aoa_to_sheet([TEMPLATE_HEADER, ...exampleData]);
  const wb = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(wb, ws, '行程表模板');

  // 设置列宽
  ws['!cols'] = [
    { wch: 20 }, // 行程标题
    { wch: 15 }, // 分类
    { wch: 10 }, // 开始时间
    { wch: 10 }, // 结束时间
    { wch: 15 }, // 日期
    { wch: 20 }, // 地点
    { wch: 30 }  // 描述
  ];

  // 导出模板文件
  XLSX.writeFile(wb, '行程表导入模板.xlsx');
};
