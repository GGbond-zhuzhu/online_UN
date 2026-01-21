import { Schedule, DailyMood } from '../types';

const STORAGE_KEY = {
  SCHEDULE_LIST: 'schedule_list',
  DAILY_MOOD: 'daily_mood',
  SHARE_LIST: 'share_list'
};

// 行程数据-增删改查
export const scheduleStorage = {
  get(): Schedule[] {
    const data = localStorage.getItem(STORAGE_KEY.SCHEDULE_LIST);
    return data ? JSON.parse(data) : [];
  },
  set(list: Schedule[]) {
    localStorage.setItem(STORAGE_KEY.SCHEDULE_LIST, JSON.stringify(list));
  },
  add(item: Schedule) {
    const list = this.get();
    list.push(item);
    this.set(list);
  },
  update(id: string, newItem: Partial<Schedule>) {
    const list = this.get();
    const index = list.findIndex(item => item.id === id);
    if (index > -1) {
      list[index] = { ...list[index], ...newItem };
      this.set(list);
    }
  },
  delete(id: string) {
    const list = this.get().filter(item => item.id !== id);
    this.set(list);
  }
};

// 每日心情存储
export const moodStorage = {
  get(): DailyMood[] {
    const data = localStorage.getItem(STORAGE_KEY.DAILY_MOOD);
    return data ? JSON.parse(data) : [];
  },
  set(list: DailyMood[]) {
    localStorage.setItem(STORAGE_KEY.DAILY_MOOD, JSON.stringify(list));
  },
  getByDate(dateKey: string): string | null {
    const mood = this.get().find(item => item.dateKey === dateKey);
    return mood ? mood.moodIcon : null;
  },
  setByDate(dateKey: string, moodIcon: string) {
    const list = this.get().filter(item => item.dateKey !== dateKey);
    list.push({ dateKey, moodIcon });
    this.set(list);
  }
};
