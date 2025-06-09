import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

// 创建axios实例
const service = axios.create({
  baseURL: '/api',
  timeout: 15000,
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 调试信息
    console.log('发送请求:', config.url, config.method, config.data || config.params)
    
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    console.log('响应成功:', response.config.url, response.data)
    const res = response.data
    if (res.code !== 0) {
      ElMessage({
        message: res.message || '请求错误',
        type: 'error',
        duration: 5 * 1000
      })
      
      // 401: 未登录或token过期
      if (res.code === 401) {
        localStorage.removeItem('token')
        localStorage.removeItem('userRole')
        router.push('/login')
      }
      
      return Promise.reject(new Error(res.message || '请求错误'))
    } else {
      return res
    }
  },
  error => {
    console.error('响应错误:', error)
    if (error.response) {
      console.error('错误详情:', {
        status: error.response.status,
        statusText: error.response.statusText,
        data: error.response.data,
        config: {
          url: error.config.url,
          method: error.config.method,
          data: error.config.data,
          headers: error.config.headers
        }
      })
      
      // 处理特定的错误状态码
      const { status } = error.response
      
      if (status === 401) {
        // 未登录或token过期
        localStorage.removeItem('token')
        localStorage.removeItem('userRole')
        
        // 如果不是登录页面，则跳转到登录页
        if (router.currentRoute.value.path !== '/login') {
          ElMessage.error('登录状态已过期，请重新登录')
          router.push('/login')
        }
      } else if (status === 403) {
        // 权限不足
        ElMessage.error('权限不足，无法访问')
      } else if (status === 400) {
        // 请求参数错误，通常是表单验证错误
        // 不在这里显示消息，让具体的业务代码处理
        return Promise.reject(error)
      } else if (status === 500) {
        // 服务器错误
        ElMessage.error('服务器错误，请稍后重试')
      } else {
        // 其他错误
        const message = (error.response.data && error.response.data.message) || error.message || '请求失败'
        // 对于注册页面，我们让具体的业务代码处理错误消息
        if (error.config.url.includes('/register')) {
          return Promise.reject(error)
        }
        ElMessage.error(message)
      }
    } else if (error.request) {
      // 请求发出但没有收到响应
      ElMessage.error('服务器无响应，请检查网络连接')
    } else {
      // 请求设置有问题
      ElMessage.error('请求配置错误: ' + error.message)
    }
    
    return Promise.reject(error)
  }
)

export default service 