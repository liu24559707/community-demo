<template>
<div class="home" style="margin-bottom: 60px">
    <div class="fixed-header">
    <div class="header">
      <span class="font">i社区</span>
      
      <van-icon class="icon" name="contact-o" @click="$router.push({path:'/user'})"/>
    </div>
    <div class="search">
        <input type="text" placeholder="搜索..." class="search-input" v-model="search" @blur="searchNews">
        &nbsp;&nbsp;
        <van-icon name="search" @click="searchNews" />
    </div>
    <!-- 选择新闻类型,从数据库中查看新闻类型，并罗列出来 -->
    <div style="">
        &nbsp;&nbsp;&nbsp;
        <span @click="selectByType(0)" :style="{ color: chooseTypeId === 0 ? 'black' : 'gray', fontSize: 'small' }">全部</span>
       <span v-for="item in newsTypes" :key="item.typeid" style="font-size: small;"
             @click="selectByType(item.typeid)"
             :style="{ color: chooseTypeId === item.typeid ? 'black' : 'gray', fontSize: 'small' }"
       >
         &nbsp;&nbsp;&nbsp;{{item.typename}}
        </span>
    </div>
    </div>
    <!-- 新闻内容 -->
    <div style="margin-top: 26%"></div>
    <div  class="news-item" v-for="item in newsList" :key="item.nid">
        <div class="news-type"><span style="font-size: medium;">{{ getType(item.newsType) }}</span></div>
        <div class="news-title" @click="$router.push({path:'/newsdetail',query:{nid:item.nid}})">{{item.title}}</div>
        <div class="news-content">点击量：{{item.clickCount}}</div>
        <div class="news-time">创建时间 {{item.createTime}}</div>
      
    </div>

</div>

   
</template>

<script lang="ts" setup>
import { ref,onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {  getNewsTypes,getNewsList } from '../api/NewsApi'

//新闻类型列表
const newsTypes = ref([]);
//搜索新闻类型id
const chooseTypeId = ref(0);
//搜索条件
let search = ref("");
//新闻
let newsList = ref([]);


// 初始化界面
onMounted(() => {
    getNewsType();
    searchNews();
})

// 获取新闻类型
function getNewsType() {
    console.log("新闻类型")
    getNewsTypes().then((res) => {
        if (res.data.code == 200) {
            newsTypes.value = res.data.data;
            console.log(newsTypes.value);
        }else{
            alert("获取新闻类型失败");
        }
    })
}

//根据新闻类型查询新闻
function selectByType(typeid: number) {
    //改变点击样式
    chooseTypeId.value = typeid;
    //查看新闻
    searchNews();
}

//带条件分页查询新闻，条件为selcetTypeId，search
function searchNews() {
    console.log("搜索新闻"+search.value);
    getNewsList(chooseTypeId.value+"",search.value,1,5).then((res) => {
     if (res.data.code == 200) {
            newsList.value = res.data.data.records;
            console.log(newsList.value);
     }
    })

}

function getType(typeid: number){
   return newsTypes.value.find(item => item.typeid == typeid)?.typename || "未知类型";
}
</script>

<style scoped>
.header {
  height: 42px;
  background-color:#84A0C8;
  display: flex;
}

.font {
  margin-left: 10px ;
  margin-top: 5px;
  font-size: 20px;
  font-weight: bold;
  color: white;
}

.search-input {
    width: 85%;
    border-radius: 50px; /* 圆角半径设置为高度的一半，形成椭圆形 */
    border: 2px solid #A9A9A9;
}
.search{
    display: flex;
    justify-content: center;
    align-items: center;
    height: 25px;
}

.news-item {  
  margin: 10px;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  background-color: #f9f9f9;
  font-size: 16px;
  height: 25%;
  color: #333;
  line-height: 1.5;
  text-align: left;
}
.news-type {
  font-size: 14px;
  color: #999;
}

.news-title {
  font-size: 18px;
  color: #333;
  cursor: pointer;
}

.news-content {
  font-size: 14px;
  color: #666;
}

.news-time {
  font-size: 12px;
  color: #999;
}

/* 固定头部位置 */
.fixed-header {
  position: fixed;
  top: 0;
  width: 100%;
  background-color: white; /* 确保背景颜色为白色 */
  z-index: 1000; /* 确保这个元素在其他元素之上显示 */
}

.icon{
  margin-left: auto;
  margin-right: 10px;
  margin-top: 10px;
  font-size: 27px;
  color: white;
}

.icon:hover{
  color: #C2D8F0;
}

.type{
  margin-left: 10px;
  margin-top: 5px;
  font-size: 20px;
  font-weight: bold;
  color: white;
}

.type:hover{
  color: #C2D8F0;
}

.search{
  margin-left: 10px;
  margin-top: 5px;
  font-size: 15px;
}
</style>
