import { RouteRecordRaw } from 'vue-router';
import { Layout } from '@/router/constant';
import { SettingOutlined } from '@vicons/antd';
import { renderIcon } from '@/utils/index';

/**
 * @param name 路由名称, 必须设置,且不能重名
 * @param meta 路由元信息（路由附带扩展信息）
 * @param redirect 重定向地址, 访问这个路由时,自定进行重定向
 * @param meta.disabled 禁用整个菜单
 * @param meta.title 菜单名称
 * @param meta.icon 菜单图标
 * @param meta.keepAlive 缓存该路由
 * @param meta.sort 排序越小越排前
 *
 * */
const routes: Array<RouteRecordRaw> = [
  {
    path: '/iframe',
    name: 'SystemSetting',
    redirect: '/iframe/iframe-ObjectStore',
    component: Layout,
    meta: {
      title: '系统设置',
      icon: renderIcon(SettingOutlined),
      sort: 4,
    },
    children: [
      {
        path: 'iframe-ObjectStore',
        name: 'iframe-ObjectStore',
        meta: {
          title: '对象储存管理',
        },
        component: () => import('@/views/iframe/ObjectStore .vue'),
      },
      {
        path: 'iframe-RegisterConfiguration',
        name: 'iframe-RegisterConfiguration',
        meta: {
          title: '微服务管理',
        },
        component: () => import('@/views/iframe/Register &Configuration Center.vue'),
      },
    ],
  },
];

export default routes;
