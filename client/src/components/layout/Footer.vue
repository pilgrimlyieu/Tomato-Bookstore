<template>
  <footer class="bg-gray-100 py-8 mt-12">
    <div class="container mx-auto px-4">
      <div class="grid grid-cols-1 md:grid-cols-4 gap-8">
        <!-- 网站信息 -->
        <div>
          <h3 class="text-lg font-bold text-gray-800 mb-4">{{ appConfig.APP_NAME }}</h3>
          <p class="text-gray-600 mb-4">
            {{ appConfig.APP_DESCRIPTION }}
          </p>
          <div class="flex space-x-4">
            <a href="#" class="text-gray-500 hover:text-tomato-600">
              <i class="fab fa-facebook"></i>
            </a>
            <a href="#" class="text-gray-500 hover:text-tomato-600">
              <i class="fab fa-twitter"></i>
            </a>
            <a href="#" class="text-gray-500 hover:text-tomato-600">
              <i class="fab fa-instagram"></i>
            </a>
          </div>
        </div>

        <!-- 快速链接 -->
        <div>
          <h3 class="text-lg font-bold text-gray-800 mb-4">快速链接</h3>
          <ul class="space-y-2">
            <li>
              <router-link :to="Routes.HOME" class="text-gray-600 hover:text-tomato-600">首页</router-link>
            </li>
            <li>
              <router-link :to="Routes.PRODUCT_LIST" class="text-gray-600 hover:text-tomato-600">
                全部商品
              </router-link>
            </li>
          </ul>
        </div>

        <!-- 用户帮助 -->
        <div>
          <h3 class="text-lg font-bold text-gray-800 mb-4">用户帮助</h3>
          <ul class="space-y-2">
            <li>
              <router-link :to="Routes.USER_PROFILE" class="text-gray-600 hover:text-tomato-600">
                个人中心
              </router-link>
            </li>
            <li>
              <router-link :to="Routes.ORDER_LIST" class="text-gray-600 hover:text-tomato-600">订单查询</router-link>
            </li>
          </ul>
        </div>

        <!-- 联系信息 -->
        <div>
          <h3 class="text-lg font-bold text-gray-800 mb-4">联系我们</h3>
          <ul class="space-y-2">
            <li class="flex items-start">
              <el-icon class="mt-1 mr-2"><Location /></el-icon>
              <span class="text-gray-600">{{ appConfig.CONTACT_ADDRESS }}</span>
            </li>
            <li class="flex items-center">
              <el-icon class="mr-2"><Phone /></el-icon>
              <span class="text-gray-600">{{ appConfig.CONTACT_PHONE }}</span>
            </li>
            <li class="flex items-center">
              <el-icon class="mr-2"><Message /></el-icon>
              <span class="text-gray-600">{{ appConfig.CONTACT_EMAIL }}</span>
            </li>
          </ul>
        </div>
      </div>

      <div class="mt-8 pt-6 border-t border-gray-200">
        <p class="text-center text-gray-500 text-sm">
          © {{ appConfig.COPYRIGHT_YEAR }} {{ appConfig.APP_NAME }} 保留所有权利
        </p>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { AppConfig } from "@/constants/config";
import { Routes } from "@/constants/routes";
import { Location, Message, Phone } from "@element-plus/icons-vue";

const appConfig = AppConfig;
</script>
