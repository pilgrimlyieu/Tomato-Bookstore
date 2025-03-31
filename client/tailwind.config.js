/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{vue,js,ts,jsx,tsx}"],
  theme: {
    extend: {
      fontFamily: {
        sans: ["Inter", "system-ui", "sans-serif"],
        display: ["Poppins", "Inter", "system-ui", "sans-serif"],
      },
      colors: {
        tomato: {
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
        // 辅助色系
        mint: {
          50: "#f2fbf9",
          100: "#d5f5ee",
          200: "#aeeadd",
          300: "#86d7c9",
          400: "#5dc3b3",
          500: "#3cab9c",
          600: "#2c897f",
          700: "#266d67",
          800: "#235653",
          900: "#1e4745",
        },
        sunshine: {
          50: "#fefce8",
          100: "#fef9c3",
          200: "#fef08a",
          300: "#fde047",
          400: "#facc15",
          500: "#eab308",
          600: "#ca8a04",
          700: "#a16207",
          800: "#854d0e",
          900: "#713f12",
        },
      },
      spacing: {
        72: "18rem",
        84: "21rem",
        96: "24rem",
        128: "32rem",
      },
      borderRadius: {
        "4xl": "2rem",
        "5xl": "2.5rem",
      },
      boxShadow: {
        soft: "0 5px 20px 0 rgba(0, 0, 0, 0.05)",
        strong: "0 10px 40px 0 rgba(0, 0, 0, 0.1)",
        "inner-light": "inset 0 2px 4px 0 rgba(0, 0, 0, 0.05)",
        "inner-strong": "inset 0 2px 4px 0 rgba(0, 0, 0, 0.1)",
        colored: "0 10px 25px -3px rgba(255, 107, 107, 0.3)",
      },
      animation: {
        float: "float 4s ease-in-out infinite",
        "bounce-slow": "bounce 2s ease-in-out infinite",
        "fade-in": "fadeIn 0.5s ease-out forwards",
        "slide-up": "slideUp 0.5s ease-out forwards",
        "slide-right": "slideRight 0.5s ease-out forwards",
        scale: "scale 0.5s ease-out forwards",
        "spin-slow": "spin 3s linear infinite",
        "pulse-slow": "pulse 3s ease-in-out infinite",
        wiggle: "wiggle 1s ease-in-out",
        shimmer: "shimmer 2.5s infinite linear",
        "background-shine": "backgroundShine 2s linear infinite",
      },
      keyframes: {
        float: {
          "0%, 100%": { transform: "translateY(0)" },
          "50%": { transform: "translateY(-10px)" },
        },
        slideUp: {
          "0%": { opacity: "0", transform: "translateY(20px)" },
          "100%": { opacity: "1", transform: "translateY(0)" },
        },
        slideRight: {
          "0%": { opacity: "0", transform: "translateX(-20px)" },
          "100%": { opacity: "1", transform: "translateX(0)" },
        },
        scale: {
          "0%": { opacity: "0", transform: "scale(0.9)" },
          "100%": { opacity: "1", transform: "scale(1)" },
        },
        wiggle: {
          "0%, 100%": { transform: "rotate(-3deg)" },
          "50%": { transform: "rotate(3deg)" },
        },
        shimmer: {
          "0%": { backgroundPosition: "-1000px 0" },
          "100%": { backgroundPosition: "1000px 0" },
        },
        backgroundShine: {
          from: { backgroundPosition: "0 0" },
          to: { backgroundPosition: "-200% 0" },
          fadeIn: {
            from: { opacity: 0, transform: "translateY(10px)" },
            to: { opacity: 1, transform: "translateY(0)" },
          },
        },
      },
      backdropBlur: {
        xs: "2px",
      },
      // 渐变效果
      backgroundImage: {
        "gradient-conic": "conic-gradient(var(--tw-gradient-stops))",
        "gradient-radial": "radial-gradient(var(--tw-gradient-stops))",
        shine:
          "linear-gradient(45deg, transparent 25%, rgba(255, 255, 255, 0.3) 50%, transparent 75%, transparent 100%)",
      },
      // 文本描边
      textStroke: {
        thin: "1px",
        medium: "2px",
        thick: "3px",
      },
    },
  },
  plugins: [
    require("@tailwindcss/forms"),
    require("@tailwindcss/typography"),
    require("@tailwindcss/aspect-ratio"),
    require("@designbycode/tailwindcss-text-shadow"),
    require("tailwind-scrollbar")({ nocompatible: true }),
    ({ addUtilities }) => {
      const textStrokeUtilities = {};
      const colors = {
        white: "white",
        black: "black",
        tomato: "#ff6b6b",
      };

      const strokes = {
        thin: "1px",
        medium: "2px",
        thick: "3px",
      };

      for (const [strokeKey, strokeWidth] of Object.entries(strokes)) {
        for (const [colorKey, colorValue] of Object.entries(colors)) {
          textStrokeUtilities[`.text-stroke-${strokeKey}-${colorKey}`] = {
            "-webkit-text-stroke-width": strokeWidth,
            "-webkit-text-stroke-color": colorValue,
          };
        }
      }

      addUtilities(textStrokeUtilities, ["responsive"]);
    },
    ({ addUtilities }) => {
      const newUtilities = {
        ".backface-visible": {
          "backface-visibility": "visible",
        },
        ".backface-hidden": {
          "backface-visibility": "hidden",
        },
        ".perspective-500": {
          perspective: "500px",
        },
        ".perspective-800": {
          perspective: "800px",
        },
        ".perspective-1000": {
          perspective: "1000px",
        },
        ".rotate-y-180": {
          transform: "rotateY(180deg)",
        },
        ".preserve-3d": {
          "transform-style": "preserve-3d",
        },
        ".rotate-y-0": {
          transform: "rotateY(0deg)",
        },
        ".book-shadow": {
          "box-shadow":
            "0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05), -1px 0 3px 0 rgba(0, 0, 0, 0.1)",
        },
        ".clip-path-slant": {
          "clip-path": "polygon(0 0, 100% 0, 100% 85%, 0 100%)",
        },
      };
      addUtilities(newUtilities);
    },
  ],
  variants: {
    extend: {
      textStroke: ["responsive"],
      borderRadius: ["hover", "focus"],
      scale: ["group-hover"],
      textShadow: ["hover", "focus"],
      scrollbar: ["rounded"],
    },
  },
};
