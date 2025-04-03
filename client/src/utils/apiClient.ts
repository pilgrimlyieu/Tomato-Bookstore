import { ErrorMessages } from "@/constants/errorCode";
import { Routes } from "@/constants/routes";
import { useUserStore } from "@/stores/user";
import { HttpStatusCode } from "axios";
import axios from "axios";
import { ElMessage } from "element-plus";

// 创建 axios 实例
const apiClient = axios.create({
  baseURL:
    import.meta.env.ENABLE_MOCK === "true"
      ? import.meta.env.MOCK_API_BASE_URL
      : import.meta.env.VITE_API_BASE_URL,
  timeout: 10000,
  headers: {
    "Content-Type": "application/json",
  },
});

// 请求拦截器 - 添加 token
apiClient.interceptors.request.use(
  (config) => {
    const userStore = useUserStore();
    if (userStore.token) {
      config.headers.Authorization = `Bearer ${userStore.token}`;
    }
    return config;
  },
  (error) => {
    console.error("请求错误：", error);
    return Promise.reject(error);
  },
);

// 响应拦截器 - 处理错误和 token 过期
apiClient.interceptors.response.use(
  (response) => {
    return response.data;
  },
  (error) => {
    if (error.response) {
      const { status, data } = error.response;

      // 处理 401 未授权错误（token 过期或无效）
      if (status == HttpStatusCode.Unauthorized) {
        const userStore = useUserStore();
        userStore.logout();
        ElMessage.error("登录已过期，请重新登录");
        window.location.href = Routes.USER_LOGIN;
        return Promise.reject(error);
      }

      // 显示错误信息，优先使用业务错误码对应的消息
      const errorMsg =
        data?.message ||
        (data?.code ? ErrorMessages[data.code] : null) ||
        ErrorMessages[status] ||
        "请求失败";
      ElMessage.error(errorMsg);
    } else if (error.request) {
      ElMessage.error("网络请求失败，请检查网络连接");
    } else {
      ElMessage.error("请求异常");
    }

    return Promise.reject(error);
  },
);
export default apiClient;
