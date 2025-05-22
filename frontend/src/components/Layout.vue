<template>
  <div class="layout">
    <!-- 头部导航 -->
    <el-header class="header">
      <div class="logo">ElyOJ 在线评测系统</div>
      <div class="menu">
        <el-menu
          mode="horizontal"
          :default-active="activeMenu"
          background-color="#409EFF"
          text-color="#fff"
          active-text-color="#ffd04b"
          router
        >
          <el-menu-item index="/questions">题目列表</el-menu-item>
          <el-menu-item index="/submissions">提交记录</el-menu-item>
          <el-menu-item v-if="userStore.isAdmin" index="/admin/questions">
            题目管理
          </el-menu-item>
        </el-menu>
      </div>
      <div class="user-info">
        <span class="welcome">欢迎，{{ userStore.user?.username }}</span>
        <el-button @click="handleLogout" text size="small">退出登录</el-button>
      </div>
    </el-header>
    <!-- 主体内容 -->
    <el-main class="main">
      <slot></slot>
    </el-main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../store/user'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 当前激活的菜单
const activeMenu = computed(() => route.path)

// 在组件挂载时获取用户信息
onMounted(async () => {
  if (userStore.isLoggedIn && !userStore.user) {
    try {
      await userStore.getUserInfo()
    } catch (error) {
      console.error('获取用户信息失败', error)
    }
  }
})

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
  }).catch(() => {})
}
</script>

<style scoped>
.layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background-color: #409EFF;
  color: white;
  display: flex;
  align-items: center;
  padding: 0 20px;
  height: 60px;
}

.logo {
  font-size: 18px;
  font-weight: bold;
  width: 200px;
}

.menu {
  flex: 1;
}

.user-info {
  display: flex;
  align-items: center;
  margin-left: 20px;
}

.welcome {
  margin-right: 10px;
}

.main {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}
</style> 