<template>
  <n-card :bordered="false" class="proCard">
    <BasicForm @register="register" @submit="handleSubmit" @reset="handleReset">
      <template #statusSlot="{ model, field }">
        <n-input v-model:value="model[field]" />
      </template>
    </BasicForm>

    <BasicTable
      :columns="columns"
      :request="loadDataTable"
      :row-key="(row) => row.studentName"
      ref="actionRef"
      :actionColumn="actionColumn"
      @update:checked-row-keys="onCheckedRow"
      :scroll-x="1090"
    >
      <template #toolbar>
        <n-button type="primary" @click="reloadTable">刷新数据</n-button>
      </template>
    </BasicTable>
  </n-card>
</template>

<script lang="ts" setup>
  import { h, reactive, ref } from 'vue';
  import { useDialog, useMessage } from 'naive-ui';
  import { BasicTable, TableAction } from '@/components/Table';
  import { BasicForm, FormSchema, useForm } from '@/components/Form/index';
  import { getTableListDemo, deleteTableListDemo } from '@/api/table/commentlistDemo';
  import { columns } from './columns';
  //import { PlusOutlined } from '@vicons/antd';
  import { useRouter } from 'vue-router';
  import { delTableListDemo } from '@/api/table/courselistDemo';

  // const rules = {
  //   name: {
  //     required: true,
  //     trigger: ['blur', 'input'],
  //     message: '请输入名称',
  //   },
  //   address: {
  //     required: true,
  //     trigger: ['blur', 'input'],
  //     message: '请输入地址',
  //   },
  //   date: {
  //     type: 'number',
  //     required: true,
  //     trigger: ['blur', 'change'],
  //     message: '请选择日期',
  //   },
  // };

  const schemas: FormSchema[] = [
    {
      field: 'studentName',
      component: 'NInput',
      label: '学生名称',
      componentProps: {
        placeholder: '发表评论的学生名字',
        onInput: (e: any) => {
          formParams.studentName = e;
        },
      },
    },
    {
      field: 'teacherName',
      component: 'NInput',
      label: '老师名称',
      componentProps: {
        placeholder: '被评价的老师名字',
        onInput: (e: any) => {
          formParams.teacherName = e;
        },
      },
    },
    {
      field: 'comment',
      component: 'NInput',
      label: '评论',
      componentProps: {
        placeholder: '请输入想要查询的评价',
        onInput: (e: any) => {
          formParams.comment = e;
        },
      },
    },
  ];

  const router = useRouter();
  //const formRef: any = ref(null);
  const message = useMessage();
  const actionRef = ref();

  //const showModal = ref(false);
  //const formBtnLoading = ref(false);
  const formParams = reactive({
    studentName: '',
    teacherName: '',
    comment: '',
  });

  const params = ref({
    pageSize: 5,
  });

  const actionColumn = reactive({
    width: 220,
    title: '操作',
    key: 'action',
    fixed: 'right',
    render(record) {
      return h(TableAction as any, {
        style: 'button',
        actions: [
          {
            label: '删除',
            type: 'error',
            onClick: handleDelete.bind(null, record),
            // 根据业务控制是否显示 isShow 和 auth 是并且关系
            ifShow: () => {
              return true;
            },
            // 根据权限控制是否显示: 有权限，会显示，支持多个
            // auth: ['basic_list'],
          },
        ],

        select: (key) => {
          message.info(`您点击了，${key} 按钮`);
        },
      });
    },
  });

  const [register, {}] = useForm({
    gridProps: { cols: '1 s:1 m:2 l:3 xl:4 2xl:4' },
    labelWidth: 70,
    schemas,
  });

  // function addTable() {
  //   showModal.value = true;
  // }

  const loadDataTable = async (res) => {
    return await getTableListDemo({ ...formParams, ...params.value, ...res });
  };

  function onCheckedRow(rowKeys) {
    console.log(rowKeys);
  }

  function reloadTable() {
    actionRef.value.reload();
  }

  // function confirmForm(e) {
  //   e.preventDefault();
  //   formBtnLoading.value = true;
  //   formRef.value.validate((errors) => {
  //     if (!errors) {
  //       message.success('新建成功');
  //       setTimeout(() => {
  //         showModal.value = false;
  //         reloadTable();
  //       });
  //     } else {
  //       message.error('请填写完整信息');
  //     }
  //     formBtnLoading.value = false;
  //   });
  // }

  function handleEdit(record: Recordable) {
    console.log('点击了编辑', record);
    router.push({ name: 'basic-info', params: { id: record.id } });
  }
  const dialog = useDialog();
  function handleDelete(record: Recordable) {
    console.log(record);
    dialog.info({
      title: '警告',
      content: `是否删除该评论 “${record.comment}”`,
      positiveText: '确定',
      negativeText: '取消',
      onPositiveClick: () => {
        deleteTableListDemo(record.id);
        reloadTable();
        message.success('删除成功');
      },
      onNegativeClick: () => {},
    });
  }

  function handleSubmit(values: Recordable) {
    console.log(values);
    reloadTable();
  }

  function handleReset(values: Recordable) {
    console.log(values);
  }
</script>

<style lang="less" scoped></style>
