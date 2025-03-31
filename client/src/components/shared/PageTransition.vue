<template>
  <transition :name="name" :mode="mode" :appear="appear" @before-enter="beforeEnter" @enter="enter"
    @after-enter="afterEnter" @enter-cancelled="enterCancelled" @before-leave="beforeLeave" @leave="leave"
    @after-leave="afterLeave" @leave-cancelled="leaveCancelled">
    <slot />
  </transition>
</template>

<script setup lang="ts">
import { gsap } from "gsap";

const props = withDefaults(
  defineProps<{
    name?: string;
    mode: "out-in" | "in-out" | "default";
    appear?: boolean;
    type?:
      | "fade"
      | "slide-up"
      | "slide-down"
      | "slide-left"
      | "slide-right"
      | "zoom"
      | "flip"
      | "custom";
    duration?: number;
    delay?: number;
    ease?: string;
  }>(),
  {
    name: "",
    mode: "default",
    appear: true,
    type: "fade",
    duration: 0.3,
    delay: 0,
    ease: "power2.out",
  },
);

// 进入前
const beforeEnter = (el: Element) => {
  if (props.type === "custom") return;

  switch (props.type) {
    case "fade":
      gsap.set(el, { autoAlpha: 0 });
      break;
    case "slide-up":
      gsap.set(el, { autoAlpha: 0, y: 20 });
      break;
    case "slide-down":
      gsap.set(el, { autoAlpha: 0, y: -20 });
      break;
    case "slide-left":
      gsap.set(el, { autoAlpha: 0, x: 20 });
      break;
    case "slide-right":
      gsap.set(el, { autoAlpha: 0, x: -20 });
      break;
    case "zoom":
      gsap.set(el, { autoAlpha: 0, scale: 0.9 });
      break;
    case "flip":
      gsap.set(el, { autoAlpha: 0, rotationY: 90, transformPerspective: 800 });
      break;
  }
};

// 进入中
const enter = (el: Element, done: () => void) => {
  if (props.type === "custom") {
    done();
    return;
  }

  const gsapParams: any = {
    autoAlpha: 1,
    duration: props.duration,
    delay: props.delay,
    onComplete: done,
    ease: props.ease,
  };

  switch (props.type) {
    case "fade":
      gsap.to(el, gsapParams);
      break;
    case "slide-up":
      gsap.to(el, { ...gsapParams, y: 0 });
      break;
    case "slide-down":
      gsap.to(el, { ...gsapParams, y: 0 });
      break;
    case "slide-left":
      gsap.to(el, { ...gsapParams, x: 0 });
      break;
    case "slide-right":
      gsap.to(el, { ...gsapParams, x: 0 });
      break;
    case "zoom":
      gsap.to(el, { ...gsapParams, scale: 1 });
      break;
    case "flip":
      gsap.to(el, { ...gsapParams, rotationY: 0, transformPerspective: 800 });
      break;
  }
};

// 进入后
const afterEnter = (el: Element) => {
  if (props.type === "custom") return;
  gsap.set(el, { clearProps: "all" });
};

const enterCancelled = afterEnter;

// 离开前
const beforeLeave = (el: Element) => {
  if (props.type === "custom") return;
  gsap.set(el, { autoAlpha: 1 });
};

// 离开中
const leave = (el: Element, done: () => void) => {
  if (props.type === "custom") {
    done();
    return;
  }

  const gsapParams: any = {
    autoAlpha: 0,
    duration: props.duration,
    delay: props.delay,
    onComplete: done,
    ease: props.ease,
  };

  switch (props.type) {
    case "fade":
      gsap.to(el, gsapParams);
      break;
    case "slide-up":
      gsap.to(el, { ...gsapParams, y: -20 });
      break;
    case "slide-down":
      gsap.to(el, { ...gsapParams, y: 20 });
      break;
    case "slide-left":
      gsap.to(el, { ...gsapParams, x: -20 });
      break;
    case "slide-right":
      gsap.to(el, { ...gsapParams, x: 20 });
      break;
    case "zoom":
      gsap.to(el, { ...gsapParams, scale: 0.9 });
      break;
    case "flip":
      gsap.to(el, { ...gsapParams, rotationY: -90, transformPerspective: 800 });
      break;
  }
};

// 离开后
const afterLeave = (el: Element) => {
  if (props.type === "custom") return;
  gsap.set(el, { clearProps: "all" });
};

const leaveCancelled = afterLeave;
</script>
