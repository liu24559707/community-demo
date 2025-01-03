<template>
    <div style="margin-bottom: 60px;">
        <div class="fixed-header">
            <div class="Header">
                <van-icon class="icon-left" name="arrow-left" @click="$router.back()"/>
                <span @click="selected=0" :style="{color:selected==0?'#6495ED':'gray',
                marginLeft:'19%',marginTop:'12px'}">
                    社区通知</span>
                <div v-if="selected==0" class="line"></div>
    
                <span @click="selected=1" :style="{color:selected==1?'#6495ED':'gray',
                    marginLeft:'30px',marginTop:'12px'
                }">我的缴费</span>
                <div v-if="selected==1" class="line" style="margin-left: 34%;">
                </div>
            
                <van-icon class="icon" name="contact-o" @click="$router.push({path:'/user'})"/>
            </div>
        </div>

          <div style="margin-top: 47px;">
            <div v-if="selected==0" style="">
              <div class="news-item" v-for="item in notice" :key="item" style="margin-top: 10px;">
                <div class="demoId"><span>{{ item.context }}</span></div>
                <!-- 发布人 -->
                <div class="prop"><span>{{ '发布人: &nbsp;&nbsp;'+ "xx社区"}}</span></div>
                <div class="time"><span>{{ '发布时间: &nbsp;&nbsp;'+item.createTime }}</span></div>
              </div>
            </div>
            <div v-if="selected==1" style="">
              <van-notice-bar mode="closeable" color="#1989fa" background="#ecf9ff" left-icon="info-o" >
                请业主及时缴纳物业费、水费、电费等费用
              </van-notice-bar>
                <div class="news-item" v-for="item in payment" :key="item.id" style="margin-top: 10px;">
                  <!-- 账单编号 -->
                    <div class="demoId"><span>{{ item.payrid }}</span></div>
                  <!-- 缴费账单日期  -->
                      <div class="time"><span>{{ item.createTime }}</span></div>
                  <!-- 缴费项目 -->
                    <div class="demo"> <span>{{ item.demoname }}</span></div>
                  <!--缴费金额-->
                      <div class="amount">&yen; <span>{{ item.amount.toFixed(2) }}</span></div>
                  <!-- 缴费房产 -->
                    <div class="prop"><span>{{'房产: &nbsp;&nbsp;'+ item.propname }}</span></div>
                  <!-- 缴费状态 -->
                   <div class="progress">
                    <span class="dot" :style="{backgroundColor:item.progress== 0?'red':'green'}"></span>
                    &nbsp;
                    <span>{{ item.progress== 0?'未缴费':'已缴费' }}</span>
                  </div>
                </div>
            </div>
          </div>  
    </div>
    
</template>

<script lang="ts" setup>
import { ref,onMounted } from 'vue';
import {queryAllPaymentApi,queryNoticeApi} from '../api/PaymentApi'

let payment = ref([])
let selected = ref(0)
let notice = ref([])

onMounted(() => {
  checkNotice()
  checkPayment()
})


function checkPayment(){
  queryAllPaymentApi().then((res)=>{
    if(res.data.code == 200){
      payment.value = res.data.data
      console.log(payment.value)
    }
  })
}

function checkNotice(){
  queryNoticeApi().then((res)=>{
    console.log("查询通知")
    console.log(res)
    if(res.data.code == 200){
      notice.value = res.data.data
      console.log(notice.value)
    }
  })
}

</script>

<style scoped>

.hander{
    width: 100%;
  font-size: 16px;
  font-weight: bold;
  color: #3e5f55;
  text-align: center;
  background-color: #F0F5F5;
}



.Header{
  height: 45px;
  background-color:white;
  display: flex;
  align-content: center;
  justify-content: space-between; /* 将子元素分布在两端 */
  padding: 0 10px; /* 添加内边距 */
  border-bottom: 1px solid #ccc;
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

.icon:hover{
  color: #C2D8F0;
}


.line{
    margin-top: 5px;
    margin-left: 11%;
    width: 67px;
    height: 2px;
    background-color: #6495ED;
    position: absolute;
    top: 40px;
    left: 20%;
}

.news-item {  
  margin: 10px;
  padding: 10px;
  border: 1px solid #ffffff;
  border-radius: 10px;
  background-color: white;
  font-size: 16px;
  height: 25%;
  color: #333;
  line-height: 1.5;
  text-align: left;
}

.time{
  color: gray;
  font-size: smaller;
}

.demo{
  margin-top: 15px;
  margin-bottom:1px ;
  text-align: center;
  font-size: smaller;
}

.amount{
  text-align: center;
  font-size: larger;
}

.prop{
  font-size: smaller;
  color: gray;
}

.progress{
  font-size: smaller;
  color: gray;
}

.demoId{
  font-size: large;

}

.dot {
            height: 7px;
            width: 7px;
            background-color: red;
            border-radius: 50%;
            display: inline-block;
        }
</style>