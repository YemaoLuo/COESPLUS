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
    path: '/ListDemo',
    name: 'ListDemo',
    redirect: '/ListDemo/basic-list-Demo',
    component: Layout,
    meta: {
      title: '教师与学生',
      icon: renderIcon(UserOutlined),
      sort: 2,
    },
    children: [
      {
        path: 'basic-list-student',
        name: 'basic-list-student',
        meta: {
          title: '学生信息列表',
        },
        component: () => import('@/views/list/listDemo/studentListDemo/index.vue'),
      },
      {
        path: 'basic-list-teacher',
        name: 'basic-list-teacher',
        meta: {
          title: '教师信息列表',
        },
        component: () => import('@/views/list/listDemo/teacherListDemo/index.vue'),
      },
      {
        path: 'basic-info-student/:id?',
        name: 'basic-info-student',
        meta: {
          title: '学生编辑页面',
          hidden: true,
          activeMenu: 'basic-info-student',
        },
        component: () => import('@/views/list/listDemo/studentListDemo/editRow.vue'),
      },
      {
        path: 'basic-info-teacher/:id?',
        name: 'basic-info-teacher',
        meta: {
          title: '教师编辑页面',
          hidden: true,
          activeMenu: 'basic-info-teacher',
        },
        component: () => import('@/views/list/listDemo/teacherListDemo/editRow.vue'),
      },
    ],
  },
];

export default routes;
