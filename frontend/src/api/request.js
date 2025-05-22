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
    }
    
    ElMessage({
      message: (error.response && error.response.data && error.response.data.message) || error.message || '网络错误',
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default service 