<template>
    <div style="height: 100vh;align-content: center">
        <div style="margin-bottom: 8%;margin-left: 32%;">
         <span style="font-style: italic;font-size: large;font-family: 微软雅黑;">欢迎来到i社区</span>
        </div>
        <div class="login" v-if="!flag">
        <input class="in" type="text" placeholder="用户名" v-model="user.username" />
        <div style="margin-top: 20px;"></div>
        <input  class="in" type="password" placeholder="密码" v-model="user.password"/>
        </div>
        <div class="login" v-else>
        <input class="in" type="text" placeholder="手机号"  v-model="user.phone"/>
        <div style="margin-top: 20px;"></div>
        <input  class="in" type="password" placeholder="验证码" v-model="user.code"/>
        <div style="margin-top: 20px;margin-left: 70%;">
            <span style="font-size: small;color: darkblue;" @click="getCode">获取验证码</span>
        </div>
        </div>
        <div style="margin-top: 20px;margin-left: 15%;">
            <van-checkbox v-model="flag" icon-size="small" checked-color="darkblue" style="font-size: small;">使用手机号登录</van-checkbox>
        </div>
        
        <div style="margin-top: 40px;margin-left: 15%;">
            <van-button round style="width: 80%;background-color: darkblue;" @click="login"><span style="color: white;">登录</span></van-button>
        </div>
        <div style="margin-top: 20px;margin-left: 15%;">
            <van-button round style="width: 80%;background-color: darkblue;"><span style="color: white;">注册</span></van-button>
        </div>

    </div>
</template>
<script lang="ts" setup>
  import { ref,reactive } from 'vue'
  import {loginApi,getCodeApi} from '../api/UserApi'
  import { showToast } from 'vant';
  import { useRouter } from 'vue-router'
import { setToken } from '../request/token-utils';
 
const router = useRouter()
  
  let user = reactive({
    username: '',
    password: '',
    phone: '',
    code: ''
  })
  let flag = ref(false)

  function login() {
    if (flag.value) {
      console.log('手机号登录')
      user.username = ''
      user.password = ''
    } else {
      console.log('用户名登录')
      user.phone = ''
      user.code = ''
    }
    if (!flag.value&&(user.username === '' || user.password === '')) {
      showToast('请输入用户名和密码')
      return
    }else if (flag.value&&(user.phone === '' || user.code === '')) {
      showToast('请输入手机号和验证码')
      return
    }
    
    loginApi(user).then((res) => {
      console.log(res)
        if (res.data.code == 200) {
          setToken(res.data.data.token)
          showToast('登录成功')
          router.push({path: '/user'})
        }else {
          showToast(res.data.message)
        }
    })
    
  }
  
  function getCode() {
    if (user.phone === '') {
      showToast('请输入手机号')
      return
    }
    getCodeApi(user.phone).then((res) => {
      console.log(res)
      if (res.data.code == 200) {
        showToast('验证码已发送')
      }else {
        showToast(res.data.message)
      }
    })
  }

  

</script>

<style scoped>
.in{
    font-family: "微软雅黑";
    margin-left: 9%;
    width: 80%;
    height: 40px;
    border: 1px solid #ccc;
    border-radius: 50px;
    padding-left: 10px;
    font-size: 16px;
    outline: none;
}



</style>