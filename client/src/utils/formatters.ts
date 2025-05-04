/**
 * 格式化日期
 *
 * @param {string | any[]} dateValue 日期值，可以是字符串或数组 [年, 月, 日, 时, 分, 秒]
 * @param {Intl.DateTimeFormatOptions} options 格式化选项，默认为中文格式
 * @returns {string} 格式化后的日期字符串，无效日期返回 "-"
 */
export const formatDate = (
  dateValue?: string | any[],
  options?: Intl.DateTimeFormatOptions,
): string => {
  if (!dateValue) {
    return "-";
  }

  // 默认格式化选项
  const defaultOptions: Intl.DateTimeFormatOptions = {
    year: "numeric",
    month: "numeric",
    day: "numeric",
    hour: "numeric",
    minute: "numeric",
    second: "numeric",
    ...options,
  };

  try {
    let date: Date;

    // 处理数组格式的日期 [年, 月, 日, 时, 分, 秒]
    if (Array.isArray(dateValue)) {
      const [year, month, day, hour = 0, minute = 0, second = 0] = dateValue;
      date = new Date(year, month - 1, day, hour, minute, second);
    } else {
      // 处理字符串格式的日期
      date = new Date(dateValue);
    }

    // 检查日期是否有效
    if (isNaN(date.getTime())) {
      return "-";
    }

    return new Intl.DateTimeFormat("zh-CN", defaultOptions).format(date);
  } catch (error) {
    console.error("日期格式化错误：", error);
    return "-";
  }
};

/**
 * 格式化价格
 *
 * @param {number | undefined} price 价格数值
 * @returns {string} 格式化后的价格字符串
 */
export const formatPrice = (price?: number): string => {
  if (price === undefined || price === null) {
    return "¥0.00";
  }
  return new Intl.NumberFormat("zh-CN", {
    style: "currency",
    currency: "CNY",
    minimumFractionDigits: price % 1 === 0 ? 0 : 2,
  }).format(price);
};
