<template>
  <a-layout style="padding: 24px 0; background: #fff; margin-top: 50px">
    <a-layout-sider width="200" style="background: #fff">
      <a-menu
          mode="inline"
          style="height: 100%"
          @click="handleClick"
      >
        <a-menu-item key="welcome">
          <HomeOutlined/>
          <span>欢迎</span>
        </a-menu-item>
        <a-sub-menu v-for="item in level1" :key="item.id">
          <template v-slot:title>
            <branches-outlined/>
            <span>
                {{ item.name }}
            </span>
          </template>
          <a-menu-item v-for="child in item.children" :key="child.id">
            <!--            <branches-outlined />-->
            <span>{{ child.name }}</span>
          </a-menu-item>
        </a-sub-menu>
      </a-menu>
    </a-layout-sider>
    <a-layout-content :style="{ padding: '0 24px', minHeight: '280px' }">
      <TheWelcome v-show="isShowWelcome"/>
      <a-list v-show="!isShowWelcome" item-layout="vertical" size="large" :data-source="ebooks"
              :grid="{gutter: 20, column: 3}">
        <template #renderItem="{ item }">
          <a-list-item key="item.name">
            <template #actions>
              <span>
                <FileOutlined style="margin-right: 8px"/>
                {{ item.docCount }}
              </span>
                <span>
                <UserOutlined style="margin-right: 8px"/>
                {{ item.viewCount }}
              </span>
                <span>
                <LikeOutlined style="margin-right: 8px"/>
                {{ item.voteCount }}
              </span>
            </template>
            <a-list-item-meta :description="item.description">
              <template #title>
                <router-link :to="'/doc?ebookId=' + item.id">
                  {{ item.name }}
                </router-link>
              </template>
              <template #avatar>
                <a-avatar :src="item.cover"/>
              </template>
            </a-list-item-meta>
          </a-list-item>
        </template>
      </a-list>
    </a-layout-content>
  </a-layout>
</template>

<script lang="ts" setup>
import axios from 'axios'
import {StarOutlined, MessageOutlined, FileOutlined, UserOutlined, LikeOutlined, BranchesOutlined, HomeOutlined} from '@ant-design/icons-vue';
import {onMounted, ref} from "vue";
import {message} from 'ant-design-vue'
import {Tool} from '@/util/tool'
import TheWelcome from '@/components/the-welcome.vue'

const level1 = ref()
let categorys: any
// 查询所有分类
const handleQueryCategory = () => {
  axios.get("/category/all").then((response) => {
    const data = response.data
    if (data.success) {
      categorys = data.content
      console.log('原始数组: ', categorys)
      level1.value = []
      level1.value = Tool.array2Tree(categorys, 0)
      console.log('树形结构: ', level1.value)
    } else {
      message.error(data.message)
    }
  })
}
let categoryId2 = 0
const isShowWelcome = ref(true)
// 菜单点击
const handleClick = (value: any) => {
  if (value.key === 'welcome') {
    isShowWelcome.value = true
  } else {
    categoryId2 = value.key
    isShowWelcome.value = false
    // 点击分类时调用
    handleQueryEbook()
  }
}
// const actions: Record<string, any>[] = [
//   {icon: StarOutlined, text: '156'},
//   {icon: LikeOutlined, text: '156'},
//   {icon: MessageOutlined, text: '2'},
// ];

const ebooks = ref()
const handleQueryEbook = () => {
  axios.get("/ebook/list", {
    params: {
      page: 1,
      size: 1000,
      categoryId2: categoryId2
    }
  }).then(res => {
    const data = res.data
    ebooks.value = data.content.list
    console.log(res)
  })
}
onMounted(() => {
  // 初始加载分类
  handleQueryCategory()
})
</script>

<style scoped>
.ant-avatar {
  width: 50px;
  height: 50px;
  line-height: 50px;
  border-radius: 8%;
  margin: 5px 0;
}
</style>
