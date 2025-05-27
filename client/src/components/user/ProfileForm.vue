<template>
  <div class="w-full">
    <el-form ref="profileFormRef" :model="profileForm" :rules="rules" label-position="top"
      @submit.prevent="handleSubmit" class="profile-form">
      <el-form-item label="用户名">
        <el-input v-model="userStore.username" disabled :prefix-icon="User" />
        <div class="text-xs text-gray-500 mt-1">用户名不可修改</div>
      </el-form-item>

      <el-form-item label="电子邮箱" prop="email">
        <el-input v-model="profileForm.email" placeholder="请输入邮箱" :prefix-icon="Message" />
      </el-form-item>

      <el-form-item label="手机号" prop="phone">
        <el-input v-model="profileForm.phone" placeholder="请输入手机号" :prefix-icon="Phone" />
      </el-form-item>

      <el-form-item label="地址" prop="address">
        <el-input v-model="profileForm.address" placeholder="请输入地址" :prefix-icon="Location" />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" :loading="userStore.loading" @click="handleSubmit" class="rounded-lg">
          保存修改
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { useUserStore } from "@/stores/user";
import { getProfileRules } from "@/utils/validators";
import { Location, Message, Phone, User } from "@element-plus/icons-vue";
import type { FormInstance } from "element-plus";
import { gsap } from "gsap";

const userStore = useUserStore();
const profileFormRef = ref<FormInstance>();
const profileForm = ref({
  email: "",
  phone: "",
  address: "",
});

const rules = getProfileRules();

// 初始化表单数据
onMounted(() => {
  if (userStore.user) {
    profileForm.value.email = userStore.user.email || "";
    profileForm.value.phone = userStore.user.phone || "";
    profileForm.value.address = userStore.user.address || "";

    // 添加表单项的入场动画
    const formItems = document.querySelectorAll(".profile-form .el-form-item");
    if (formItems.length > 0) {
      gsap.fromTo(
        formItems,
        { opacity: 0, y: 20 },
        {
          opacity: 1,
          y: 0,
          stagger: 0.1,
          duration: 0.5,
          ease: "power2.out",
        },
      );
    }
  }
});

const handleSubmit = async () => {
  if (!profileFormRef.value) return;

  await profileFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await userStore.updateUserProfile(profileForm.value);

        // 成功提交动画
        const button = document.querySelector(".profile-form .el-button");
        if (button) {
          gsap.fromTo(
            button,
            { scale: 1 },
            {
              scale: 1.05,
              duration: 0.2,
              ease: "back.out(2)",
              repeat: 1,
              yoyo: true,
            },
          );
        }
      } catch (error) {
        console.error("更新个人资料失败：", error);
      }
    } else {
      // 错误提示动画
      const invalidItems = document.querySelectorAll(
        ".profile-form .el-form-item.is-error",
      );
      if (invalidItems.length > 0) {
        gsap.fromTo(
          invalidItems,
          { x: 0 },
          {
            x: (i, _) => {
              return [0, -5, 5, -3, 3, 0][i % 6];
            },
            duration: 0.5,
            ease: "power2.out",
          },
        );
      }
    }
  });
};
</script>

<style scoped>
.profile-form :deep(.el-input__wrapper) {
  border-radius: 0.75rem;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.profile-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.08);
}

.profile-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(255, 107, 107, 0.3), 0 3px 10px rgba(0, 0, 0, 0.08);
}

.profile-form :deep(.el-form-item__label) {
  font-weight: 500;
  color: #4b5563;
  margin-bottom: 0.5rem;
}

.profile-form :deep(.el-form-item.is-error .el-input__wrapper) {
  box-shadow: 0 0 0 1px rgba(244, 63, 94, 0.3);
}
</style>
