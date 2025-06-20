import type { FormItemRule, FormRules } from "element-plus";

/**
 * 验证两次输入的密码是否一致
 *
 * @param {() => string} getPasswordValue 获取原始密码值的函数
 * @returns {Function} 验证函数
 */
export const validateConfirmPassword = (getPasswordValue: () => string) => {
  return (_: any, value: string, callback: any) => {
    if (value === "") {
      callback(new Error("请再次输入密码"));
    } else if (value !== getPasswordValue()) {
      callback(new Error("两次输入的密码不一致"));
    } else {
      callback();
    }
  };
};

/**
 * 验证手机号格式（中国大陆手机号）
 *
 * 手机号格式：11 位数字，以 1 开头，第二位数字为 3-9 之间的任意数字
 *
 * @param {any} _ 当前表单项
 * @param {string} value 输入的手机号
 * @param {Function} callback 回调函数
 * @returns {void}
 */
export const validatePhone = (
  _: any,
  value: string,
  callback: (error?: Error) => void,
) => {
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
 *
 * @description 密码强度评估标准：
 * 1. 长度：6-50 个字符
 * 2. 包含大写字母、小写字母、数字和特殊字符
 * 3. 强度分为三个等级：成功、警告和异常
 * 4. 成功：70% 以上，警告：40%-70%，异常：40% 以下
 *
 * @param {string} password 输入的密码
 * @returns {Object} 包含密码强度百分比和状态的对象
 * @property {number} percentage 密码强度百分比
 * @property {string} status 密码强度状态（成功、警告、异常）
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
 *
 * @param {() => string} getPasswordValue 获取原始密码值的函数
 * @returns {FormItemRule[]} 确认密码验证规则数组
 */
export const confirmPasswordRules = (
  getPasswordValue: () => string,
): FormItemRule[] => [
  { required: true, message: "请再次输入密码", trigger: "blur" },
  { validator: validateConfirmPassword(getPasswordValue), trigger: "blur" },
];

/**
 * 评分验证规则
 */
export const ratingRules: FormItemRule[] = [
  { required: true, message: "请选择评分", trigger: "change" },
  {
    type: "number",
    min: 0,
    max: 10,
    message: "评分范围为 0-10 分",
    trigger: "change",
  },
];

/**
 * 评论内容验证规则
 */
export const commentContentRules: FormItemRule[] = [
  { required: true, message: "请输入评论内容", trigger: "blur" },
  {
    min: 2,
    max: 500,
    message: "评论长度应在 2 到 500 个字符之间",
    trigger: "blur",
  },
];

/**
 * 长评论内容验证规则（用于评价等场景）
 */
export const longCommentContentRules: FormItemRule[] = [
  { max: 1000, message: "评论内容不能超过 1000 个字符", trigger: "blur" },
];

/**
 * 笔记标题验证规则
 */
export const noteTitleRules: FormItemRule[] = [
  { required: true, message: "请输入笔记标题", trigger: "blur" },
  {
    min: 2,
    max: 50,
    message: "标题长度应在 2 到 50 个字符之间",
    trigger: "blur",
  },
];

/**
 * 笔记内容验证规则
 */
export const noteContentRules: FormItemRule[] = [
  { required: true, message: "请输入笔记内容", trigger: "blur" },
  {
    min: 10,
    max: 2000,
    message: "内容长度应在 10 到 2000 个字符之间",
    trigger: "blur",
  },
];

/**
 * 广告标题验证规则
 */
export const advertisementTitleRules: FormItemRule[] = [
  { required: true, message: "请输入广告标题", trigger: "blur" },
  { max: 50, message: "广告标题不能超过 50 个字符", trigger: "blur" },
];

/**
 * 广告内容验证规则
 */
export const advertisementContentRules: FormItemRule[] = [
  { required: true, message: "请输入广告内容", trigger: "blur" },
  { max: 500, message: "广告内容不能超过 500 个字符", trigger: "blur" },
];

/**
 * 广告图片 URL 验证规则
 */
export const advertisementImageUrlRules: FormItemRule[] = [
  {
    required: true,
    message: "请上传广告图片或输入图片 URL",
    trigger: "blur",
  },
  { max: 500, message: "图片 URL 不能超过 500 个字符", trigger: "blur" },
];

/**
 * 广告关联商品 ID 验证规则
 */
export const advertisementProductIdRules: FormItemRule[] = [
  { required: true, message: "请输入关联商品 ID", trigger: "blur" },
  { type: "number", min: 1, message: "商品 ID 必须大于 0", trigger: "blur" },
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
export const getRegisterRules = (getPasswordValue: () => string): FormRules => {
  return {
    username: usernameRules,
    password: passwordRules,
    confirmPassword: confirmPasswordRules(getPasswordValue),
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
export const getChangePasswordRules = (getNewPasswordValue: () => string): FormRules => {
  return {
    newPassword: passwordRules,
    confirmPassword: confirmPasswordRules(getNewPasswordValue),
  };
};

/**
 * 商品名称验证规则
 */
export const titleRules: FormItemRule[] = [
  { required: true, message: "请输入商品名称", trigger: "blur" },
  {
    min: 1,
    max: 100,
    message: "长度应在 1 到 100 个字符之间",
    trigger: "blur",
  },
];

/**
 * 商品价格验证规则
 */
export const priceRules: FormItemRule[] = [
  { required: true, message: "请输入商品价格", trigger: "blur" },
  { type: "number", min: 0, message: "价格不能小于 0", trigger: "blur" },
];

/**
 * 商品评分验证规则
 */
export const rateRules: FormItemRule[] = [
  { required: true, message: "请设置商品评分", trigger: "change" },
  {
    type: "number",
    min: 0,
    max: 10,
    message: "评分应在 0 到 10 之间",
    trigger: "blur",
  },
];

/**
 * 商品规格验证规则生成函数
 *
 * @param {number} index 规格项的索引
 * @returns {Object} 包含 item 和 value 验证规则的对象
 */
export const getSpecificationRules = (index: number) => {
  return {
    [`specifications.${index}.item`]: [
      { required: true, message: "请输入规格名称", trigger: "blur" },
    ],
    [`specifications.${index}.value`]: [
      { required: true, message: "请输入规格值", trigger: "blur" },
    ],
  };
};

/**
 * 获取商品表单验证规则
 */
export const getProductRules = (): FormRules => {
  return {
    title: titleRules,
    price: priceRules,
    rate: rateRules,
    cover: [
      { max: 255, message: "URL 长度不能超过 255 个字符", trigger: "blur" },
    ],
    description: [
      { max: 500, message: "描述长度不能超过 500 个字符", trigger: "blur" },
    ],
    detail: [
      { max: 5000, message: "详情长度不能超过 5000 个字符", trigger: "blur" },
    ],
  };
};

/**
 * 收货地址验证规则
 */
export const shippingAddressRules: FormItemRule[] = [
  { required: true, message: "请输入收货地址", trigger: "blur" },
  { max: 200, message: "地址长度不能超过 200 个字符", trigger: "blur" },
];

/**
 * 获取结账表单验证规则
 */
export const getCheckoutRules = (): FormRules => {
  return {
    shippingAddress: shippingAddressRules,
  };
};

/**
 * 获取评价表单验证规则
 */
export const getReviewRules = (): FormRules => {
  return {
    rating: ratingRules,
    content: longCommentContentRules,
  };
};

/**
 * 获取笔记表单验证规则
 */
export const getNoteRules = (): FormRules => {
  return {
    title: noteTitleRules,
    content: noteContentRules,
  };
};

/**
 * 获取评论表单验证规则
 */
export const getCommentRules = (): FormRules => {
  return {
    content: commentContentRules,
  };
};

/**
 * 获取广告表单验证规则
 */
export const getAdvertisementRules = (): FormRules => {
  return {
    title: advertisementTitleRules,
    content: advertisementContentRules,
    imageUrl: advertisementImageUrlRules,
    productId: advertisementProductIdRules,
  };
};

/**
 * 获取注册表单分步验证规则
 */
export const getRegisterStepRules = (getPasswordValue: () => string) => {
  return {
    account: {
      username: usernameRules,
      password: passwordRules,
      confirmPassword: confirmPasswordRules(getPasswordValue),
    } as FormRules,
    profile: {
      email: emailRules,
      phone: phoneRules,
    } as FormRules,
  };
};
