import { defineStore } from 'pinia'
import { login, getCurrentUser, logout } from '../api/user'
import { ElMessage } from 'element-plus'
import router from '../router'

export const useUserStore = defineStore('user', {
  state: () => ({
    user: null,
    token: localStorage.getItem('token') || '',
    role: localStorage.getItem('userRole') || ''
  }),
  
  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) => state.role === 'admin'
  },
  
  actions: {
    async login(loginForm) {
      try {
        const res = await login(loginForm)
        const { token, role } = res.data
        
        // 保存token和用户角色
        this.token = token
        this.role = role
        localStorage.setItem('token', token)
        localStorage.setItem('userRole', role)
        
        // 获取用户信息
        await this.getUserInfo()
        
        // 登录成功后跳转
        router.push('/questions')
        
        ElMessage({
          message: '登录成功',
          type: 'success'
        })
        
        return Promise.resolve()
      } catch (error) {
        return Promise.reject(error)
      }
    },
    
    async getUserInfo() {
      try {
        const res = await getCurrentUser()
        this.user = res.data
        return Promise.resolve(res.data)
      } catch (error) {
        return Promise.reject(error)
      }
    },
    
    async logout() {
      try {
        await logout()
        this.resetState()
        router.push('/login')
        
        ElMessage({
          message: '已退出登录',
          type: 'success'
        })
        
        return Promise.resolve()
      } catch (error) {
        return Promise.reject(error)
      }
    },
    
    resetState() {
      this.user = null
      this.token = ''
      this.role = ''
      localStorage.removeItem('token')
      localStorage.removeItem('userRole')
    }
  }
}) 