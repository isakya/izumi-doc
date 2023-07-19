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
          <a-menu-item key="ebook">
            <router-link to="/admin/ebook">电子书管理</router-link>
          </a-menu-item>
          <a-menu-item key="category">
            <router-link to="/admin/category">分类管理</router-link>
          </a-menu-item>
          <a-menu-item key="user">
            <router-link to="/admin/user">用户管理</router-link>
          </a-menu-item>
          <a-menu-item key="about">
            <router-link to="/about">关于我们</router-link>
          </a-menu-item>
        </a-menu>
      </a-col>
      <a-col :span="4">
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
// import store form '@/store'
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
const user = ref({})
// 登录
const login = () => {
  console.log('开始登录')
  loginModalLoading.value = true
  loginUser.value.password = hexMd5(loginUser.value.password + KEY)
  axios.post("/user/login", loginUser.value).then((res) => {
    loginModalLoading.value = false
    const data = res.data
    if (data.success) {
      loginModalVisible.value = false
      message.success("登录成功!")
      user.value = data.content
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
