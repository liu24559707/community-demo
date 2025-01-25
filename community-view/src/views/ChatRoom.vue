<template>
     <div class="fixed-header">
            <div class="Header">
                <van-icon class="icon-left" name="arrow-left" @click="$router.back()"/>
                <span class="title">{{ name }}</span>
                <!-- 修改为搜索 -->
                <van-icon class="icon" name="ellipsis" @click="$router.push({path:'/user'})"/>
            </div>
     </div>
     
     <!-- 聊天记录 -->
     <div style="margin-top: 45px;margin-bottom: 40px;">
      <van-pull-refresh
  v-model="isLoading"
  success-text="刷新成功"
  @refresh="onRefresh"
  :style="{ height: chat.length<=3?'100vh':'100%'}"
>
<div class="chat-container">

  
  <!-- 遍历并判断消息位置左右 -->
  <div class="chat" v-for="(item,index) in chat" :key="index" >
    
    <!-- 判断左右 -->
      <!-- 他人的消息 -->
      <div v-if="item.from==name">
        <div class="chat-left-content">
          <!--头像和时间和姓名  -->
          <van-image round width="45" height="45" src="https://img01.yzcdn.cn/vant/cat.jpeg" />
          <span class="name">{{ name }}</span>&nbsp;&nbsp;
          <span class="name">{{ item.dateTime }}</span>           
        </div>
        
        <!-- 消息 -->
        <div class="chat-left-message">
          <p class="message" >{{ item.message }}</p>
        </div>
      </div>
      
      <!-- 自己的消息 -->
      <div v-else>
        <div class="chat-right-content">
          <span class="name">{{ item.from }}</span>&nbsp;&nbsp;
          <span class="name">{{ item.dateTime }}</span>
          <van-image round width="45" height="45" src="https://img01.yzcdn.cn/vant/cat.jpeg" />          
        </div>
        
        <!-- 消息 -->
        <div class="chat-right-message">
          <p class="message">{{ item.message }}</p>
        </div>
      </div>
    </div>
    <div id="bottom" style="margin-top: 20px;"></div>
  </div>
  </van-pull-refresh>
</div>

     <!-- 底部输入框 -->
     <div class="fixed-bottom">
        <input class="input" type="text" placeholder="请输入内容" v-model="context" @click="scrollToBottom"/>
          <van-button class="send" round type="primary" size="small" @click="send">
           <van-icon name="share" />
          </van-button>
     </div>
</template>

<script lang="ts" setup>
import { ref,onMounted, reactive } from 'vue'
import { useRoute } from 'vue-router'
import {format} from 'date-fns'

const route = useRoute()
const name = route.query.name
let context = ref('')
let chat = ref([])
let ws = new WebSocket(`ws://localhost:8002/chat/${localStorage.getItem('user')}/${name}`)


onMounted(() => {
  ws.onopen = function () {
   console.log("连接成功") 
  }
  console.log( name)
  receive()
})

const isLoading = ref(false)
const onRefresh = () => {
  setTimeout(() => {
    isLoading.value = false
  }, 1000)
  console.log('刷新')
}

function send(){
  console.log('发送')
  if(context.value == ''){
    return
  }
  let message = {
    from: localStorage.getItem('user'),
    to: name,
    message: context.value,
    dateTime: format(new Date(), 'yyyy-MM-dd HH:mm:ss'),
    type: 2
  }
  ws.send(JSON.stringify(message))
  chat.value.push(message)
  context.value = ''
  setTimeout(() => {
    scrollToBottom()
  },  100)
}

function receive(){
  ws.onmessage = function (event) {
    console.log(event.data)
    let message = JSON.parse(event.data)
    console.log(message.constructor)
    if(message.constructor == Array){
      message.forEach((item:any) => {
        console.log( JSON.parse(item))
        chat.value.push(JSON.parse(item))
      })
    }else{
      if (message.type == 2) {
        chat.value.push(message)
      }
    }
  }
}

function scrollToBottom() {
  console.log('滚动到底部');
    const bottomElement = document.getElementById('bottom');
    if (bottomElement) {
        bottomElement.scrollIntoView({ behavior: "smooth", block: "end" });
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

.fixed-bottom{
  display: flex;
  position: fixed;
  bottom: 0;
  width: 100%;
  background-color: white; /* 确保背景颜色为白色 */
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

.input{
  font-family: "微软雅黑";
  margin-left: 5px;
    width: 80%;
    height: 30px;
    border: 1px solid #ccc;
    border-radius: 50px;
    padding-left: 10px;
    font-size: 16px;
    outline: none;
}

.send{
  padding: 10px;
  height: 30px;
  width: 50px;
  font-size: 16px;
  margin-left: 7px;
}

.chat-container{
  margin-top: 10px;
}

.chat-left-content {
  display: flex;
  justify-content: flex-start;
  text-align: left;
  margin-top: 10px;
  margin-bottom: 10px;
}

.chat-right-content {
  display: flex;
  justify-content: flex-end;
  text-align: right;
  margin-top: 10px;
  margin-bottom: 10px;
}

.name{
  font-size: 14px;
  color: gray;
}

.message{
  font-size: 16px;
  color: black;
  padding: 10px;
  border-radius: 10px;
  background-color: #e0e0e0;
}

.chat-left-message {
  display: flex;
  justify-content: flex-start;
  margin-right: 10%;
  margin-left: 10%;
}

.chat-right-message {
  display: flex;
  justify-content: flex-end;
  margin-right: 10%;
  margin-left: 10%;
}


</style>