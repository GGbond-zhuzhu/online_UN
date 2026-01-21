<template>
  <div class="secondhand-page">
    <!-- 顶部导航栏 - 完全复用，无修改 -->
    <NavBar />

    <div class="container">
      <!-- 顶部欢迎区域 -->
      <section class="header-section">
        <div class="greeting">
          <h2 class="greeting-text">你好，今天也要加油哦~</h2>
          <p class="date-text">{{ currentDate }}</p>
        </div>
        <div class="weather-info">
          <i class="fas fa-sun weather-icon"></i>
          <span class="weather-text">{{ weatherInfo }}</span>
        </div>
      </section>

      <!-- 搜索栏区域 - 移到最上方 -->
      <section class="search-bar-section">
        <div class="search-container">
          <div class="search-box">
            <input
              v-model="filters.search"
              type="text"
              placeholder="搜索课程/日程/教室/教师..."
              class="search-input"
              @keyup.enter="handleFilter"
            />
            <i class="fas fa-search search-icon"></i>
          </div>
          <div class="search-actions">
            <button class="action-btn" @click="handleQuick('addSchedule')">
              <i class="fas fa-plus"></i> 新增日程
            </button>
            <button class="action-btn" @click="handleQuick('remind')">
              <i class="fas fa-bell"></i> 日程提醒
            </button>
            <button class="action-btn" @click="handleQuick('profile')">
              <i class="fas fa-user"></i> 个人日程
            </button>
          </div>
        </div>
      </section>

      <!-- 快速操作卡片组 -->
      <section class="quick-actions-section">
        <div class="quick-actions-grid">
          <div class="quick-action-card card-yellow" @click="handleQuick('addSchedule')">
            <div class="action-icon-wrapper icon-yellow">
              <i class="fas fa-calendar-plus"></i>
            </div>
            <span class="action-text">添加行程</span>
          </div>
          <div class="quick-action-card card-blue" @click="handleQuick('timeTable')">
            <div class="action-icon-wrapper icon-blue">
              <i class="fas fa-clock"></i>
            </div>
            <span class="action-text">时间表格</span>
          </div>
          <div class="quick-action-card card-pink" @click="handleQuick('courseTable')">
            <div class="action-icon-wrapper icon-pink">
              <i class="fas fa-graduation-cap"></i>
            </div>
            <span class="action-text">课程表</span>
          </div>
          <div class="quick-action-card card-purple" @click="handleQuick('team')">
            <div class="action-icon-wrapper icon-purple">
              <i class="fas fa-users"></i>
            </div>
            <span class="action-text">团队</span>
          </div>
        </div>
      </section>

      <!-- 今日统计卡片 -->
      <section class="stats-section">
        <div 
          class="stat-card" 
          v-for="(item, idx) in overviewList" 
          :key="idx" 
          :style="{ background: statCardColors[idx] }"
        >
          <div class="stat-number">{{ item.count }}</div>
          <div class="stat-label">{{ item.name }}</div>
        </div>
      </section>


      <!-- 快速筛选栏 - 完全复用样式，替换行程相关筛选条件 -->
      <section class="filter-section">
        <div class="filter-row">
          <div class="filter-group">
            <span class="filter-label">学期：</span>
            <select class="filter-select" v-model="filters.semester">
              <option value="all">全部学期</option>
              <option value="2025-1">2025上学期</option>
              <option value="2025-2">2025下学期</option>
              <option value="2026-1">2026上学期</option>
            </select>
          </div>
          <div class="filter-group">
            <span class="filter-label">周次：</span>
            <select class="filter-select" v-model="filters.week">
              <option value="all">全部周次</option>
              <option value="1-5">第1-5周</option>
              <option value="6-10">第6-10周</option>
              <option value="11-15">第11-15周</option>
              <option value="16-20">第16-20周</option>
            </select>
          </div>
          <div class="filter-group">
            <span class="filter-label">校区：</span>
            <select class="filter-select" v-model="filters.campus">
              <option value="all">全部校区</option>
              <option value="main">主校区</option>
              <option value="east">东校区</option>
              <option value="west">西校区</option>
            </select>
          </div>
          <div class="filter-group">
            <span class="filter-label">类型：</span>
            <select class="filter-select" v-model="filters.type">
              <option value="all">全部类型</option>
              <option value="course">课程安排</option>
              <option value="study">自习规划</option>
              <option value="activity">校园活动</option>
              <option value="exam">考试安排</option>
            </select>
          </div>
          <div class="filter-actions">
            <button class="confirm-btn" @click="handleFilter">确定筛选</button>
            <button class="reset-btn" @click="handleReset">重置</button>
          </div>
        </div>
      </section>

      <!-- 快捷标签筛选 -->
      <section class="filter-tabs-section">
        <div class="filter-tabs-container">
          <div 
            class="filter-tab" 
            v-for="(item, idx) in funcTabs" 
            :key="idx"
            :class="{ active: activeTab === idx }"
            @click="handleTabChange(idx)"
          >
            <i :class="getTabIcon(item.icon)" class="filter-icon"></i>
            <span class="filter-text">{{ item.name }}</span>
          </div>
        </div>
      </section>

      <!-- 本周概览日历 -->
      <section class="week-overview-section">
        <div class="week-overview-card">
          <div class="card-header">
            <h3 class="card-title">本周行程</h3>
            <button class="action-icon-btn" @click="switchToMonth">
              <i class="fas fa-calendar-alt"></i>
            </button>
          </div>
          <div class="calendar-grid">
            <div 
              class="calendar-day" 
              v-for="(day, idx) in weekCalendarDays" 
              :key="idx" 
              :class="{ today: day.isToday }"
            >
              <span class="day-name">{{ day.name }}</span>
              <span class="day-number">{{ day.date }}</span>
              <div class="day-dot" v-if="day.tag && day.tag !== '无'"></div>
            </div>
          </div>
        </div>
      </section>

      <!-- 排序和快捷筛选栏 - 样式不变，替换行程相关选项 -->
      <section class="sort-price-bar">
        <div class="sort-section">
          <span class="sort-label">排序：</span>
          <div 
            v-for="(item, index) in sortOptions" 
            :key="item.value"
            class="sort-item"
            :class="{ active: sortType === item.value }"
            @click="handleSort(item.value)"
          >
            <i :class="item.icon"></i>
            <span>{{ item.label }}</span>
          </div>
        </div>
        <div class="price-quick-section">
          <span class="price-label">快捷筛选：</span>
          <div class="price-quick-btns">
            <div 
              v-for="(item, index) in quickOptions" 
              :key="index"
              class="price-quick-btn"
              :class="{ active: quickIndex === index }"
              @click="handleQuickFilter(index, item.type)"
            >
              {{ item.label }}
            </div>
          </div>
        </div>
      </section>

      <!-- 主要内容区域 - 核心：直接展示课程表+日程表，置顶无隐藏 -->
      <div class="main-content">
        <div class="product-list">
          <!-- ===== 核心1：本周课程表 置顶展示 ===== -->
          <div class="search-result-section" style="margin-bottom: 25px;">
            <h2 class="section-title">
              <i class="fas fa-calendar-check"></i> 本周课程表
              <button class="refresh-btn" type="button" @click="refreshCourseTable" style="margin-left:auto">切换周次</button>
            </h2>
            <div class="course-table-container">
              <div class="course-table">
                <div class="table-header">
                  <div class="table-cell empty"></div>
                  <div class="table-cell" v-for="day in weekDays" :key="day">{{day}}</div>
                </div>
                <div class="table-row" v-for="(section, idx) in sections" :key="idx">
                  <div class="table-cell section-cell">{{idx+1}}节</div>
                  <div class="table-cell" v-for="(day, dayIdx) in weekDays" :key="dayIdx">
                    <div 
                      class="course-card" 
                      v-for="course in getCourseByDayAndSection(dayIdx, idx)" 
                      :key="course.id"
                      :style="{background: course.color}"
                      @click="goToDetail(course.id)"
                    >
                      <div class="course-name">{{course.name}}</div>
                      <div class="course-teacher">{{course.teacher}}</div>
                      <div class="course-place">{{course.place}}</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- ===== 核心2：今日日程表 紧随课程表展示 ===== -->
          <div class="recommendation-section" style="margin-bottom: 25px;">
            <h2 class="section-title">
              <i class="fas fa-list-check"></i> {{todayDate}} 今日日程
              <button class="refresh-btn" type="button" @click="refreshTodaySchedule">刷新</button>
            </h2>
            <!-- 今日待办日程 -->
            <div class="schedule-todo">
              <div class="schedule-header">
                <i class="fas fa-clock"></i> 待办事项
              </div>
              <div class="schedule-list">
                <div 
                  class="schedule-item-card" 
                  v-for="item in filteredTodayScheduleList" 
                  :key="item.id" 
                  @click="goToDetail(item.id)"
                  :style="{ borderLeftColor: getTagBorderColor(item.tag) }"
                >
                  <div class="schedule-time-block">
                    <div class="time-main">{{ item.time }}</div>
                    <div class="time-end" v-if="item.endTime">{{ item.endTime }}</div>
                  </div>
                  <div class="schedule-content">
                    <div class="schedule-header-row">
                      <div class="schedule-title">{{ item.title }}</div>
                      <div class="schedule-tag" :style="{ background: getTagColor(item.tag) }">
                        {{ item.tag }}
                      </div>
                    </div>
                    <div class="schedule-desc" v-if="item.desc">{{ item.desc }}</div>
                    <div class="schedule-meta">
                      <div class="schedule-location" v-if="item.location">
                        <i class="fas fa-map-marker-alt"></i>
                        <span>{{ item.location }}</span>
                      </div>
                      <div class="series-badge-mini" v-if="item.isRouteSeries">
                        <i class="fas fa-route"></i>
                        <span>多地点</span>
                      </div>
                    </div>
                    <div class="remind-badges" v-if="item.remindTypes && item.remindTypes.length > 0">
                      <div class="remind-badge" v-for="(type, typeIdx) in item.remindTypes" :key="typeIdx">
                        <i :class="getRemindMethodIcon(type)" class="remind-icon"></i>
                      </div>
                    </div>
                  </div>
                  <div class="schedule-status" :style="{ color: item.statusColor }">
                    <i class="fas fa-circle status-dot" :style="{ color: item.statusColor }"></i>
                    <div class="status-text">{{ item.status }}</div>
                  </div>
                </div>
                <div class="empty-state" v-if="filteredTodayScheduleList.length === 0">
                  <i class="fas fa-calendar-check empty-icon"></i>
                  <p class="empty-text">{{ activeTab === 0 ? '今天还没有行程，点击上方按钮添加吧~' : `暂无${funcTabs[activeTab]?.name || ''}类型的行程` }}</p>
                </div>
              </div>
            </div>
            <!-- 今日已完成日程 -->
            <div class="schedule-done" style="margin-top:15px;">
              <div class="schedule-header">
                <i class="fas fa-check-circle"></i> 已完成事项
              </div>
              <div class="schedule-grid">
                <div class="schedule-card done-card" v-for="item in todayDoneList" :key="item.id">
                  <div class="schedule-time">
                    <i class="fas fa-calendar-check"></i> {{item.time}}
                  </div>
                  <div class="schedule-title">{{item.title}}</div>
                  <div class="schedule-desc">{{item.desc}}</div>
                </div>
              </div>
            </div>
          </div>

          <!-- ===== 独立提醒模块 ===== -->
          <div class="reminder-section">
            <div class="section-header">
              <div class="header-left">
                <i class="fas fa-bell section-icon"></i>
                <h2 class="section-title">独立提醒</h2>
                <span class="section-count">({{ reminderList.length }})</span>
              </div>
              <button class="add-btn-small" @click="handleQuick('addReminder')">
                <i class="fas fa-plus"></i>
              </button>
            </div>
            <div class="reminder-list">
              <div 
                class="reminder-item-card" 
                v-for="(item, idx) in reminderList" 
                :key="idx"
                :class="{ completed: item.completed }"
              >
                <div class="reminder-checkbox" @click.stop="toggleReminderComplete(idx)">
                  <i class="fas fa-check" v-if="item.completed"></i>
                </div>
                <div class="reminder-content">
                  <div class="reminder-title">{{ item.title }}</div>
                  <div class="reminder-time">
                    <i class="fas fa-clock"></i>
                    <span>{{ item.time }}</span>
                  </div>
                </div>
                <div class="reminder-actions">
                  <button class="action-icon-btn-small" @click.stop="goToEditReminderPage(idx)">
                    <i class="fas fa-edit"></i>
                  </button>
                  <button class="action-icon-btn-small" @click.stop="deleteReminder(idx)">
                    <i class="fas fa-trash"></i>
                  </button>
                </div>
              </div>
              <div class="empty-state" v-if="reminderList.length === 0">
                <i class="fas fa-bell-slash empty-icon"></i>
                <p class="empty-text">还没有提醒事项</p>
              </div>
            </div>
          </div>

          <!-- ===== 团队管理功能模块 ===== -->
          <div class="team-management-section">
            <div class="section-header">
              <div class="header-left">
                <i class="fas fa-users section-icon"></i>
                <h2 class="section-title">团队管理</h2>
                <span class="section-count">({{ teamList.length }})</span>
              </div>
              <button class="add-btn-small" @click="handleQuick('createTeam')">
                <i class="fas fa-plus"></i>
              </button>
            </div>
            <div class="team-list">
              <div 
                class="team-item-card" 
                v-for="(item, idx) in teamList" 
                :key="idx"
                @click="goToTeamDetail(idx)"
              >
                <div class="team-info">
                  <div class="team-avatar">
                    <span class="avatar-text">{{ item.name.substring(0, 1) }}</span>
                  </div>
                  <div class="team-detail">
                    <div class="team-name-row">
                      <div class="team-name">{{ item.name }}</div>
                      <div class="admin-badge" v-if="item.isAdmin">
                        <i class="fas fa-crown"></i>
                        <span>管理员</span>
                      </div>
                    </div>
                    <div class="team-member-count">{{ item.memberCount }} 名成员</div>
                  </div>
                </div>
                <div class="team-actions">
                  <button class="team-action-btn" @click.stop="goToEditTeam(idx)" title="编辑">
                    <i class="fas fa-edit"></i>
                  </button>
                  <button class="team-action-btn" @click.stop="goToMemberManage(idx)" title="成员管理">
                    <i class="fas fa-users"></i>
                  </button>
                  <button class="team-action-btn" @click.stop="goToSyncSchedule(idx)" v-if="item.isAdmin" title="同步行程">
                    <i class="fas fa-sync-alt"></i>
                  </button>
                  <button class="team-action-btn danger" @click.stop="deleteTeam(idx)" title="删除">
                    <i class="fas fa-trash"></i>
                  </button>
                </div>
              </div>
              <div class="empty-state" v-if="teamList.length === 0">
                <i class="fas fa-users empty-icon"></i>
                <p class="empty-text">还没有团队，点击上方按钮创建吧~</p>
              </div>
            </div>
          </div>

          <!-- ===== 补充：近期日程列表 按需查看 ===== -->
          <div class="recommendation-section">
            <h2 class="section-title">
              <i class="fas fa-calendar-days"></i> 近期日程安排
            </h2>
            <div class="recommendation-grid">
              <div class="product-card" v-for="item in recentScheduleList" :key="item.id" @click="goToDetail(item.id)">
                <div class="product-image">
                  <i :class="item.icon"></i>
                  <div class="product-tag" :style="{background: item.tagColor}">{{item.tag}}</div>
                  <div class="wishlist-btn" @click.stop="toggleFavorite(item.id)">
                    <i :class="item.isFavorite ? 'fas fa-star' : 'far fa-star'" :style="{ color: item.isFavorite ? '#ffc107' : '#999' }"></i>
                  </div>
                </div>
                <div class="product-info">
                  <div class="product-title">{{ item.title }}</div>
                  <div class="product-desc">{{ item.date }} {{item.time}} | {{item.place}}</div>
                  <div class="product-price" style="color:#666;">{{ item.typeName }}</div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 侧边栏 - 样式完全复用，内容替换为行程相关，配色不变 -->
        <aside class="sidebar">
          <div class="sidebar-widget featured-tags-widget">
            <h3 class="widget-title">
              <i class="fas fa-tags"></i>
              行程分类
            </h3>
            <div class="tags-grid">
              <div 
                v-for="(tag, index) in featuredTags" 
                :key="index"
                class="featured-tag-card"
                :style="{ background: tag.color }"
                @click="handleTagClick(tag.value)"
              >
                <div class="tag-icon">
                  <i :class="tag.icon"></i>
                </div>
                <span class="tag-label">{{ tag.label }}</span>
              </div>
            </div>
          </div>

          <div class="sidebar-widget">
            <h3 class="widget-title">
              <i class="fas fa-fire"></i>
              常用分类
            </h3>
            <ul class="category-list">
              <li 
                class="category-item" 
                v-for="item in hotCategories" 
                :key="item.key" 
                :class="{ active: filters.type === item.key }"
                @click="filterByType(item.key)"
              >
                <i class="fas fa-chevron-right"></i>
                {{ item.name }}
              </li>
            </ul>
          </div>

          <div class="sidebar-widget">
            <h3 class="widget-title">
              <i class="fas fa-bolt"></i>
              快捷操作
            </h3>
            <div class="quick-action">
              <a href="#" @click.prevent="handleQuick('addSchedule')" class="action-button primary">
                <i class="fas fa-plus-circle"></i>
                <span>新增日程</span>
              </a>
              <a href="#" @click.prevent="handleQuick('remind')" class="action-button">
                <i class="fas fa-bell"></i>
                <span>日程提醒</span>
              </a>
              <a href="#" @click.prevent="handleQuick('exam')" class="action-button">
                <i class="fas fa-pen-to-square"></i>
                <span>考试安排</span>
              </a>
              <a href="#" @click.prevent="handleQuick('favorite')" class="action-button">
                <i class="fas fa-star"></i>
                <span>我的收藏</span>
              </a>
            </div>
          </div>

          <div class="sidebar-widget tips-widget">
            <h3 class="widget-title">
              <i class="fas fa-lightbulb"></i>
              温馨提示
            </h3>
            <div class="safety-tips">
              <div class="tip-item" v-for="(item, idx) in tips" :key="idx">
                <i class="fas fa-check-circle"></i>
                <span>{{ item }}</span>
              </div>
            </div>
          </div>
        </aside>
      </div>
    </div>

    <!-- 校车实时信息卡片 - 移到页脚上方 -->
    <section class="bus-info-section-bottom">
      <div class="bus-info-card">
        <div class="bus-card-header">
          <div class="bus-header-left">
            <i class="fas fa-bus bus-icon"></i>
            <h3 class="bus-title">校车实时</h3>
          </div>
          <button class="bus-refresh-btn" @click="refreshBusInfo">
            <i class="fas fa-sync-alt"></i>
          </button>
        </div>
        <div class="bus-routes-list">
          <div 
            class="bus-route-item" 
            v-for="(route, idx) in busRoutes" 
            :key="idx"
            @click="goToBusRouteDetail(route)"
          >
            <div class="route-info">
              <div class="route-name-row">
                <span class="route-name">{{ route.name }}</span>
                <span class="route-status-badge" :class="route.status">
                  {{ route.statusText }}
                </span>
              </div>
              <div class="route-details">
                <span class="route-stops">{{ route.startStop }} → {{ route.endStop }}</span>
                <span class="route-time">下一班：{{ route.nextBusTime }}</span>
              </div>
            </div>
            <div class="route-arrow">
              <i class="fas fa-chevron-right"></i>
            </div>
          </div>
          <div class="bus-empty-state" v-if="busRoutes.length === 0">
            <i class="fas fa-bus empty-icon"></i>
            <p class="empty-text">暂无校车信息</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 页脚 - 完全复用，无修改 -->
    <footer class="footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-section">
            <div class="footer-logo">上大学Online</div>
            <p class="contact-info">我们致力于构建便捷的校园行程管理体系，让课程与日程一目了然，丰富校园生活。</p>
          </div>
          <div class="footer-section">
            <h3>联系我们</h3>
            <div class="contact-info">
              <p>服务热线：400-123-4567</p>
              <p>行程管理客服：schedule@campus.edu.cn</p>
              <p>问题反馈：feedback@campus.edu.cn</p>
            </div>
          </div>
          <div class="footer-section">
            <h3>快速链接</h3>
            <div class="footer-links">
              <router-link to="/">首页</router-link>
              <a href="#">日程指南</a>
              <a href="#">课程查询</a>
              <a href="#">意见反馈</a>
            </div>
          </div>
        </div>
        <div class="copyright">
          © 2024 上大学Online校园综合服务平台 版权所有 | 让校园生活更简单
        </div>
      </div>
    </footer>

    <!-- 创建行程弹窗（替代独立页面） -->
    <div class="modal-overlay" v-if="showCreateScheduleModal" @click.self="closeCreateScheduleModal">
      <div class="modal-card">
        <div class="modal-header">
          <div class="modal-title">
            <i class="fas fa-calendar-plus"></i>
            创建行程
          </div>
          <button class="modal-close" type="button" @click="closeCreateScheduleModal">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="modal-form">
            <div class="form-row">
              <div class="form-group">
                <label>标题 <span class="req">*</span></label>
                <input v-model="createForm.title" type="text" placeholder="请输入行程标题" />
              </div>
              <div class="form-group">
                <label>日期</label>
                <input v-model="createForm.date" type="date" />
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>开始时间</label>
                <input v-model="createForm.startTime" type="time" />
              </div>
              <div class="form-group">
                <label>结束时间</label>
                <input v-model="createForm.endTime" type="time" />
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>地点</label>
                <input v-model="createForm.location" type="text" placeholder="例如：教学楼205" />
              </div>
              <div class="form-group">
                <label>分类</label>
                <select v-model="createForm.tag">
                  <option value="事务">事务</option>
                  <option value="课程">课程</option>
                  <option value="自习">自习</option>
                  <option value="活动">活动</option>
                  <option value="考试">考试</option>
                  <option value="会议">会议</option>
                  <option value="校车">校车</option>
                </select>
              </div>
            </div>

            <div class="form-group">
              <label>备注</label>
              <textarea v-model="createForm.desc" rows="3" placeholder="可选：补充说明"></textarea>
            </div>

            <div class="form-row switches">
              <label class="switch-item">
                <input type="checkbox" v-model="createForm.isRouteSeries" />
                <span>多地点行程</span>
              </label>
              <div class="switch-item">
                <span style="margin-right:8px;">提醒</span>
                <label class="mini-check"><input type="checkbox" value="notification" v-model="createForm.remindTypes" />通知</label>
                <label class="mini-check"><input type="checkbox" value="alarm" v-model="createForm.remindTypes" />闹钟</label>
                <label class="mini-check"><input type="checkbox" value="message" v-model="createForm.remindTypes" />短信</label>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn ghost" type="button" @click="closeCreateScheduleModal">取消</button>
          <button class="btn primary" type="button" @click="submitCreateSchedule">创建</button>
        </div>
      </div>
    </div>

    <FloatingMenu />
  </div>
</template>

<script setup lang="ts">
// 引入和二手页面完全一致的依赖，无新增
import { reactive, ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import NavBar from '@/components/common/NavBar.vue'
import FloatingMenu from '@/components/common/FloatingMenu.vue'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const hasMore = ref(true)

// 天气信息
const weatherInfo = ref('晴 13℃')

// 当前日期
const currentDate = computed(() => {
  const date = new Date()
  const weekDayNames = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  const month = date.getMonth() + 1
  const day = date.getDate()
  const weekDay = weekDayNames[date.getDay()]
  return `${month}月${day}日 ${weekDay}`
})

// 快捷标签
const funcTabs = ref([
  { name: '全部', icon: 'grid', value: '全部' },
  { name: '课程', icon: 'book', value: '课程' },
  { name: '考试', icon: 'pen-alt', value: '考试' },
  { name: '社团', icon: 'users', value: '社团' },
  { name: '兼职', icon: 'briefcase', value: '兼职' },
  { name: '事务', icon: 'list', value: '事务' },
  { name: '校车', icon: 'bus', value: '校车' }
])
const activeTab = ref(0)

// 统计卡片颜色（马卡龙色系）
const statCardColors = ref([
  '#FFF9C4', // 鹅黄色
  '#B3E5FC', // 淡蓝色
  '#FFB6C1', // 淡粉红色
  '#E1BEE7'  // 淡紫色
])

// 今日概览
const overviewList = ref([
  { count: '5', name: '今日行程' },
  { count: '2', name: '已完成' },
  { count: '3', name: '进行中' },
  { count: '1', name: '即将开始' }
])

// 校车路线信息
const busRoutes = ref([
  {
    id: 1,
    name: '1号线',
    startStop: '东门',
    endStop: '西门',
    nextBusTime: '15:30',
    status: 'running',
    statusText: '运行中',
    distance: '距离本站500米',
    remainingSeats: 15
  },
  {
    id: 2,
    name: '2号线',
    startStop: '南门',
    endStop: '北门',
    nextBusTime: '15:45',
    status: 'waiting',
    statusText: '等待发车',
    distance: '距离本站1.2公里',
    remainingSeats: 20
  },
  {
    id: 3,
    name: '3号线',
    startStop: '图书馆',
    endStop: '体育馆',
    nextBusTime: '16:00',
    status: 'running',
    statusText: '运行中',
    distance: '距离本站800米',
    remainingSeats: 8
  }
])

// 本周概览日历数据
const weekCalendarDays = ref([
  { name: '周一', date: '7', tag: '', isToday: false },
  { name: '周二', date: '8', tag: '2个', isToday: false },
  { name: '周三', date: '9', tag: '2个', isToday: false },
  { name: '周四', date: '10', tag: '2个', isToday: true },
  { name: '周五', date: '11', tag: '1个', isToday: false },
  { name: '周六', date: '12', tag: '1个', isToday: false },
  { name: '周日', date: '13', tag: '1个', isToday: false }
])

// 独立提醒
const reminderList = ref([
  { title: '提交课程作业', time: '今天 18:00', completed: false },
  { title: '准备小组讨论', time: '明天 14:00', completed: false },
  { title: '还图书馆书籍', time: '1月18日 16:00', completed: true }
])

// 团队列表
const teamList = ref([
  {
    id: 1,
    name: '数据结构学习小组',
    memberCount: 8,
    isAdmin: true,
    avatar: ''
  },
  {
    id: 2,
    name: '项目开发团队',
    memberCount: 5,
    isAdmin: false,
    avatar: ''
  },
  {
    id: 3,
    name: '英语角活动组',
    memberCount: 12,
    isAdmin: true,
    avatar: ''
  }
])

// 行程筛选条件 - 替换二手商品筛选，字段适配行程场景
const filters = reactive({
  semester: 'all',
  week: 'all',
  campus: 'all',
  type: 'all',
  search: ''
})

// 行程特色标签 - 配色完全复用二手页的马卡龙色系
const featuredTags = ref([
  { label: '今日重点', value: 'today', icon: 'fas fa-star', color: '#FFB6C1' },
  { label: '本周课程', value: 'course', icon: 'fas fa-book', color: '#B0E0E6' },
  { label: '自习规划', value: 'study', icon: 'fas fa-lightbulb', color: '#FFF8DC' },
  { label: '校园活动', value: 'activity', icon: 'fas fa-users', color: '#98FB98' },
  { label: '考试安排', value: 'exam', icon: 'fas fa-pen-to-square', color: '#DDA0DD' },
  { label: '重要事项', value: 'important', icon: 'fas fa-flag', color: '#FFE4B5' }
])

// 排序选项 - 适配行程场景
const sortType = ref('default')
const sortOptions = ref([
  { label: '默认', value: 'default', icon: 'fas fa-list' },
  { label: '时间', value: 'time', icon: 'fas fa-clock' },
  { label: '重要性', value: 'level', icon: 'fas fa-star' },
  { label: '校区', value: 'campus', icon: 'fas fa-building' }
])

// 行程快捷筛选 - 替换价格筛选
const quickIndex = ref(-1)
const quickOptions = ref([
  { label: '今日日程', type: 'today' },
  { label: '本周课程', type: 'weekCourse' },
  { label: '本月活动', type: 'monthActivity' },
  { label: '考试安排', type: 'exam' },
  { label: '重要事项', type: 'important' }
])

// 课程表基础配置 - 核心数据
const weekDays = ref(['周一','周二','周三','周四','周五','周六','周日'])
const sections = ref([1,2,3,4,5,6,7,8,9,10])
const todayDate = ref(new Date().toLocaleDateString().replace(/\//g,'-'))
// 本周课程数据 - 模拟真实课程表，可直接替换接口数据
const courseList = ref([
  {id:1, dayIdx:0, sectionIdx:0, name:'高等数学', teacher:'张教授', place:'主教学楼101', color:'#FFB6C1'},
  {id:2, dayIdx:0, sectionIdx:1, name:'高等数学', teacher:'张教授', place:'主教学楼101', color:'#FFB6C1'},
  {id:3, dayIdx:1, sectionIdx:2, name:'大学英语', teacher:'李老师', place:'东校区203', color:'#B0E0E6'},
  {id:4, dayIdx:1, sectionIdx:3, name:'大学英语', teacher:'李老师', place:'东校区203', color:'#B0E0E6'},
  {id:5, dayIdx:2, sectionIdx:0, name:'数据结构', teacher:'王老师', place:'西校区机房', color:'#FFF8DC'},
  {id:6, dayIdx:3, sectionIdx:4, name:'操作系统', teacher:'赵老师', place:'主教学楼302', color:'#98FB98'},
  {id:7, dayIdx:4, sectionIdx:1, name:'体育', teacher:'孙老师', place:'体育场', color:'#DDA0DD'},
  {id:8, dayIdx:4, sectionIdx:2, name:'体育', teacher:'孙老师', place:'体育场', color:'#DDA0DD'},
])

// 今日日程-待办（优化数据结构，添加状态和提醒方式）
const todayTodoList = ref([
  {
    id:101, 
    title:'高数作业提交', 
    time:'09:00', 
    endTime:'10:00',
    desc:'提交至学习通平台', 
    tag:'作业', 
    tagColor:'#FF6B9D', 
    isFavorite:true,
    location: '学习通平台',
    isRouteSeries: false,
    status: '已完成',
    statusColor: '#66BB6A',
    remindTypes: ['notification', 'message']
  },
  {
    id:102, 
    title:'班级例会', 
    time:'14:30', 
    endTime:'15:30',
    desc:'教学楼205教室，班委参会', 
    tag:'会议', 
    tagColor:'#FF8FB3', 
    isFavorite:false,
    location: '教学楼205教室',
    isRouteSeries: false,
    status: '进行中',
    statusColor: '#42A5F5',
    remindTypes: ['notification', 'alarm']
  },
  {
    id:103, 
    title:'图书馆自习', 
    time:'18:00', 
    endTime:'21:00',
    desc:'图书馆3楼自习室', 
    tag:'自习', 
    tagColor:'#98FB98', 
    isFavorite:true,
    location: '图书馆3楼自习室',
    isRouteSeries: false,
    status: '即将开始',
    statusColor: '#FFA726',
    remindTypes: ['notification', 'vibration']
  },
  {
    id:104,
    title: '乘坐校车1号线',
    time: '15:30',
    endTime: '16:00',
    location: '东门 → 西门',
    desc: '校车班次，预计30分钟到达',
    tag: '校车',
    tagColor: '#FFB6C1',
    isFavorite: false,
    isRouteSeries: true,
    status: '即将开始',
    statusColor: '#FF1493',
    remindTypes: ['notification', 'alarm']
  }
])

// 根据选中的标签筛选今日行程列表
const filteredTodayScheduleList = computed(() => {
  if (activeTab.value === 0) {
    return todayTodoList.value // 显示全部
  }
  const selectedTag = funcTabs.value[activeTab.value].value
  return todayTodoList.value.filter(item => item.tag === selectedTag)
})
// 今日日程-已完成
const todayDoneList = ref([
  {id:201, title:'晨读打卡', time:'07:00-07:30', desc:'校园晨读区'},
  {id:202, title:'计算机课', time:'10:20-12:00', desc:'西校区机房'},
])

// 近期日程列表
const recentScheduleList = ref([
  {id:301, title:'篮球赛决赛', date:'2026-01-12', time:'16:00-18:00', place:'体育场', tag:'活动', tagColor:'#FF6B9D', icon:'fas fa-users', typeName:'校园活动', isFavorite:false},
  {id:302, title:'期末考试-高数', date:'2026-01-18', time:'09:00-11:00', place:'主教学楼101', tag:'考试', tagColor:'#DDA0DD', icon:'fas fa-pen-to-square', typeName:'考试安排', isFavorite:true},
  {id:303, title:'社团招新', date:'2026-01-15', time:'12:00-17:00', place:'食堂门口', tag:'活动', tagColor:'#98FB98', icon:'fas fa-handshake', typeName:'校园活动', isFavorite:false},
  {id:304, title:'专业课答疑', date:'2026-01-11', time:'15:00-16:00', place:'教师办公室', tag:'答疑', tagColor:'#FFB6C1', icon:'fas fa-question-circle', typeName:'课程相关', isFavorite:true},
])

// 常用分类
const hotCategories = ref([
  { key: 'course', name: '课程安排' },
  { key: 'study', name: '自习规划' },
  { key: 'activity', name: '校园活动' },
  { key: 'exam', name: '考试安排' },
  { key: 'meeting', name: '会议安排' },
  { key: 'others', name: '其他事项' }
])

// 温馨提示
const tips = ref([
  '课程表每周自动更新',
  '重要日程可点击收藏置顶',
  '考试安排提前一周提醒',
  '校区切换请在筛选栏选择',
  '新增日程支持重复提醒'
])

// 判断是否有搜索或筛选操作
const hasSearchOrFilter = computed(() => {
  return filters.search.trim() !== '' ||
         filters.semester !== 'all' ||
         filters.week !== 'all' ||
         filters.campus !== 'all' ||
         filters.type !== 'all'
})

// 根据周几和节次匹配课程
const getCourseByDayAndSection = (dayIdx:number, sectionIdx:number) => {
  return courseList.value.filter(item => item.dayIdx === dayIdx && item.sectionIdx === sectionIdx)
}

// 刷新课程表
const refreshCourseTable = () => {
  // 实际项目中调用接口切换周次，这里模拟刷新
  courseList.value = [...courseList.value]
}

// 刷新今日日程
const refreshTodaySchedule = () => {
  todayTodoList.value = [...todayTodoList.value]
  todayDoneList.value = [...todayDoneList.value]
}

// 节流滚动监听 - 完全复用二手页逻辑
let lastScrollRefreshTime = 0
const handleScroll = () => {
  const now = Date.now()
  if (now - lastScrollRefreshTime < 1000) return
  const scrollTop = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop || 0
  const windowHeight = window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight
  const docHeight = document.documentElement.scrollHeight || document.body.scrollHeight
  if (docHeight - (scrollTop + windowHeight) < 150) {
    lastScrollRefreshTime = now
    if (hasMore.value) {
      loadData(false)
    }
  }
}

// 加载行程数据
const loadData = async (reset = false) => {
  try {
    if (loading.value) return
    if (reset) {
      currentPage.value = 1
      hasMore.value = true
    }
    loading.value = true
    // 实际项目中调用行程接口，这里使用模拟数据
    loading.value = false
  } catch (error: any) {
    console.error('加载行程数据失败:', error)
    loading.value = false
  }
}

// 筛选相关方法 - 逻辑不变，适配行程字段
const handleFilter = () => { loadData(true) }
const handleReset = () => {
  filters.semester = 'all'
  filters.week = 'all'
  filters.campus = 'all'
  filters.type = 'all'
  filters.search = ''
  sortType.value = 'default'
  quickIndex.value = -1
  loadData(true)
}
const filterByType = (typeKey: string) => { filters.type = typeKey; handleFilter() }
const handleTagClick = (tagValue: string) => {
  const tagMap: Record<string, string> = {
    'today':'all','course':'course','study':'study','activity':'activity','exam':'exam','important':'all'
  }
  filters.type = tagMap[tagValue] || 'all'
  handleFilter()
}
const handleSort = (value: string) => { sortType.value = value; loadData(true) }
const handleQuickFilter = (index: number, type: string) => {
  quickIndex.value = index
  // 快捷筛选逻辑
  handleFilter()
}

// 跳转详情
const goToDetail = (id: number) => {
  router.push(`/schedule/detail/${id}`)
}

// 生命周期 - 完全复用
onMounted(() => {
  loadData(true)
  updateStats()
  window.addEventListener('scroll', handleScroll)
})
onBeforeUnmount(() => {
  window.removeEventListener('scroll', handleScroll)
})

// 获取标签图标
const getTabIcon = (iconName: string) => {
  const iconMap: Record<string, string> = {
    'grid': 'fas fa-th',
    'book': 'fas fa-book',
    'pen-alt': 'fas fa-pen-alt',
    'users': 'fas fa-users',
    'briefcase': 'fas fa-briefcase',
    'list': 'fas fa-list',
    'bus': 'fas fa-bus'
  }
  return iconMap[iconName] || 'fas fa-circle'
}

// 获取提醒方式图标
const getRemindMethodIcon = (type: string) => {
  const iconMap: Record<string, string> = {
    'notification': 'fas fa-bell',
    'message': 'fas fa-comment',
    'alarm': 'fas fa-clock',
    'vibration': 'fas fa-mobile-alt'
  }
  return iconMap[type] || 'fas fa-bell'
}

// 获取标签颜色（马卡龙色系）
const getTagColor = (tag: string) => {
  const tagColorMap: Record<string, string> = {
    '课程': '#F8BBD0', // 淡粉色
    '考试': '#FFB3BA', // 淡珊瑚色
    '社团': '#E1BEE7', // 淡紫色
    '兼职': '#FFF9C4', // 鹅黄色
    '事务': '#B3E5FC', // 淡蓝色
    '校车': '#FFB6C1', // 淡粉红色
    '作业': '#FFB6C1',
    '会议': '#FF8FB3',
    '自习': '#98FB98'
  }
  return tagColorMap[tag] || '#E1BEE7'
}

// 获取标签边框颜色（更深的对比色）
const getTagBorderColor = (tag: string) => {
  const tagBorderColorMap: Record<string, string> = {
    '课程': '#F06292', // 粉色
    '考试': '#EF5350', // 红色
    '社团': '#BA68C8', // 紫色
    '兼职': '#FFD54F', // 黄色
    '事务': '#4FC3F7', // 蓝色
    '校车': '#FF1493', // 玫红色
    '作业': '#FF1493',
    '会议': '#FF6B9D',
    '自习': '#66BB6A'
  }
  return tagBorderColorMap[tag] || '#BA68C8'
}

// 切换标签筛选
const handleTabChange = (idx: number) => {
  activeTab.value = idx
}

// 切换月视图
const switchToMonth = () => {
  alert('月视图开发中')
}

// 刷新校车信息
const refreshBusInfo = () => {
  // 模拟刷新数据
  console.log('刷新校车信息')
}

// 查看校车路线详情
const goToBusRouteDetail = (route: any) => {
  console.log('查看校车路线详情', route)
  // router.push(`/schedule/bus-detail/${route.id}`)
}

// 实时更新统计数据
const updateStats = () => {
  const total = todayTodoList.value.length
  const completed = todayTodoList.value.filter(item => item.status === '已完成').length
  const inProgress = todayTodoList.value.filter(item => item.status === '进行中').length
  const upcoming = todayTodoList.value.filter(item => item.status === '即将开始').length
  
  overviewList.value = [
    { count: String(total), name: '今日行程' },
    { count: String(completed), name: '已完成' },
    { count: String(inProgress), name: '进行中' },
    { count: String(upcoming), name: '即将开始' }
  ]
}

// 切换提醒完成状态
const toggleReminderComplete = (idx: number) => {
  reminderList.value[idx].completed = !reminderList.value[idx].completed
}

// 删除提醒
const deleteReminder = (idx: number) => {
  if (confirm('是否确定删除该提醒？')) {
    reminderList.value.splice(idx, 1)
  }
}

// 跳转到编辑提醒页面
const goToEditReminderPage = (idx: number) => {
  // 暂时使用提醒管理页面，后续可以创建专门的编辑页面
  router.push({ name: 'schedule-reminder', query: { edit: 'true', idx: String(idx) } })
}

// 团队管理相关方法
const goToTeamDetail = (idx: number) => {
  router.push({ name: 'schedule-team-detail', params: { id: teamList.value[idx].id } })
}

const goToEditTeam = (idx: number) => {
  console.log('编辑团队', teamList.value[idx])
  // router.push({ name: 'schedule-team-edit', params: { id: teamList.value[idx].id } })
}

const goToMemberManage = (idx: number) => {
  console.log('成员管理', teamList.value[idx])
  // 可以打开成员管理弹窗或跳转到成员管理页面
}

const goToSyncSchedule = (idx: number) => {
  console.log('同步行程', teamList.value[idx])
  // 可以打开同步行程弹窗
}

const deleteTeam = (idx: number) => {
  if (confirm(`确定要删除团队"${teamList.value[idx].name}"吗？此操作不可恢复。`)) {
    teamList.value.splice(idx, 1)
  }
}

// ========= 创建行程弹窗（替代 /schedule/add 页面）=========
const showCreateScheduleModal = ref(false)
const createForm = reactive({
  title: '',
  date: new Date().toISOString().slice(0, 10), // yyyy-mm-dd
  startTime: '',
  endTime: '',
  location: '',
  desc: '',
  tag: '事务',
  remindTypes: [] as string[],
  isRouteSeries: false
})

const openCreateScheduleModal = (preset?: Partial<typeof createForm>) => {
  createForm.title = ''
  createForm.date = new Date().toISOString().slice(0, 10)
  createForm.startTime = ''
  createForm.endTime = ''
  createForm.location = ''
  createForm.desc = ''
  createForm.tag = '事务'
  createForm.remindTypes = []
  createForm.isRouteSeries = false
  if (preset) Object.assign(createForm, preset)
  showCreateScheduleModal.value = true
}

const closeCreateScheduleModal = () => {
  showCreateScheduleModal.value = false
}

const submitCreateSchedule = () => {
  if (!createForm.title.trim()) return
  todayTodoList.value.unshift({
    id: Date.now(),
    title: createForm.title.trim(),
    time: createForm.startTime || '09:00',
    endTime: createForm.endTime || '',
    desc: createForm.desc || '',
    tag: createForm.tag,
    tagColor: getTagColor(createForm.tag),
    isFavorite: false,
    location: createForm.location || '',
    isRouteSeries: createForm.isRouteSeries,
    status: '即将开始',
    statusColor: '#FFA726',
    remindTypes: createForm.remindTypes || []
  })
  showCreateScheduleModal.value = false
}

// 快捷操作
const handleQuick = (type: string) => {
  switch (type) {
    case 'addSchedule': openCreateScheduleModal(); break
    case 'remind': router.push('/schedule/remind'); break
    case 'addReminder': router.push({ name: 'schedule-reminder', query: { add: 'true' } }); break
    case 'timeTable': router.push('/schedule/time-table'); break
    case 'courseTable': router.push('/schedule/course-table'); break
    case 'team': router.push('/schedule/team'); break
    case 'createTeam': router.push('/schedule/team-create'); break
    case 'exam': router.push('/schedule/exam'); break
    case 'favorite': router.push('/schedule/favorites'); break
    case 'profile': router.push('/profile'); break
  }
}

onMounted(() => {
  // 兼容旧入口：访问 /schedule/add 会重定向到 /schedule?create=1
  if (route.query.create === '1') {
    openCreateScheduleModal()
    const { create, ...rest } = route.query
    router.replace({ query: rest })
  }
})

// 收藏行程
const toggleFavorite = async (id: number) => {
  const item = recentScheduleList.value.find(p => p.id === id) || todayTodoList.value.find(p => p.id === id)
  if (item) {
    try {
      item.isFavorite = !item.isFavorite
    } catch (error) {
      console.error('收藏操作失败:', error)
      item.isFavorite = !item.isFavorite
    }
  }
}
</script>

<!-- 完全复用二手交易页面的所有样式 + 新增课程表/日程表专属样式，配色不变 -->
<style scoped>
/* 基础布局/配色/通用样式 - 完全复制二手页，无修改 */
.secondhand-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #FFF0F5 0%, #F0F8FF 100%);
  color: #333333;
  line-height: 1.6;
  font-family: 'Arial', 'Microsoft YaHei', 'PingFang SC', sans-serif;
}
* { margin: 0;padding: 0;box-sizing: border-box;}
a { text-decoration: none;color: #444;transition: all 0.3s;}
a:hover { color: #d81b60 !important;}
li { list-style: none;}
.container { max-width: 1200px;margin: 0 auto;padding: 0 15px;}

/* 搜索栏样式 - 完全复用 */
.search-bar-section {
  background: white;
  border-radius: 12px;
	padding: 15px 20px;
	margin: 20px 0;
	box-shadow: 0 4px 15px rgba(255, 107, 157, 0.12);
}
.search-container { display: flex;align-items: center;justify-content: space-between;gap: 20px;}
.search-box { flex:1;max-width:600px;position:relative;}
.search-input {
  width:100%;padding:12px 15px 12px 20px;border:2px solid #FF6B9D;border-radius:25px;
  font-size:16px;outline:none;box-shadow:0 2px 5px rgba(255,107,157,0.15);color:#333;
}
.search-input::placeholder { color:#666;opacity:0.8;}
.search-icon { position:absolute;right:15px;top:50%;transform:translateY(-50%);color:#FF6B9D;font-size:18px;cursor:pointer;}
.search-actions { display:flex;align-items:center;gap:15px;}
.action-btn {
  background:none;border:none;font-size:16px;cursor:pointer;color:#333;
  display:flex;align-items:center;padding:8px 15px;border-radius:20px;transition:all 0.3s;
}
.action-btn:hover { background:rgba(255,107,157,0.1);color:#FF6B9D !important;}
.action-btn i { margin-right:5px;}

/* 特色标签样式 - 完全复用 */
.featured-tags-widget { margin-bottom:24px;}
.tags-grid { display:grid;grid-template-columns:repeat(2,1fr);gap:12px;}
.featured-tag-card {
  display:flex;flex-direction:column;align-items:center;justify-content:center;
  padding:16px 12px;border-radius:12px;color:white;font-size:13px;gap:8px;
  box-shadow:0 2px 8px rgba(0,0,0,0.1);transition:all 0.3s;cursor:pointer;text-align:center;min-height:90px;
}
.featured-tag-card:hover { transform:translateY(-3px);box-shadow:0 4px 12px rgba(0,0,0,0.2);}
.tag-icon { font-size:24px;margin-bottom:4px;}
.tag-label { font-weight:600;font-size:12px;}

/* 排序和快捷筛选栏 - 完全复用 */
.sort-price-bar {
  background:white;border-radius:12px;padding:20px;margin:20px 0;
  box-shadow:0 4px 15px rgba(0,0,0,0.08);
}
.sort-section { display:flex;align-items:center;gap:15px;margin-bottom:15px;flex-wrap:wrap;}
.sort-label { font-size:14px;color:#666;font-weight:500;}
.sort-item {
  display:flex;align-items:center;gap:6px;padding:8px 16px;background:#F5F5F5;
  border-radius:20px;font-size:14px;color:#666;transition:all 0.3s;cursor:pointer;
}
.sort-item:hover { background:#e0e0e0;}
.sort-item.active {
  background:linear-gradient(135deg, #FF6B9D 0%, #FF8FB3 100%);
  color:#FFFFFF;font-weight:600;
}
.sort-item i { font-size:12px;}
.price-quick-section { display:flex;align-items:center;gap:15px;flex-wrap:wrap;}
.price-label { font-size:14px;color:#666;font-weight:500;}
.price-quick-btns { display:flex;gap:10px;flex-wrap:wrap;flex:1;}
.price-quick-btn {
  padding:8px 16px;background:#F5F5F5;border-radius:20px;font-size:14px;
  color:#666;transition:all 0.3s;white-space:nowrap;cursor:pointer;
}
.price-quick-btn:hover { background:#e0e0e0;}
.price-quick-btn.active {
  background:linear-gradient(135deg, #FF6B9D 0%, #FF8FB3 100%);
  color:#FFFFFF;font-weight:600;
}

/* 筛选栏 - 完全复用 */
.filter-section {
  background:white;border-radius:12px;padding:20px;margin:20px 0;
  box-shadow:0 4px 15px rgba(0,0,0,0.08);
}
.filter-row { display:flex;flex-wrap:wrap;gap:15px;margin-bottom:15px;align-items:center;}
.filter-group { display:flex;align-items:center;gap:10px;}
.filter-label { font-size:14px;color:#666;white-space:nowrap;}
.filter-select,.filter-input {
  padding:8px 12px;border:1px solid #ddd;border-radius:6px;
  background:white;font-size:14px;
}
.filter-input { width:100px;}
.price-range { display:flex;align-items:center;gap:5px;}
.filter-actions { display:flex;gap:10px;margin-left:auto;}
.confirm-btn {
  background:linear-gradient(135deg, #FF6B9D 0%, #FF8FB3 100%) !important;
  color:#FFFFFF !important;border:none;padding:10px 20px;border-radius:6px;
  font-size:14px;font-weight:600;cursor:pointer;transition:all 0.3s;
}
.confirm-btn:hover {
  background:linear-gradient(135deg, #E91E63 0%, #FF6B9D 100%) !important;
  transform:translateY(-2px);box-shadow:0 4px 10px rgba(255,107,157,0.3);
}
.reset-btn {
  background:#f5f5f5 !important;color:#666 !important;border:1px solid #ddd;
  padding:10px 20px;border-radius:6px;font-size:14px;cursor:pointer;transition:all 0.3s;
}
.reset-btn:hover { background:#e0e0e0 !important;}

/* 主要内容区域 - 复用+新增课程表/日程表样式 */
.main-content { display:flex;gap:20px;margin-bottom:40px;}
.product-list { flex:1;}
.search-result-section {
  background:white;border-radius:12px;padding:20px;margin-bottom:20px;
  box-shadow:0 4px 15px rgba(0,0,0,0.08);
}
.recommendation-section {
  background:white;border-radius:12px;padding:20px;
  box-shadow:0 4px 15px rgba(0,0,0,0.08);
}
.section-title {
  font-size:20px;font-weight:bold;margin-bottom:15px;color:#333;
  display:flex;align-items:center;justify-content:flex-start;
}
.section-title i { color:#FF6B9D !important;margin-right:8px;}
.refresh-btn {
  margin-left:auto;padding:4px 10px;font-size:12px;border-radius:12px;
  border:1px solid #FF6B9D;background:#fff;color:#FF6B9D;cursor:pointer;
  transition:all 0.2s;font-weight:500;
}
.refresh-btn:hover { background:#FF6B9D;color:#fff;}
.recommendation-grid { display:grid;grid-template-columns:repeat(auto-fill, minmax(180px,1fr));gap:15px;}

/* ===== 新增：课程表专属样式 ===== */
.course-table-container { width:100%;overflow-x:auto;padding:10px 0;}
.course-table { width:100%;border-collapse:collapse;}
.table-header { display:flex;width:100%;border-bottom:1px solid #eee;}
.table-row { display:flex;width:100%;border-bottom:1px solid #eee;}
.table-cell {
  flex:1;text-align:center;padding:8px;border-right:1px solid #eee;
  display:flex;align-items:center;justify-content:center;min-height:70px;
  position:relative;
}
.table-cell.empty { flex:0;width:60px;border-right:none;}
.section-cell { font-size:12px;color:#666;font-weight:bold;}
.course-card {
  width:95%;padding:6px;border-radius:8px;color:#333;margin:4px 0;
  box-shadow:0 2px 6px rgba(0,0,0,0.1);cursor:pointer;transition:all 0.3s;
}
.course-card:hover { transform:scale(1.02);box-shadow:0 3px 8px rgba(0,0,0,0.2);}
.course-name { font-size:13px;font-weight:bold;margin-bottom:4px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis;}
.course-teacher { font-size:11px;color:#444;margin-bottom:2px;}
.course-place { font-size:10px;color:#666;}

/* ===== 新增：日程表专属样式 ===== */
.schedule-header {
  font-size:16px;font-weight:bold;color:#333;padding:8px 0;border-bottom:1px solid #eee;
  display:flex;align-items:center;margin-bottom:10px;
}
.schedule-header i { color:#FF6B9D;margin-right:8px;}
.schedule-grid { display:grid;grid-template-columns:repeat(auto-fill, minmax(280px,1fr));gap:12px;}
.schedule-card {
  background:#fff;border-radius:10px;padding:12px;box-shadow:0 2px 8px rgba(0,0,0,0.1);
  border:1px solid #f0f0f0;cursor:pointer;transition:all 0.3s;
}
.schedule-card:hover { transform:translateY(-3px);box-shadow:0 5px 15px rgba(255,107,157,0.2);}
.done-card { opacity:0.7;background:#f9f9f9;}
.schedule-time { font-size:12px;color:#666;margin-bottom:6px;display:flex;align-items:center;}
.schedule-time i { color:#FF6B9D;margin-right:4px;font-size:10px;}
.schedule-title { font-size:14px;font-weight:bold;margin-bottom:4px;}
.schedule-desc { font-size:12px;color:#666;margin-bottom:8px;}
.schedule-tag {
  display:inline-block;padding:2px 8px;border-radius:12px;font-size:10px;
  color:#fff;font-weight:bold;
}

/* 商品卡片/行程卡片样式 - 完全复用 */
.product-card {
  background:white;border-radius:10px;overflow:hidden;box-shadow:0 2px 8px rgba(0,0,0,0.1);
  transition:transform 0.3s;cursor:pointer;border:1px solid #f0f0f0;
}
.product-card:hover { transform:translateY(-5px);box-shadow:0 5px 15px rgba(255,107,157,0.2);}
.product-image {
  height:150px;background:#f5f5f5;display:flex;align-items:center;justify-content:center;
  position:relative;overflow:hidden;
}
.product-image img { width:100%;height:100%;object-fit:cover;}
.product-image i { font-size:50px;color:#FF6B9D !important;}
.wishlist-btn {
  position:absolute;bottom:8px;right:8px;width:36px;height:36px;
  background:rgba(255,255,255,0.95);border-radius:50%;display:flex;
  align-items:center;justify-content:center;color:#FF6B9D;font-size:16px;
  box-shadow:0 2px 8px rgba(255,107,157,0.25);transition:all 0.3s;z-index:3;cursor:pointer;
}
.wishlist-btn:hover { background:#FF6B9D;color:white;transform:scale(1.1);}
.product-info { padding:10px;}
.product-title { font-size:14px;font-weight:bold;margin-bottom:5px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;}
.product-desc { font-size:12px;color:#666;margin-bottom:5px;}
.product-price { font-size:16px;font-weight:bold;color:#E91E63 !important;letter-spacing:0.3px;}
.product-tag {
  position:absolute;top:5px;right:5px;background:#FF6B9D !important;color:white !important;
  padding:2px 8px;border-radius:10px;font-size:12px;font-weight:600;
}
.seller-info { display:flex;align-items:center;margin-top:8px;font-size:12px;color:#888;}
.seller-avatar { width:20px;height:20px;border-radius:50%;background:#ddd;margin-right:5px;}
.seller-rating { margin-left:auto;display:flex;align-items:center;}
.seller-rating i { color:#ffc107 !important;font-size:10px;margin-right:2px;}

/* 无结果提示 - 复用 */
.no-results { text-align:center;padding:60px 20px;background:#fafafa;border-radius:10px;margin:10px 0;}
.no-results i { font-size:60px;color:#FF6B9D !important;margin-bottom:20px;}
.no-results h2 { font-size:24px;margin-bottom:10px;color:#333;}
.no-results p { color:#666;margin-bottom:20px;}

/* 侧边栏 - 完全复用 */
.sidebar {
  width:280px;flex-shrink:0;position:sticky;top:20px;align-self:flex-start;
  max-height:calc(100vh - 40px);overflow-y:auto;
}
.sidebar::-webkit-scrollbar { width:6px;}
.sidebar::-webkit-scrollbar-thumb { background:#FF6B9D;border-radius:3px;}
.sidebar-widget {
  background:white;border-radius:16px;padding:20px;margin-bottom:20px;
  box-shadow:0 2px 12px rgba(0,0,0,0.08);transition:box-shadow 0.3s;
}
.sidebar-widget:hover { box-shadow:0 4px 16px rgba(0,0,0,0.12);}
.widget-title {
  font-size:16px;font-weight:600;margin-bottom:16px;padding-bottom:12px;
  border-bottom:2px solid #f1f5f9;color:#1e293b;display:flex;align-items:center;gap:8px;
}
.widget-title i { color:#FF6B9D;font-size:18px;}
.category-list { list-style:none;padding:0;margin:0;}
.category-item {
  display:flex;align-items:center;padding:12px 16px;margin-bottom:4px;
  border-radius:8px;cursor:pointer;transition:all 0.2s;color:#64748b;font-size:14px;position:relative;
}
.category-item i { font-size:10px;margin-right:8px;color:#94a3b8;transition:all 0.2s;}
.category-item:hover {
  background:rgba(255,107,157,0.1);color:#FF6B9D;transform:translateX(4px);
}
.category-item:hover i { color:#FF6B9D;}
.category-item.active {
  background:linear-gradient(135deg, #FFE5F1 0%, #FFB3D1 100%);
  color:#E91E63;font-weight:600;
}
.category-item.active i { color:#E91E63;}
.quick-action { display:flex;flex-direction:column;gap:10px;}
.action-button {
  display:flex;align-items:center;padding:12px 16px;background:#f8fafc;
  border-radius:10px;cursor:pointer;transition:all 0.3s;color:#475569;
  font-size:14px;font-weight:500;border:2px solid transparent;
}
.action-button.primary {
  background:linear-gradient(135deg, #FF6B9D 0%, #FF8FB3 100%);color:white;
  box-shadow:0 2px 8px rgba(255,107,157,0.25);font-weight:600;
}
.action-button.primary:hover {
  transform:translateY(-2px);box-shadow:0 4px 12px rgba(255,107,157,0.35);
}
.action-button:not(.primary):hover {
  background:rgba(255,107,157,0.1);border-color:#FFB3D1;color:#FF6B9D;
  transform:translateX(4px);
}
.action-button i { margin-right:10px;font-size:16px;width:20px;text-align:center;}
.tips-widget {
  background:linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  border-left:4px solid #f59e0b;
}
.tips-widget .widget-title { color:#92400e;border-bottom-color:rgba(245,158,11,0.2);}
.tips-widget .widget-title i { color:#f59e0b;}
.safety-tips { display:flex;flex-direction:column;gap:10px;}
.tip-item {
  display:flex;align-items:flex-start;gap:10px;padding:8px 0;
  color:#78350f;font-size:13px;line-height:1.5;
}
.tip-item i { color:#f59e0b;margin-top:2px;flex-shrink:0;font-size:14px;}

/* 页脚 - 完全复用 */
.footer {
  background:#2c3e50 !important;color:white !important;padding:50px 0 20px;
  margin-top:60px;
}
.footer-content {
  display:grid;grid-template-columns:repeat(auto-fit, minmax(250px,1fr));
  gap:40px;margin-bottom:30px;
}
.footer-section h3 { color:white !important;margin-bottom:20px;font-size:18px;}
.footer-logo { font-size:24px;font-weight:bold;color:#FF6B9D !important;margin-bottom:15px;}
.footer-links a { color:#bdc3c7 !important;display:block;margin-bottom:10px;transition:color 0.3s;}
.footer-links a:hover { color:#FF6B9D !important;}
.contact-info { color:#bdc3c7 !important;line-height:1.8;}
.copyright {
  text-align:center;padding-top:30px;margin-top:30px;border-top:1px solid #34495e;
  color:#bdc3c7 !important;font-size:14px;
}

/* ===== 新增：顶部欢迎区域样式 ===== */
.header-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
  margin-bottom: 20px;
}
.greeting-text {
  font-size: 24px;
  font-weight: 600;
  color: #5a4f7a;
  margin-bottom: 8px;
}
.date-text {
  font-size: 16px;
  color: #8b7fa8;
}
.weather-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: #FFF9C4;
  border-radius: 30px;
  box-shadow: 0 2px 8px rgba(255, 213, 79, 0.2);
}
.weather-icon {
  color: #F57F17;
  font-size: 18px;
}
.weather-text {
  font-size: 14px;
  color: #5a4f7a;
}

/* ===== 新增：快速操作卡片组样式 ===== */
.quick-actions-section {
  margin-bottom: 20px;
}
.quick-actions-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}
.quick-action-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px 12px;
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  min-height: 120px;
  transition: all 0.3s;
  cursor: pointer;
  border: 2px solid transparent;
}
.quick-action-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
}
.card-yellow {
  background: #FFF9C4;
  border-color: rgba(255, 213, 79, 0.5);
}
.card-blue {
  background: #B3E5FC;
  border-color: rgba(79, 195, 247, 0.5);
}
.card-pink {
  background: #F8BBD0;
  border-color: rgba(240, 98, 146, 0.5);
}
.card-purple {
  background: #E1BEE7;
  border-color: rgba(186, 104, 200, 0.5);
}
.action-icon-wrapper {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
.icon-yellow { background: rgba(255, 213, 79, 0.3); }
.icon-blue { background: rgba(79, 195, 247, 0.3); }
.icon-pink { background: rgba(240, 98, 146, 0.3); }
.icon-purple { background: rgba(186, 104, 200, 0.3); }
.action-icon-wrapper i {
  font-size: 28px;
  color: #FF1493;
}
.action-text {
  font-size: 14px;
  color: #5a4f7a;
  font-weight: 600;
}

/* ===== 新增：统计卡片样式 ===== */
.stats-section {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-bottom: 20px;
}
.stat-card {
  padding: 20px 12px;
  border-radius: 16px;
  text-align: center;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  border: 2px solid rgba(255, 255, 255, 0.8);
  transition: all 0.3s;
}
.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
}
.stat-number {
  display: block;
  font-size: 32px;
  font-weight: 700;
  color: #5a4f7a;
  margin-bottom: 8px;
}
.stat-label {
  display: block;
  font-size: 14px;
  color: #5a4f7a;
  font-weight: 500;
}

/* ===== 新增：校车信息卡片样式 ===== */
.bus-info-section {
  margin-bottom: 20px;
}
.bus-info-section-bottom {
  margin: 40px 0 20px 0;
}
.bus-info-card {
  background: white;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 6px 24px rgba(255, 20, 147, 0.2);
  border: 2px solid rgba(255, 20, 147, 0.2);
  position: relative;
}
.bus-info-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 6px;
  background: linear-gradient(90deg, #FF1493 0%, #FF69B4 50%, #FFB6C1 100%);
  border-radius: 20px 20px 0 0;
}
.bus-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.bus-header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.bus-icon {
  color: #FF1493;
  font-size: 24px;
  animation: busMove 2s ease-in-out infinite;
}
@keyframes busMove {
  0%, 100% { transform: translateX(0); }
  50% { transform: translateX(4px); }
}
.bus-title {
  font-size: 20px;
  font-weight: 700;
  color: #FF1493;
}
.bus-refresh-btn {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 20, 147, 0.1);
  border-radius: 50%;
  border: 2px solid rgba(255, 20, 147, 0.2);
  transition: all 0.3s;
  cursor: pointer;
  color: #FF1493;
}
.bus-refresh-btn:hover {
  background: rgba(255, 20, 147, 0.2);
  transform: rotate(180deg);
}
.bus-routes-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.bus-route-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  background: linear-gradient(135deg, #FFF9C4 0%, #FFE0E6 100%);
  border-radius: 12px;
  border-left: 6px solid #FF1493;
  box-shadow: 0 4px 12px rgba(255, 20, 147, 0.15);
  transition: all 0.3s;
  cursor: pointer;
}
.bus-route-item:hover {
  transform: translateX(4px);
  box-shadow: 0 6px 16px rgba(255, 20, 147, 0.25);
}
.route-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.route-name-row {
  display: flex;
  align-items: center;
  gap: 12px;
}
.route-name {
  font-size: 18px;
  font-weight: 700;
  color: #FF1493;
}
.route-status-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}
.route-status-badge.running {
  background: #C8E6C9;
  color: #2E7D32;
}
.route-status-badge.waiting {
  background: #FFF9C4;
  color: #F57F17;
}
.route-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.route-stops {
  font-size: 14px;
  color: #8b7fa8;
  font-weight: 500;
}
.route-time {
  font-size: 13px;
  color: #FF1493;
  font-weight: 600;
}
.route-arrow {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 20, 147, 0.1);
  border-radius: 50%;
  color: #FF1493;
}
.bus-empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: #b8a9d4;
}
.bus-empty-state .empty-icon {
  font-size: 48px;
  margin-bottom: 12px;
  opacity: 0.5;
}
.bus-empty-state .empty-text {
  font-size: 14px;
  color: #b8a9d4;
}

/* ===== 新增：快捷标签筛选样式 ===== */
.filter-tabs-section {
  margin-bottom: 20px;
}
.filter-tabs-container {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding: 10px 0;
}
.filter-tabs-container::-webkit-scrollbar {
  height: 4px;
}
.filter-tabs-container::-webkit-scrollbar-thumb {
  background: #FF1493;
  border-radius: 2px;
}
.filter-tab {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  background: white;
  border-radius: 30px;
  box-shadow: 0 2px 8px rgba(225, 190, 231, 0.15);
  transition: all 0.3s;
  cursor: pointer;
  border: 2px solid transparent;
  white-space: nowrap;
}
.filter-tab:hover {
  background: rgba(255, 20, 147, 0.1);
}
.filter-tab.active {
  background: linear-gradient(135deg, #FF1493 0%, #FF69B4 100%);
  box-shadow: 0 4px 12px rgba(255, 20, 147, 0.3);
  border-color: rgba(255, 20, 147, 0.3);
  transform: scale(1.05);
}
.filter-tab.active .filter-text {
  color: white;
  font-weight: 600;
}
.filter-icon {
  font-size: 14px;
  color: #8b7fa8;
}
.filter-tab.active .filter-icon {
  color: white;
}
.filter-text {
  font-size: 14px;
  color: #8b7fa8;
}

/* ===== 新增：本周概览日历样式 ===== */
.week-overview-section {
  margin-bottom: 20px;
}
.week-overview-card {
  background: white;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 6px 24px rgba(255, 20, 147, 0.2);
  border: 2px solid rgba(255, 20, 147, 0.2);
  position: relative;
}
.week-overview-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 6px;
  background: linear-gradient(90deg, #FF1493 0%, #FF69B4 50%, #FFB6C1 100%);
  border-radius: 20px 20px 0 0;
}
.week-overview-card .card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.week-overview-card .card-title {
  font-size: 18px;
  font-weight: 600;
  color: #5a4f7a;
}
.action-icon-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #FF1493;
  font-size: 16px;
  border-radius: 50%;
  transition: all 0.3s;
  background: rgba(255, 20, 147, 0.1);
  border: 1px solid rgba(255, 20, 147, 0.2);
  cursor: pointer;
}
.action-icon-btn:hover {
  background: rgba(255, 20, 147, 0.2);
  transform: scale(0.9);
}
.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
}
.calendar-day {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 12px 8px;
  border-radius: 12px;
  background: #F3E5F5;
  position: relative;
  border: 2px solid transparent;
  transition: all 0.3s;
}
.calendar-day.today {
  background: #FFF9C4;
  box-shadow: 0 4px 12px rgba(255, 213, 79, 0.4);
  border-color: #FFD54F;
}
.day-name {
  font-size: 12px;
  color: #8b7fa8;
}
.calendar-day.today .day-name {
  color: #F57F17;
  font-weight: 600;
}
.day-number {
  font-size: 18px;
  font-weight: 600;
  color: #5a4f7a;
}
.calendar-day.today .day-number {
  color: #F57F17;
}
.day-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #81D4FA;
  margin-top: 2px;
}
.calendar-day.today .day-dot {
  background: #F57F17;
}

/* ===== 新增：独立提醒模块样式 ===== */
.reminder-section {
  background: white;
  border-radius: 16px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
}
.reminder-section .section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.reminder-section .header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}
.reminder-section .section-icon {
  color: #b8a9d4;
  font-size: 20px;
}
.reminder-section .section-title {
  font-size: 18px;
  font-weight: 600;
  color: #5a4f7a;
  margin: 0;
}
.reminder-section .section-count {
  font-size: 14px;
  color: #b8a9d4;
}
.add-btn-small {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #FF1493 0%, #FF69B4 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
  box-shadow: 0 4px 12px rgba(255, 20, 147, 0.4);
  transition: all 0.3s;
  border: none;
  cursor: pointer;
}
.add-btn-small:hover {
  transform: scale(0.95);
  box-shadow: 0 2px 8px rgba(255, 20, 147, 0.3);
}
.reminder-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.reminder-item-card {
  display: flex;
  align-items: center;
  padding: 16px;
  background: linear-gradient(135deg, #FFFFFF 0%, #FFF9C4 100%);
  border-radius: 16px;
  box-shadow: 0 4px 16px rgba(255, 182, 193, 0.15);
  border-left: 6px solid #FFB6C1;
  transition: all 0.3s;
}
.reminder-item-card:hover {
  transform: translateX(4px);
  box-shadow: 0 6px 20px rgba(255, 182, 193, 0.25);
}
.reminder-item-card.completed {
  opacity: 0.6;
  border-left-color: #81C784;
}
.reminder-checkbox {
  width: 40px;
  height: 40px;
  border: 3px solid #FF1493;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  background: rgba(255, 20, 147, 0.1);
  transition: all 0.3s;
  cursor: pointer;
  color: #FF1493;
}
.reminder-item-card.completed .reminder-checkbox {
  background: linear-gradient(135deg, #FF1493 0%, #FF69B4 100%);
  border-color: #FF69B4;
}
.reminder-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}
.reminder-title {
  font-size: 16px;
  color: #5a4f7a;
  font-weight: 500;
}
.reminder-item-card.completed .reminder-title {
  text-decoration: line-through;
}
.reminder-time {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #b8a9d4;
}
.reminder-time i {
  font-size: 12px;
}
.reminder-actions {
  display: flex;
  gap: 8px;
}
.action-icon-btn-small {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #FF1493;
  font-size: 14px;
  border-radius: 50%;
  transition: all 0.3s;
  background: rgba(255, 182, 193, 0.1);
  border: 1px solid rgba(255, 182, 193, 0.2);
  cursor: pointer;
}
.action-icon-btn-small:hover {
  background: rgba(255, 182, 193, 0.2);
  transform: scale(0.9);
}

/* ===== 优化：今日行程列表样式 ===== */
.schedule-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.schedule-item-card {
  display: flex;
  align-items: flex-start;
  padding: 16px;
  background: white;
  border-radius: 16px;
  border-left: 8px solid;
  box-shadow: 0 4px 16px rgba(255, 20, 147, 0.18);
  transition: all 0.3s;
  cursor: pointer;
  position: relative;
}
.schedule-item-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #FF1493 0%, #FF69B4 50%, #FFB6C1 100%);
  border-radius: 16px 16px 0 0;
  opacity: 0.7;
}
.schedule-item-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(255, 20, 147, 0.3);
}
.schedule-time-block {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 80px;
  margin-right: 16px;
}
.time-main {
  font-size: 18px;
  font-weight: 600;
  color: #5a4f7a;
}
.time-end {
  font-size: 12px;
  color: #b8a9d4;
  margin-top: 4px;
}
.schedule-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.schedule-header-row {
  display: flex;
  align-items: center;
  gap: 12px;
}
.schedule-title {
  font-size: 16px;
  font-weight: 600;
  color: #5a4f7a;
}
.schedule-tag {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  color: #5a4f7a;
  font-weight: 600;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
.schedule-desc {
  font-size: 13px;
  color: #8b7fa8;
}
.schedule-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}
.schedule-location {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #b8a9d4;
}
.schedule-location i {
  font-size: 12px;
}
.series-badge-mini {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  background: #FFB6C1;
  border-radius: 10px;
  font-size: 11px;
  color: #FF1493;
  font-weight: 500;
}
.series-badge-mini i {
  font-size: 10px;
}
.schedule-status {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  min-width: 60px;
}
.status-dot {
  font-size: 8px;
}
.status-text {
  font-size: 12px;
  font-weight: 500;
}
.remind-badges {
  display: flex;
  gap: 6px;
  margin-top: 6px;
}
.remind-badge {
  width: 28px;
  height: 28px;
  background: #FFB6C1;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 4px rgba(255, 20, 147, 0.3);
}
.remind-icon {
  font-size: 12px;
  color: #FF1493;
}

/* 响应式适配 - 完全复用+适配课程表 */
@media (max-width: 768px) {
  .search-container { flex-direction:column;gap:15px;}
  .search-box { width:100%;max-width:none;}
  .search-actions { width:100%;justify-content:center;}
  .main-content { flex-direction:column;}
  .sidebar { width:100%;position:static;max-height:none;}
  .tags-grid { grid-template-columns:repeat(3,1fr);}
  .recommendation-grid { grid-template-columns:repeat(2,1fr);}
  .filter-row { flex-direction:column;align-items:flex-start;}
  .filter-actions { margin-left:0;width:100%;justify-content:center;margin-top:10px;}
  .schedule-grid { grid-template-columns:1fr;}
  .quick-actions-grid { grid-template-columns:repeat(2,1fr);}
  .stats-section { grid-template-columns:repeat(2,1fr);}
  .calendar-grid { grid-template-columns:repeat(4,1fr);}
}
@media (max-width: 480px) {
  .search-actions { flex-wrap:wrap;gap:10px;}
  .action-btn { padding:6px 10px;font-size:14px;}
  .recommendation-grid { grid-template-columns:1fr;}
  .filter-input { width:80px;}
  .table-cell { min-height:60px;}
  .course-name { font-size:12px;}
  .quick-actions-grid { grid-template-columns:1fr;}
  .stats-section { grid-template-columns:1fr;}
}
</style>