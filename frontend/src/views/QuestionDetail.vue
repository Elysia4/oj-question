<template>
  <Layout>
    <div v-loading="loading" class="question-detail-container">
      <template v-if="question">
        <!-- 题目信息 -->
        <div class="question-header">
          <div class="title-info">
            <h2>{{ question.title }}</h2>
            <div class="tags">
              <el-tag
                v-for="(tag, index) in parseTags(question.tags)"
                :key="index"
                size="small"
                style="margin-right: 5px"
              >
                {{ tag }}
              </el-tag>
              <el-tag
                :type="getDifficultyTag(question.difficulty)"
                size="small"
              >
                {{ getDifficultyLabel(question.difficulty) }}
              </el-tag>
            </div>
          </div>
          <div class="stats">
            <div class="stat-item">
              <div class="label">提交数</div>
              <div class="value">{{ question.submitNum }}</div>
            </div>
            <div class="stat-item">
              <div class="label">通过数</div>
              <div class="value">{{ question.acceptedNum }}</div>
            </div>
            <div class="stat-item">
              <div class="label">通过率</div>
              <div class="value">{{ calculateAcceptRate(question) }}</div>
            </div>
          </div>
        </div>
        
        <!-- 题目内容和代码编辑器 -->
        <div class="question-body">
          <div class="content-pane">
            <div class="content" v-html="formatContent(question.content)"></div>
          </div>
          
          <div class="editor-pane">
            <div class="editor-header">
              <span>编程语言：</span>
              <el-select v-model="language"  placeholder="请选择语言">
                <el-option label="Java" value="java" />
                <el-option label="C++" value="cpp" />
                <el-option label="Python" value="python" />
                <el-option label="JavaScript" value="javascript" />
              </el-select>
              <el-button type="primary" :loading="submitting" @click="handleSubmit">
                提交代码
              </el-button>
            </div>
            <div class="editor-wrapper">
              <el-input
                v-model="code"
                type="textarea"
                :rows="15"
                placeholder="请输入代码..."
                resize="none"
                style="height: 100%"
              />
            </div>
          </div>
        </div>
      </template>
    </div>
  </Layout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import Layout from '../components/Layout.vue'
import { getQuestionDetail } from '../api/question'
import { submitCode } from '../api/submit'

const route = useRoute()
const router = useRouter()
const questionId = route.params.id
const loading = ref(true)
const submitting = ref(false)
const question = ref(null)
const language = ref('java')
const code = ref('')

// 获取题目详情
const fetchQuestionDetail = async () => {
  loading.value = true
  try {
    const res = await getQuestionDetail(questionId)
    question.value = res.data
  } catch (error) {
    console.error('获取题目详情失败:', error)
    ElMessage.error('获取题目详情失败')
  } finally {
    loading.value = false
  }
}

// 初始化
onMounted(() => {
  fetchQuestionDetail()
})

// 提交代码
const handleSubmit = async () => {
  if (!code.value.trim()) {
    ElMessage.warning('请输入代码')
    return
  }
  
  submitting.value = true
  try {
    const submitData = {
      language: language.value,
      code: code.value,
      questionId
    }
    
    await submitCode(submitData)
    ElMessage.success('提交成功')
    router.push('/submissions')
  } catch (error) {
    console.error('提交代码失败:', error)
    ElMessage.error('提交代码失败')
  } finally {
    submitting.value = false
  }
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

// 格式化内容（保持换行等）
const formatContent = (content) => {
  if (!content) return ''
  return content.replace(/\n/g, '<br>')
}
</script>

<style scoped>
.question-detail-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.question-header {
  background-color: white;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: space-between;
}

.tags {
  margin-top: 10px;
}

.stats {
  display: flex;
  gap: 20px;
}

.stat-item {
  text-align: center;
}

.stat-item .label {
  font-size: 14px;
  color: #666;
}

.stat-item .value {
  font-size: 18px;
  font-weight: bold;
  color: #409EFF;
}

.question-body {
  display: flex;
  gap: 20px;
  height: calc(100vh - 260px);
}

.content-pane {
  flex: 1;
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  padding: 20px;
  overflow-y: auto;
}

.editor-pane {
  flex: 1;
  background-color: white;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
}

.editor-header {
  padding: 10px;
  border-bottom: 1px solid #eee;
  display: flex;
  align-items: center;
  gap: 10px;
}

.editor-wrapper {
  flex: 1;
  padding: 10px;
  overflow: hidden;
}

.content {
  line-height: 1.6;
}
</style> 