<template>
  <a-layout style="padding: 24px 0; background: #fff; margin-top: 50px">
    <a-layout-content :style="{ padding: '0 24px', minHeight: '280px' }">
      <p>
        <a-form layout="inline" :model="param">
          <a-form-item>
            <a-input v-model:value="param.loginName" placeholder="登录名"></a-input>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="handleQuery({page: 1, size: pagination.pageSize})">
              查询
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
          :columns="columns"
          :data-source="users"
          :row-key="record => record.id"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'action'">
            <a-space size="small">
              <a-button type="primary" @click="edit(record)">
                编辑
              </a-button>
              <a-popconfirm
                  title="确认要删除吗?"
                  ok-text="确认"
                  cancel-text="取消"
                  @confirm="del(record.id)"
              >
                <a-button type="danger">
                  删除
                </a-button>
              </a-popconfirm>
            </a-space>
            </template>
          </template>
      </a-table>
    </a-layout-content>
  </a-layout>
  <a-modal v-model:open="modalVisible" :confirm-loading="modalLoading" title="电子书表单" @ok="handleModalOk">
    <a-form :modal="user" :label-col="{span: 3}">
      <a-form-item label="登录名">
        <a-input v-model:value="user.loginName" :disabled="!!user.id"/>
      </a-form-item>
      <a-form-item label="昵称">
        <a-input v-model:value="user.name"/>
      </a-form-item>
      <a-form-item label="密码">
        <a-input v-model:value="user.password"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>
<script lang="ts" setup>
// 告诉typescript 这两个变量是存在的
declare let hexMd5: any;
declare let KEY: any;
import {onMounted, ref} from 'vue'
import axios from 'axios'
import {message} from 'ant-design-vue'
import {Tool} from "@/util/tool";

const param = ref()
// 这里必须设置value 为一个空对象
param.value = {}
const users = ref()
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0
})
// 加载分类
// 数组 [100, 101] 对应 前端开发/Vue
const categoryIds = ref()
// 普通变量
let categorys: any
const loading = ref(false)
// 表单
const user = ref()

const modalVisible = ref<boolean>(false)
const modalLoading = ref<boolean>(false)
const edit = (record: any) => {
  modalVisible.value = true
  user.value = Tool.copy(record)
  // 组合进行显示
  categoryIds.value = [user.value.category1Id, user.value.category2Id]
};
// 新增
const add = () => {
  modalVisible.value = true
  user.value = {}
}
// 删除
const del = (id: number) => {
  axios.delete("/user/delete/" + id).then((response) => {
    const data = response.data
    if(data.success) {
      // 重新加载列表
      handleQuery({
        page: pagination.value.current,
        size: pagination.value.pageSize
      })
    }
  })
}
const handleModalOk = (e: MouseEvent) => {
  // 对前端传的密码进行加密
  user.value.password = hexMd5(user.value.password + KEY);
  modalLoading.value = true
  // 两个分类
  axios.post("/user/save", user.value).then((response) => {
    modalLoading.value = false

    const data = response.data
    if(data.success) {
      modalVisible.value = false
      // 重新加载列表
      handleQuery({
        page: pagination.value.current,
        size: pagination.value.pageSize
      })
    } else {
      message.error(data.message)
    }
  })
};





const columns = [
  {
    title: '登陆名',
    dataIndex: 'loginName',
    key: 'loginName'
  },
  {
    title: '名称',
    key: 'name',
    dataIndex: 'name'
  },
  {
    title: '密码',
    dataIndex: 'password',
    key: 'password'
  },
  {
    title: 'Action',
    key: 'action',
  }
];

/**
 * 数据查询
 */
const handleQuery = (params: any) => {
  loading.value = true
  // 如果不清空现有数据，则编辑保存重新加载数据后，再点编辑
  users.value = []
  axios.get("/user/list", {
    params: {
      page: params.page,
      size: params.size,
      loginName: param.value?.loginName
    }
  }).then((response) => {
    loading.value = false
    const data = response.data
    if (data.success) {
      users.value = data.content.list
      pagination.value.current = params.page
      pagination.value.total = data.content.total
    } else {
      message.error(data.message)
    }
  })
}
// 表格点击页码时触发
const handleTableChange = (pagination: any) => {
  console.log('看看自带的分页参数都有啥: ' + pagination)
  handleQuery({
    page: pagination.current,
    size: pagination.pageSize
  })
}

onMounted(() => {
  handleQuery({
    page: 1,
    size: pagination.value.pageSize,
  })
})
</script>

<style scoped>
img {
  width: 50px;
  height: 50px;
}
</style>
