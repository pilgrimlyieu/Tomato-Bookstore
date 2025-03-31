import type { FormItemRule, FormRules } from "element-plus";

/**
 * 验证两次输入的密码是否一致
 * @param firstField 第一次输入密码的字段名
 */
export const validateConfirmPassword = (firstField: string = "password") => {
  return (_: any, value: string, callback: any) => {
    if (value === "") {
      callback(new Error("请再次输入密码"));
    } else if (value !== (_.form as any)[firstField]) {
      callback(new Error("两次输入的密码不一致"));
    } else {
      callback();
    }
  };
};

/**
 * 验证手机号格式（中国大陆手机号）
 */
export const validatePhone = (_: any, value: string, callback: any) => {
  if (!value) {
    callback();
    return;
  }

  const phoneRegex = /^1[3-9]\d{9}$/;
  if (!phoneRegex.test(value)) {
    callback(new Error("请输入有效的 11 位手机号"));
  } else {
    callback();
  }
};

/**
 * 验证密码强度
 * @returns 返回一个包含百分比和状态的对象
 */
export const checkPasswordStrength = (
  password: string,
): {
  percentage: number;
  status: "success" | "exception" | "warning";
} => {
  if (!password) {
    return { percentage: 0, status: "exception" };
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
  const percentage = Math.min(100, strength);
  let status: "success" | "exception" | "warning" = "exception";

  if (strength < 40) {
    status = "exception";
  } else if (strength < 70) {
    status = "warning";
  } else {
    status = "success";
  }

  return { percentage, status };
};

/**
 * 生成用户名验证规则
 */
export const usernameRules: FormItemRule[] = [
  { required: true, message: "请输入用户名", trigger: "blur" },
  {
    min: 3,
    max: 20,
    message: "用户名长度必须在 3-20 个字符之间",
    trigger: "blur",
  },
];

/**
 * 生成密码验证规则
 */
export const passwordRules: FormItemRule[] = [
  { required: true, message: "请输入密码", trigger: "blur" },
  {
    min: 6,
    max: 50,
    message: "密码长度必须在 6-50 个字符之间",
    trigger: "blur",
  },
];

/**
 * 生成邮箱验证规则
 */
export const emailRules: FormItemRule[] = [
  { required: true, message: "请输入邮箱", trigger: "blur" },
  {
    validator: (_, value, callback) => {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!value) {
        callback(new Error("请输入邮箱"));
      } else if (!emailRegex.test(value)) {
        callback(new Error("请输入有效的邮箱地址"));
      } else {
        callback();
      }
    },
    trigger: "blur",
  },
];

/**
 * 生成手机号验证规则
 */
export const phoneRules: FormItemRule[] = [
  { required: true, message: "请输入手机号", trigger: "blur" },
  { validator: validatePhone, trigger: "blur" },
];

/**
 * 生成确认密码验证规则
 * @param firstField 第一次输入密码的字段名
 */
export const confirmPasswordRules = (
  firstField: string = "password",
): FormItemRule[] => [
  { required: true, message: "请再次输入密码", trigger: "blur" },
  { validator: validateConfirmPassword(firstField), trigger: "blur" },
];

/**
 * 获取登录表单验证规则
 */
export const getLoginRules = (): FormRules => {
  return {
    username: usernameRules,
    password: passwordRules,
  };
};

/**
 * 获取注册表单验证规则
 */
export const getRegisterRules = (): FormRules => {
  return {
    username: usernameRules,
    password: passwordRules,
    confirmPassword: confirmPasswordRules(),
    email: emailRules,
    phone: phoneRules,
  };
};

/**
 * 获取用户资料表单验证规则
 */
export const getProfileRules = (): FormRules => {
  return {
    email: emailRules,
    phone: phoneRules,
    address: [
      { max: 100, message: "地址长度不能超过 100 个字符", trigger: "blur" },
    ],
  };
};

/**
 * 获取密码修改表单验证规则
 */
export const getChangePasswordRules = (): FormRules => {
  return {
    currentPassword: passwordRules,
    newPassword: passwordRules,
    confirmPassword: confirmPasswordRules("newPassword"),
  };
};
