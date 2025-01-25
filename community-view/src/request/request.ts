import axios from 'axios'
import { getToken, removeToken } from './token-utils';
import { showToast } from 'vant';

// 创建 Axios 实例 设置一些基础属性
const request = axios.create({
  baseURL:'http://localhost:8002/',
  timeout: 10000,
  
})

// 添加请求拦截器
request.interceptors.request.use(
  config => {
    const token = getToken();
    if (token) {
      config.headers['token'] =  token;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 添加响应拦截器
request.interceptors.response.use(
  response => {
    if (response.data.code === 510) {
      showToast('登录超时');
      removeToken();
      window.location.href = '/login';
    }
    if (response.data.code === 555) {
      showToast('账号在其他地方登录');
      removeToken();
      window.location.href = '/login';
    }
   return response;
  },
  error => {
    if (error.response.status === 404) {
        //到404页面
    }else{
        // 其他情况 返回一个promise    
        return Promise.reject(error)
    }
  }
)
//对外暴漏axios实例。
export default request