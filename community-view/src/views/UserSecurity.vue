<template>
    <div class="fixed-header">
        <div class="Header">
            <van-icon class="icon-left" name="arrow-left" @click="$router.back()"/>
            <span style="color: white; font-size: 18px; margin: 10px">账号与安全</span>
            <van-icon class="icon-right" name="ellipsis" />
        </div>
    </div>

    <dictionary >
        <van-cell title="修改密码" is-link  style="margin-top: 50px;" @click="choose = 1,showBottom = true"/>
        <van-cell title="绑定手机号" is-link  @click="choose = 2,showBottom = true" />
        <van-cell title="换绑手机号" is-link  @click="choose = 3,showBottom = true" />
        <van-cell title="注销账号" is-link  @click="choose = 4,showBottom = true" />
    </dictionary>
    <!-- 修改密码 -->
    <div v-if="choose == 1">
        <van-popup
        v-model:show="showBottom"
        position="bottom"
        :style="{ height: '50%' }"
        @close="updatePassWord = {},confirmPassWord = ''"
        >
       <div style="margin: 20px;">
        <van-field  label="旧密码" placeholder="请输入旧密码" v-model:model-value="updatePassWord.oldPassword"/>
        <van-field  label="新密码" placeholder="请输入新密码" v-model:model-value="updatePassWord.newPassword"/>
        <van-field  label="确认密码" placeholder="请再次输入新密码" v-model:model-value="confirmPassWord"/>
        <van-button round style="margin-left: 10%;width: 80%;background-color: darkblue;margin-top: 10%;" 
        @click="changePassword">
            <span style="color: white;">修改密码</span></van-button>
       </div>
       </van-popup>
    </div>
    <!--绑定手机号  -->
    <div v-if="choose == 2">
        <van-popup
        v-model:show="showBottom"
        position="bottom"
        :style="{ height: '50%' }"
        @close="phone = '',confirmPhone = ''"
        >
       <div style="margin: 20px;">
        <van-field  label="手机号" placeholder="请输入手机号" v-model:model-value="phone"/>
        <van-field  label="确认手机号" placeholder="请再次输入手机号" v-model:model-value="confirmPhone"/>
        <van-button round style="margin-left: 10%;width: 80%;background-color: darkblue;margin-top: 10%;" 
        @click="bindPhone">
            <span style="color: white;">确认绑定</span></van-button>
       </div>
       </van-popup>
    </div>
    <!-- 换绑手机号 -->
    <div v-if="choose == 3">
        <van-popup
        v-model:show="showBottom"
        position="bottom"
        :style="{ height: '50%' }"
        @close="oldPhone = '',phone = '',confirmPhone = ''"
        >
       <div style="margin: 20px;">
        <van-field  label="旧手机号" placeholder="请输入旧手机号" v-model:model-value="oldPhone"/>
        <van-field  label="新手机号" placeholder="请输入新手机号" v-model:model-value="phone"/>
        <van-field  label="确认手机号" placeholder="请再次输入新手机号" v-model:model-value="confirmPhone"/>
        <van-button round style="margin-left: 10%;width: 80%;background-color: darkblue;margin-top: 10%;" 
        @click="changeBind">
            <span style="color: white;">确认换绑</span></van-button>
       </div>
       </van-popup>
    </div>
    <!-- 注销账号 -->
    <div v-if="choose == 4">
        <van-popup
        v-model:show="showBottom"
        position="bottom"
        :style="{ height: '20%' }"
        >
       <div style="margin: 20px;">
        <van-button round style="margin-left: 10%;width: 80%;background-color: red;margin-top: 10%;"
        @click="deleteUser" >
            <span style="color: white;">确定要注销该账号吗</span></van-button>
       </div>
       </van-popup>
    </div>
   
   
</template>

<script lang="ts" setup>
import { showToast } from 'vant'
import { ref } from 'vue'
import {updatePasswordApi,bindPhoneApi,changeBindApi,delUserApi}from '../api/UserApi'
import { useRouter } from 'vue-router'
import { removeToken } from '../request/token-utils'

let router = useRouter()
let choose = ref(0)
let showBottom = ref(true)
let updatePassWord = ref({})
let confirmPassWord = ref()
let phone = ref()
let oldPhone = ref()
let confirmPhone = ref()

function changePassword(){
  if(updatePassWord.value.oldPassword == updatePassWord.value.newPassword){
     showToast('新旧密码不能相同')
     return
  }
  if(updatePassWord.value.newPassword != confirmPassWord.value){
      showToast('两次输入的密码不一致')
      return
  }
  // TODO: 发送请求修改密码
  updatePasswordApi(updatePassWord.value).then(res=>{
    console.log(res)
    if(res.data.code == 200){
        showToast('修改密码成功')
        showBottom.value = false
    }else{
     showToast(res.data.message)   
    }
  })
  confirmPassWord.value = ''
  updatePassWord.value = {}
}

function bindPhone(){
    if(phone.value != confirmPhone.value){
        showToast('两次输入的手机号不一致')
        return
    }
  // TODO: 发送请求绑定手机号
  bindPhoneApi(phone.value).then(res=>{
      if(res.data.code == 200){
        showToast('绑定手机号成功')
        showBottom.value = false

      }else{
        showToast(res.data.message)
        showBottom.value = false
      }
  })
  confirmPhone.value = ''
        phone.value = ''
}

function changeBind(){
    if(oldPhone.value == phone.value){
        showToast('新旧手机号不能相同')
        return
    }
    if(phone.value != confirmPhone.value){
        showToast('两次输入的手机号不一致')
        return
    }
  // TODO: 发送请求换绑手机号
  changeBindApi({oldPhone:oldPhone.value,newPhone:phone.value}).then(res=>{
      if(res.data.code == 200){
        showToast('换绑手机号成功')
        showBottom.value = false
      }else{
        showToast(res.data.message)
      }
  })
  confirmPhone.value = ''
        phone.value = ''
}

function deleteUser(){
  // TODO: 发送请求注销账号
  delUserApi().then(res=>{
      if(res.data.code == 200){
        showToast('注销账号成功')
        showBottom.value = false
        removeToken()
        router.push('/login')
      }else{
        showToast(res.data.message)
      }
  })
}
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
