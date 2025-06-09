import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('../views/Login.vue')
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('../views/Register.vue')
    },
    {
      path: '/questions',
      name: 'Questions',
      component: () => import('../views/Questions.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/questions/:id',
      name: 'QuestionDetail',
      component: () => import('../views/QuestionDetail.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/submissions',
      name: 'Submissions',
      component: () => import('../views/Submissions.vue'),
      meta: { requiresAuth: true }
    },
    {
      path: '/admin/questions',
      name: 'AdminQuestions',
      component: () => import('../views/AdminQuestions.vue'),
      meta: { requiresAuth: true, requiresAdmin: true }
    }
  ]
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userRole = localStorage.getItem('userRole')
  
  // 需要登录的页面
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } 
  // 需要管理员权限的页面
  else if (to.meta.requiresAdmin && userRole !== 'admin') {
    next('/questions')
  } 
  else {
    next()
  }
})

export default router 