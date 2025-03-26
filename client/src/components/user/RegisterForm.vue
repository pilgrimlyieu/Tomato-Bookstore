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
import { Lock, Message, Phone, User } from "@element-plus/icons-vue";
import type { FormInstance, FormRules } from "element-plus";
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

// 验证两次输入的密码是否一致
const validateConfirmPassword = (rule: any, value: string, callback: any) => {
  if (value === "") {
    callback(new Error("请再次输入密码"));
  } else if (value !== registerForm.value.password) {
    callback(new Error("两次输入的密码不一致"));
  } else {
    callback();
  }
};

// 验证手机号格式
const validatePhone = (rule: any, value: string, callback: any) => {
  const phoneRegex = /^1[3-9]\d{9}$/;
  if (value === "") {
    callback(new Error("请输入手机号"));
  } else if (!phoneRegex.test(value)) {
    callback(new Error("请输入有效的 11 位手机号"));
  } else {
    callback();
  }
};

// 表单验证规则
const rules: FormRules = {
  username: [
    { required: true, message: "请输入用户名", trigger: "blur" },
    {
      min: 3,
      max: 20,
      message: "用户名长度必须在 3-20 个字符之间",
      trigger: "blur",
    },
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    {
      min: 6,
      max: 50,
      message: "密码长度必须在 6-50 个字符之间",
      trigger: "blur",
    },
  ],
  confirmPassword: [
    { required: true, message: "请再次输入密码", trigger: "blur" },
    { validator: validateConfirmPassword, trigger: "blur" },
  ],
  email: [
    { required: true, message: "请输入邮箱", trigger: "blur" },
    { type: "email", message: "请输入有效的邮箱地址", trigger: "blur" },
  ],
  phone: [
    { required: true, message: "请输入手机号", trigger: "blur" },
    { validator: validatePhone, trigger: "blur" },
  ],
};

const handleSubmit = async () => {
  if (!registerFormRef.value) return;

  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      // 去掉确认密码字段
      const { confirmPassword, ...registerData } = registerForm.value;

      const success = await userStore.register(registerData);

      if (success) {
        router.push(Routes.USER_LOGIN);
      }
    }
  });
};
</script>
