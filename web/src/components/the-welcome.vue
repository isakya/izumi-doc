<template>
  <div class="welcome" >
    <a-space :size="220">
      <div>
        <h1>
          欢迎使用IZUMI书库
        </h1>
        <p>(*￣︶￣)</p>
      </div>

      <img src="/image/home.jpg" alt="IZUMI">
    </a-space>
  </div>
</template>

<script setup lang="ts">

</script>

<style scoped>
.welcome {
  display: flex;
  justify-content: center;
  align-items: center;
}
.welcome h1,p {
  font-size: 30px;
  color: #ebab4b;
}
.welcome h1 {
  font-style: italic;
}
</style>
