import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [vue()],
    build: {
        outDir: '../server/src/main/resources/static'
    },
    server: {
        proxy: {
            '/api': 'http://localhost:8080'
        }
    }
})
