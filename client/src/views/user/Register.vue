<template>
  <el-main class="flex items-center justify-center min-h-screen bg-repeat" style="background-image: url('/cartoon-tomatoes.png'); background-size: 100% 100%">
    <div
      class="h-screen"
      style="width: 800px;background: linear-gradient(to right bottom, #fff5f5, #ffe3e3);"
    >
    <div class="w-[60%] bg-pink-100 bg-opacity-80 backdrop-blur-md rounded-2xl shadow-xl p-10">
      <h1 class="text-3xl font-bold text-center text-red-500 mb-8" style="margin-left: 16px;">创建一个新的账户</h1>

      <el-form>
        <div class="grid grid-cols-12 gap-4">
          <div class="col-span-12 md:col-span-5">
            <el-form-item label="昵称" style="margin-left: 16px;">
              <el-input v-model="name" style="width: 300px; margin: 0 auto;" placeholder="请输入昵称" />
            </el-form-item>
          </div>

          <div class="col-span-12 md:col-span-5">
            <el-form-item label="身份" style="margin-left: 16px;">
              <el-select style="width: 300px; margin: 0 auto;" v-model="identity" placeholder="请选择" class="w-full">
                <el-option value="CUSTOMER" label="顾客" />
                <el-option value="STAFF" label="商家" />
              </el-select>
            </el-form-item>
          </div>

          <div class="col-span-12 md:col-span-5">
            <el-form-item :label="telLabel" style="margin-left: 16px;">
              <el-input style="width: 300px; margin: 0 auto;" v-model="tel" placeholder="请输入手机号" :class="{ 'border-red-500': hasTelInput && !telLegal }" />
            </el-form-item>
          </div>

          <div class="col-span-12 md:col-span-5">
            <el-form-item label="地址" style="margin-left: 16px;">
              <el-input  style="width: 300px; margin: 0 auto;" v-model="address" placeholder="请输入地址" />
            </el-form-item>
          </div>

          <div class="col-span-12 md:col-span-5" v-if="identity === 'STAFF'">
            <el-form-item label="所属商店" style="margin-left: 16px; width: 300px;">
              <el-select style="width: 300px; margin: 0 auto;" v-model="storeId" placeholder="请选择" class="w-full">
                <el-option v-for="store in storeList" :key="store.id" :label="store.name" :value="store.id" />
              </el-select>
            </el-form-item>
          </div>

          <div class="col-span-12 md:col-span-5">
            <el-form-item label="密码" style="margin-left: 16px;">
              <el-input type="password" style="width: 300px; margin: 0 auto;" v-model="password" placeholder="请输入密码" />
            </el-form-item>
          </div>

          <div class="col-span-12 md:col-span-5">
            <el-form-item style="margin-left: 16px;">
              <label v-if="!hasConfirmPasswordInput">确认密码</label>
              <label v-else-if="!isPasswordIdentical" class="text-red-500">密码不一致</label>
              <label v-else>确认密码</label>
              <el-input type="password" style="width: 300px; margin: 0 auto;" v-model="confirmPassword" placeholder="请再次输入密码" :class="{ 'border-red-500': hasConfirmPasswordInput && !isPasswordIdentical } " />
            </el-form-item>
          </div>
        </div>

        <div
          class="flex justify-end space-x-4 mt-6"
          style="margin-left: 16px; gap: 16px;">
          <el-button type="primary" :disabled="registerDisabled" @click="handleRegister" style="margin-right: 16px;">创建账户</el-button>

          <RouterLink to="/login">
            <el-button>去登录</el-button>
          </RouterLink>
        </div>
      </el-form>
    </div>
    </div>
  </el-main>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const name = ref('')
const identity = ref('')
const tel = ref('')
const address = ref('')
const password = ref('')
const confirmPassword = ref('')

const storeList = ref([
  { id: 1, name: '商店A' },
  { id: 2, name: '商店B' },
  { id: 3, name: '商店C' }
])
const storeId = ref()

const hasTelInput = computed(() => tel.value !== '')
const hasPasswordInput = computed(() => password.value !== '')
const hasConfirmPasswordInput = computed(() => confirmPassword.value !== '')
const hasAddressInput = computed(() => address.value !== '')
const hasIdentityChosen = computed(() => identity.value !== '')
const hasStoreName = computed(() => storeId.value !== undefined)
const chinaMobileRegex = /^1(3[0-9]|4[579]|5[0-35-9]|6[2567]|7[0-8]|8[0-9]|9[1589])\d{8}$/
const telLegal = computed(() => chinaMobileRegex.test(tel.value))
const isPasswordIdentical = computed(() => password.value === confirmPassword.value)

const telLabel = computed(() => {
  if (!hasTelInput.value) return '注册手机号'
  if (!telLegal.value) return '手机号不合法'
  return '注册手机号'
})

const registerDisabled = computed(() => {
  if (!hasIdentityChosen.value) return true
  if (identity.value === 'CUSTOMER') {
    return !(
      hasTelInput.value &&
      hasPasswordInput.value &&
      hasConfirmPasswordInput.value &&
      hasAddressInput.value &&
      telLegal.value &&
      isPasswordIdentical.value
    )
  }
  if (identity.value === 'STAFF') {
    return !(
      hasTelInput.value &&
      hasPasswordInput.value &&
      hasConfirmPasswordInput.value &&
      hasAddressInput.value &&
      hasStoreName.value &&
      telLegal.value &&
      isPasswordIdentical.value
    )
  }
  return true
})

const router = useRouter()
const handleRegister = () => {
  ElMessage.success('注册成功！')
  router.push('/login')
}
</script>

<style>
body {
  margin: 0;
}
</style>
