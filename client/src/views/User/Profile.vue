<template>
  <div class="container mx-auto py-12 px-4">
    <div class="max-w-5xl mx-auto">
      <h1 class="text-3xl font-bold text-gray-800 mb-8">个人信息</h1>

      <div class="bg-white/90 backdrop-blur-md shadow-md rounded-xl p-6">
        <div class="flex flex-col md:flex-row gap-10">
          <!-- 左侧头像和用户基本信息 -->
          <div class="w-full md:w-1/3">
            <div class="flex flex-col items-center">
              <el-avatar :size="120" :src="userStore.user?.avatar || ''"
                class="border-4 border-white shadow-md mb-4" :class="[
                  userStore.user?.avatar ? '' : 'bg-gradient-to-r from-tomato-500 to-tomato-600 text-white text-4xl font-bold'
                ]">
                {{ userStore.username ? userStore.username.substring(0, 1).toUpperCase() : 'U' }}
              </el-avatar>

              <h2 class="text-xl font-semibold text-gray-800 mb-1">{{ userStore.username }}</h2>
              <p class="text-gray-500 mb-4">{{ userStore.isAdmin ? '管理员' : '普通顾客' }}</p>

              <el-button size="small" class="rounded-full" @click="visibleUploadAvatar = true">
                <el-icon class="mr-1">
                  <Upload />
                </el-icon>
                更换头像
              </el-button>
            </div>
          </div>

          <!-- 右侧个人资料表单 -->
          <div class="w-full md:w-2/3">
            <el-tabs class="modern-tabs">
              <el-tab-pane label="个人资料">
                <div class="py-2">
                  <ProfileForm />
                </div>
              </el-tab-pane>
              <el-tab-pane label="安全设置">
                <div class="py-4">
                  <h3 class="text-xl font-medium text-gray-800 mb-6">修改密码</h3>
                  <el-form label-position="top" :model="passwordForm" :rules="passwordRules" ref="passwordFormRef">
                    <el-form-item label="当前密码" prop="currentPassword">
                      <el-input v-model="passwordForm.currentPassword" type="password" placeholder="请输入当前密码"
                        :prefix-icon="Lock" show-password />
                    </el-form-item>
                    <el-form-item label="新密码" prop="newPassword">
                      <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" :prefix-icon="Lock"
                        show-password />
                    </el-form-item>
                    <el-form-item label="确认新密码" prop="confirmPassword">
                      <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码"
                        :prefix-icon="Lock" show-password />
                    </el-form-item>
                    <el-form-item>
                      <el-button type="primary" class="rounded-lg" @click="handleChangePassword" :loading="changingPassword">
                        更新密码
                      </el-button>
                    </el-form-item>
                  </el-form>

                  <el-divider content-position="left">账户安全</el-divider>

                  <div class="space-y-4">
                    <div class="flex justify-between items-center">
                      <div>
                        <h4 class="text-gray-800 font-medium">双因素认证</h4>
                        <p class="text-gray-500 text-sm">启用后将通过手机短信或邮件验证登录</p>
                      </div>
                      <el-switch v-model="twoFactorEnabled" />
                    </div>

                    <div class="flex justify-between items-center">
                      <div>
                        <h4 class="text-gray-800 font-medium">登录通知</h4>
                        <p class="text-gray-500 text-sm">有新设备登录时通过邮件通知</p>
                      </div>
                      <el-switch v-model="loginNotifications" />
                    </div>
                  </div>
                </div>
              </el-tab-pane>
              <el-tab-pane label="账户绑定">
                <div class="py-4">
                  <h3 class="text-xl font-medium text-gray-800 mb-6">社交账号绑定</h3>

                  <div class="space-y-6">
                    <div class="flex items-center justify-between">
                      <div class="flex items-center">
                        <img src="https://img.icons8.com/color/48/000000/wechat--v1.png" alt="WeChat" class="w-8 h-8 mr-3">
                        <div>
                          <h4 class="text-gray-800 font-medium">微信</h4>
                          <p class="text-gray-500 text-sm">未绑定</p>
                        </div>
                      </div>
                      <el-button size="small" class="rounded-full">绑定</el-button>
                    </div>

                    <div class="flex items-center justify-between">
                      <div class="flex items-center">
                        <img src="https://img.icons8.com/color/48/000000/alipay.png" alt="Alipay" class="w-8 h-8 mr-3">
                        <div>
                          <h4 class="text-gray-800 font-medium">支付宝</h4>
                          <p class="text-gray-500 text-sm">未绑定</p>
                        </div>
                      </div>
                      <el-button size="small" class="rounded-full">绑定</el-button>
                    </div>

                    <div class="flex items-center justify-between">
                      <div class="flex items-center">
                        <img src="https://img.icons8.com/color/48/000000/google-logo.png" alt="Google" class="w-8 h-8 mr-3">
                        <div>
                          <h4 class="text-gray-800 font-medium">Google</h4>
                          <p class="text-gray-500 text-sm">未绑定</p>
                        </div>
                      </div>
                      <el-button size="small" class="rounded-full">绑定</el-button>
                    </div>
                  </div>
                </div>
              </el-tab-pane>
            </el-tabs>
          </div>
        </div>
      </div>
    </div>

    <!-- 上传头像对话框 -->
    <el-dialog v-model="visibleUploadAvatar" title="更换头像" width="400px">
      <div class="flex flex-col items-center">
        <el-avatar :size="120" :src="avatarPreview || userStore.user?.avatar || ''"
          class="border-4 border-white shadow-md mb-6" :class="[
            avatarPreview || userStore.user?.avatar ? '' : 'bg-gradient-to-r from-tomato-500 to-tomato-600 text-white text-4xl font-bold'
          ]">
          {{ userStore.username ? userStore.username.substring(0, 1).toUpperCase() : 'U' }}
        </el-avatar>

        <el-upload action="#" list-type="picture-card" :auto-upload="false" :show-file-list="false"
          :on-change="handleAvatarChange">
          <el-icon>
            <Plus />
          </el-icon>
          <template #tip>
            <div class="el-upload__tip text-center">
              支持 JPG, PNG 文件，最大 2MB
            </div>
          </template>
        </el-upload>
      </div>
      <template #footer>
        <div class="flex justify-end gap-2">
          <el-button @click="visibleUploadAvatar = false" class="rounded-lg">取消</el-button>
          <el-button type="primary" @click="handleUploadAvatar" :loading="uploadingAvatar" class="rounded-lg">保存</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import ProfileForm from "@/components/user/ProfileForm.vue";
import { useUserStore } from "@/stores/user";
import { getChangePasswordRules } from "@/utils/validators";
import { Lock, Plus, Upload, User } from "@element-plus/icons-vue";
import type { FormInstance, UploadFile } from "element-plus";
import { ElMessage } from "element-plus";
import gsap from "gsap";
import { ref } from "vue";

const userStore = useUserStore();

// 修改密码相关
const passwordFormRef = ref<FormInstance>();
const passwordForm = ref({
  currentPassword: "",
  newPassword: "",
  confirmPassword: "",
});
const changingPassword = ref(false);

// 使用统一的密码修改验证规则
const passwordRules = getChangePasswordRules();

// 修改密码处理函数
const handleChangePassword = async () => {
  if (!passwordFormRef.value) return;

  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      changingPassword.value = true;
      try {
        // await userStore.changePassword({
        //   currentPassword: passwordForm.value.currentPassword,
        //   newPassword: passwordForm.value.newPassword,
        // }); // TODO

        ElMessage.success("密码修改成功");
        passwordForm.value = {
          currentPassword: "",
          newPassword: "",
          confirmPassword: "",
        };
      } catch (error) {
        console.error("修改密码失败：", error);
        ElMessage.error("修改密码失败，请重试");
      } finally {
        changingPassword.value = false;
      }
    }
  });
};

// 安全设置开关
const twoFactorEnabled = ref(false);
const loginNotifications = ref(true);

// 头像上传相关
const visibleUploadAvatar = ref(false);
const avatarPreview = ref("");
const uploadingAvatar = ref(false);

// 头像变更处理
const handleAvatarChange = (file: UploadFile) => {
  const isImage =
    file.raw &&
    ["image/jpeg", "image/png", "image/gif"].includes(file.raw.type);
  const isLt2M = file.raw && file.raw.size / 1024 / 1024 < 2;

  if (!isImage) {
    ElMessage.error("上传头像图片只能是 JPG/PNG/GIF 格式！");
    return false;
  }
  if (!isLt2M) {
    ElMessage.error("上传头像图片大小不能超过 2MB！");
    return false;
  }

  // 创建文件阅读器预览图片
  const reader = new FileReader();
  reader.readAsDataURL(file.raw!);
  reader.onload = () => {
    avatarPreview.value = reader.result as string;

    // 使用 GSAP 添加简单动画
    const avatar = document.querySelector(".el-avatar");
    if (avatar) {
      gsap.from(avatar, {
        scale: 0.8,
        opacity: 0.5,
        duration: 0.5,
        ease: "back.out(1.7)",
      });
    }
  };
};

// 上传头像处理
const handleUploadAvatar = async () => {
  if (!avatarPreview.value) {
    ElMessage.warning("请先选择图片");
    return;
  }

  uploadingAvatar.value = true;
  try {
    // 调用 API 上传头像
    // await userStore.updateUserProfile({ avatar: avatarPreview.value });
    // TODO: image service

    visibleUploadAvatar.value = false;
    ElMessage.success("头像上传成功");
  } catch (error) {
    console.error("上传头像失败：", error);
    ElMessage.error("上传头像失败，请重试");
  } finally {
    uploadingAvatar.value = false;
  }
};
</script>

<style scoped>
.modern-tabs :deep(.el-tabs__item) {
  font-size: 1rem;
  padding: 0 1.5rem;
  height: 48px;
  line-height: 48px;
  transition: all 0.3s ease;
}

.modern-tabs :deep(.el-tabs__item.is-active) {
  font-weight: 600;
}

.modern-tabs :deep(.el-tabs__active-bar) {
  height: 3px;
  border-radius: 3px;
}

.modern-tabs :deep(.el-tabs__nav-wrap::after) {
  height: 1px;
  background-color: rgba(229, 231, 235, 0.5);
}
</style>
