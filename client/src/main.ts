import ElementPlus from "element-plus";
import { createPinia } from "pinia";
import { createApp } from "vue";
import "element-plus/dist/index.css";
// 移除 animate.css 导入
import gsap from "gsap";
import "./style.css";
import { Icon } from "@iconify/vue";
import VueTippy from "vue-tippy";
import App from "./App.vue";
import router from "./router";
import "tippy.js/dist/tippy.css";
import "tippy.js/themes/light.css";
import { Carousel, Navigation, Pagination, Slide } from "vue3-carousel";
import "vue3-carousel/dist/carousel.css";
import VueConfettiExplosion from "vue-confetti-explosion";

// 创建应用实例
const app = createApp(App);

// 注册全局组件
app.component("IconBox", Icon);
app.component("VConfetti", VueConfettiExplosion);
app.component("Carousel", Carousel);
app.component("Slide", Slide);
app.component("Pagination", Pagination);
app.component("Navigation", Navigation);

// 使用插件
app.use(createPinia());
app.use(ElementPlus, {
  size: "default",
});
app.use(router);
app.use(VueTippy, {
  defaultProps: {
    placement: "bottom",
    allowHTML: true,
    theme: "tomato",
    animation: "scale",
    duration: [275, 200],
    delay: [0, 0],
  },
});

// 全局GSAP配置
gsap.defaults({
  ease: "power2.out",
  duration: 0.5,
  overwrite: "auto",
});

// 挂载应用
app.mount("#app");
