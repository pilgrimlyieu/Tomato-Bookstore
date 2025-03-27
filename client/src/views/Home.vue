<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import { userInfo, userInfoUpdate } from '@/api/user'
import { ElMessage } from 'element-plus'

onMounted(() => {
  userInfo().then(res => {
    if (res.data.code === '000') {
      name.value = res.data.name
      tel.value = res.data.phone
      address.value = res.data.address
      regTime.value = res.data.createTime
    } else {
      ElMessage.error('获取用户信息失败')
    }
  })
})

const authStore = useAuthStore()
const router = useRouter()

const name = ref(authStore.username || 'Tomato')
const tel = ref('138****8888')
const address = ref('江苏省南京市鼓楼区')
const regTime = ref('2024-01-01')
const newName = ref(name.value)
const newAddress = ref(address.value)

const editing = ref(false)

const password = ref('')
const confirmPassword = ref('')

const hasConfirmPasswordInput = computed(() => confirmPassword.value !== '')
const isPasswordIdentical = computed(() => password.value === confirmPassword.value)
const changeDisabled = computed(() => !hasConfirmPasswordInput.value || !isPasswordIdentical.value)

function logout() {
  authStore.logout()
  router.push('/login')
}

function updateInfo() {
  name.value = newName.value
  address.value = newAddress.value
  editing.value = false
  alert('信息更新成功！')
}

function updatePassword() {
  if (changeDisabled.value) return
  password.value = ''
  confirmPassword.value = ''
  alert('密码修改成功，请重新登录')
  logout()
}
</script>
<template>
  <el-container style="width: 100vw; height: 100vh; position: relative;background: url('/cartoon-tomatoes.png') repeat; background-size: 100% 100%">
    <!-- 背景图层 -->
    <div
      class="h-screen"
      style="width: 800px;background: linear-gradient(to right bottom, #fff5f5, #ffe3e3);"
    >
    <!-- 内容层 -->
    <el-main class="flex items-center justify-center">
      <div
        class="w-[800px] h-[90%] bg-white/90 backdrop-blur-lg rounded-3xl shadow-2xl p-10 overflow-auto ring-1 ring-white/30"
      >
        <!-- 欢迎栏 -->
        <div class="flex justify-between items-center mb-6">
          <h1 class="text-2xl font-bold text-tomato-600">欢迎回来，{{ name }}</h1>
          <button @click="logout" class="text-white bg-red-500 hover:bg-red-600 px-4 py-1 rounded-lg">
            退出登录
          </button>
        </div>

        <!-- 信息展示 + 编辑 -->
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div>
            <p class="mb-1 text-gray-600 font-semibold">手机号：</p>
            <p class="mb-4">{{ tel }}</p>

            <p class="mb-1 text-gray-600 font-semibold">地址：</p>
            <p class="mb-4">{{ address }}</p>

            <p class="mb-1 text-gray-600 font-semibold">注册时间：</p>
            <p>{{ regTime }}</p>
          </div>

          <!-- 编辑信息 -->
          <div v-if="editing">
            <label class="block mb-2 text-gray-700 font-semibold">昵称</label>
            <input v-model="newName" class="w-full border rounded px-3 py-2 mb-4" />

            <label class="block mb-2 text-gray-700 font-semibold">地址</label>
            <input v-model="newAddress" class="w-full border rounded px-3 py-2 mb-4" />

            <button @click="updateInfo" class="bg-green-500 hover:bg-green-600 text-white px-4 py-2 rounded">
              保存修改
            </button>
          </div>

          <!-- 修改按钮 -->
          <div v-else>
            <button @click="editing = true" class="bg-blue-500 hover:bg-blue-600 text-white px-4 py-2 rounded">
              修改信息
            </button>
          </div>
        </div>

        <!-- 修改密码部分 -->
        <div class="mt-8">
          <h2 class="text-lg font-semibold text-gray-700 mb-4">修改密码</h2>
          <input
            type="password"
            v-model="password"
            placeholder="新密码"
            class="w-full border rounded px-3 py-2 mb-2"
          />
          <input
            type="password"
            v-model="confirmPassword"
            placeholder="确认密码"
            class="w-full border rounded px-3 py-2 mb-4"
            :class="{ 'border-red-500': hasConfirmPasswordInput && !isPasswordIdentical }"
          />
          <button
            :disabled="changeDisabled"
            @click="updatePassword"
            class="bg-yellow-500 hover:bg-yellow-600 disabled:opacity-50 text-white px-4 py-2 rounded"
          >
            修改密码
          </button>
        </div>
      </div>
    </el-main>
    </div>

  </el-container>
</template>

<style scoped>
.text-tomato-600 {
  color: #fa5252;
}
</style>
