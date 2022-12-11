import './styles/tailwind.css';
import { createApp } from 'vue';
import App from './App.vue';
import router, { setupRouter } from './router';
import { setupStore } from '@/store';
import { setupNaive, setupDirectives } from '@/plugins';
import { AppProvider } from '@/components/Application';
import axios from 'axios';
import { ResultEnum } from '@/enums/httpEnum';

async function bootstrap() {
  const appProvider = createApp(AppProvider);

  const app = createApp(App);

  // const userStore = useUserStore();

  // 注册全局常用的 naive-ui 组件
  setupNaive(app);

  // 注册全局自定义组件
  //setupCustomComponents();

  // 注册全局自定义指令，如：v-permission权限指令
  setupDirectives(app);

  // 注册全局方法，如：app.config.globalProperties.$message = message
  //setupGlobalMethods(app);

  // 挂载状态管理
  setupStore(app);

  //优先挂载一下 Provider 解决路由守卫，Axios中可使用，Dialog，Message 等之类组件
  appProvider.mount('#appProvider', true);

  // 挂载路由
  await setupRouter(app);

  // 路由准备就绪后挂载APP实例
  await router.isReady();

  app.mount('#app', true);

  //定义一个响应拦截器
  axios.interceptors.response.use(function (config) {
    const code = config.data.codeSession; //在这里对返回的数据进行处理
    const statecode = config.data.code;
    if (code === ResultEnum.FORBIDDEN || statecode == 403) {
      router
        .replace({
          name: 'Login',
        })
        .finally(() => {
          location.reload();
        }); //跳转到登录页面
    }
    return config;
  });
}

void bootstrap();
