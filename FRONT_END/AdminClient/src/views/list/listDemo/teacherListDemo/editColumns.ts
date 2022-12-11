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
    editComponent: 'NInput',
    edit: true,
    width: 100,
    align: 'center',
  },
  {
    title: '性别',
    key: 'sex',
    editComponent: 'NSelect',
    editComponentProps: {
      options: [
        {
          label: '男',
          value: '男',
        },
        {
          label: '女',
          value: '女',
        },
      ],
    },
    edit: true,
    width: 50,
    align: 'center',
  },
  {
    title: '电话',
    key: 'telephone',
    editComponent: 'NInput',
    edit: true,
    width: 150,
    align: 'center',
  },
  {
    title: '邮箱',
    key: 'email',
    editComponent: 'NInput',
    edit: true,
    width: 150,
    align: 'center',
  },
  {
    title: '学院',
    key: 'facultyName',
    editComponent: 'NInput',
    edit: true,
    width: 100,
    align: 'center',
  },
  {
    title: '入职年份',
    key: 'joinYear',
    editComponent: 'NInput',
    edit: true,
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
    editComponent: 'NInput',
    edit: true,
    width: 100,
    align: 'center',
  },
  {
    title: '更新日期',
    key: 'updateTime',
    editComponent: 'NInput',
    edit: true,
    width: 100,
    align: 'center',
  },
  // {
  //   title: '是否禁用',
  //   key: 'isDeleted',
  //   editComponent: 'NSwitch',
  //   editComponentProps: {
  //     checkedValue: '0',
  //     uncheckedValue: '1',
  //   },
  //   editValueMap: (value) => {
  //     return value == '0' ? '启用' : '禁用';
  //   },
  //   edit: true,
  //   width: 100,
  //   align: 'center',
  // },
];
