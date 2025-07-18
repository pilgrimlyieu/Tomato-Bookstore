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
              <el-tab-pane label="我的书评">
                <div class="py-4">
                  <div class="flex justify-between items-center mb-6">
                    <h3 class="text-xl font-medium text-gray-800">
                      我的书评
                      <el-badge :value="userReviews.length" type="primary" class="ml-2" v-if="userReviews.length > 0" />
                    </h3>
                    <router-link :to="{ path: Routes.USER_REVIEWS }">
                      <el-button type="primary" size="small" class="rounded-lg" :disabled="userReviews.length === 0">
                        查看全部 <el-icon class="ml-1"><arrow-right /></el-icon>
                      </el-button>
                    </router-link>

                  </div>

                    <div v-if="userReviews.length > 0">
                    <div v-for="review in reviewsWithProductNames.slice(0, 3)" :key="review.id" class="mb-4 border-b pb-4">
                      <div class="flex justify-between items-start">
                      <div>
                        <div class="font-medium text-gray-900 mb-1">
                        《{{ review.productName }}》
                        </div>
                        <router-link
                        :to="buildRoute(Routes.PRODUCT_DETAIL, { id: review.productId })"
                        class="text-primary hover:underline mb-2 block text-sm"
                        >
                        查看商品详情
                        </router-link>
                        <el-rate
                        v-model="review.rating"
                        disabled
                        :max="10"
                        show-score
                        score-template="{value}"
                        />
                      </div>
                      <div class="text-gray-500 text-sm">
                        {{ formatDate(review.createdAt) }}
                      </div>
                      </div>
                      <div class="mt-2 text-gray-700" v-if="review.content">
                      {{ review.content }}
                      </div>
                      <div class="mt-2 text-gray-400 italic" v-else>
                      此评价没有评论内容
                      </div>
                    </div>

                    <div v-if="userReviews.length > 3" class="text-center mt-4">
                      <router-link :to="{ path: Routes.USER_REVIEWS }" class="inline-flex items-center text-primary hover:underline hover:shadow-sm p-2 rounded-lg transition-all">
                        <span>查看剩余 {{ userReviews.length - 3 }} 条书评</span>
                        <el-icon class="ml-1"><arrow-right /></el-icon>
                      </router-link>
                    </div>
                  </div>

                  <el-empty v-else description="您还没有发表过任何书评">
                    <router-link :to="Routes.PRODUCT_LIST">
                      <el-button type="primary" class="rounded-lg">浏览商品</el-button>
                    </router-link>
                  </el-empty>
                </div>
              </el-tab-pane>
              <el-tab-pane label="我的读书笔记">
                <div class="py-4">
                  <div class="flex justify-between items-center mb-6">
                    <h3 class="text-xl font-medium text-gray-800">
                      我的读书笔记
                      <el-badge :value="userNotes.length" type="primary" class="ml-2" v-if="userNotes.length > 0" />
                    </h3>
                    <router-link :to="{ path: Routes.USER_NOTES }">
                      <el-button type="primary" size="small" class="rounded-lg" :disabled="userNotes.length === 0">
                        查看全部 <el-icon class="ml-1"><arrow-right /></el-icon>
                      </el-button>
                    </router-link>
                  </div>

                  <div v-if="userNotes.length > 0">
                    <div v-for="note in notesWithProductNames.slice(0, 2)" :key="note.id" class="mb-4 border-b pb-4">
                      <div class="flex justify-between items-start">
                        <div>
                          <div class="font-medium text-gray-900 mb-1">
                            {{ note.title }}
                          </div>
                          <router-link
                            :to="buildRoute(Routes.PRODUCT_DETAIL, { id: note.productId })"
                            class="text-primary hover:underline mb-2 block text-sm"
                          >
                            《{{ note.productName }}》
                          </router-link>
                        </div>
                        <div class="text-gray-500 text-sm">
                          {{ formatDate(note.createdAt) }}
                        </div>
                      </div>
                      <div class="mt-2 text-gray-700 line-clamp-2">
                        {{ note.content }}
                      </div>
                      <div class="mt-3 flex justify-end">
                        <router-link :to="buildRoute(Routes.NOTE_DETAIL, { noteId: note.id })">
                          <el-button type="primary" size="small" class="rounded-lg" text>
                            阅读全文 <el-icon class="ml-1"><arrow-right /></el-icon>
                          </el-button>
                        </router-link>
                      </div>
                    </div>

                    <div v-if="userNotes.length > 2" class="text-center mt-4">
                      <router-link :to="{ path: Routes.USER_NOTES }" class="inline-flex items-center text-primary hover:underline hover:shadow-sm p-2 rounded-lg transition-all">
                        <span>查看剩余 {{ userNotes.length - 2 }} 篇读书笔记</span>
                        <el-icon class="ml-1"><arrow-right /></el-icon>
                      </router-link>
                    </div>
                  </div>

                  <el-empty v-else description="您还没有创建过任何读书笔记">
                    <router-link :to="Routes.PRODUCT_LIST">
                      <el-button type="primary" class="rounded-lg">浏览商品</el-button>
                    </router-link>
                  </el-empty>
                </div>
              </el-tab-pane>
              <el-tab-pane label="安全设置">
                <div class="py-4">
                  <h3 class="text-xl font-medium text-gray-800 mb-6">修改密码</h3>
                  <el-form label-position="top" :model="passwordForm" :rules="passwordRules" ref="passwordFormRef">
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
              支持 JPG, PNG 文件，最大 5MB
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
import { useNote } from "@/composables/useNote";
import { Routes } from "@/constants/routes";
import uploadService from "@/services/upload-service";
import { useProductStore } from "@/stores/product";
import { useReviewStore } from "@/stores/review";
import { useUserStore } from "@/stores/user";
import { formatDate } from "@/utils/formatters";
import { buildRoute } from "@/utils/routeHelper";
import { getChangePasswordRules } from "@/utils/validators";
import { ArrowRight, Lock, Plus, Upload } from "@element-plus/icons-vue";
import type { FormInstance, UploadFile } from "element-plus";
import { ElMessage } from "element-plus";
import gsap from "gsap";

const userStore = useUserStore();
const reviewStore = useReviewStore();
const productStore = useProductStore();
const { userNotes, fetchUserNotes, userNotesWithProductNames } = useNote();

// 加载状态
const loading = ref(false);

// 获取用户书评和读书笔记
onMounted(async () => {
  loading.value = true;
  try {
    await Promise.all([
      reviewStore.fetchUserReviews(),
      productStore.fetchAllProducts(),
      fetchUserNotes(),
    ]);
  } catch (error) {
    console.error("获取用户数据失败：", error);
    ElMessage.error("获取数据失败，请刷新重试");
  } finally {
    loading.value = false;
  }
});

// 用户书评列表与读书笔记列表计算属性
const userReviews = computed(() => {
  return reviewStore.userReviews;
});

const reviewsWithProductNames = computed(() => {
  return reviewStore.userReviews.map((review) => ({
    ...review,
    productName:
      productStore.products.find((p) => p.id === review.productId)?.title ||
      "未知书名",
  }));
});

const notesWithProductNames = computed(() => {
  return userNotesWithProductNames.value;
});

// 修改密码相关
const passwordFormRef = ref<FormInstance>();
const passwordForm = ref({
  newPassword: "",
  confirmPassword: "",
});
const changingPassword = ref(false);

// 简化的密码验证规则
const passwordRules = getChangePasswordRules(
  () => passwordForm.value.newPassword,
);

// 修改密码处理函数
const handleChangePassword = async () => {
  if (!passwordFormRef.value) return;

  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      changingPassword.value = true;
      try {
        await userStore.changePassword(passwordForm.value.newPassword);

        passwordForm.value = {
          newPassword: "",
          confirmPassword: "",
        };

        // 重置表单验证状态
        passwordFormRef.value?.resetFields();
        ElMessage.success("密码修改成功");
      } catch (error) {
        console.error("修改密码失败：", error);
      } finally {
        changingPassword.value = false;
      }
    }
  });
};

// 头像上传相关
const visibleUploadAvatar = ref(false);
const avatarPreview = ref("");
const uploadingAvatar = ref(false);
const avatarFile = ref<File | null>(null);

// 头像变更处理
const handleAvatarChange = (file: UploadFile) => {
  const isImage =
    file.raw &&
    ["image/jpeg", "image/png", "image/gif", "image/webp"].includes(
      file.raw.type,
    );
  const isLt5M = file.raw && file.raw.size / 1024 / 1024 < 5;

  if (!isImage) {
    ElMessage.error("上传头像图片只能是 JPG/PNG/GIF/WebP 格式！");
    return false;
  }
  if (!isLt5M) {
    ElMessage.error("上传头像图片大小不能超过 5MB！");
    return false;
  }

  // 创建文件阅读器预览图片
  const reader = new FileReader();
  reader.readAsDataURL(file.raw!);
  reader.onload = () => {
    avatarPreview.value = reader.result as string;
    // 存储原始文件用于上传
    avatarFile.value = file.raw!;

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
  if (!avatarFile.value) {
    ElMessage.warning("请先选择图片");
    return;
  }

  uploadingAvatar.value = true;
  try {
    const response = await uploadService.uploadAvatar(avatarFile.value);

    // 更新用户头像
    await userStore.updateUserProfile({ avatar: response.data });

    visibleUploadAvatar.value = false;
    avatarPreview.value = "";
    avatarFile.value = null;
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

.line-clamp-2 {
  display: -webkit-box;
  line-clamp: 2;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
</style>
