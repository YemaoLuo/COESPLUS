import { h } from 'vue';
import { NAvatar } from 'naive-ui';

export const columns = [
  {
    title: '教师编号',
    key: 'teacherId',
    width: 100,
    align: 'center',
  },
  {
    title: '姓名',
    key: 'name',
    width: 100,
    align: 'center',
  },
  {
    title: '性别',
    key: 'sex',
    width: 50,
    align: 'center',
  },
  {
    title: '电话',
    key: 'telephone',
    width: 150,
    align: 'center',
  },
  {
    title: '邮箱',
    key: 'email',
    width: 150,
    align: 'center',
  },
  {
    title: '学院',
    key: 'facultyName',
    width: 100,
    align: 'center',
  },
  {
    title: '入职年份',
    key: 'joinYear',
    width: 100,
    align: 'center',
  },
  {
    title: '头像',
    key: 'photo',
    width: 100,
    render(row) {
      return h(NAvatar, {
        size: 48,
        src: row.photo,
      });
    },
    align: 'center',
  },
  {
    title: '创建日期',
    key: 'createTime',
    width: 100,
    align: 'center',
  },
  {
    title: '更新日期',
    key: 'updateTime',
    width: 100,
    align: 'center',
  },
];
