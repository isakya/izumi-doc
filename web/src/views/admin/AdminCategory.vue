<template>
  <a-layout style="padding: 24px 0; background: #fff; margin-top: 50px">
    <a-layout-content :style="{ padding: '0 24px', minHeight: '280px' }">
      <p>
        <a-form layout="inline" :model="param">
          <a-form-item>
            <a-input v-model:value="param.name" placeholder="名称"></a-input>
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
          :data-source="categorys"
          :row-key="record => record.id"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'cover'">
            <img :src="record.cover" alt="avatar"/>
          </template>
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
  <a-modal v-model:open="modalVisible" :confirm-loading="modalLoading" title="分类表单" @ok="handleModalOk">
    <a-form :modal="category" :label-col="{span: 3}">
      <a-form-item label="名称">
        <a-input v-model:value="category.name"/>
      </a-form-item>
      <a-form-item label="父分类">
        <a-input v-model:value="category.parent"/>
      </a-form-item>
      <a-form-item label="排序">
        <a-input v-model:value="category.sort"/>
      </a-form-item>
    </a-form>
  </a-modal>
</template>
<script lang="ts" setup>
import {onMounted, ref} from 'vue'
import axios from 'axios'
import {message} from 'ant-design-vue'
import {Tool} from "@/util/tool";

const param = ref()
// 这里必须设置value 为一个空对象
param.value = {}
const categorys = ref()
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0
})
const loading = ref(false)
// 表单
const category = ref()

const modalVisible = ref<boolean>(false)
const modalLoading = ref<boolean>(false)
const edit = (record: any) => {
  modalVisible.value = true
  category.value = Tool.copy(record)
};
// 新增
const add = () => {
  modalVisible.value = true
  category.value = {}
}
// 删除
const del = (id: number) => {
  axios.delete("/category/delete/" + id).then((response) => {
    const data = response.data
    if (data.success) {
      // 重新加载列表
      handleQuery({
        page: pagination.value.current,
        size: pagination.value.pageSize
      })
    }
  })
}
const handleModalOk = (e: MouseEvent) => {
  modalLoading.value = true
  axios.post("/category/save", category.value).then((response) => {
    modalLoading.value = false
    const data = response.data
    if (data.success) {
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
    title: '名称',
    dataIndex: 'name',
    key: 'name'
  },
  {
    title: '父分类',
    dataIndex: 'parent',
    key: 'parent',
  },
  {
    title: '顺序',
    dataIndex: 'sort',
    key: 'sort'
  },
  {
    title: 'Action',
    key: 'action'
  }
]

/**
 * 数据查询
 */
const handleQuery = (params: any) => {
  loading.value = true
  // 如果不清空现有数据，则编辑保存重新加载数据后，再点编辑
  categorys.value = []
  axios.get("/category/list", {
    params: {
      page: params.page,
      size: params.size,
      name: param.value?.name
    }
  }).then((response) => {
    loading.value = false
    const data = response.data
    if (data.success) {
      categorys.value = data.content.list
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
    size: pagination.value.pageSize
  })
})
</script>

<style scoped>
img {
  width: 50px;
  height: 50px;
}
</style>
