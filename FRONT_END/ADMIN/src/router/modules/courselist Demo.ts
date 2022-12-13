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
    path: '/courseListDemo',
    name: 'courseListDemo',
    redirect: '/courseListDemo/basic-list-course',
    component: Layout,
    meta: {
      title: '学院与课程',
      icon: renderIcon(ReadOutlined),
      sort: 2,
    },
    children: [
      {
        path: 'basic-list-course',
        name: 'basic-list-course',
        meta: {
          title: '课程信息列表',
        },
        component: () => import('@/views/list/courselist/courseListDemo/index.vue'),
      },
      {
        path: 'basic-list-semester',
        name: 'basic-list-semester',
        meta: {
          title: '查询学期课程',
        },
        component: () => import('@/views/list/courselist/semesterListDemo/index.vue'),
      },
      {
        path: 'basic-list-faculty',
        name: 'basic-list-faculty',
        meta: {
          title: '学院信息列表',
        },
        component: () => import('@/views/list/courselist/facultyListDemo/index.vue'),
      },
      {
        path: 'basic-info-course/:id?',
        name: 'basic-info-course',
        meta: {
          title: '课程编辑页面',
          hidden: true,
          activeMenu: 'basic-info-course',
        },
        component: () => import('@/views/list/courselist/courseListDemo/editRow.vue'),
      },
      {
        path: 'basic-info-courseStudent/:id?',
        name: 'basic-info-courseStudent',
        meta: {
          title: '已选课程学生列表',
          hidden: true,
          activeMenu: 'basic-info-courseStudent',
        },
        component: () => import('@/views/list/courselist/courseListDemo/indexStudent.vue'),
      },
      {
        path: 'basic-info-faculty/:id?',
        name: 'basic-info-faculty',
        meta: {
          title: '学院编辑页面',
          hidden: true,
          activeMenu: 'basic-info-faculty',
        },
        component: () => import('@/views/list/courselist/facultyListDemo/editRow.vue'),
      },
      // {
      //   path: 'basic-list-semesterResult',
      //   name: 'basic-list-semesterResult',
      //   meta: {
      //     title: '查询学期课程结果',
      //     hidden: true,
      //     activeMenu: 'basic-info-semesterResult',
      //   },
      //   component: () => import('@/views/list/courselist/semesterListDemo/index.vue'),
      // },
    ],
  },
];

export default routes;
