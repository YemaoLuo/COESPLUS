import { RouteRecordRaw } from 'vue-router';
import { Layout } from '@/router/constant';
import { CommentOutlined } from '@vicons/antd';
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
    path: '/commentListDemo',
    name: 'commentListDemo',
    redirect: '/commentListDemo/basic-list-comment',
    component: Layout,
    meta: {
      title: '评论区',
      icon: renderIcon(CommentOutlined),
      sort: 4,
    },
    children: [
      {
        path: 'basic-list-comment',
        name: 'basic-list-comment',
        meta: {
          title: '老师评论',
          permissions: ['select_course'],
        },
        component: () => import('@/views/list/commentList/index.vue'),
      },
      {
        path: 'basic-list-mycomment',
        name: 'basic-list-mycomment',
        meta: {
          title: '我的评论',
          permissions: ['score'],
        },
        component: () => import('@/views/list/OwncommentList/index.vue'),
      },
    ],
  },
];

export default routes;
