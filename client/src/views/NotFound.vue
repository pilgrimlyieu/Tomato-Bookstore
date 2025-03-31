<template>
  <div class="min-h-[60vh] flex flex-col items-center justify-center py-12 px-4">
    <div class="text-center">
      <h1 class="text-9xl font-bold text-tomato-500">404</h1>
      <h2 class="text-3xl font-semibold text-gray-800 mt-4 mb-6">页面不存在</h2>
      <p class="text-gray-600 mb-8">
        抱歉，您访问的页面不存在或已被移除。
      </p>
      <router-link to="/">
        <el-button type="primary" size="large">返回首页</el-button>
      </router-link>
    </div>
  </div>
</template>
