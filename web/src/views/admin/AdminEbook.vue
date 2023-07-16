<template>
  <a-layout style="padding: 24px 0; background: #fff; margin-top: 50px">
    <a-layout-content :style="{ padding: '0 24px', minHeight: '280px' }">
      <p>
        <a-button type="primary" @click="add" size="large">
          新增
        </a-button>
      </p>
      <a-table
          :columns="columns"
          :data-source="ebooks"
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
              <a-button type="danger">
                删除
              </a-button>
            </a-space>
            </template>
          </template>
      </a-table>
    </a-layout-content>
  </a-layout>
  <a-modal v-model:open="modalVisible" :confirm-loading="modalLoading" title="电子书表单" @ok="handleModalOk">
    <a-form :modal="ebook" :label-col="{span: 2}">
      <a-form-item label="封面">
        <a-input v-model:value="ebook.cover" />
      </a-form-item>
      <a-form-item label="名称">
        <a-input v-model:value="ebook.name" />
      </a-form-item>
      <a-form-item label="分类1">
        <a-input v-model:value="ebook.category1Id" />
      </a-form-item>
      <a-form-item label="分类2">
        <a-input v-model:value="ebook.category2Id" />
      </a-form-item>
      <a-form-item label="描述">
        <a-input v-model:value="ebook.desc" type="text" />
      </a-form-item>
    </a-form>
  </a-modal>
</template>
<script lang="ts" setup>
import {onMounted, ref} from 'vue'
import axios from 'axios'
import {message} from 'ant-design-vue'

const ebooks = ref()
const pagination = ref({
  current: 1,
  pageSize: 2,
  total: 0
})
const loading = ref(false)
// 表单
const ebook = ref()

const modalVisible = ref<boolean>(false)
const modalLoading = ref<boolean>(false)
const edit = (record: any) => {
  modalVisible.value = true
  ebook.value = record
};
// 新增
const add = () => {
  modalVisible.value = true
  ebook.value = {}
}
const handleModalOk = (e: MouseEvent) => {
  modalLoading.value = true
  axios.post("/ebook/save", ebook.value).then((response) => {
    const data = response.data
    if(data.success) {
      modalVisible.value = false
      modalLoading.value = false
      // 重新加载列表
      handleQuery({
        page: pagination.value.current,
        size: pagination.value.pageSize
      })
    }
  })
};





const columns = [
  {
    title: '封面',
    dataIndex: 'cover',
    key: 'cover'
  },
  {
    title: '名称',
    dataIndex: 'name',
    key: 'name'
  },
  {
    title: '分类一',
    dataIndex: 'category1Id',
    key: 'category1Id',
  },
  {
    title: '分类二',
    dataIndex: 'category2Id',
    key: 'category2Id'
  },
  {
    title: '文档数',
    dataIndex: 'docCount',
    key: 'docCount'
  },
  {
    title: '阅读数',
    dataIndex: 'viewCount',
    key: 'viewCount',
  },
  {
    title: '点赞数',
    dataIndex: 'voteCount',
    key: 'voteCount',

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
  ebooks.value = []
  axios.get("/ebook/list", {
    params: {
      page: params.page,
      size: params.size,
      name: params.value?.name
    }
  }).then((response) => {
    loading.value = false
    const data = response.data
    if (data.success) {
      ebooks.value = data.content.list
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
