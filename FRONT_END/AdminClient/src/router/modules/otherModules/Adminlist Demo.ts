import { RouteRecordRaw } from 'vue-router';
import { Layout } from '@/router/constant';
import { UserOutlined } from '@vicons/antd';
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
    path: '/AdminList',
    name: 'Administ',
    redirect: '/AdminListDemo/Adminbasic-list-Demo',
    component: Layout,
    meta: {
      title: '管理员',
      icon: renderIcon(UserOutlined),
      sort: 2,
    },
    children: [
      {
        path: 'basic-list-admin',
        name: 'basic-list-admin',
        meta: {
          title: '管理员信息列表',
        },
        component: () => import('@/views/list/listDemo/AdministratorListDemo/index.vue'),
      },
    ],
  },
];

export default routes;
