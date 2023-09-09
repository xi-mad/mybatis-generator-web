import axios, {AxiosInstance, AxiosRequestConfig} from "axios";
import {ElLoading, ElMessage} from "element-plus";
import {downloadFile} from "@/utils/file";


let loadingInstance = null;
let requestNum = 0;

const addLoading = () => {
    // 增加loading 如果pending请求数量等于1，弹出loading, 防止重复弹出
    requestNum++;
    if (requestNum == 1) {
        loadingInstance = ElLoading.service({
            text: "正在努力加载中....",
            background: "rgba(0, 0, 0, 0)",
        });
    }
};

const cancelLoading = () => {
    // 取消loading 如果pending请求数量等于0，关闭loading
    requestNum--;
    if (requestNum === 0) loadingInstance?.close();
};

export const createAxiosByInterceptors = (config?: AxiosRequestConfig): AxiosInstance => {
    const instance = axios.create({
        baseURL: '',
        timeout: 1000,
        withCredentials: true,
        ...config,
    });

    // 添加请求拦截器
    instance.interceptors.request.use(
        function (config: any) {
            // 在发送请求之前做些什么
            const {loading = true} = config;
            if (loading) addLoading();
            return config;
        },
        function (error) {
            // 对请求错误做些什么
            return Promise.reject(error);
        }
    );

    // 添加响应拦截器
    instance.interceptors.response.use(
        function (response) {
            // 对响应数据做点什么
            const {loading = true} = response.config;
            if (loading) cancelLoading();
            const {code, message} = response.data;
            // config设置responseType为blob 处理文件下载
            if (response.data instanceof Blob) {
                return downloadFile(response);
            } else {
                if (code === 0) {
                    return response.data;
                } else {
                    ElMessage.error(message);
                    return Promise.reject(response.data);
                }
            }
        },
        function (error) {
            // 对响应错误做点什么
            const {loading = true} = error.config;
            if (loading) cancelLoading();
            ElMessage.error(error?.message || "服务端异常");
            return Promise.reject(error);
        }
    );
    return instance;
};

export const request = createAxiosByInterceptors({});