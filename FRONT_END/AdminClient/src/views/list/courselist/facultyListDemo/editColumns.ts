// import { h } from 'vue';

export const columns = [
  {
    title: 'ID',
    key: 'id',
    width: 150,
    editComponent: 'NInput',
    edit: true,
    align: 'center',
  },
  {
    title: '学院名称',
    key: 'name',
    width: 150,
    editComponent: 'NInput',
    edit: true,
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
