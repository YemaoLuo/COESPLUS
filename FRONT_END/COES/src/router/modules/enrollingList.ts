import { RouteRecordRaw } from 'vue-router';
import { Layout } from '@/router/constant';
import { TableOutlined } from '@vicons/antd';
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
    path: '/enrollinglist',
    name: 'enrollingList',
    redirect: '/enrollinglist/enrolling-info',
    component: Layout,
    meta: {
      title: '列表页面',
      icon: renderIcon(TableOutlined),
      permissions: ['select_course'],
      sort: 2,
    },
    children: [
      {
        path: 'enrolling-info/:id?',
        name: 'enrolling-info',
        meta: {
          title: '选课',
          activeMenu: 'enrolling-info',
          permissions: ['select_course'],
        },
        component: () => import('@/views/list/enrollingList/info.vue'),
      },
      {
        path: 'enrolling-list',
        name: 'enrolling-list',
        meta: {
          title: '选课界面',
          hidden: true,
          permissions: ['select_course'],
        },
        component: () => import('@/views/list/enrollingList/index.vue'),
      },
    ],
  },
];

export default routes;
