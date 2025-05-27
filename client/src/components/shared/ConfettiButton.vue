<template>
  <div class="relative inline-block">
    <!-- 彩带效果 -->
    <div v-if="showConfetti" class="absolute top-0 left-1/2 transform -translate-x-1/2 -translate-y-1/2 z-10">
      <v-confetti :particles="200" :force="0.3" :stageHeight="600" :stageWidth="600" />
    </div>

    <!-- 实际按钮 -->
    <el-button :type="type" :size="size" :loading="loading" :disabled="disabled" :autofocus="autofocus" :round="round"
      :circle="circle" :plain="plain" :text="text" :link="link" :bg="bg" :color="color" v-bind="$attrs"
      @click="handleClick">
      <slot></slot>
    </el-button>
  </div>
</template>

<script setup lang="ts">

// 按钮属性，与 ElButton 一致
const props = defineProps({
  type: {
    type: String as PropType<
      | ""
      | "primary"
      | "success"
      | "warning"
      | "danger"
      | "info"
      | "default"
      | "text"
    >,
    default: "primary",
    validator: (val: string) =>
      [
        "primary",
        "success",
        "warning",
        "danger",
        "info",
        "default",
        "text",
      ].includes(val),
  },
  size: {
    type: String as PropType<"" | "large" | "default" | "small">,
    default: "default",
    validator: (val: string) => ["large", "default", "small"].includes(val),
  },
  loading: Boolean,
  disabled: Boolean,
  autofocus: Boolean,
  round: Boolean,
  circle: Boolean,
  plain: Boolean,
  text: Boolean,
  link: Boolean,
  bg: Boolean,
  color: String,

  // 彩带属性
  confettiDuration: {
    type: Number,
    default: 2000,
  },
  // 是否显示彩带
  enableConfetti: {
    type: Boolean,
    default: true,
  },
});

const emit = defineEmits(["click"]);

const showConfetti = ref(false);

// 处理点击
const handleClick = (event: MouseEvent) => {
  // 触发彩带效果
  if (props.enableConfetti && !props.disabled && !props.loading) {
    showConfetti.value = true;
    setTimeout(() => {
      showConfetti.value = false;
    }, props.confettiDuration);
  }

  // 传递事件
  emit("click", event);
};
</script>
