import { RouteRecordRaw } from 'vue-router';
import { Layout } from '@/router/constant';
import { ReadOutlined } from '@vicons/antd';
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
    path: '/courselist',
    name: 'courseList',
    redirect: '/courselist/mycourse-list',
    component: Layout,
    meta: {
      title: '列表页面',
      icon: renderIcon(ReadOutlined),
      sort: 2,
      permissions: ['select_course'],
    },
    children: [
      {
        path: 'mycourse-list',
        name: 'mycourse-list',
        meta: {
          title: '我的课程',
          permissions: ['select_course'],
        },
        component: () => import('@/views/list/myCourseList/index.vue'),
      },
      {
        path: 'course-info/:id?',
        name: 'course-info',
        meta: {
          title: '课程详情',
          hidden: true,
          activeMenu: 'course-list',
        },
        component: () => import('@/views/list/myCourseList/info.vue'),
      },
    ],
  },
];

export default routes;
