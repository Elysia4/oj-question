# ElyOJ - 在线判题系统

ElyOJ是一个在线判题系统，允许用户注册、登录，管理员可以创建题目，用户可以提交代码解答题目并获得实时判题结果。    

项目仓库：https://github.com/Elysia4/oj-question

## 系统架构

系统主要包含三个数据表：
- `user`：用户信息表
- `question`：题目信息表
- `question_submit`：用户提交记录表

## 系统操作流程

### 1. 用户管理流程

1. **用户注册**：
   - 新用户填写用户名、密码、邮箱等信息
   - 系统验证信息合法性，将用户信息存储到`user`表
   - 默认角色为`user`，状态为正常(0)

2. **用户登录**：
   - 用户输入用户名和密码
   - 系统验证用户名和密码是否匹配
   - 验证成功后生成登录凭证

3. **权限控制**：
   - 普通用户(`user`)：可以浏览题目、提交代码
   - 管理员(`admin`)：可以管理用户、创建和编辑题目

### 2. 题目管理流程

1. **管理员创建题目**：
   - 管理员登录系统
   - 填写题目标题、内容、标签、答案、难度等信息
   - 设置判题用例和判题配置
   - 系统将题目信息存储到`question`表

2. **题目查询与浏览**：
   - 用户可以根据标题、标签、难度等条件筛选题目
   - 查看题目详情，包括描述、示例等

### 3. 代码提交与判题流程

1. **用户提交代码**：
   - 用户选择编程语言，编写代码解答题目
   - 提交代码后，系统将提交信息记录到`question_submit`表
   - 初始状态为待判题(0)

2. **判题过程**：
   - 系统从待判题队列中获取提交记录
   - 将状态更新为判题中(1)
   - 根据题目配置的判题用例执行代码
   - 比对执行结果与预期输出

3. **判题结果**：
   - 所有测试用例通过：更新状态为成功(2)，记录运行时间、内存消耗等信息
   - 测试用例未通过或发生错误：更新状态为失败(3)，记录错误信息
   - 更新对应题目的提交数和通过数

### 4. 数据统计

1. **用户统计**：
   - 跟踪用户提交次数、通过题目数等

2. **题目统计**：
   - 记录题目的提交次数、通过次数、通过率等

## 示例数据说明

系统初始包含以下数据：

- 1个管理员账户（admin/admin123）
- 4个普通用户账户（user1、user2、teacher1、student1，密码均为123456）
- 4道不同难度和类型的编程题目
- 4条用户提交记录（其中3条成功，1条失败）

## 如何使用测试数据

1. 先执行`1.sql`创建数据库和表结构
2. 再执行`test_data.sql`插入测试数据
3. 登录系统：
   - 管理员账户：admin/admin123
   - 用户账户：user1/123456（其一）

## 项目功能

### 用户模块
- **用户登录**：支持用户名密码登录，密码错误时自动跳转回登录页面
- **权限管理**：区分普通用户和管理员角色

### 题目管理模块
- **题目列表**：分页展示所有题目，支持按标题和难度等条件筛选
- **题目详情**：展示题目的详细信息，包括内容、标签、难度等
- **题目创建/编辑/删除**：管理员可进行题目的增删改操作

### 提交评测模块
- **代码提交**：用户可以提交代码解答题目
- **评测结果**：展示代码的评测结果和相关信息
- **提交历史**：查看用户的历史提交记录

## 技术栈

### 后端
- Spring Boot 3.x
- MyBatis-Plus
- Spring Security
- MySQL 数据库

### 前端
- Vue 3
- Element Plus
- Axios
- Vue Router

## 数据库设计

### 用户表（user）
| 字段名     | 类型         | 说明                     |
|------------|--------------|--------------------------|
| id         | bigint       | 主键                     |
| username   | varchar(256) | 用户名（唯一）           |
| password   | varchar(512) | 密码                     |
| email      | varchar(256) | 邮箱                     |
| role       | varchar(256) | 角色（user/admin）       |
| status     | int          | 状态（0-正常、1-禁用）   |
| createTime | datetime     | 创建时间                 |
| updateTime | datetime     | 更新时间                 |
| isDelete   | tinyint      | 是否删除                 |

### 题目表（question）
| 字段名      | 类型         | 说明                          |
|-------------|--------------|-------------------------------|
| id          | bigint       | 主键                          |
| title       | varchar(512) | 标题                          |
| content     | text         | 内容                          |
| tags        | varchar(1024)| 标签列表（json 数组）         |
| answer      | text         | 题目答案                      |
| submitNum   | int          | 题目提交数                    |
| acceptedNum | int          | 题目通过数                    |
| judgeCase   | text         | 判题用例（json 数组）         |
| judgeConfig | text         | 判题配置（json 对象）         |
| difficulty  | int          | 难度（0-简单、1-中等、2-困难）|
| userId      | bigint       | 创建用户 id                   |
| createTime  | datetime     | 创建时间                      |
| updateTime  | datetime     | 更新时间                      |
| isDelete    | tinyint      | 是否删除                      |

### 题目提交表（question_submit）
| 字段名     | 类型         | 说明                                     |
|------------|--------------|------------------------------------------|
| id         | bigint       | 主键                                     |
| language   | varchar(128) | 编程语言                                 |
| userId     | bigint       | 提交用户 id                              |
| questionId | bigint       | 题目 id                                  |
| code       | text         | 用户代码                                 |
| judgeInfo  | text         | 判题信息（json 对象）                    |
| status     | int          | 判题状态（0-待判题、1-判题中、2-成功、3-失败）|
| createTime | datetime     | 创建时间                                 |
| updateTime | datetime     | 更新时间                                 |
| isDelete   | tinyint      | 是否删除                                 |

## 接口设计

### 用户接口
- POST `/api/user/login`：用户登录
- GET `/api/user/current`：获取当前登录用户信息
- POST `/api/user/logout`：用户退出登录

### 题目接口
- GET `/api/question/list`：获取题目列表（分页 + 条件查询）
- GET `/api/question/{id}`：获取题目详情
- POST `/api/question/add`：创建题目（管理员）
- PUT `/api/question/update`：更新题目（管理员）
- DELETE `/api/question/{id}`：删除题目（管理员）

### 提交接口
- POST `/api/question_submit/submit`：提交代码
- GET `/api/question_submit/list`：获取提交记录（分页 + 条件查询）
- GET `/api/question_submit/{id}`：获取提交详情

## 前端结构

### 主要目录
- `src/api/`: API接口请求
- `src/components/`: 公共组件
- `src/router/`: 路由配置
- `src/store/`: 状态管理
- `src/views/`: 页面视图

### 关键组件
- `Layout.vue`: 通用布局组件，包含导航栏和页面框架
- `Login.vue`: 登录页面
- `Questions.vue`: 题目列表页面
- `QuestionDetail.vue`: 题目详情页面
- `Submissions.vue`: 提交记录页面
- `AdminQuestions.vue`: 管理员题目管理页面

## 开发与部署

### 开发环境配置
1. 克隆项目仓库
2. 创建MySQL数据库，执行`1.sql`脚本初始化数据库
3. 配置后端`application.yml`中的数据库连接信息
4. 安装前端依赖：`cd frontend && npm install`

### 启动项目
1. 启动后端：`mvn spring-boot:run`
2. 启动前端：`cd frontend && npm run dev`
3. 访问：`http://localhost:5173`    

### 生产环境部署
1. 构建前端：`cd frontend && npm run build`
2. 构建后端：`mvn clean package`
3. 将前端构建产物`dist`目录下的文件复制到后端的`static`目录
4. 部署Spring Boot JAR包：`java -jar target/elyoj-0.0.1-SNAPSHOT.jar`

## 使用说明

### 用户登录
- 管理员账号：admin / admin123
- 访问系统后自动跳转到登录页面

### 题目浏览与代码提交
1. 登录后进入题目列表页面
2. 点击题目标题进入题目详情页
3. 在详情页编写代码并选择编程语言
4. 点击"提交代码"按钮提交解答

### 查看提交记录
1. 在导航栏点击"提交记录"
2. 可以筛选查询特定的提交记录
3. 点击"详情"按钮查看提交详情和代码

### 管理员功能
1. 以管理员身份登录
2. 在导航栏点击"题目管理"
3. 可以进行题目的增加、编辑和删除操作

## 项目配置

### 前端配置
前端配置位于`frontend/src/api/request.js`：
- API请求基础URL
- 请求超时时间
- Token处理逻辑

### 后端配置
后端配置位于`src/main/resources/application.yml`：
- 服务器端口
- 数据库连接配置
- JWT令牌配置
- MyBatis-Plus配置