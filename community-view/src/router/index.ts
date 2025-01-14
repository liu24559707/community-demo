import { createRouter, createWebHistory } from 'vue-router'


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'Main',
      component: () => import('../views/Main.vue'),
      children:[
        {
          path: '/',
          name: 'Home',
          component: () => import('../views/Home.vue')
        },{
          path: '/user',
          name: 'User',
          component: () => import('../views/User.vue')
        },{
          path: '/mes',
          name: 'Mes',
          component: () => import('../views/Mes.vue')
        },{
          path:'/commission',
          name:'Commission',
          component:()=>import('../views/Entrust.vue')
        },{
          path:'/login',
          name:'Login',
          component:()=>import('../views/Login.vue')
        },{
          path:'/newsdetail',
          name:'NewsDetail',
          component:()=>import('../views/newsdetail.vue')
        },{
          path:'/userInfo',
          name:'UserInfo',
          component:()=>import('../views/userInfo.vue')
        },{
          path:'/userSecurity',
          name:'UserSecurity',
          component:()=>import('../views/UserSecurity.vue')
        },{
          path:'/complaint',
          name:'Complain',
          component:()=>import('../views/complain.vue')
        },{
          path:'/complainAdd',
          n