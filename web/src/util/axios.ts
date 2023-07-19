import axios from "axios"
import store from "@/store";
import {Tool} from "@/util/tool";

const runAxiosConfig = () => {
    axios.defaults.baseURL = process.env.VUE_APP_SERVER
    /**
     * axios拦截器
     */
    axios.interceptors.request.use(function (config) {
        console.log('请求参数：', config)
        const token = store.state.user.token
        if (Tool.isNotEmpty(token)) {
            config.headers.token = token
            console.log('请求headers增加token', token)
        }
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

