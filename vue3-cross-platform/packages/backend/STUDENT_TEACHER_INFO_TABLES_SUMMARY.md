# 学生和教师信息表创建总结

## 创建时间
2024年12月

## ✅ 已完成的工作

### 1. 数据库表创建（2个表）

#### `student_info` 表（学生信息表）

**用途**：存储学生信息，用于学生身份认证（替代教务系统接口）

**主要字段**：
- `school_id` - 学校ID（关联university表）
- `student_id` - 学号（唯一，与school_id联合唯一）
- `name` - 姓名
- `id_card` - 身份证号
- `major` - 专业
- `grade` - 年级
- `class_name` - 班级
- `status` - 状态（ACTIVE/GRADUATED/SUSPENDED）
- `is_deleted` - 逻辑删除

**索引**：
- `uk_school_student` - 学校ID+学号唯一索引
- `idx_student_id` - 学号索引
- `idx_id_card` - 身份证号索引
- `idx_school_id` - 学校ID索引

**外键**：
- `fk_student_university` - 关联university表

---

#### `teacher_info` 表（教师信息表）

**用途**：存储教师信息，用于教师身份认证（替代教师认证系统接口）

**主要字段**：
- `school_id` - 学校ID（关联university表）
- `teacher_id` - 工号（唯一，与school_id联合唯一）
- `name` - 姓名
- `id_card` - 身份证号
- `department` - 部门
- `title` - 职称
- `phone` - 联系电话
- `email` - 邮箱
- `status` - 状态（ACTIVE/RESIGNED/SUSPENDED）
- `is_deleted` - 逻辑删除

**索引**：
- `uk_school_teacher` - 学校ID+工号唯一索引
- `idx_teacher_id` - 工号索引
- `idx_id_card` - 身份证号索引
- `idx_school_id` - 学校ID索引

**外键**：
- `fk_teacher_university` - 关联university表

---

### 2. 实体类创建（2个）

- ✅ `StudentInfo.java` - 学生信息实体类
- ✅ `TeacherInfo.java` - 教师信息实体类

---

### 3. Mapper 接口创建（2个）

- ✅ `StudentInfoMapper.java` - 学生信息Mapper接口
- ✅ `TeacherInfoMapper.java` - 教师信息Mapper接口

---

### 4. Excel DTO 创建（2个）

- ✅ `StudentInfoExcelDTO.java` - 学生信息Excel导入导出DTO
- ✅ `TeacherInfoExcelDTO.java` - 教师信息Excel导入导出DTO

---

### 5. Service 方法实现（12个方法）

#### 学生信息管理（6个方法）

- ✅ `getStudentInfoList()` - 获取学生信息列表（支持筛选和搜索）
- ✅ `addStudentInfo()` - 添加单个学生信息
- ✅ `importStudentInfo()` - Excel批量导入学生信息
- ✅ `exportStudentInfo()` - Excel导出学生信息
- ✅ `updateStudentInfo()` - 更新学生信息
- ✅ `deleteStudentInfo()` - 删除学生信息（逻辑删除）

#### 教师信息管理（6个方法）

- ✅ `getTeacherInfoList()` - 获取教师信息列表（支持筛选和搜索）
- ✅ `addTeacherInfo()` - 添加单个教师信息
- ✅ `importTeacherInfo()` - Excel批量导入教师信息
- ✅ `exportTeacherInfo()` - Excel导出教师信息
- ✅ `updateTeacherInfo()` - 更新教师信息
- ✅ `deleteTeacherInfo()` - 删除教师信息（逻辑删除）

---

### 6. Controller 接口添加（12个接口）

#### 学生信息管理接口（6个）

- ✅ `GET /api/university/students` - 获取学生信息列表
- ✅ `POST /api/university/students` - 添加学生信息
- ✅ `POST /api/university/students/import` - 批量导入学生信息
- ✅ `GET /api/university/students/export` - 导出学生信息
- ✅ `PUT /api/university/students/{studentInfoId}` - 更新学生信息
- ✅ `DELETE /api/university/students/{studentInfoId}` - 删除学生信息

#### 教师信息管理接口（6个）

- ✅ `GET /api/university/teachers` - 获取教师信息列表
- ✅ `POST /api/university/teachers` - 添加教师信息
- ✅ `POST /api/university/teachers/import` - 批量导入教师信息
- ✅ `GET /api/university/teachers/export` - 导出教师信息
- ✅ `PUT /api/university/teachers/{teacherInfoId}` - 更新教师信息
- ✅ `DELETE /api/university/teachers/{teacherInfoId}` - 删除教师信息

---

### 7. AuthService 更新

**更新内容**：
- ✅ `applyStudentAuth()` - 使用 `student_info` 表验证学生身份
- ✅ `applyTeacherAuth()` - 使用 `teacher_info` 表验证教师身份

**验证逻辑**：
- 验证学号/工号、姓名、身份证号是否匹配
- 验证学生/教师状态是否为ACTIVE
- 验证是否属于指定高校

---

## 📊 创建统计

- **数据库表**：2个
- **实体类**：2个
- **Mapper接口**：2个
- **Excel DTO**：2个
- **Service方法**：12个
- **Controller接口**：12个
- **测试数据**：学生3条，教师2条

---

## 🔄 使用流程

### 1. 高校管理员导入学生/教师信息

**方式一：Excel批量导入**
1. 准备Excel文件（格式见Excel DTO）
2. 调用导入接口：`POST /api/university/students/import` 或 `POST /api/university/teachers/import`
3. 系统自动验证并导入数据

**方式二：手动添加**
1. 调用添加接口：`POST /api/university/students` 或 `POST /api/university/teachers`
2. 填写学生/教师信息

### 2. 学生/教师身份认证

**学生认证**：
1. 用户提交认证申请（学号、姓名、身份证号）
2. 系统从 `student_info` 表查询验证
3. 验证通过后创建认证申请记录

**教师认证**：
1. 用户提交认证申请（工号、姓名、身份证号、证明文件）
2. 系统从 `teacher_info` 表查询验证
3. 验证通过后创建认证申请记录

---

## 📝 Excel导入格式

### 学生信息Excel格式

| 学号 | 姓名 | 身份证号 | 专业 | 年级 | 班级 | 状态 |
|------|------|----------|------|------|------|------|
| 20230001 | 张三 | 110101199001011234 | 计算机科学与技术 | 2023 | 计科2301 | ACTIVE |

### 教师信息Excel格式

| 工号 | 姓名 | 身份证号 | 部门 | 职称 | 联系电话 | 邮箱 | 状态 |
|------|------|----------|------|------|----------|------|------|
| T001 | 张教授 | 110101197001011234 | 计算机学院 | 教授 | 13800138001 | zhang@example.com | ACTIVE |

---

## ✅ 优势

1. **无需外部接口**：不依赖教务系统或教师认证系统接口
2. **灵活管理**：高校管理员可以自主管理学生和教师信息
3. **批量导入**：支持Excel批量导入，提高效率
4. **数据完整**：包含专业、年级、部门、职称等详细信息
5. **状态管理**：支持学生毕业、教师离职等状态管理

---

## 🔄 后续优化建议

1. **数据同步**：如果后续对接了教务系统，可以实现数据同步功能
2. **批量更新**：支持Excel批量更新学生/教师信息
3. **数据校验**：增强身份证号格式验证、学号格式验证等
4. **历史记录**：记录学生/教师信息的变更历史

---

**创建完成时间**：2024年12月  
**创建人员**：AI Assistant
