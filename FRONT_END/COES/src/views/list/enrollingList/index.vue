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
      <template #toolbar>
        <n-button type="primary" @click="reloadTable">刷新数据</n-button>
      </template>
    </BasicTable>

    <n-modal v-model:show="showExportModal" :show-icon="false" preset="dialog" title="导出课程信息">
      <n-form
        :model="exportformParams"
        ref="formRef"
        label-placement="left"
        :label-width="80"
        class="py-4"
      >
        <n-form-item label="名称" path="name">
          <n-input placeholder="请输入课程名称" v-model:value="exportformParams.name" />
        </n-form-item>
        <n-form-item label="禁用状态" path="isDeleted">
          <n-select
            v-model:value="exportformParams.isDeleted"
            placeholder="请选择禁用状态"
            :options="isDeletedList"
          />
        </n-form-item>
        <n-form-item label="课程编号" path="courseId">
          <n-input placeholder="请输入课程编号 " v-model:value="exportformParams.courseId" />
        </n-form-item>
        <n-form-item label="学院名称" path="facultyName">
          <n-select
            v-model:value="exportformParams.facultyName"
            placeholder="请输入学院名称"
            :options="facultyList"
          />
        </n-form-item>
        <n-form-item label="老师名字" path="teacherName">
          <n-input placeholder="请输入老师名字 " v-model:value="exportformParams.teacherName" />
        </n-form-item>
      </n-form>

      <template #action>
        <n-space>
          <n-button @click="() => (showExportModal = false)">取消</n-button>
          <n-button type="info" :loading="formBtnLoading" @click="confirmExportForm">确定</n-button>
        </n-space>
      </template>
    </n-modal>
  </n-card>
</template>

<script lang="ts" setup>
  import { h, reactive, ref } from 'vue';
  import { useMessage, useDialog } from 'naive-ui';
  import { BasicTable, TableAction } from '@/components/Table';
  import { BasicForm } from '@/components/Form/index';
  // import { BasicForm, FormSchema, useForm } from '@/components/Form/index';
  import { columns } from './columns';
  // import { PlusOutlined } from '@vicons/antd';
  // import { useRouter } from 'vue-router';
  // import { storage } from '@/utils/Storage';
  // import { ACCESS_TOKEN } from '@/store/mutation-types';
  import { getTableList, courseAction, checkEnrollingState } from '@/api/table/enrollinglist';
  //
  // const schemas: FormSchema[] = [
  //   {
  //     field: 'courseId',
  //     component: 'NInput',
  //     label: '课程ID',
  //     componentProps: {
  //       placeholder: '请输入该课程id编号',
  //       onInput: (e: any) => {
  //         formParams.courseId = e;
  //       },
  //     },
  //   },
  //   {
  //     field: 'name',
  //     component: 'NInput',
  //     label: '课程名称',
  //     componentProps: {
  //       placeholder: '请输入课程名',
  //       onInput: (e: any) => {
  //         formParams.name = e;
  //       },
  //     },
  //   },
  //   {
  //     field: 'facultyName',
  //     component: 'NInput',
  //     label: '学院',
  //     componentProps: {
  //       placeholder: '请输入课程所属学院',
  //       onInput: (e: any) => {
  //         formParams.facultyName = e;
  //       },
  //     },
  //   },
  //   {
  //     field: 'teacherName',
  //     component: 'NInput',
  //     label: '授课教师',
  //     componentProps: {
  //       placeholder: '请输入该课程的授课教师名称',
  //       onInput: (e: any) => {
  //         formParams.teacherName = e;
  //       },
  //     },
  //   },
  //   {
  //     field: 'isDeleted',
  //     component: 'NSwitch',
  //     label: '是否禁用',
  //     componentProps: {
  //       placeholder: '禁用',
  //       onUpdateValue: (e: any) => {
  //         if (e) formParams.isDeleted = '1';
  //         else formParams.isDeleted = '0';
  //       },
  //     },
  //   },
  // ];

  // const router = useRouter();
  const formRef: any = ref(null);
  const actionRef = ref();
  // const token = storage.get(ACCESS_TOKEN);
  const dialog = useDialog();
  const message = useMessage();

  const showExportModal = ref(false);
  // const showModal = ref(false);
  const formBtnLoading = ref(false);
  const formParams = reactive({
    id: '',
    state: '',
    isChosen: '',
  });

  const params = ref({
    pageSize: 10,
    currentPage: 1,
  });

  const actionColumn = reactive({
    width: 160,
    title: '操作',
    key: 'action',
    fixed: 'right',
    align: 'center',
    render(record) {
      return h(TableAction as any, {
        style: 'button',
        actions: [
          {
            label: '退选',
            type: 'error',
            onClick: cancelCourse.bind(null, record),
            // 根据业务控制是否显示 isShow 和 auth 是并且关系
            ifShow: () => {
              if (record.isChosen == '1') return true;
            },
            // 根据权限控制是否显示: 有权限，会显示，支持多个
          },
          {
            label: '加选',
            type: 'success',
            onClick: addCourse.bind(null, record),
            ifShow: () => {
              if (record.isChosen == '0') return true;
            },
          },
        ],
        // select: (key) => {
        //   // window['$message'].info(`您点击了，${key} 按钮`);
        // },
      });
    },
  });

  // const [register, {}] = useForm({
  //   gridProps: { cols: '1 s:1 m:2 l:3 xl:4 2xl:4' },
  //   labelWidth: 80,
  //   schemas,
  // });
  //
  // function addTable() {
  //   showModal.value = true;
  // }

  const loadDataTable = async (res) => {
    const resp = await checkEnrollingState({});
    formParams.id = resp.result;
    return await getTableList({ ...params.value, ...res });
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
  //       window['$message'].success('新建成功');
  //       setTimeout(() => {
  //         showModal.value = false;
  //         reloadTable();
  //       });
  //     } else {
  //       window['$message'].error('请填写完整信息');
  //     }
  //     formBtnLoading.value = false;
  //   });
  // }

  async function addCourse(record: Recordable) {
    let response = await courseAction({ id: record.id, state: 0 });
    console.log(response);
    if (response.code == 200) {
      message.success('加选成功');
    }
    if (response.code == 400) {
      message.error(response.message);
    }
    reloadTable();
  }

  function cancelCourse(record: Recordable) {
    console.log('点击了删除', record);
    dialog.info({
      title: '警告',
      content: `您真的要退选课程“${record.courseName}”吗`,
      positiveText: '退选',
      negativeText: '取消',
      onPositiveClick: async () => {
        let response = await courseAction({ id: record.id, state: 1 });
        console.log(response);
        if (response.code == 200) {
          message.success('退选成功');
        }
        if (response.code == 400) {
          message.error(response.message);
        }
      },
      onNegativeClick: () => {},
    });
    reloadTable();
  }

  function handleSubmit(values: Recordable) {
    console.log(values);
    reloadTable();
  }

  // function handleReset(values: Recordable) {
  //   formParams.courseId = '';
  //   formParams.name = '';
  //   formParams.facultyName = '';
  //   formParams.teacherName = '';
  //   formParams.isDeleted = '';
  //   reloadTable();
  //   console.log(values);
  // }
</script>

<style lang="less" scoped></style>
