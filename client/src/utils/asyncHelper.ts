import type { ApiResponse } from "@/types/api";
import { HttpStatusCode } from "axios";
import { ElMessage } from "element-plus";

/**
 * 执行异步操作的辅助函数
 *
 * @template T 返回数据类型
 * @template R 最终结果类型
 * @param {() => Promise<ApiResponse<T>>} apiCall API 调用函数
 * @param {(data: T) => R} onSuccess 成功时的回调函数
 * @param {string} errorMessage 错误信息
 * @param {boolean} [showErrorMessage=true] 是否显示错误消息
 * @param {(state: any) => void} [beforeRequest] 请求前的回调函数
 * @param {(state: any) => void} [afterRequest] 请求后的回调函数
 * @param {number[]} [successCodes=[HttpStatusCode.Ok]] 成功状态码列表
 * @param {(data: T) => void} [successMessageFn] 成功消息函数
 * @returns {Promise<R | null>} 处理结果
 */
export async function performAsync<T, R = boolean>(
  apiCall: () => Promise<ApiResponse<T>>,
  onSuccess: (data: T) => R,
  errorMessage: string,
  showErrorMessage: boolean = true,
  beforeRequest?: (state: any) => void,
  afterRequest?: (state: any) => void,
  successCodes: number[] = [HttpStatusCode.Ok],
  successMessageFn?: (data: T) => string,
): Promise<R | null> {
  try {
    if (beforeRequest) {
      beforeRequest(true);
    }
    const response = await apiCall();

    if (successCodes.includes(response.code)) {
      if (successMessageFn) {
        ElMessage.success(successMessageFn(response.data));
      }
      return onSuccess(response.data);
    }
    return null;
  } catch (error) {
    console.error(errorMessage, error);
    if (showErrorMessage) {
      ElMessage.error(errorMessage);
    }
    return null;
  } finally {
    if (afterRequest) {
      afterRequest(false);
    }
  }
}

/**
 * 执行带有加载状态的异步操作
 *
 * @template T 返回数据类型
 * @param {object} state 状态对象
 * @param {string} loadingKey 加载状态的键名
 * @param {() => Promise<ApiResponse<T>>} apiCall API 调用函数
 * @param {(data: T) => void} onSuccess 成功时的回调函数
 * @param {string} errorMessage 错误信息
 * @param {boolean} [showErrorMessage=true] 是否显示错误消息
 * @param {number[]} [successCodes=[HttpStatusCode.Ok]] 成功状态码列表
 * @param {string} [successMessage] 成功提示消息
 * @returns {Promise<boolean>} 是否成功
 */
export async function performAsyncAction<T>(
  state: any,
  loadingKey: string,
  apiCall: () => Promise<ApiResponse<T>>,
  onSuccess: (data: T) => void,
  errorMessage: string,
  showErrorMessage: boolean = true,
  successCodes: number[] = [HttpStatusCode.Ok],
  successMessage?: string,
): Promise<boolean> {
  const result = await performAsync<T, boolean>(
    apiCall,
    (data) => {
      onSuccess(data);
      if (successMessage) {
        ElMessage.success(successMessage);
      }
      return true;
    },
    errorMessage,
    showErrorMessage,
    (loading) => (state[loadingKey] = loading),
    (loading) => (state[loadingKey] = loading),
    successCodes,
  );
  return result ?? false;
}
