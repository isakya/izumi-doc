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

</style>
