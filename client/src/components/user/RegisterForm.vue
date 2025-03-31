<template>
  <div class="w-full">
    <el-form ref="registerFormRef" :model="registerForm" :rules="rules" label-position="top" size="large"
      @submit.prevent="handleSubmit">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="registerForm.username" placeholder="请输入用户名" :prefix-icon="User" />
      </el-form-item>

      <el-form-item label="密码" prop="password">
        <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" :prefix-icon="Lock"
          show-password />
      </el-form-item>

      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请再次输入密码" :prefix-icon="Lock"
          show-password />
      </el-form-item>

      <el-form-item label="邮箱" prop="email">
        <el-input v-model="registerForm.email" placeholder="请输入邮箱" :prefix-icon="Message" />
      </el-form-item>

      <el-form-item label="手机号" prop="phone">
        <el-input v-model="registerForm.phone" placeholder="请输入手机号" :prefix-icon="Phone" />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" class="w-full" :loading="userStore.loading" @click="handleSubmit">
          注册
        </el-button>
      </el-form-item>

      <div class="text-center mt-4">
        已有账号？
        <router-link :to="Routes.USER_LOGIN" class="text-tomato-600 hover:text-tomato-800">
          返回登录
        </router-link>
      </div>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { Routes } from "@/constants/routes";
import { useUserStore } from "@/stores/user";
import { getRegisterRules } from "@/utils/validators";
import { Lock, Message, Phone, User } from "@element-plus/icons-vue";
import type { FormInstance } from "element-plus";
import { ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const userStore = useUserStore();

const registerFormRef = ref<FormInstance>();
const registerForm = ref({
  username: "",
  password: "",
  confirmPassword: "",
  email: "",
  phone: "",
});

const rules = getRegisterRules();

const handleSubmit = async () => {
  if (!registerFormRef.value) return;

  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      const { confirmPassword, ...registerData } = registerForm.value;
      const success = await userStore.register(registerData);
      if (success) {
        router.push(Routes.USER_LOGIN);
      }
    }
  });
};
</script>
