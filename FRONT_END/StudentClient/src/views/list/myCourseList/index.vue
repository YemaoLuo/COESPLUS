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
        <n-button type="primary" @click="exportTable">
          <template #icon>
            <!--            <n-icon>-->
            <!--              <TableExport />-->
            <!--            </n-icon>-->
          </template>
          导出课程信息
        </n-button>
      </template>

      <template #toolbar>
        <n-button type="primary" @click="reloadTable">刷新数据</n-button>
      </template>
    </BasicTable>

    <n-space>
      <n-modal v-model:show="showTableModal" preset="card" transform-origin="center">
        <BasicTable
          :columns="columnsStudents"
          :request="loadStudentsTable"
          :row-key="(row) => row.result.id"
          ref="actionRef"
          @update:checked-row-keys="onCheckedRow"
          :scroll-x="1090"
        />
        <template #action>
          <n-space>
            <n-button @click="() => (showTableModal = false)">取消</n-button>
            <n-button type="info" :loading="formBtnLoading" @click="() => (showTableModal = false)"
              >确定</n-button
            >
          </n-space>
        </template>
      </n-modal>
    </n-space>

    <!--    <n-modal v-model:show="showExportModal" :show-icon="false" preset="dialog" title="导出课程信息">-->
    <!--      <n-form-->
    <!--        :model="exportformParams"-->
    <!--        ref="formRef"-->
    <!--        label-placement="left"-->
    <!--        :label-width="80"-->
    <!--        class="py-4"-->
    <!--      >-->
    <!--        <n-form-item label="名称" path="name">-->
    <!--          <n-input placeholder="请输入课程名称" v-model:value="exportformParams.name" />-->
    <!--        </n-form-item>-->
    <!--        <n-form-item label="禁用状态" path="isDeleted">-->
    <!--          <n-select-->
    <!--            v-model:value="exportformParams.isDeleted"-->
    <!--            placeholder="请选择禁用状态"-->
    <!--            :options="isDeletedList"-->
    <!--          />-->
    <!--        </n-form-item>-->
    <!--        <n-form-item label="课程编号" path="courseId">-->
    <!--          <n-input placeholder="请输入课程编号 " v-model:value="exportformParams.courseId" />-->
    <!--        </n-form-item>-->
    <!--        <n-form-item label="学院名称" path="facultyName">-->
    <!--          <n-select-->
    <!--            v-model:value="exportformParams.facultyName"-->
    <!--            placeholder="请输入学院名称"-->
    <!--            :options="facultyList"-->
    <!--          />-->
    <!--        </n-form-item>-->
    <!--        <n-form-item label="老师名字" path="teacherName">-->
    <!--          <n-input placeholder="请输入老师名字 " v-model:value="exportformParams.teacherName" />-->
    <!--        </n-form-item>-->
    <!--      </n-form>-->

    <!--      <template #action>-->
    <!--        <n-space>-->
    <!--          <n-button @click="() => (showExportModal = false)">取消</n-button>-->
    <!--          <n-button type="info" :loading="formBtnLoading" @click="confirmExportForm">确定</n-button>-->
    <!--        </n-space>-->
    <!--      </template>-->
    <!--    </n-modal>-->
  </n-card>
</template>

<script lang="ts" setup>
  import { useMessage } from 'naive-ui';
  import { columnsStudents } from './columnsStudents';
  // import { TableExport, UserPlus } from '@vicons/tabler';

  import { h, reactive, ref } from 'vue';
  // import { useMessage } from 'naive-ui';
  import { BasicTable, TableAction } from '@/components/Table';
  import { BasicForm, FormSchema, useForm } from '@/components/Form/index';
  import { exportcoursedemo, getCourseList, getTableList } from '@/api/table/courselist';
  import { columns } from './columns';
  // import { PlusOutlined } from '@vicons/antd';
  import { useRouter } from 'vue-router';
  import { storage } from '@/utils/Storage';
  import { ACCESS_TOKEN } from '@/store/mutation-types';

  // const exportformParams = reactive({
  //   name: '',
  //   isDeleted: '',
  //   courseId: '',
  //   facultyName: '',
  //   teacherName: '',
  // });
  // const facultyList = [
  //   {
  //     label: '资讯科技学院',
  //     value: '10067d5164a24943a73f8c6f88b6185a',
  //   },
  //   {
  //     label: '光电学院',
  //     value: '11966e25a7644418ac432b6ea120c297',
  //   },
  //   {
  //     label: '传媒学院',
  //     value: '401d10acefb54add88564ad82ab1af60',
  //   },
  //   {
  //     label: '文学院',
  //     value: '531ec96b8bec405ab48bff436dd73997',
  //   },
  //   {
  //     label: '法学院',
  //     value: '98284e6e127c46a48c81e810a1fd7b63',
  //   },
  //   {
  //     label: '医学院',
  //     value: 'aeaa10eb4aa9484aa3e099aafc23c734',
  //   },
  //   {
  //     label: '人文艺术学院',
  //     value: 'b26447ea005c4193b69f0fe4d5ffb9e1',
  //   },
  // ];
  // const dayList = [
  //   {
  //     label: '星期一',
  //     value: 'Monday',
  //   },
  //   {
  //     label: '星期二',
  //     value: 'Tuesday',
  //   },
  //   {
  //     label: '星期三',
  //     value: 'Wednesday',
  //   },
  //   {
  //     label: '星期四',
  //     value: 'Thursday',
  //   },
  //   {
  //     label: '星期五',
  //     value: 'Friday',
  //   },
  //   {
  //     label: '星期六',
  //     value: 'Saturday',
  //   },
  //   {
  //     label: '星期日',
  //     value: 'Sunday',
  //   },
  // ];
  const schemas: FormSchema[] = [
    {
      field: 'courseId',
      component: 'NInput',
      label: '课程ID',
      componentProps: {
        placeholder: '请输入该课程id编号',
        onInput: (e: any) => {
          formParams.courseId = e;
        },
      },
    },
    {
      field: 'name',
      component: 'NInput',
      label: '课程名称',
      componentProps: {
        placeholder: '请输入课程名',
        onInput: (e: any) => {
          formParams.name = e;
        },
      },
    },
    {
      field: 'facultyName',
      component: 'NInput',
      label: '学院',
      componentProps: {
        placeholder: '请输入课程所属学院',
        onInput: (e: any) => {
          formParams.facultyName = e;
        },
      },
    },
    {
      field: 'teacherName',
      component: 'NInput',
      label: '授课教师',
      componentProps: {
        placeholder: '请输入该课程的授课教师名称',
        onInput: (e: any) => {
          formParams.teacherName = e;
        },
      },
    },
    {
      field: 'isDeleted',
      component: 'NSwitch',
      label: '是否禁用',
      componentProps: {
        placeholder: '禁用',
        onUpdateValue: (e: any) => {
          if (e) formParams.isDeleted = '1';
          else formParams.isDeleted = '0';
        },
      },
    },
  ];

  // const dialog = useDialog();
  const router = useRouter();
  // const formRef: any = ref(null);
  const message = useMessage();
  const actionRef = ref();
  const token = storage.get(ACCESS_TOKEN);

  // const showModal = ref(false);
  const showTableModal = ref(false);
  // const showExportModal = ref(false);
  const formBtnLoading = ref(false);
  // const isDeletedList = [
  //   {
  //     label: '禁用中',
  //     value: '1',
  //   },
  //   {
  //     label: '启用中',
  //     value: '0',
  //   },
  // ];
  const formParams = reactive({
    courseId: '',
    name: '',
    facultyName: '',
    teacherName: '',
    isDeleted: '0',
  });

  const recordGlobal = reactive({
    courseId: '',
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
            label: '查看详情',
            type: 'info',
            onClick: handleQueryStudent.bind(null, record),
            // handleQueryStudent.bind(null, record.id),
            // 根据业务控制是否显示 isShow 和 auth 是并且关系
            ifShow: () => {
              return true;
            },
            // 根据权限控制是否显示: 有权限，会显示，支持多个
            auth: ['basic_list'],
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
    labelWidth: 80,
    schemas,
  });

  function exportTable() {
    exportcoursedemo().then((res) => {
      //构造blob对象，type是文件类型，详情可以参阅blob文件类型;
      let blob = new Blob([res], { type: 'application/vnd.ms-excel' }); //我是下载zip压缩包
      let url = window.URL.createObjectURL(blob); //生成下载链接
      const link = document.createElement('a'); //创建超链接a用于文件下载
      link.href = url; //赋值下载路径
      // link.target = '_blank'; //打开新窗口下载，不设置则为在本窗口下载
      link.download = `courselist.xlsx`; //下载的文件名称（不设置就会随机生成）
      link.click(); //点击超链接触发下载
      URL.revokeObjectURL(url); //释放URL
      // showExportModal.value = false;
      message.success('下载完成');
    });
  }
  const loadDataTable = async (res) => {
    return await getTableList(token, { ...formParams, ...params.value, ...res });
  };

  const loadStudentsTable = async (res) => {
    return await getCourseList(token, { ...recordGlobal, ...params.value, ...res });
  };

  function onCheckedRow(rowKeys) {
    console.log(rowKeys);
  }

  function reloadTable() {
    actionRef.value.reload();
  }

  function handleQueryStudent(record: Recordable) {
    console.log('点击了编辑', record);
    router.push({ name: 'course-info', params: { id: record.id } });
  }

  function handleSubmit(values: Recordable) {
    console.log(values);
    reloadTable();
  }

  function handleReset(values: Recordable) {
    formParams.courseId = '';
    formParams.name = '';
    formParams.facultyName = '';
    formParams.teacherName = '';
    formParams.isDeleted = '';
    reloadTable();
    console.log(values);
  }
</script>

<style lang="less" scoped></style>
