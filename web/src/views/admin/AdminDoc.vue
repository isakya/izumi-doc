<template>
  <a-layout style="padding: 24px 0; background: #fff; margin-top: 50px">
    <a-layout-content :style="{ padding: '0 24px', minHeight: '280px' }">
      <a-row :gutter="24">
        <a-col :span="8">
          <p>
            <a-form layout="inline">
              <a-form-item>
                <a-button type="primary" @click="handleQuery()">
                  刷新
                </a-button>
              </a-form-item>
              <a-form-item>
                <a-button type="primary" @click="add">
                  新增
                </a-button>
              </a-form-item>
            </a-form>
          </p>
          <a-table
              v-if="level1?.length > 0"
              :columns="columns"
              :data-source="level1"
              :row-key="record => record.id"
              :loading="loading"
              size="small"
              :pagination="false"
              :defaultExpandAllRows="true"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'name'">
                {{record.sort}} {{record.name}}
              </template>
              <template v-if="column.key === 'cover'">
                <img :src="record.cover" alt="avatar"/>
              </template>
              <template v-if="column.key === 'action'">
                <a-space size="small">
                  <a-button type="primary" @click="edit(record)" size="small">
                    编辑
                  </a-button>
                  <a-popconfirm
                      title="确认要删除吗?"
                      ok-text="确认"
                      cancel-text="取消"
                      @confirm="del(record.id)"
                  >
                    <a-button type="danger" size="small">
                      删除
                    </a-button>
                  </a-popconfirm>
                </a-space>
              </template>
            </template>
          </a-table>
        </a-col>
        <a-col :span="16">
          <p>
            <a-button type="primary" @click="handleSave">
              保存
            </a-button>
          </p>
          <a-form :modal="doc" :label-col="{span: 3}" laout="vertical">
            <a-form-item>
              <a-input v-model:value="doc.name" placeholder="请填写名称"/>
            </a-form-item>
            <a-form-item>
              <a-tree-select
                  v-model:value="doc.parent"
                  style="width: 100%"
                  :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
                  placeholder="请选择父文档"
                  tree-default-expand-all
                  :tree-data="treeSelectData"
                  :fieldNames="{label: 'name', key: 'id', value: 'id'}"
              >
              </a-tree-select>
            </a-form-item>
            <a-form-item>
              <a-input v-model:value="doc.sort" placeholder="请填写排序"/>
            </a-form-item>
            <a-form-item>
              <a-button type="primary" @click="handlePreviewContent">
                <EyeOutlined /> 内容预览
              </a-button>
            </a-form-item>
            <a-form-item>
              <div id="content"></div>
            </a-form-item>
          </a-form>
        </a-col>
      </a-row>
    </a-layout-content>
    <!-- 抽屉 -->
    <a-drawer width="900" placement="right" :closeable="false" :visible="drawerVisible" @close="onDrawerClose">
      <div class="wangeditor" :innerHTML="previewHtml"></div>
    </a-drawer>
  </a-layout>
</template>
<script lang="ts" setup>
import {onMounted, ref, createVNode} from 'vue'
import axios from 'axios'
import {message, Modal} from 'ant-design-vue'
import {Tool} from "@/util/tool";
import {useRoute} from "vue-router";
import E from 'wangeditor'
import { EyeOutlined, ExclamationCircleOutlined } from '@ant-design/icons-vue';

const route = useRoute()
const docs = ref()
const editor = new E('#content')
editor.config.zIndex = 0

// 获取所有要删除的文档名称
const deleteNames: Array<string> = [];

// 一级文档
const level1 = ref()
const loading = ref(false)

// 表单
const doc = ref({})


const edit = (record: any) => {
  // 清空富文本框
  editor.txt.html('')
  doc.value = Tool.copy(record)
  handleQueryContent()
  // 不能选择当前节点及其所有子孙节点
  treeSelectData.value = Tool.copy(level1.value)
  setDisable(treeSelectData.value, record.id)
  // 为选择树添加一个 "无"字
  treeSelectData.value.unshift({id: 0, name: '无'})
};

// 新增
const add = () => {
  // 清空富文本框
  editor.txt.html('')
  // 清空
  doc.value = {
    ebookId: route.query.ebookId
  }
}

// 树选择组件会随着当前编辑的节点而变化，所以单独声明一个响应式的变量
const treeSelectData = ref()
treeSelectData.value = []

// 将某节点及其子子孙孙节点全部置为disabled
const setDisable = (treeSelectData: any, id: any) => {
  console.log(treeSelectData, id)
  // 遍历数组，即遍历某一层节点
  for (let i = 0; i < treeSelectData.length; i++) {
    const node = treeSelectData[i]
    if (node.id === id) {
      // 如果当前节点就是目标节点
      console.log("disabled", node)
      // 将目标节点设置为disabled
      node.disabled = true
      // 遍历所有自及诶单，将所有子节点全部都加上disabled
      const children = node.children
      if (Tool.isNotEmpty(children)) {
        for (let j = 0; j < children.length; j++) {
          setDisable(children, children[j].id)
        }
      }
    } else {
      // 如果当前节点不是目标节点，则到其子节点再找找看
      const children = node.children
      if (Tool.isNotEmpty(children)) {
        setDisable(children, id)
      }
    }
  }
}

// 存储删除的id结果集
const ids: Array<string> = []
const getDeleteIds = (treeSelectData: any, id: any) => {
  // console.log(treeSelectData, id)
  // 遍历数组，即遍历某一层节点
  for (let i = 0; i < treeSelectData.length; i++) {
    const node = treeSelectData[i]
    if (node.id === id) {
      // 将目标放入id结果集
      ids.push(id)
      // 将目标名称放入名称结果集
      deleteNames.push(node.name)
      // 遍历所有自及诶单，将所有子节点全部都加上disabled
      const children = node.children
      if (Tool.isNotEmpty(children)) {
        for (let j = 0; j < children.length; j++) {
          getDeleteIds(children, children[j].id)
        }
      }
    } else {
      // 如果当前节点不是目标节点，则到其子节点再找找看
      const children = node.children
      if (Tool.isNotEmpty(children)) {
        getDeleteIds(children, id)
      }
    }
  }
}

// 删除
const del = (id: number) => {
  // 清空数组，否则多次删除时，数组会一直增加
  ids.length = 0
  deleteNames.length = 0
  // 注意，响应式变量，必须加上 .value
  getDeleteIds(level1.value, id)
  Modal.confirm({
    title: '重要提醒',
    icon: createVNode(ExclamationCircleOutlined),
    content: '将删除: 【' + deleteNames.join(",") + "】 删除后不可恢复,确定删除?",
    onOk() {
      axios.delete("/doc/delete/" + ids.join(",")).then((response) => {
        const data = response.data
        if (data.success) {
          // 重新加载列表
          handleQuery()
        } else {
          message.error(data.message)
        }
      })
    }
  })
}

const handleSave = (e: MouseEvent) => {
  doc.value.content = editor.txt.html()
  axios.post("/doc/save", doc.value).then((response) => {
    const data = response.data
    if (data.success) {
      message.success("保存成功！")
      // 重新加载列表
      handleQuery()
    } else {
      message.error(data.message)
    }
  })
};


const columns = [
  {
    title: '名称',
    dataIndex: 'name',
    key: 'name'
  },
  {
    title: 'Action',
    key: 'action',
    width: 100,
  }
]


/**
 * 数据查询
 */
const handleQuery = () => {
  loading.value = true
  // 如果不清空现有数据，则编辑保存重新加载数据后，再点编辑
  docs.value = []
  // 清除表格数据
  level1.value = []
  axios.get("/doc/all/" + route.query.ebookId).then((response) => {
    loading.value = false
    const data = response.data
    if (data.success) {
      docs.value = data.content
      console.log('原始数组: ', docs.value)

      level1.value = []
      level1.value = Tool.array2Tree(docs.value, 0)
      console.log('树形机构: ', level1.value)

      // 父文档下拉框初始化，相当于点击新增
      treeSelectData.value = Tool.copy(level1.value)
      // 添加一个无字
      treeSelectData.value.unshift({id: 0, name: '无'})
    } else {
      message.error(data.message)
    }
  })
}

/**
 * 内容查询
 */
const handleQueryContent = () => {
  axios.get("/doc/find-content/" + doc.value.id).then((response) => {
    const data = response.data
    if (data.success) {
      editor.txt.html(data.content)
    } else {
      message.error(data.message)
    }
  })
}

// 富文本预览
const drawerVisible = ref(false)
const previewHtml = ref()
const handlePreviewContent = () => {
  const html = editor.txt.html()
  previewHtml.value = html
  drawerVisible.value = true
}
const onDrawerClose = () => {
  drawerVisible.value = false
}

onMounted(() => {
  handleQuery()
  editor.create()
})
</script>

<style scoped>
img {
  width: 50px;
  height: 50px;
}
</style>
