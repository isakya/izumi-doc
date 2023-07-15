import axios from "axios"

const runAxiosConfig = () => {
    axios.defaults.baseURL = process.env.VUE_APP_SERVER
    /**
     * axios拦截器
     */
    axios.interceptors.request.use(function (config) {
        console.log('请求参数：', config)
        return config;
    }, error => {
        return Promise.reject(error);
    })
    axios.interceptors.response.use(function (response) {
        console.log('返回结果：', response)
        return response;
    }, error => {
        console.log('返回结果：', error)
        return Promise.reject(error)
    })
}

export default runAxiosConfig

