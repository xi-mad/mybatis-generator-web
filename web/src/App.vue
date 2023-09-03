<template>
  <Suspense>
    <Main/>
  </Suspense>
</template>

<script setup>
import Main from './components/Main.vue'
</script>

<style lang="less">
html, body {
  height: 100%
}
</style>