import axios from 'axios'
import qs from 'qs'
// 配置项
const axiosOption = {
  baseURL: '/api',
  timeout: 5000
}

// 创建一个单例
const instance = axios.create(axiosOption);

// 添加请求拦截器
instance.interceptors.request.use(function (config) {
  let accessId = localStorage.getItem('accessId')
  let telephone = localStorage.getItem('telephone')
  if (accessId) {
    config.headers = {
      'accessid': accessId,
      'telephone': telephone,
      'content-type': 'application/x-www-form-urlencoded'
    }
  } else {
    config.headers = {
      'content-type': 'application/x-www-form-urlencoded'
    }
  }
  config.data = qs.stringify(config.data)
  // console.log(config)
  return config;
}, function (error) {
  // 对请求错误做些什么
  return Promise.reject(error);
});

// 添加响应拦截器
instance.interceptors.response.use(function (response) {
  // console.log(response.data.data != null)
  if(response.data.data != null){
    if (response.data.data.errCode === 20003) {
      setTimeout(() => {
        window.location.href = '/login'
      }, 2000)
    }
  }
  // 对响应数据做点什么
  return response.data;
}, function (error) {
  // 对响应错误做点什么
  return Promise.reject(error);
});

export default instance;

