<template>
 <router-view></router-view>
 <van-tabbar v-model="active" >
  <van-tabbar-item icon="home-o" @click="$router.push('/')">首页</van-tabbar-item>
  <van-tabbar-item icon="newspaper-o" @click="$router.push('/commission')">委托</van-tabbar-item>
  <van-tabbar-item icon="chat-o" @click="$router.push('/mes')">通知</van-tabbar-item>
  <van-tabbar-item icon="contact-o" @click="$router.push('/user')">我的</van-tabbar-item>
</van-tabbar>
</template>
<script lang="ts" setup>

import { ref, watch } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute()
const active = ref(0)

const list = ["/", "/commission", "/mes", "/user"]

watch(route,(newRoute)=>{
    active.value = list.indexOf(newRoute.path)
})
</script>

<style scoped>
/* 设置点击颜色 */
.van-tabbar-item--active{
    color: #C2D8F0;
}


</style>