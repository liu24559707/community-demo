<template>
    <div class="fixed-header">
        <div class="Header">
            <van-icon class="icon-left" name="arrow-left" @click="$router.back()"/>
            <span style="color: white; font-size: 18px; margin: 10px">物业投诉</span>
            <van-icon class="icon-right" name="ellipsis" />
        </div>
    </div>
    
    <div style="margin-top: 60px; margin-bottom: 50px">
        <van-cell-group v-for="item in complain" :key="item.complainid">
            <div class="news-item">
              <div style="text-align: center;">
                <img :src="item.complainimg" alt="投诉照片" style="width: 60%; height: 100px; margin: 10px;">
              </div>
              <div style="font-weight: bold;">投诉内容： {{ item.context }}</div>
              <div style="font-weight: bold;">投诉时间：
               {{  (item.createTime+'').split('T')[0]+":"+new Date(item.createTime).toLocaleTimeString().split(':')[0] + ':' + new Date(item.createTime).toLocaleTimeString().split(':')[1] }}
              </div>
              <div style="font-weight: bold;">投诉状态：{{ item.progress== 0 ? '待受理' : '已受理'}}</div>
            </div>
        </van-cell-group>
    </div>

    <div>
        <van-floating-bubble
                v-model:offset="offset"
                axis="xy"
                icon="add"
                :style="{backgroundColor: 'darkblue'}"
                @offset-change="console.log(offset.x, offset.y)"
                @click="router.push('/complainAdd')"
                />
    </div>
</template>

<script lang="ts" setup>
import { ref,onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { showToast } from 'vant';
import { findAll } from '../api/ComplainApi'

const router = useRouter()
const route = useRoute()


const offset = ref({ x: 335, y: 425 });
let complain = ref([])

onMounted(()=>{
    getComplain()
})

//获取个人的投诉记录
function getComplain(){
    findAll().then((res)=>{
        if(res.data.code == 200){
            complain.value = res.data.data
            console.log(complain.value)
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

.news-item {  
  margin: 10px;
  padding: 10px;
  border: 1px solid #ffffff;
  border-radius: 10px;
  background-color: rgba(255, 255, 255, 0.49);
  font-size: 16px;
  height: 25%;
  color: #333;
  line-height: 1.5;
  text-align: left;
}
</style>