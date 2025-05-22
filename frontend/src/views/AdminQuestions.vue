<template>
  <Layout>
    <div class="admin-question-container">
      <!-- 操作按钮 -->
      <div class="operation-container">
        <el-button type="primary" @click="handleAdd">新增题目</el-button>
      </div>
      
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
              <el-option label="全部" value="" />
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
          <el-table-column label="提交数" prop="submitNum" width="100" />
          <el-table-column label="通过数" prop="acceptedNum" width="100" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="scope">
              <el-button
                type="primary"
                size="small"
                link
                @click="handleEdit(scope.row)"
              >
                编辑
              </el-button>
              <el-button
                type="danger"
                size="small"
                link
                @click="handleDelete(scope.row)"
              >
                删除
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
      
      <!-- 编辑/添加对话框 -->
      <el-dialog
        v-model="dialogVisible"
        :title="dialogType === 'add' ? '新增题目' : '编辑题目'"
        width="70%"
        append-to-body
      >
        <el-form
          ref="questionFormRef"
          :model="questionForm"
          :rules="questionRules"
          label-width="100px"
        >
          <el-form-item label="标题" prop="title">
            <el-input v-model="questionForm.title" placeholder="请输入题目标题" />
          </el-form-item>
          
          <el-form-item label="内容" prop="content">
            <el-input
              v-model="questionForm.content"
              type="textarea"
              :rows="6"
              placeholder="请输入题目内容"
            />
          </el-form-item>
          
          <el-form-item label="标签">
            <el-tag
              v-for="(tag, index) in formTags"
              :key="index"
              closable
              @close="removeTag(index)"
              style="margin-right: 5px"
            >
              {{ tag }}
            </el-tag>
            <el-input
              v-if="inputTagVisible"
              ref="tagInputRef"
              v-model="inputTagValue"
              size="small"
              style="width: 100px"
              @keyup.enter="handleInputConfirm"
              @blur="handleInputConfirm"
            />
            <el-button v-else size="small" @click="showTagInput">
              + 新增标签
            </el-button>
          </el-form-item>
          
          <el-form-item label="难度" prop="difficulty">
            <el-radio-group v-model="questionForm.difficulty">
              <el-radio :label="0">简单</el-radio>
              <el-radio :label="1">中等</el-radio>
              <el-radio :label="2">困难</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item label="答案" prop="answer">
            <el-input
              v-model="questionForm.answer"
              type="textarea"
              :rows="6"
              placeholder="请输入参考答案"
            />
          </el-form-item>
          
          <el-form-item label="判题用例" prop="judgeCase">
            <el-input
              v-model="questionForm.judgeCase"
              type="textarea"
              :rows="4"
              placeholder="请输入判题用例，JSON格式数组"
            />
          </el-form-item>
          
          <el-form-item label="判题配置" prop="judgeConfig">
            <el-input
              v-model="questionForm.judgeConfig"
              type="textarea"
              :rows="4"
              placeholder="请输入判题配置，JSON格式对象"
            />
          </el-form-item>
        </el-form>
        
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button
              type="primary"
              :loading="submitLoading"
              @click="submitQuestion"
            >
              确定
            </el-button>
          </div>
        </template>
      </el-dialog>
    </div>
  </Layout>
</template>

<script setup>
import { ref, reactive, nextTick, onMounted } from 'vue'
import Layout from '../components/Layout.vue'
import { getQuestionList, addQuestion, updateQuestion, deleteQuestion, getQuestionDetail } from '../api/question'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const submitLoading = ref(false)
const questionList = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogType = ref('add')
const questionFormRef = ref(null)
const tagInputRef = ref(null)
const inputTagVisible = ref(false)
const inputTagValue = ref('')
const formTags = ref([])

// 查询参数
const queryParams = reactive({
  current: 1,
  pageSize: 10,
  title: '',
  difficulty: ''
})

// 表单对象
const questionForm = reactive({
  id: undefined,
  title: '',
  content: '',
  tags: '',
  answer: '',
  judgeCase: '',
  judgeConfig: '',
  difficulty: 0
})

// 表单校验规则
const questionRules = {
  title: [{ required: true, message: '请输入题目标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入题目内容', trigger: 'blur' }],
  difficulty: [{ required: true, message: '请选择难度', trigger: 'change' }],
  answer: [{ required: true, message: '请输入参考答案', trigger: 'blur' }]
}

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

// 重置表单
const resetForm = () => {
  questionForm.id = undefined
  questionForm.title = ''
  questionForm.content = ''
  questionForm.tags = ''
  questionForm.answer = ''
  questionForm.judgeCase = ''
  questionForm.judgeConfig = ''
  questionForm.difficulty = 0
  formTags.value = []
}

// 添加题目
const handleAdd = () => {
  resetForm()
  dialogType.value = 'add'
  dialogVisible.value = true
}

// 编辑题目
const handleEdit = (row) => {
  resetForm()
  dialogType.value = 'edit'
  
  // 获取题目详情以确保获取完整数据
  getQuestionDetail(row.id).then(res => {
    const detailData = res.data;
    Object.assign(questionForm, detailData)
    formTags.value = parseTags(detailData.tags)
    
    // 确保答案字段正确赋值
    if (detailData.answer) {
      questionForm.answer = detailData.answer
    }
    
    dialogVisible.value = true
  }).catch(error => {
    console.error('获取题目详情失败:', error)
    ElMessage.error('获取题目详情失败')
  })
}

// 删除题目
const handleDelete = (row) => {
  ElMessageBox.confirm(`确认删除题目 "${row.title}" 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      await deleteQuestion(row.id)
      ElMessage.success('删除成功')
      fetchQuestionList()
    } catch (error) {
      console.error('删除题目失败:', error)
      ElMessage.error('删除题目失败')
    }
  }).catch(() => {})
}

// 显示标签输入框
const showTagInput = () => {
  inputTagVisible.value = true
  nextTick(() => {
    tagInputRef.value.focus()
  })
}

// 确认输入标签
const handleInputConfirm = () => {
  const tag = inputTagValue.value.trim()
  if (tag) {
    formTags.value.push(tag)
  }
  inputTagVisible.value = false
  inputTagValue.value = ''
}

// 移除标签
const removeTag = (index) => {
  formTags.value.splice(index, 1)
}

// 提交题目表单
const submitQuestion = async () => {
  try {
    // 表单验证
    await questionFormRef.value.validate()
    
    submitLoading.value = true
    
    // 处理标签
    questionForm.tags = JSON.stringify(formTags.value)
    
    if (dialogType.value === 'add') {
      // 添加题目
      await addQuestion(questionForm)
      ElMessage.success('添加成功')
    } else {
      // 更新题目
      await updateQuestion(questionForm)
      ElMessage.success('更新成功')
    }
    
    // 关闭对话框并刷新列表
    dialogVisible.value = false
    fetchQuestionList()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('提交失败')
  } finally {
    submitLoading.value = false
  }
}
</script>

<style scoped>
.admin-question-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.operation-container {
  display: flex;
  justify-content: flex-end;
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style> 