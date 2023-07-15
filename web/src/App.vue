<template>
  <a-layout>
    <the-header></the-header>
    <a-layout-content style="padding: 0 50px">
      <router-view />
    </a-layout-content>
    <the-footer></the-footer>
  </a-layout>
</template>
<script lang="ts" setup>
import TheHeader from '@/components/the-header.vue'
import TheFooter from '@/components/the-footer.vue'


</script>

<style scoped>
#components-layout-demo-top-side .logo {
  float: left;
  width: 120px;
  height: 31px;
  margin: 16px 24px 16px 0;
  background: rgba(255, 255, 255, 0.3);
}

.ant-row-rtl #components-layout-demo-top-side .logo {
  float: right;
  margin: 16px 0 16px 24px;
}
</style>
