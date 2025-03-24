import path from "node:path";
import { URL, fileURLToPath } from "node:url";
import vue from "@vitejs/plugin-vue";
import AutoImport from "unplugin-auto-import/vite";
import { ElementPlusResolver } from "unplugin-vue-components/resolvers";
import Components from "unplugin-vue-components/vite";
import { defineConfig } from "vite";

export default defineConfig({
	resolve: {
		alias: {
			"@": fileURLToPath(new URL("./src", import.meta.url)),
			"@components": path.resolve(__dirname, "src/components"),
			"@stores": path.resolve(__dirname, "src/stores"),
			"@utils": path.resolve(__dirname, "src/utils"),
			"@views": path.resolve(__dirname, "src/views"),
		},
	},
	plugins: [
		vue(),
		AutoImport({
			imports: ["vue", "vue-router"],
			resolvers: [ElementPlusResolver()],
			dts: "src/auto-imports.d.ts",
		}),
		Components({
			dirs: ["src/components"],
			resolvers: [ElementPlusResolver()],
			dts: "src/components.d.ts",
		}),
	],
	server: {
		proxy: {
			"/api": {
				target: "http://localhost:8080",
				changeOrigin: true,
			},
		},
	},
});
