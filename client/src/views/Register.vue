<template>
  <div class="min-h-screen flex flex-col md:flex-row-reverse py-0">
    <!-- 右侧图片背景区域 (中等尺寸以上显示) -->
    <div
      class="hidden md:flex md:w-1/2 bg-gradient-to-br from-tomato-500 to-tomato-700 items-center justify-center relative overflow-hidden">
      <!-- 粒子背景 - 替换为纯CSS效果 -->
      <div class="particles-bg absolute inset-0"></div>

      <div class="text-white max-w-lg relative z-10 p-8 animate-fade-in">
        <div class="absolute -top-20 -left-20 w-40 h-40 bg-white/10 rounded-full"></div>
        <div class="absolute -bottom-10 -right-10 w-20 h-20 bg-white/10 rounded-full"></div>

        <div class="text-5xl mb-6 animate-float">🚀</div>
        <h2 class="text-4xl font-bold mb-4 gradient-text-white">加入番茄书城</h2>
        <p class="text-xl mb-8 text-white/90">
          创建您的账户，开启精彩阅读之旅，成为我们不断成长的阅读社区的一员。
        </p>

        <!-- 会员权益 -->
        <div class="space-y-6">
          <div v-for="(benefit, index) in benefits" :key="index" class="flex items-start animate-slide-up"
            :style="`animation-delay: ${0.2 + index * 0.1}s`">
            <div class="bg-white/20 rounded-full p-2 mr-4 flex-shrink-0">
              <el-icon class="text-white" size="18">
                <component :is="benefit.icon" />
              </el-icon>
            </div>
            <div>
              <h3 class="font-semibold text-lg mb-1">{{ benefit.title }}</h3>
              <p class="text-white/80 text-sm">{{ benefit.description }}</p>
            </div>
          </div>
        </div>

        <!-- 装饰图形 -->
        <div class="absolute top-1/4 right-6 opacity-10 rotate-12 animate-spin-slow">
          <svg width="150" height="150" viewBox="0 0 100 100" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="50" cy="50" r="40" stroke="white" stroke-width="3" />
            <rect x="25" y="25" width="50" height="50" rx="8" stroke="white" stroke-width="3" />
            <path d="M25 75L75 25" stroke="white" stroke-width="3" />
          </svg>
        </div>
      </div>
    </div>

    <!-- 左侧注册表单区域 -->
    <div class="w-full md:w-1/2 flex items-center justify-center p-6 md:p-10 animate-fade-in bg-white">
      <div class="w-full max-w-md">
        <div class="text-center mb-8">
          <div class="inline-block mb-6">
            <div class="animate-pulse-custom">
              <div
                class="flex items-center justify-center w-20 h-20 mx-auto bg-tomato-100 text-tomato-600 rounded-full text-4xl shadow-md">
                🍅
              </div>
            </div>
          </div>
          <h2 class="text-3xl font-bold text-gray-900 mb-2 heading-accent">创建账号</h2>
          <p class="text-gray-600">成为番茄书城的一员，探索阅读的乐趣</p>
        </div>

        <!-- 进度步骤 -->
        <div class="mb-8">
          <el-steps :active="activeStep" finish-status="success">
            <el-step title="账号信息"></el-step>
            <el-step title="个人资料"></el-step>
            <el-step title="完成"></el-step>
          </el-steps>
        </div>

        <div v-if="activeStep === 1" class="animate-fade-in">
          <el-form ref="accountFormRef" :model="registerForm" :rules="accountRules" label-position="top">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="registerForm.username" placeholder="设置您的用户名" :prefix-icon="User" />
            </el-form-item>

            <el-form-item label="密码" prop="password">
              <el-input v-model="registerForm.password" type="password" placeholder="设置登录密码" :prefix-icon="Lock"
                show-password @input="checkPasswordStrength" />
              <div class="mt-1 flex items-center">
                <span class="text-xs text-gray-500 mr-2">密码强度:</span>
                <el-progress :percentage="passwordStrength.percentage" :status="passwordStrength.status"
                  :stroke-width="6" :show-text="false" class="flex-grow" />
                <span class="text-xs ml-2" :class="{
                  'text-danger': passwordStrength.status === 'exception',
                  'text-warning': passwordStrength.status === 'warning',
                  'text-success': passwordStrength.status === 'success'
                }">
                  {{ passwordStrength.status === 'exception' ? '弱' : passwordStrength.status === 'warning' ? '中' : '强'
                  }}
                </span>
              </div>
            </el-form-item>

            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="registerForm.confirmPassword" type="password" placeholder="再次输入密码" :prefix-icon="Lock"
                show-password />
            </el-form-item>

            <div class="flex justify-end mt-4">
              <el-button type="primary" @click="nextStep">下一步</el-button>
            </div>
          </el-form>
        </div>

        <div v-else-if="activeStep === 2" class="animate-fade-in">
          <!-- 第二步：个人资料 -->
          <el-form ref="profileFormRef" :model="registerForm" :rules="profileRules" label-position="top">
            <el-form-item label="电子邮箱" prop="email">
              <el-input v-model="registerForm.email" placeholder="您的电子邮箱地址" :prefix-icon="Message" />
            </el-form-item>

            <el-form-item label="手机号码" prop="phone">
              <el-input v-model="registerForm.phone" placeholder="您的手机号码" :prefix-icon="Phone" />
            </el-form-item>

            <el-form-item>
              <div class="flex items-center mb-4">
                <el-checkbox v-model="agreeTerms" label="同意" size="large" />
                <span class="ml-2 text-gray-700">
                  我已阅读并同意
                  <a href="#" class="text-tomato-600 hover:text-tomato-800">服务条款</a>
                  和
                  <a href="#" class="text-tomato-600 hover:text-tomato-800">隐私政策</a>
                </span>
              </div>
            </el-form-item>

            <div class="flex justify-between mt-4">
              <el-button @click="prevStep">返回</el-button>
              <el-button type="primary" @click="submitRegistration" :disabled="!agreeTerms">注册</el-button>
            </div>
          </el-form>
        </div>

        <div v-else class="text-center py-4 animate-fade-in">
          <!-- 第三步：完成 -->
          <div class="mb-6">
            <el-icon class="text-tomato-500" size="64">
              <CircleCheck />
            </el-icon>
          </div>

          <h3 class="text-2xl font-bold text-gray-800 mb-3">注册成功！</h3>
          <p class="text-gray-600 mb-6">恭喜您，您的账号已创建成功。</p>

          <div class="mt-6">
            <confetti-button type="primary" size="large" @click="goToLogin">前往登录</confetti-button>
          </div>
        </div>

        <div class="text-center mt-6 text-gray-600 text-sm">
          已有账号？
          <router-link :to="Routes.USER_LOGIN" class="text-tomato-600 hover:text-tomato-800 font-medium">
            立即登录
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import ConfettiButton from "@/components/shared/ConfettiButton.vue";
import { Routes } from "@/constants/routes";
import { useUserStore } from "@/stores/user";
import {
  CircleCheck,
  Discount,
  Lock,
  Message,
  Phone,
  // Gift icon is not available, using Present instead
  Present,
  Reading,
  User,
} from "@element-plus/icons-vue";
import type { FormInstance, FormRules } from "element-plus";
import { ElMessage } from "element-plus";
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const userStore = useUserStore();
const activeStep = ref(1);
const agreeTerms = ref(false);

const accountFormRef = ref<FormInstance>();
const profileFormRef = ref<FormInstance>();

const registerForm = reactive({
  username: "",
  password: "",
  confirmPassword: "",
  email: "",
  phone: "",
});

// 会员权益数据
const benefits = [
  {
    icon: Reading,
    title: "无限阅读",
    description:
      "注册会员可以浏览书城内所有图书的详细信息，获取更专业的阅读推荐。",
  },
  {
    icon: Discount,
    title: "会员折扣",
    description:
      "享受图书独家优惠价格，参与限时特价活动，比普通渠道至少低10%。",
  },
  {
    icon: Present,
    title: "生日礼遇",
    description: "会员生日当月可获赠精美书签和生日优惠券，享受会员专属礼遇。",
  },
];

// 验证两次输入的密码是否一致
const validateConfirmPassword = (rule: any, value: string, callback: any) => {
  if (value === "") {
    callback(new Error("请再次输入密码"));
  } else if (value !== registerForm.password) {
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

// 表单验证规则 - 账号信息
const accountRules: FormRules = {
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
};

// 表单验证规则 - 个人资料
const profileRules: FormRules = {
  email: [
    { required: true, message: "请输入邮箱", trigger: "blur" },
    { type: "email", message: "请输入有效的邮箱地址", trigger: "blur" },
  ],
  phone: [
    { required: true, message: "请输入手机号", trigger: "blur" },
    { validator: validatePhone, trigger: "blur" },
  ],
};

// 密码强度状态对象
const passwordStrength = reactive({
  percentage: 0,
  status: "exception" as "success" | "exception" | "warning",
});

// 检查密码强度的函数
const checkPasswordStrength = () => {
  const password = registerForm.password;

  if (!password) {
    passwordStrength.percentage = 0;
    passwordStrength.status = "exception";
    return;
  }

  // 密码强度评估标准
  let strength = 0;

  // 长度检查
  if (password.length >= 6) strength += 20;
  if (password.length >= 10) strength += 10;

  // 复杂度检查
  if (/[A-Z]/.test(password)) strength += 20; // 大写字母
  if (/[a-z]/.test(password)) strength += 15; // 小写字母
  if (/[0-9]/.test(password)) strength += 15; // 数字
  if (/[^A-Za-z0-9]/.test(password)) strength += 20; // 特殊字符

  // 设置强度状态
  passwordStrength.percentage = Math.min(100, strength);

  if (strength < 40) {
    passwordStrength.status = "exception";
  } else if (strength < 70) {
    passwordStrength.status = "warning";
  } else {
    passwordStrength.status = "success";
  }
};

// 下一步
const nextStep = async () => {
  if (!accountFormRef.value) return;

  await accountFormRef.value.validate((valid) => {
    if (valid) {
      activeStep.value++;
    }
  });
};

// 上一步
const prevStep = () => {
  activeStep.value--;
};

// 提交注册
const submitRegistration = async () => {
  if (!profileFormRef.value) return;

  await profileFormRef.value.validate(async (valid) => {
    if (valid) {
      if (!agreeTerms.value) {
        ElMessage.warning("请先同意服务条款和隐私政策");
        return;
      }

      try {
        // 去掉确认密码字段
        const { confirmPassword, ...registerData } = registerForm;

        const success = await userStore.register(registerData);

        if (success) {
          activeStep.value++;
        }
      } catch (error) {
        console.error("注册失败:", error);
      }
    }
  });
};

// 前往登录页
const goToLogin = () => {
  router.push(Routes.USER_LOGIN);
};
</script>

<style scoped>
/* 渐变文本颜色 - 白色版本 */
.gradient-text-white {
  background: linear-gradient(to right, white, #ffc9c9);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  display: inline-block;
}

/* 自定义元素动画 */
.animate-slide-up {
  opacity: 0;
  animation: slideUp 0.5s ease-out forwards;
}

/* 粒子背景的CSS替代方案 */
.particles-bg {
  background-image: radial-gradient(circle at 15% 50%, rgba(255, 255, 255, 0.3) 0%, transparent 20%),
    radial-gradient(circle at 85% 30%, rgba(255, 255, 255, 0.3) 0%, transparent 20%),
    radial-gradient(circle at 50% 80%, rgba(255, 255, 255, 0.3) 0%, transparent 20%),
    radial-gradient(circle at 30% 20%, rgba(255, 255, 255, 0.3) 0%, transparent 20%),
    radial-gradient(circle at 70% 60%, rgba(255, 255, 255, 0.3) 0%, transparent 20%);
  overflow: hidden;
}

.particles-bg::before,
.particles-bg::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at center, rgba(255, 255, 255, 0.2) 0%, transparent 60%);
  animation: pulse 8s infinite alternate;
}

.particles-bg::after {
  animation-delay: 4s;
}

@keyframes pulse {
  0% {
    opacity: 0.2;
    transform: scale(0.8);
  }

  100% {
    opacity: 0.5;
    transform: scale(1.2);
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 避免步骤条的内部样式影响 */
:deep(.el-steps) {
  --el-text-color-secondary: #64748b;
}

:deep(.el-step__title.is-process) {
  font-weight: 600;
  color: var(--el-color-primary);
}

:deep(.el-step__title.is-wait) {
  color: #94a3b8;
}
</style>
