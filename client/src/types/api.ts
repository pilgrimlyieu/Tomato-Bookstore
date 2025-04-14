import { BusinessErrorCode, ErrorMessages } from "@/constants/errorCode";
import { type HttpStatusCode } from "axios";

/**
 * API 响应类型
 */
export interface ApiResponse<T = object | null> {
  /**
   * 响应码
   */
  code: HttpStatusCode | BusinessErrorCode;

  /**
   * 响应信息
   */
  message: string;

  /**
   * 响应数据
   */
  data: T;
}

export { ErrorMessages };
