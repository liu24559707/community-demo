<template>
    <div>

        <div class="fixed-header">
            <div class="Header">
                <van-icon class="icon-left" name="arrow-left" @click="$router.back()"/>
                <span class="title">委托栏</span>
                <van-icon class="icon" name="contact-o" @click="$router.push({path:'/user'})"/>
            </div>
            <div style="background-color: white; border-bottom: 1px solid #ccc">
                <div style="height: 20px;"></div>
                <div style="display: flex; justify-content: space-between; align-items: center; padding: 0 10px; width: 80%;margin-left: 4%;">
                    <div @click="changeSelected(0)"><span :style="{color:selected==0?'black':'gray'}">全部委托</span></div>
                    <div @click="changeSelected(1)"><span :style="{color:selected==1?'black':'gray'}">已接受</span></div>
                    <div @click="changeSelected(2)"><span :style="{color:selected==2?'black':'gray'}">已发布</span></div>
                </div>
            </div>
            <van-notice-bar mode="closeable" color="#1989fa" background="#ecf9ff" left-icon="info-o" >
              <span>发布委托时，请尽量详细描述，以便他人尽快接受</span>
              </van-notice-bar>
        </div>

        <div style="margin-top: 22%; margin-bottom: 23%;">
            <div v-for="item in entrustList" :key="item.entrustid" class="entrust">
                <!-- 日期和编号后3位 -->
                <div class="entrust-title">
                    <div >
                        <span class="date">{{(item.createTime+'').split('T')[0]}}</span>
                        <span class="id" style="margin-left: 180px">{{"#"+(item.entrustid+'').slice(-3)}}</span>
                    </div>
                    <span class="time">{{ new Date(item.createTime).toLocaleTimeString().split(':')[0] + ':' + new Date(item.createTime).toLocaleTimeString().split(':')[1] }}</span>
                    <span v-if="selected==0 " class="grade" style="margin-left: 10px">{{"等级: "+(item.grade==1?'普通':'紧急') }}</span>
                    <br/>
                    <span class="publisher" v-if="selected==0">{{"发布者: " +item.publisher }}</span>
                    <div v-if="selected==1">
                        <span class="publisher">{{"接受者: " +item.acceptUser }}</span>
                        <div class="progress">
                    <span class="dot" :style="{backgroundColor:item.progress== 0?'yellow':item.progress== 1?'green':'red'}"></span>
                    &nbsp;
                    <span>{{ item.progress== 2?'已超时':item.progress== 1?'已完成':'进行中' }}</span>
                  </div>
                    </div>
                    <div style="margin-top: 10px;"> {{ item.context }}</div>
                </div>
                    <VanButton v-if="selected==0 && item.grade!=0" style="margin-left: 15%;width: 60%;height: 30px;" block @click="accept(item.entrustid)">
                        接受委托</VanButton>
                    <VanButton v-if="selected==2" style="margin-left: 15%;width: 60%;height: 30px;" block @click="del(item.entrustid)">
                        撤销委托</VanButton>
                </div>
        </div>
        <div class="fixed-bottom" :style="{position: selected!=2?'fixed':'fixed'}">
            <VanButton  style="width: 90%;height: 30px;" block @click="refresh()">刷新委托</VanButton>
            <van-floating-bubble
                v-if="selected==2"
                v-model:offset="offset"
                axis="xy"
                icon="add"
                :style="{backgroundColor: 'darkblue'}"
                @offset-change="console.log(offset.x, offset.y)"
                @click="add"
                />
            </div>
        </div>

   
        
        <van-popup 
        v-model:show="show" 
        position="bottom"
        >
        <span style="color: gray;margin-left: 80px;font: small;">委托默认结束时间为2天后</span>
        <div style="margin: 20px;">
        <van-cell-group inset>
           <van-field
          v-model="context"
          rows="2"
          autosize
        label="委托内容"
         type="textarea"
        maxlength="50"
        placeholder="请输入委托内容"
        show-word-limit
       />
       </van-cell-group>
       
       <van-radio-group v-model="grade" direction="horizontal" style="margin-left: 45px;">
          <van-radio name="0" style="margin-right: 50px">紧急委托</van-radio>
        <van-radio name="1">日常委托</van-radio>
       </van-radio-group>
       
        <van-button round style="margin-left: 10%;width: 80%;background-color: darkblue;margin-top: 10%;"
        @click="addEntrust" >
            <span style="color: white;" >添加委托</span>
        </van-button>
       </div>
        </van-popup>

       
</template>

<script lang="ts" setup>
import {ref,onMounted} from 'vue'
import {getEntrustListApi,AcceptApi,RefreshApi,addEntrustApi,delApi} from '../api/Entrust'
import { showToast } from 'vant';

let selected = ref(0)
let entrustList = ref([])
const offset = ref({ x: 335, y: 425 });
let show = ref(false)
let context = ref('')
let grade = ref(0)

onMounted(()=>{
    search()
})

function add(){
    console.log('add')
    show.value = true
}

function search(){
    console.log(selected.value)
    getEntrustListApi(selected.value).then((res)=>{
        if(res.data.code == 200){
            entrustList.value = res.data.data
            console.log(entrustList.value)
        }
    })
}

function changeSelected(index:number){
    selected.value = index
    search()
}

function accept(id:number){
    console.log(id)
    //后端接受委托
    AcceptApi(id).then((res)=>{
        console.log(res)
        if(res.data.code == 200){
            showToast('接受成功')
        }else{
            showToast(res.data.message)
        }
    })
}

//后端刷新缓存
function refresh(){
    RefreshApi(selected.value).then((res)=>{
        console.log(res)
        if(res.data.code == 200){
            entrustList.value = res.data.data
            console.log(entrustList.value)
        }
    })
}

//添加委托
function addEntrust(){
    console.log('add')
    const entrust = {
        context:context.value,
        grade:grade.value,
    }
    addEntrustApi(entrust).then((res)=>{
        if(res.data.code == 200){
            showToast('添加成功')
            show.value = false
            search()
        }else{
            showToast(res.data.message)
        }
    })
}

//撤销委托
function del(id:number){
    console.log(id)
    delApi(id).then((res)=>{
        if(res.data.code == 200){
            showToast('撤销成功')
            search()
        }else{
            showToast(res.data.message)
        }
    })
}



</script>


<style scoped>
.publisher{
    color: gray;
    font: smaller;
}

.time{
    color: gray;
    height: 3px;
    width: 10px;
}

.id{
    border: black 2px solid;
}

.entrust {
  background-color: white;
  margin: 10px;
  padding: 10px;
  border-radius: 10px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: flex-start;
}

.entrust-title {
    margin-left: 15px;
}

.fixed-bottom {
  height: 45px;
  background-color: rgb(255, 253, 253);
  bottom: 50px;
 
  width: 100%;
  align-items: center ;
  display: flex;
  justify-content: center;
  z-index: 1000;
  border-bottom: 1px solid #ccc; /* 添加顶部边框 */
}
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
  align-content: center;
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

.dot {
            height: 7px;
            width: 7px;
            background-color: yellow;
            border-radius: 50%;
            display: inline-block;
        }



</style>