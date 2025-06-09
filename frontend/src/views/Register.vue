<template>
  <div class="register-container">
    <div class="register-card">
      <h2 class="title">ElyOJ 在线评测系统</h2>
        <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="registerRules"
          label-position="top"
          @keyup.enter="handleRegister"
        >
          <el-form-item label="用户名" prop="username">
            <el-input 
              v-model="registerForm.username" 
              :status="!usernameAvailable ? 'error' : ''"
            />
            <div v-if="usernameChecking" class="checking-tip">正在检查用户名是否可用...</div>
            <div v-else-if="!usernameAvailable && registerForm.username.length >= 3" class="error-tip">
              该用户名已被使用，请更换
            </div>
          </el-form-item>
          
          <el-form-item label="邮箱" prop="email">
            <el-input 
              v-model="registerForm.email"
              :status="!emailAvailable ? 'error' : ''"
            />
            <div v-if="emailChecking" class="checking-tip">正在检查邮箱是否可用...</div>
            <div v-else-if="!emailAvailable && registerForm.email.includes('@')" class="error-tip">
              该邮箱已被注册，请更换
            </div>
          </el-form-item>
          
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="registerForm.password"
              show-password
              type="password"
            />
          </el-form-item>
          
          <el-form-item label="确认密码" prop="checkPassword">
            <el-input
              v-model="registerForm.checkPassword"
              show-password
              type="password"
            />
          </el-form-item>
          
          <el-form-item>
            <el-button
              type="primary"
              :loading="loading"
              class="register-button"
              @click="handleRegister"
            >
              注册
            </el-button>
          </el-form-item>
          
          <div class="login-link">
            已有账号？<router-link to="/login">立即登录</router-link>
          </div>
        </el-form>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, reactive, watch } from 'vue'
import { useRouter } from 'vue-router'
import { register, checkUsername, checkEmail } from '../api/user'
import { ElMessage } from 'element-plus'
import { debounce } from 'lodash-es'
  
  const router = useRouter()
  const registerFormRef = ref(null)
const loading = ref(false)
const usernameChecking = ref(false)
const emailChecking = ref(false)
const usernameAvailable = ref(true)
const emailAvailable = ref(true)

// 注册表单
const registerForm = reactive({
  username: '',
  email: '',
  password: '',
  checkPassword: ''
})

// 防抖检查用户名
const checkUsernameAvailability = debounce(async (username) => {
  if (!username || username.length < 3) return
  
  try {
    usernameChecking.value = true
    const res = await checkUsername(username)
    usernameAvailable.value = res.data
  } catch (error) {
    console.error('检查用户名出错:', error)
    usernameAvailable.value = false
  } finally {
    usernameChecking.value = false
  }
}, 500)

// 防抖检查邮箱
const checkEmailAvailability = debounce(async (email) => {
  if (!email || !email.includes('@')) return
  
  try {
    emailChecking.value = true
    const res = await checkEmail(email)
    emailAvailable.value = res.data
  } catch (error) {
    console.error('检查邮箱出错:', error)
    emailAvailable.value = false
  } finally {
    emailChecking.value = false
  }
}, 500)

// 监听用户名变化
watch(() => registerForm.username, (newVal) => {
  if (newVal && newVal.length >= 3) {
    checkUsernameAvailability(newVal)
  } else {
    usernameAvailable.value = true
  }
})

// 监听邮箱变化
watch(() => registerForm.email, (newVal) => {
  if (newVal && newVal.includes('@')) {
    checkEmailAvailability(newVal)
  } else {
    emailAvailable.value = true
  }
})
  
  // 校验密码是否一致
  const validatePassword = (rule, value, callback) => {
    if (value !== registerForm.password) {
      callback(new Error('两次输入的密码不一致'))
    } else {
      callback()
    }
  }
  
  // 表单校验规则
  const registerRules = {
    username: [
      { required: true, message: '请输入用户名', trigger: 'blur' },
      { min: 3, max: 20, message: '用户名长度必须在3-20之间', trigger: 'blur' }
    ],
    email: [
      { required: true, message: '请输入邮箱', trigger: 'blur' },
      { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
    ],
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' },
      { min: 6, max: 20, message: '密码长度必须在6-20之间', trigger: 'blur' }
    ],
    checkPassword: [
      { required: true, message: '请确认密码', trigger: 'blur' },
      { validator: validatePassword, trigger: 'blur' }
    ]
  }
  
  // 注册方法
  const handleRegister = async () => {
    if (loading.value) return
    
    // 检查用户名和邮箱是否可用
    if (!usernameAvailable.value) {
      ElMessage.error('用户名已被使用，请更换')
      return
    }
    
    if (!emailAvailable.value) {
      ElMessage.error('邮箱已被注册，请更换')
      return
    }
    
    try {
      // 表单验证
      await registerFormRef.value.validate()
      
      console.log('注册表单:', JSON.stringify(registerForm))
      
      loading.value = true
      
      // 调用注册API
      await register(registerForm)
      
      ElMessage({
        message: '注册成功，请登录',
        type: 'success'
      })
      
      // 注册成功后跳转到登录页
      router.push('/login')
    } catch (error) {
      console.error('注册失败:', error)
      
      // 处理不同类型的错误
      if (error.response) {
        const { data } = error.response
        
        // 处理后端返回的具体错误信息
        if (data && data.message) {
          if (data.message.includes('用户名已存在')) {
            ElMessage.error('用户名已被注册，请更换用户名')
          } else if (data.message.includes('邮箱已被注册')) {
            ElMessage.error('邮箱已被注册，请更换邮箱或找回密码')
          } else {
            ElMessage.error(data.message)
          }
        } else {
          ElMessage.error('注册失败，请稍后重试')
        }
      } else if (error.message) {
        // 处理网络错误等
        ElMessage.error(`注册失败: ${error.message}`)
      } else {
        ElMessage.error('注册失败，请稍后重试')
      }
    } finally {
      loading.value = false
    }
  }
  </script>
  
  <style scoped>
  .register-container {
    height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: #f5f7fa;
  }
  
  .register-card {
    width: 400px;
    padding: 30px;
    background-color: white;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  }
  
  .title {
    text-align: center;
    margin-bottom: 30px;
    color: #409EFF;
  }
  
  .register-button {
    width: 100%;
  }
  
  .login-link {
    text-align: center;
    margin-top: 15px;
    font-size: 14px;
  }
  
  .login-link a {
  color: #409EFF;
  text-decoration: none;
}

.checking-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.error-tip {
  font-size: 12px;
  color: #F56C6C;
  margin-top: 5px;
}
  </style>