<template>
  <div class="w-full">
    <el-form ref="loginFormRef" :model="loginForm" :rules="rules" label-position="top" size="large"
      @submit.prevent="handleSubmit">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="loginForm.username" placeholder="请输入用户名" :prefix-icon="User" />
      </el-form-item>

      <el-form-item label="密码" prop="password">
        <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" :prefix-icon="Lock" show-password />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" class="w-full" :loading="userStore.loading" @click="handleSubmit">
          登录
        </el-button>
      </el-form-item>

      <div class="text-center mt-4">
        还没有账号？
        <router-link :to="Routes.USER_REGISTER" class="text-tomato-600 hover:text-tomato-800">
          立即注册
        </router-link>
      </div>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { Routes } from "@/constants/routes";
import { useUserStore } from "@/stores/user";
import { getLoginRules } from "@/utils/validators";
import { Lock, User } from "@element-plus/icons-vue";
import type { FormInstance } from "element-plus";
import { useRoute, useRouter } from "vue-router";

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const loginFormRef = ref<FormInstance>();
const loginForm = ref({
  username: "",
  password: "",
});

const rules = getLoginRules();

const handleSubmit = async () => {
  if (!loginFormRef.value) return;

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      const success = await userStore.login(loginForm.value);

      if (success) {
        // 如果有重定向参数，登录后跳转到指定页面
        const redirectPath = (route.query.redirect as string) || Routes.HOME;
        router.push(redirectPath);
      }
    }
  });
};
</script>
