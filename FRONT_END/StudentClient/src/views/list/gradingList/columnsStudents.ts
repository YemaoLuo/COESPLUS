import { h } from 'vue';
import { NAvatar } from 'naive-ui';

export const columnsStudents = [
  {
    title: '学生号',
    key: 'studentId',
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
    title: '分数',
    key: 'grade',
    width: 50,
    align: 'center',
  },
  {
    title: '头像',
    key: 'photo',
    width: 70,
    align: 'center',
    render(row) {
      return h(NAvatar, {
        size: 48,
        src: row.photo,
      });
    },
  },
];
