import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import Antd from 'ant-design-vue';
import 'ant-design-vue/dist/reset.css';
import runAxiosConfig from '@/util/axios'

createApp(App).use(store).use(router).use(Antd).mount('#app')
runAxiosConfig()
console.log('环境：', process.env.NODE_ENV)
console.log('服务端：', process.env.VUE_APP_SERVER)
