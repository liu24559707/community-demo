<template>
  <div style="height: 100vh;background-color: white;">
    <div class="fixed-header">
        <div class="Header">
            <van-icon class="icon-left" name="arrow-left" @click="$router.back()"/>
            <van-icon class="icon-right" name="share-o" />
        </div>
    </div>
    <div class="title">
        <Span style="font-size: 20px;font-family: '黑体';">{{news.title}}</Span>
        <div style="font-size: 14px;font-family: '黑体';color: #999;background-color: white;">
            {{news.createTime}}
            <van-icon name="eye-o" style="margin-left: 10px;background-color: white;"/>
            {{ news.clickCount }}
        </div>
    </div>
    <div class="content">
        <div style="background-color: white; height: 5px;"></div>
        <div>
            <span style="font-size: 16px;font-family: '宋体';">{{news.content}}</span>
        </div>
    </div>

  </div>
</template>

<script lang="ts" setup>
import { useRoute } from 'vue-router'
import { ref, reactive, onMounted } from 'vue'
import { getNewsDetail } from "../api/NewsApi"


const route = useRoute()
const newsid = ref(route.query.nid)
let news = ref({})

onMounted(() => {
  getNewsDetailApi()
 console.log(newsid.value)
})

function getNewsDetailApi(){
    getNewsDetail(Number(newsid.value)).then(res=>{
        if(res.data.code == 200){
            news.value = res.data.data
            console.log(news)
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
.title{
  height: 50px;
  background-color: #fff;
  margin-top: 55px;
}
.content{
  height: 100%;
  background-color: #fff;
  margin-top: 10px ;
}
</style>