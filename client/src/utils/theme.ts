// 颜色主题工具

// 番茄主题色
export const COLORS = {
  primary: {
    50: "#fff5f5",
    100: "#ffe3e3",
    200: "#ffc9c9",
    300: "#ffa8a8",
    400: "#ff8787",
    500: "#ff6b6b", // 主色调
    600: "#fa5252",
    700: "#f03e3e",
    800: "#e03131",
    900: "#c92a2a",
    950: "#7d0b0b",
  },
  gray: {
    50: "#f9fafb",
    100: "#f3f4f6",
    200: "#e5e7eb",
    300: "#d1d5db",
    400: "#9ca3af",
    500: "#6b7280",
    600: "#4b5563",
    700: "#374151",
    800: "#1f2937",
    900: "#111827",
  },
};

// 设置 CSS 变量主题
export const setCSSVariables = () => {
  const root = document.documentElement;

  // 设置主色调变量
  Object.entries(COLORS.primary).forEach(([key, value]) => {
    root.style.setProperty(`--color-primary-${key}`, value);
  });

  // 设置灰度色变量
  Object.entries(COLORS.gray).forEach(([key, value]) => {
    root.style.setProperty(`--color-gray-${key}`, value);
  });

  // 设置 Element Plus 变量
  root.style.setProperty("--el-color-primary", COLORS.primary[500]);
  root.style.setProperty("--el-color-primary-light-3", COLORS.primary[400]);
  root.style.setProperty("--el-color-primary-light-5", COLORS.primary[300]);
  root.style.setProperty("--el-color-primary-light-7", COLORS.primary[200]);
  root.style.setProperty("--el-color-primary-light-8", COLORS.primary[100]);
  root.style.setProperty("--el-color-primary-light-9", COLORS.primary[50]);
  root.style.setProperty("--el-color-primary-dark-2", COLORS.primary[600]);
};

// 获取 CSS 变量值
export const getCSSVariable = (varName: string): string => {
  return getComputedStyle(document.documentElement)
    .getPropertyValue(varName)
    .trim();
};

// 应用圆角风格
export const applyRoundedStyle = (radius: "sm" | "md" | "lg" | "xl" = "lg") => {
  const radiusValues = {
    sm: "0.375rem", // 6px
    md: "0.5rem", // 8px
    lg: "0.75rem", // 12px
    xl: "1rem", // 16px
  };

  const root = document.documentElement;
  root.style.setProperty("--el-border-radius-base", radiusValues[radius]);

  // 应用到 Element Plus 组件
  document.body.classList.remove(
    "rounded-sm",
    "rounded-md",
    "rounded-lg",
    "rounded-xl",
  );
  document.body.classList.add(`rounded-${radius}`);
};

// 导出默认方法
export default {
  COLORS,
  setCSSVariables,
  getCSSVariable,
  applyRoundedStyle,
};
