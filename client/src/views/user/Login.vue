<template>
  <el-container direction="vertical" style="width: 100vw; height: 100vh; overflow: hidden;background: url('/cartoon-tomatoes.png') repeat; background-size: 100% 100%">
    <el-main
      style="
        flex: 1;
        display: flex;
        align-items: center;
        justify-content: center;
        background-color: transparent;
      "
    >
      <!-- 遮罩层 -->
      <div
        class="h-screen"
        style="width: 800px;background: linear-gradient(to right bottom, #fff5f5, #ffe3e3);"
      >
        <!-- 毛边羽化容器 -->
        <div
          class="rounded-[32px] p-[2px] mx-auto max-w-2xl space-y-6"
          style="
            background: radial-gradient(rgba(255, 255, 255, 0.5), rgba(255, 255, 255, 0.1));
            box-shadow: 0 0 30px rgba(255, 100, 100, 0.3);
            backdrop-filter: blur(6px);
            -webkit-backdrop-filter: blur(6px);
            min-height: 100vh;
          "
        >
          <!-- 登录框主体 -->
          <div class="w-screen h-screen flex items-center justify-center bg-pink-50">
          <div
            class="bg-white p-12 border rounded-[28px] shadow-lg"
            style="
              background: rgba(255, 255, 255, 0.7);
              backdrop-filter: blur(16px);
              -webkit-backdrop-filter: blur(16px);
              border-color: #ffe3e3;
              margin: 0 auto;
            "
          >
            <h2 class="w-fit mx-auto text-2xl font-bold text-[#fa5252] mb-6">
              登录账号
            </h2>



            <el-form :model="form" :rules="rules" ref="formRef" label-position="top" class="space-y-4">
              <el-form-item label="用户名" prop="username" style="margin: 0 auto;">
                <el-input v-model="form.username" placeholder="请输入用户名"  style="width: 300px; margin: 0 auto;" />
              </el-form-item>

              <el-form-item label="密码" prop="password" style="margin: 0 auto;">
                <el-input v-model="form.password" type="password" show-password placeholder="请输入密码"   style="width: 300px; margin: 0 auto;"/>
              </el-form-item>

              <el-form-item v-if="!loginSuccess"  style="margin-top: 16px;">
                <button
                  class="w-full text-white font-semibold py-2 rounded-md transition duration-200"
                  style="background-color: #ff6b6b;margin: 0 auto;"
                  @mouseover="hover = true"
                  @mouseleave="hover = false"
                  :style="{ backgroundColor: hover ? '#fa5252' : '#ff6b6b' }"
                  @click="handleLogin"
                >
                  登录
                </button>
              </el-form-item>
            </el-form>

            <p class="text-sm text-center text-gray-500 mt-4">
              没有账号？
              <RouterLink
                to="/register"
                class="hover:underline"
                style="color: #fa5252;"
              >
                点击注册
              </RouterLink>
            </p>
            <!-- 跳过判断直接进入首页按钮 -->
            <button
              v-if="loginSuccess"
              class="mt-4 w-full bg-gray-300 hover:bg-gray-400 text-black font-semibold py-2 rounded-md transition duration-200"
              style="margin: 0 auto;"
              @click="skipToHome"
            >
              🧪 进入首页
            </button>

          </div>
        </div>
      </div>
      </div>
    </el-main>
  </el-container>
</template>

<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router' // ✅ 加 useRoute
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'  // ✅ 引入 Pinia store
import { userLogin } from '@/api/user'

const loginSuccess = ref(false) // 默认未登录成功

const router = useRouter()
// ✅ 使用 store
const route = useRoute()
const formRef = ref()
const form = ref({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const hover = ref(false)
const skipToHome = () => {
  router.push('/Home') // ✅ 注意大小写必须和你的路由路径一致！
}
const fakeUser = {
  username: '1',
  password: '1'
}
const useFakeLogin = true
const handleLogin = () => {
  formRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    const { username, password } = form.value
    const authStore = useAuthStore()
    // 判断是否为 fakeUser
    const isValidUser = username === fakeUser.username && password === fakeUser.password

  if (useFakeLogin){
    if (isValidUser) {
      authStore.login(username)
      ElMessage.success('登录成功')

      loginSuccess.value = true // ✅ 切换显示按钮
    } else {
      ElMessage.error('未注册的用户！')
      loginSuccess.value = false // ✅ 确保保持隐藏
    }
  }else {
    userLogin({ phone: username, password }).then(res => {
      if (res.data.code === '000') {
        authStore.login(username) // 或使用 res.data.username
        ElMessage.success('登录成功')
        router.push('/Home')
      } else {
        ElMessage.error(res.data.msg || '登录失败')
      }
    }).catch(() => {
      ElMessage.error('网络异常，请稍后重试')
    })
  }

  })
}

// const handleLogin = () => {
//   formRef.value.validate(async (valid: boolean) => {
//     if (!valid) return
//
//     // 假设登录成功
//     const success = true
//
//     if (success) {
//       const authStore = useAuthStore()
//       authStore.login(form.value.username)
//       console.log('[登录] 登录成功，isLoggedIn:', authStore.isLoggedIn)
//       ElMessage.success('登录成功')
//
//       await nextTick()
//
//       // ✅ 根据 query.redirect 判断跳转去哪
//       await router.push('/Home')
//
//     } else {
//       ElMessage.error('用户名或密码错误')
//     }
//   })
// }
</script>

<style>
html,
body,
#app {
  margin: 0;
  padding: 0;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}
</style>
