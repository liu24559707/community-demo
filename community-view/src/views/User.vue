<template>
      <div class="fixed-header">
        <div class="Header">
            <van-icon class="icon-left" name="arrow-left" @click="$router.back()"/>
            <span style="color: white; font-size: 18px; margin: 10px ;margin-right: 40%; font-weight: bold">个人中心</span>
        </div>
    </div>

  <div style="height: 100vh;background-color: white; padding-top: 39px">
    <div>
      <div style="background-color: #F0F5F5;height: 90vh;border: 1px solid #eee;">
        <van-cell-group style="margin-top: 2vh;">
            <van-cell icon="location-o" title="个人信息" is-link  @click="$router.push('/userInfo')"/>
            <van-cell icon="location-o" title="账号与安全" is-link  @click="$router.push('/userSecurity')"/>
        </van-cell-group>

        <van-cell-group style="margin-top: 2vh;">
            <van-cell icon="setting-o" title="我的委托" is-link />
            <van-cell icon="warning-o" title="物业投诉" is-link />
            <van-cell icon="after-sale" title="缴费" is-link />
            <van-cell icon="chat-o" title="关于我们" is-link />
        </van-cell-group>

        <van-cell-group style="margin-top: 2vh;">
            <van-cell icon="question-o" title="检查更新" is-link />
            <van-cell icon="location-o" title="网络诊断" is-link />
            <van-cell icon="close" title="退出登录" is-link @click="logout()" />
        </van-cell-group>
    </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { useRouter } from 'vue-router'
import { getToken,removeToken } from '../request/token-utils';
import { ref } from 'vue';
import { showToast, Toast } from 'vant';
import {loginOutApi} from '../api/UserApi'


const router = useRouter()
const token = getToken()
if (!token) {
  router.push('/login')
}

function logout() {
  loginOutApi().then(res => {
    if (res.data.code == 200) {
      removeToken()
      showToast('退出成功')
      router.push('/login')
    } else {
      showToast('退出失败')
    }
  })
}

</script>

<style scoped>
.Header{
  height: 50px;
  background-color:#84A0C8;
  display: flex;

  justify-content: space-between; /* 将子元素分布在两端 */
  align-items: center;
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
