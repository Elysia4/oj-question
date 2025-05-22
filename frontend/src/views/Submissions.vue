<template>
  <Layout>
    <div class="submissions-container">
      <!-- 搜索和筛选 -->
      <div class="filter-container">
        <el-form :inline="true" :model="queryParams" size="small">
          <el-form-item label="题目ID">
            <el-input
              v-model="queryParams.questionId"
              placeholder="请输入题目ID"
              clearable
              @keyup.enter="handleQuery"
            />
          </el-form-item>
          
          <el-form-item label="语言">
            <el-select
              v-model="queryParams.language"
              placeholder="请选择语言"
              clearable
              style="width: 100px"
            >
              <el-option label="Java" value="java" />
              <el-option label="C++" value="cpp" />
              <el-option label="Python" value="python" />
              <el-option label="JavaScript" value="javascript" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="状态">
            <el-select
              v-model="queryParams.status"
              placeholder="请选择状态"
              clearable
              style="width: 100px"
            >
              <el-option label="待判题" value="0" />
              <el-option label="判题中" value="1" />
              <el-option label="成功" value="2" />
              <el-option label="失败" value="3" />
            </el-select>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 提交记录列表 -->
      <div class="submissions-list">
        <el-table
          v-loading="loading"
          :data="submissionList"
          border
          style="width: 100%"
        >
          <el-table-column label="ID" prop="id" width="80" />
          <el-table-column label="题目ID" prop="questionId" width="100" />
          <el-table-column label="语言" prop="language" width="120" />
          <el-table-column label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusTag(scope.row.status)">
                {{ getStatusLabel(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="执行结果" min-width="150">
            <template #default="scope">
              {{ formatJudgeInfo(scope.row.judgeInfo) }}
            </template>
          </el-table-column>
          <el-table-column label="提交时间" prop="createTime" width="180" />
          <el-table-column label="操作" width="100" fixed="right">
            <template #default="scope">
              <el-button
                type="primary"
                size="small"
                link
                @click="showSubmitDetail(scope.row)"
              >
                详情
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          v-model:current-page="queryParams.current"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
      
      <!-- 提交详情对话框 -->
      <el-dialog
        v-model="dialogVisible"
        title="提交详情"
        width="70%"
        append-to-body
      >
        <div v-if="currentSubmit" class="submit-detail">
          <div class="detail-item">
            <span class="label">提交ID：</span>
            <span class="value">{{ currentSubmit.id }}</span>
          </div>
          <div class="detail-item">
            <span class="label">题目ID：</span>
            <span class="value">{{ currentSubmit.questionId }}</span>
          </div>
          <div class="detail-item">
            <span class="label">语言：</span>
            <span class="value">{{ currentSubmit.language }}</span>
          </div>
          <div class="detail-item">
            <span class="label">状态：</span>
            <el-tag :type="getStatusTag(currentSubmit.status)">
              {{ getStatusLabel(currentSubmit.status) }}
            </el-tag>
          </div>
          <div class="detail-item">
            <span class="label">提交时间：</span>
            <span class="value">{{ currentSubmit.createTime }}</span>
          </div>
          <div class="detail-item">
            <span class="label">判题结果：</span>
            <span class="value">{{ formatJudgeInfo(currentSubmit.judgeInfo) }}</span>
          </div>
          <div class="code-container">
            <div class="code-header">提交的代码：</div>
            <el-input
              type="textarea"
              v-model="currentSubmit.code"
              :rows="15"
              readonly
            />
          </div>
        </div>
      </el-dialog>
    </div>
  </Layout>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import Layout from '../components/Layout.vue'
import { getSubmitList, getSubmitDetail } from '../api/submit'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const submissionList = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const currentSubmit = ref(null)

// 查询参数
const queryParams = reactive({
  current: 1,
  pageSize: 10,
  questionId: '',
  language: '',
  status: ''
})

// 获取提交记录列表
const fetchSubmissionList = async () => {
  loading.value = true
  try {
    const res = await getSubmitList(queryParams)
    submissionList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('获取提交记录失败:', error)
    ElMessage.error('获取提交记录失败')
  } finally {
    loading.value = false
  }
}

// 初始化时获取数据
onMounted(() => {
  fetchSubmissionList()
})

// 查询按钮点击
const handleQuery = () => {
  queryParams.current = 1
  fetchSubmissionList()
}

// 重置按钮点击
const resetQuery = () => {
  queryParams.questionId = ''
  queryParams.language = ''
  queryParams.status = ''
  handleQuery()
}

// 页码变化
const handleCurrentChange = (val) => {
  queryParams.current = val
  fetchSubmissionList()
}

// 每页显示数量变化
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  queryParams.current = 1
  fetchSubmissionList()
}

// 获取状态标签类型
const getStatusTag = (status) => {
  const map = {
    0: 'info',
    1: 'warning',
    2: 'success',
    3: 'danger'
  }
  return map[status] || 'info'
}

// 获取状态标签文本
const getStatusLabel = (status) => {
  const map = {
    0: '待判题',
    1: '判题中',
    2: '成功',
    3: '失败'
  }
  return map[status] || '未知'
}

// 格式化判题信息
const formatJudgeInfo = (judgeInfo) => {
  if (!judgeInfo) return '暂无结果'
  
  try {
    const info = JSON.parse(judgeInfo)
    if (info.message) {
      return info.message
    }
    return JSON.stringify(info)
  } catch (error) {
    return judgeInfo
  }
}

// 查看提交详情
const showSubmitDetail = async (row) => {
  try {
    const res = await getSubmitDetail(row.id)
    currentSubmit.value = res.data
    dialogVisible.value = true
  } catch (error) {
    console.error('获取提交详情失败:', error)
    ElMessage.error('获取提交详情失败')
  }
}
</script>

<style scoped>
.submissions-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.filter-container {
  background-color: white;
  padding: 15px;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.submissions-list {
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.submit-detail {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.detail-item {
  display: flex;
  align-items: center;
}

.detail-item .label {
  width: 100px;
  font-weight: bold;
}

.code-container {
  margin-top: 15px;
}

.code-header {
  font-weight: bold;
  margin-bottom: 10px;
}
</style> 