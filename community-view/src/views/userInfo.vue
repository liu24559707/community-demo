<template>
        <div class="fixed-header">
        <div class="Header">
            <van-icon class="icon-left" name="arrow-left" @click="$router.back()"/>
            <span style="color: white; font-size: 18px; margin: 10px">个人信息</span>
            <van-icon class="icon-right" name="ellipsis" />
        </div>
    </div>


    <div style="margin-top: 60px;">
        <van-cell title="头像" >
        </van-cell>
        <van-cell title="用户名"  :value="userInfo.username" />
        <van-cell title="身份"   :value="userInfo.role!=4?'工作人员':'业主'" />
        <van-cell title="住址"   :value="userInfo.address"/>
        <van-cell title="手机号"  :value="userInfo.phone"/>
        <van-cell title="社区信誉"   :value="userInfo.creditScore"/>
    </div>
</template>

<script lang="ts" setup>
import { ref,onMounted } from 'vue';
import {getUserInfoApi }from '../api/UserApi'

let userInfo = ref({})

function getUserInfo(){
    getUserInfoApi().then(res=>{
        if(res.data.code == 200){
          userInfo.value = res.data.data
        }
    })
}


onMounted(()=>{
    getUserInfo()
})
</script>


<style scoped>
.Header{
  height: 50px;
  background-color:#84A0C8;
  display: flex;
  align-content: center;
  justify-content: space-between; /* 将子元素分布在两端 */

}

.fixed-header {
  position: fixed;
  top: 0;
  width: 100%;
  background-color: white; /* 确保背景颜色为白色 */
  z-index: 1000; /* 确保这个元素在其他元素之上显示 */
}
    
.icon-left,
.icon-right{
  float: right;
  font-size: 24px;
  color: white;
  margin: 10px;
}
</style>