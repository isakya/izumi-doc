<template>
  <a-layout style="padding: 24px 0; background: #fff; margin-top: 50px">
    <a-layout-content :style="{background: '#fff', padding: '24px', margin: 0, minHeight: '280px'}">
      <a-row>
        <a-col :span="6">
          <a-tree
              v-if="level1.length > 0"
              :tree-data="level1"
              @select="onSelect"
              :replaceFields="{title: 'name', key: 'id', value: 'id'}"
              :defaultExpandAll="true"
          ></a-tree>
        </a-col>
        <a-col :span="18">
          <div :innerHTML="html"></div>
        </a-col>
      </a-row>
    </a-layout-content>
  </a-layout>
</template>

<script lang="ts" setup>
import { onMounted, ref } from 'vue';
import axios from "axios";
import {message} from "ant-design-vue";
import {Tool} from "@/util/tool";
import {useRoute} from "vue-router";

const route = useRoute()
const docs = ref()
const level1 = ref()
level1.value = []
const html = ref()


// 数据查询
const handleQuery = () => {
  axios.get("/doc/all/" + route.query.ebookId).then((res) => {
    const data = res.data
    if (data.success) {
      docs.value = data.content
      level1.value = []
      level1.value = Tool.array2Tree(docs.value, 0)
    } else {
      message.error(data.message)
    }
  })
}

// 内容查询
const handleQueryContent = (id: number) => {
  axios.get("/doc/find-content/" + id).then((response) => {
    const data = response.data
    if (data.success) {
      // 给富文本框赋值
      html.value = data.content
    } else {
      message.error(data.message)
    }
  })
}

const onSelect = (selectedKeys: any, info: any) => {
  console.log(selectedKeys, info)
  if (Tool.isNotEmpty(selectedKeys)) {
    // 加载内容
    handleQueryContent(selectedKeys[0])
  }
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
</style>
