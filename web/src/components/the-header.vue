<template>
  <a-layout-header class="header">
    <div class="logo"/>
    <a-row>
      <a-col :span="20">
        <a-menu
            theme="dark"
            mode="horizontal"
            :style="{ lineHeight: '64px' }"
        >
          <a-menu-item key="/">
            <router-link to="/">首页</router-link>
          </a-menu-item>
          <a-menu-item key="ebook" :style="user?.id ? null : {display: 'none'}">
            <router-link to="/admin/ebook">电子书管理</router-link>
          </a-menu-item>
          <a-menu-item key="category" :style="user?.id ? null : {display: 'none'}">
            <router-link to="/admin/category">分类管理</router-link>
          </a-menu-item>
          <a-menu-item key="user" :style="user?.id ? null : {display: 'none'}">
            <router-link to="/admin/user">用户管理</router-link>
          </a-menu-item>
          <a-menu-item key="about">
            <router-link to="/about">关于我们</router-link>
          </a-menu-item>
        </a-menu>
      </a-col>
      <a-col :span="4">
        <a-popconfirm
            title="确认退出吗?"
            ok-text="是"
            cancel-text="否"
            @confirm="logout">
          <a class="login-menu" v-show="!!user.id">
            <span>退出登录</span>
          </a>
        </a-popconfirm>
        <div class="login-menu" v-show="!!user.id">
          <span>您好: {{user.name}}</span>
        </div>
        <div class="login-menu">
          <span @click="showLoginModal" v-show="!user.id">登录</span>
        </div>
      </a-col>
    </a-row>
  </a-layout-header>
  <a-modal
      title="登录"
      v-model:open="loginModalVisible"
      :confirm-loading="loginModalLoading"
      @ok="login">
    <a-form :model="loginUser" :label-col="{span: 6}" :wrapper-col="{span: 18}">
      <a-form-item label="登录名">
        <a-input v-model:value="loginUser.loginName"></a-input>
      </a-form-item>
      <a-form-item label="密码">
        <a-input v-model:value="loginUser.password" type="password"></a-input>
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<script lang="ts" setup>
import {ref, computed} from 'vue'
import axios from "axios";
import {message} from "ant-design-vue";
import store from '@/store'
declare let hexMd5: any
declare let KEY: any

// 用来登录
const loginUser = ref({
  loginName: 'test',
  password: 'test'
})
const loginModalVisible = ref(false)
const loginModalLoading = ref(false)
// 显示登录模态框
const showLoginModal = () => {
  loginModalVisible.value = true
}

const user = computed(() => store.state.user)

// 登录
const login = () => {
  console.log('开始登录')
  loginModalLoading.value = true
  // 前端校验
  if (loginUser.value.password.length < 6 || loginUser.value.password.length > 32) {
    message.error('【密码】6~32位')
  }
  loginUser.value.password = hexMd5(loginUser.value.password + KEY)
  axios.post("/user/login", loginUser.value).then((res) => {
    loginModalLoading.value = false
    const data = res.data
    if (data.success) {
      loginModalVisible.value = false
      message.success("登录成功!")
      // user.value = data.content
      // 触发vuex的方法
      store.commit("setUser", data.content)
    } else {
      message.error(data.message)
    }
  })
}

// 退出登录
const logout = () => {
  console.log('退出登录开始')
  axios.get("/user/logout/" + user?.value?.token).then((res) => {
    const data = res.data
    if (data.success) {
      message.success("退出成功")
      store.commit("setUser", {})
    } else {
      message.error(data.message)
    }
  })
}

</script>

<style scoped>
.login-menu:hover {
  color: white;
}

.login-menu {
  float: right;
  margin-left: 20px;
  color: rgba(255, 255, 255, 0.65);
  cursor: pointer;
  transition: color 0.3s;
}
</style>
