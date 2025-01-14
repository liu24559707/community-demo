<template>
    <div class="fixed-header">
        <div class="Header">
            <van-icon class="icon-left" name="arrow-left" @click="$router.back()"/>
            <span style="color: white; font-size: 18px; margin: 10px">增加物业投诉</span>
            <van-icon class="icon-right" name="ellipsis" />
        </div>
    </div>
    <!-- 保修图片，保修详情 -->
    <div style="margin-top: 50px;">
        <van-form @submit="onSubmit">
            <van-field v-model="message" rows="5" autosize type="textarea" maxlength="200" placeholder="请输入投诉详情" show-word-limit />
            <van-field name="uploader" label="上传图片"> 
                <template #input>
                    <van-uploader v-model="fileList" :after-read="afterRead" multiple :max-count="1" />
                </template>
            </van-field>
            <div style="margin: 16px;">
                <van-button round block type="success" native-type="submit">提交</van-button>
            </div>
        </van-form>
    </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import { showToast } from 'vant';
import { useRouter } from 'vue-router';
import {add}from '../api/ComplainApi'

const router = useRouter();
const message = ref('');
const fileList = ref([]);
const complain=ref({});

const afterRead = (file) => {
    // 此时可以自行将文件上传至服务器
    console.log(file);
};

const onSubmit = () => {
    if (message.value.trim() === '') {
        showToast('请输入投诉详情');
        return;
    }
    if (fileList.value.length === 0) {
        showToast('请上传图片');
        return;
    }
    complain.value.context = message.value;
    complain.value.complainimg = fileList.value[0].content;
    
    add(complain.value).then((res) => {
        console.log(res);
    })

    showToast('提交成功');
    // 提交表单
    router.back();
};
</script>


<script lang="ts" setup>

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