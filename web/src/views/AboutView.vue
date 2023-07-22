<template>
  <a-layout style="padding: 24px 0; background: #fff; margin-top: 50px">
    <div class="welcome">
      <a-space :size="220">
        <div>
          <h1>
            欢迎使用IZUMI文档库
          </h1>
          <p>(*￣︶￣)</p>
        </div>

        <img src="/image/home.jpg" alt="IZUMI">
      </a-space>
    </div>
  </a-layout>
</template>

<script setup lang="ts">

</script>

<style scoped>
.welcome {
  display: flex;
  justify-content: center;
  align-items: center;
}

.welcome h1, p {
  font-size: 30px;
  color: #ebab4b;
}

.welcome h1 {
  font-style: italic;
}
</style>
