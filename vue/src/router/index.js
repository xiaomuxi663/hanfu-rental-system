import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/home/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/Login.vue'),
    meta: { title: '用户登录' }
  },
  {
    path: '/admin-login',
    name: 'AdminLogin',
    component: () => import('@/views/login/AdminLogin.vue'),
    meta: { title: '管理员登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/register/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/user/Profile.vue'),
    meta: { title: '个人中心', requireAuth: true }
  },
  {
    path: '/hanfu',
    name: 'HanfuList',
    component: () => import('@/views/hanfu/HanfuList.vue'),
    meta: { title: '汉服展示' }
  },
  {
    path: '/hanfu/:id',
    name: 'HanfuDetail',
    component: () => import('@/views/hanfu/HanfuDetail.vue'),
    meta: { title: '汉服详情' }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/layout/AdminLayout.vue'),
    meta: { title: '管理后台', requireAuth: true },
    children: [
      {
        path: '',
        redirect: '/admin/dashboard'
      },
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '工作台' }
      },
      {
        path: 'user',
        name: 'UserManage',
        component: () => import('@/views/admin/UserManage.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'notice',
        name: 'NoticeManage',
        component: () => import('@/views/admin/NoticeManage.vue'),
        meta: { title: '公告管理' }
      },
      {
        path: 'banner',
        name: 'BannerManage',
        component: () => import('@/views/admin/BannerManage.vue'),
        meta: { title: '轮播图管理' }
      },
      {
        path: 'config',
        name: 'ConfigManage',
        component: () => import('@/views/admin/ConfigManage.vue'),
        meta: { title: '系统配置' }
      },
      {
        path: 'category',
        name: 'CategoryManage',
        component: () => import('@/views/admin/CategoryManage.vue'),
        meta: { title: '分类管理' }
      },
      {
        path: 'hanfu',
        name: 'HanfuManage',
        component: () => import('@/views/admin/HanfuManage.vue'),
        meta: { title: '汉服管理' }
      },
      {
        path: 'sku',
        name: 'SkuManage',
        component: () => import('@/views/admin/SkuManage.vue'),
        meta: { title: '库存管理' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 汉服租赁平台` : '汉服租赁平台'
  
  const token = localStorage.getItem('token')
  if (to.meta.requireAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
