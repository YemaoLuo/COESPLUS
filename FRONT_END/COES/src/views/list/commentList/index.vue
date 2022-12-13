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
      :row-key="(row) => row.id"
      ref="actionRef"
      :actionColumn="actionColumn"
      @update:checked-row-keys="onCheckedRow"
      :scroll-x="1090"
    >
      <template #tableTitle>
        <n-button type="primary" @click="addTable"> 发表评论 </n-button>
      </template>
      <template #toolbar>
        <n-button type="primary" @click="reloadTable">刷新数据</n-button>
      </template>
    </BasicTable>
  </n-card>
  <n-modal v-model:show="showModal" :show-icon="false" preset="dialog" title="添加评论">
    <n-form
      :model="addformParams"
      ref="formRef"
      label-placement="left"
      :label-width="80"
      class="py-4"
    >
      <n-form-item label="老师名称" path="name">
        <n-input placeholder="请输入想要评价老师" v-model:value="addformParams.teacherName" />
      </n-form-item>
      <n-form-item label="评论" path="name">
        <n-input placeholder="请留下你对老师的评论吧" v-model:value="addformParams.comment" />
      </n-form-item>
    </n-form>

    <template #action>
      <n-space>
        <n-button @click="() => (showModal = false)">取消</n-button>
        <n-button type="info" :loading="formBtnLoading" @click="confirmForm">确定</n-button>
      </n-space>
    </template>
  </n-modal>
</template>

<script lang="ts" setup>
  import { h, reactive, ref } from 'vue';
  import { useDialog, useMessage } from 'naive-ui';
  import { BasicTable, TableAction } from '@/components/Table';
  import { BasicForm, FormSchema, useForm } from '@/components/Form/index';
  import { getTableList, addComment } from '@/api/table/commentlist';
  import { columns } from './columns';
  //import { PlusOutlined } from '@vicons/antd';
  import { useRouter } from 'vue-router';
  // import { TABS_ROUTES } from '@/store/mutation-types';
  // import { useTime } from '@/hooks/useTime';
  import { useUserStore } from '@/store/modules/user';
  import { delTableListDemo } from '@/api/table/commentlist';
  //import { delTableListDemo } from "@/api/table/courselistDemo";

  const addformParams = reactive({
    teacherName: '',
    comment: '',
  });
  const userStore = useUserStore();
  function addTable() {
    showModal.value = true;
    //const { month, day } = useTime();
    const UserName = userStore.getNickname;
    console.log(UserName);
  }
  const schemas: FormSchema[] = [
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
  const formRef: any = ref(null);
  const message = useMessage();
  const actionRef = ref();

  const showModal = ref(false);
  const formBtnLoading = ref(false);
  const formParams = reactive({
    studentName: '',
    teacherName: '',
    comment: '',
    isMine: '',
  });

  const params = ref({
    pageSize: 5,
    currentPage: 1,
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
              if (record.isMine == '1') return true;
              else return false;
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

  function handleDelete(record) {
    console.log(record);
    dialog.warning({
      title: '警告',
      content: `是否删除该评论 “${record.comment}”`,
      positiveText: '确定',
      negativeText: '取消',
      onPositiveClick: async () => {
        const response = await delTableListDemo(record.id);
        if (response.code == 200) {
          message.success('删除成功');
        }
        if (response.code == 400) {
          message.error(response.message);
        }
        reloadTable();
      },
      onNegativeClick: () => {},
    });
  }
  const loadDataTable = async (res) => {
    const response = await getTableList({ ...formParams, ...params.value, ...res });
    return response;
  };

  function onCheckedRow(rowKeys) {
    console.log(rowKeys);
  }

  function reloadTable() {
    actionRef.value.reload();
  }

  function handleEdit(record: Recordable) {
    console.log('点击了编辑', record);
    router.push({ name: 'basic-info', params: { id: record.id } });
  }
  const dialog = useDialog();
  function confirmForm(e) {
    dialog.info({
      title: '警告',
      content: `言论将会公开，请注意一下文明用语噢”`,
      positiveText: '确定发表',
      negativeText: '我再想想',
      onPositiveClick: () => {
        e.preventDefault();
        formBtnLoading.value = true;
        formRef.value.validate(async (errors) => {
          if (!errors) {
            const response = await addComment({ ...addformParams, ...params.value, e });
            if (response.code == 200) {
              message.success('发表成功');
            }
            if (response.code == 400) {
              message.error(response.message);
            }
            message.toString();
            setTimeout(() => {
              showModal.value = false;
              reloadTable();
            });
          } else {
            message.error('请填写完整信息');
          }
          formBtnLoading.value = false;
        });
      },
      onNegativeClick: () => {
        formBtnLoading.value = false;
      },
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
