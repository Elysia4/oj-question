<template>
  <Layout>
    <div class="question-container">
      <!-- 搜索和筛选 -->
      <div class="filter-container">
        <el-form :inline="true" :model="queryParams" size="small">
          <el-form-item label="标题">
            <el-input
              v-model="queryParams.title"
              placeholder="请输入标题"
              clearable
              @keyup.enter="handleQuery"
            />
          </el-form-item>
          
          <el-form-item label="难度">
            <el-select
              v-model="queryParams.difficulty"
              placeholder="请选择难度"
              clearable
              style="width: 100px"
            >
              <el-option label="简单" value="0" />
              <el-option label="中等" value="1" />
              <el-option label="困难" value="2" />
            </el-select>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 题目列表 -->
      <div class="question-list">
        <el-table
          v-loading="loading"
          :data="questionList"
          border
          style="width: 100%"
          @row-click="handleRowClick"
        >
          <el-table-column label="ID" prop="id" width="80" />
          <el-table-column label="标题" prop="title" min-width="200" show-overflow-tooltip />
          <el-table-column label="标签" min-width="150">
            <template #default="scope">
              <el-tag
                v-for="(tag, index) in parseTags(scope.row.tags)"
                :key="index"
                size="small"
                style="margin-right: 5px"
              >
                {{ tag }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="难度" width="100">
            <template #default="scope">
              <el-tag
                :type="getDifficultyTag(scope.row.difficulty)"
                size="small"
              >
                {{ getDifficultyLabel(scope.row.difficulty) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="通过率" width="120">
            <template #default="scope">
              {{ calculateAcceptRate(scope.row) }}
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
    </div>
  </Layout>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import Layout from '../components/Layout.vue'
import { getQuestionList } from '../api/question'

const router = useRouter()
const loading = ref(false)
const questionList = ref([])
const total = ref(0)

// 查询参数
const queryParams = reactive({
  current: 1,
  pageSize: 10,
  title: '',
  difficulty: ''
})

// 获取题目列表数据
const fetchQuestionList = async () => {
  loading.value = true
  try {
    const res = await getQuestionList(queryParams)
    questionList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('获取题目列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 初始化时获取数据
onMounted(() => {
  fetchQuestionList()
})

// 查询按钮点击
const handleQuery = () => {
  queryParams.current = 1
  fetchQuestionList()
}

// 重置按钮点击
const resetQuery = () => {
  queryParams.title = ''
  queryParams.difficulty = ''
  handleQuery()
}

// 页码变化
const handleCurrentChange = (val) => {
  queryParams.current = val
  fetchQuestionList()
}

// 每页显示数量变化
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  queryParams.current = 1
  fetchQuestionList()
}

// 解析标签
const parseTags = (tags) => {
  if (!tags) return []
  try {
    return JSON.parse(tags)
  } catch (error) {
    return []
  }
}

// 获取难度标签类型
const getDifficultyTag = (difficulty) => {
  const map = {
    0: 'success',
    1: 'warning',
    2: 'danger'
  }
  return map[difficulty] || 'info'
}

// 获取难度标签文本
const getDifficultyLabel = (difficulty) => {
  const map = {
    0: '简单',
    1: '中等',
    2: '困难'
  }
  return map[difficulty] || '未知'
}

// 计算通过率
const calculateAcceptRate = (row) => {
  if (!row.submitNum || row.submitNum === 0) {
    return '0%'
  }
  const rate = (row.acceptedNum / row.submitNum * 100).toFixed(1)
  return `${rate}%`
}

// 行点击事件
const handleRowClick = (row) => {
  router.push(`/questions/${row.id}`)
}
</script>

<style scoped>
.question-container {
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

.question-list {
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style> 