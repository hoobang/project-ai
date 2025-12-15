import { createRouter, createWebHistory } from 'vue-router'
import Login from './components/Login.vue'
import ChatMain from './components/ChatMain.vue'

const routes = [
  {
    path: '/',
    name: 'Login',
    component: Login
  },
  {
    path: '/chat',
    name: 'ChatMain',
    component: ChatMain,
    meta: {
      requiresAuth: true
    }
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫，检查用户是否已登录
router.beforeEach((to, from, next) => {
  const isAuthenticated = localStorage.getItem('token') !== null
  
  if (to.matched.some(record => record.meta.requiresAuth)) {
    // 路由需要认证
    if (isAuthenticated) {
      next()
    } else {
      // 未登录，重定向到登录页
      next('/')
    }
  } else {
    // 路由不需要认证
    next()
  }
})

export default router
