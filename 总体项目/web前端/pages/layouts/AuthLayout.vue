<!-- pages/layouts/AuthLayout.vue -->
<template>
  <div class="auth-container">
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'AuthLayout'
}
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f5f5;
}
</style>
