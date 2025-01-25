<template>
      <div class="fixed-header">
            <div class="Header">
                <van-icon class="icon-left" name="arrow-left" @click="$router.back()"/>
                <span class="title">消息</span>
                <!-- 修改为搜索 -->
                <van-icon class="icon" name="search" @click="$router.push({path:'/user'})"/>
            </div>
     </div>
     
     <!-- 在线用户列表 -->
     <div class="user-list">
        <van-cell v-for="user in userList" :key="user.name" :style="{padding: '0px'}"  >
        <div class="user" @click="goToChat(user.name)">
            <!--头像  -->
            <van-image round width="50" height="50" src="https://img01.yzcdn.cn/vant/cat.jpeg" />
            &nbsp;
            &nbsp;
            <!-- 姓名 -->
            <span style="font-size: 18px;  color: black;margin-top: 15px;">{{ user.name }}</span>
        </div>
        </van-cell>
        <van-divider>没有更多了</van-divider>
     </div>
    
</template>

<script lang="ts" setup>
import { getToken } from '../request/token-utils'
import {ref,onMounted} from 'vue'
import { useRouter } from 'vue-router'

const name = ref()
const router = useRouter()
let ws = new WebSocket(`ws://localhost:8002/chat/${localStorage.getItem('user')}/${name.value}`)

onMounted(()=>{
    ws.onopen = function(event) {
        console.log('WebSocket is open now.')
    }
    getMessage()
})

let userList = ref([
    
])

function goToChat(name:string){
    setTimeout(()=>{
        router.push({path:'/chatView/chatroom', query:{name:name}})
    },200)
}

function getMessage(){
    ws.onmessage = function(event) {
      console.log(event.data)
        let message=JSON.parse(event.data)
        if(message.type==3){//朋友列表
            console.log(message.message)
            JSON.parse(message.message).forEach((item:any)=>{
                userList.value.push({name:item})
            })
        }
        if(message.type==4){//推送最新在线用户
          let username = message.message
          if(!userList.value.some((item:any)=>item.name==username)){
             userList.value.unshift({name:username})
          }
        }
    }
}
</script>



<style scoped>
.title{
  font-size: 18px;
  font-weight: '微软雅黑';
  margin-left: 30%;
  margin-top: 10px ;
}
.Header{
  height: 45px;
  background-color:white;
  display: flex;
  justify-content: space-between; /* 将子元素分布在两端 */
  padding: 0 10px; /* 添加内边距 */
 
}

.fixed-header {
  position: fixed;
  top: 0;
  width: 100%;
  background-color: gray; /* 确保背景颜色为白色 */
  z-index: 1000; /* 确保这个元素在其他元素之上显示 */
}

.icon-left,
.icon-right{
  float: right;
  font-size: 24px;
  color: #3e5f55;
  margin: 10px;
}

.icon{
  margin-left: auto;
  margin-right: 10px;
  margin-top: 10px;
  font-size: 27px;
  color: #3e5f55;
}

.user-list{
  margin-top: 50px;
}

.user{
    display: flex;

  padding: 10px;
   height: 50px;
}

.user:hover{
  background-color: rgb(234, 229, 229);
}
</style>