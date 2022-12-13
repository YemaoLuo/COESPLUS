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
          导出课程表
        </n-button>
      </template>
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

    <n-space>
      <n-modal
        v-model:show="showNoticeModal"
        :show-icon="false"
        preset="dialog"
        title="发送班级通知"
      >
        <n-form-item path="noticeMessage">
          <n-input
            v-model:value="noticeFormParams.message"
            type="textarea"
            placeholder="在此输入通知消息吧！"
            round
          />
        </n-form-item>
        <template #action>
          <n-space>
            <n-button @click="() => (showNoticeModal = false)">取消</n-button>
            <n-button type="info" :loading="formBtnLoading" @click="noticeSubmit">确定</n-button>
          </n-space>
        </template>
      </n-modal>
    </n-space>

    <n-space>
      <n-modal v-model:show="showGradeModal" preset="dialog" transform-origin="center">
        <div>
          <n-form-item label="请输入成绩" path="Grade">
            <n-input v-model:value="recordGlobal.grade" />
          </n-form-item>
        </div>
        <template #action>
          <n-space>
            <n-button @click="() => (showGradeModal = false)">取消</n-button>
            <n-button type="info" :loading="formBtnLoading" @click="gradingSubmit">确定</n-button>
          </n-space>
        </template>
      </n-modal>
    </n-space>

    <n-space>
      <n-modal v-model:show="showTableModal" preset="card" transform-origin="center">
        <BasicTable
          :columns="columnsStudents"
          :request="loadStudentsTable"
          :row-key="(row) => row.id"
          ref="actionRef"
          :actionColumn="actionStudentColumn"
          @update:checked-row-keys="onCheckedRow"
          :scroll-x="1090"
        >
          <template #tableTitle>
            <n-button type="primary" @click="exportStudentScoreTable">
              <template #icon>
                <!--            <n-icon>-->
                <!--              <TableExport />-->
                <!--            </n-icon>-->
              </template>
              导出课程学生成绩
            </n-button>
          </template>
        </BasicTable>
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
  </n-card>
</template>

<script lang="ts" setup>
  import { h, reactive, ref } from 'vue';
  // import { useMessage } from 'naive-ui';
  import { BasicTable, TableAction } from '@/components/Table';
  import { BasicForm, FormSchema, useForm } from '@/components/Form/index';
  // import {
  //   getStudentsTableListDemo,
  //   getTableList,
  //   myTeachingList,
  // } from '@/api/table/courseTeacherList';
  import {
    getTableList,
    gradingList,
    exportcourse,
    getStudentsTableListDemo,
    exportStudentScore, NoticeDemo,
  } from '@/api/table/courseTeacherList';
  import { columns } from './columns';
  //import { PlusOutlined } from '@vicons/antd';
  import { columnsStudents } from './columnsStudents';
  // import { PlusOutlined } from '@vicons/antd';
  // import { useRouter } from 'vue-router';
  import { useUserStore } from '@/store/modules/user';
  import { useMessage } from 'naive-ui';
  //import {exportcoursedemo} from "@/api/table/courselist";
  // import { storage } from '@/utils/Storage';
  // import { ACCESS_TOKEN } from '@/store/mutation-types';
  function exportTable() {
    exportcourse().then((res) => {
      //构造blob对象，type是文件类型，详情可以参阅blob文件类型;
      let blob = new Blob([res], { type: 'application/vnd.ms-excel' }); //我是下载zip压缩包
      let url = window.URL.createObjectURL(blob); //生成下载链接
      const link = document.createElement('a'); //创建超链接a用于文件下载
      link.href = url; //赋值下载路径
      // link.target = '_blank'; //打开新窗口下载，不设置则为在本窗口下载
      link.download = `CourseStudentList.xlsx`; //下载的文件名称（不设置就会随机生成）
      link.click(); //点击超链接触发下载
      URL.revokeObjectURL(url); //释放URL
      // showExportModal.value = false;
      message.success('下载完成');
    });
  }
  function exportStudentScoreTable() {
    exportStudentScore({ ...recordGlobal }).then((res) => {
      //构造blob对象，type是文件类型，详情可以参阅blob文件类型;
      let blob = new Blob([res], { type: 'application/vnd.ms-excel' }); //我是下载zip压缩包
      let url = window.URL.createObjectURL(blob); //生成下载链接
      const link = document.createElement('a'); //创建超链接a用于文件下载
      link.href = url; //赋值下载路径
      // link.target = '_blank'; //打开新窗口下载，不设置则为在本窗口下载
      link.download = `CourseStudentList.xlsx`; //下载的文件名称（不设置就会随机生成）
      link.click(); //点击超链接触发下载
      URL.revokeObjectURL(url); //释放URL
      // showExportModal.value = false;
      message.success('下载完成');
    });
  }
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
  ];
  const message = useMessage();
  // const router = useRouter();
  const formRef: any = ref(null);
  // const message = useMessage();
  const actionRef = ref();
  const token = useUserStore().getToken;
  const showTableModal = ref(false);

  const showNoticeModal = ref(false);
  const showGradeModal = ref(false);
  const showExportModal = ref(false);
  // const showModal = ref(false);
  const formBtnLoading = ref(false);
  const formParams = reactive({
    courseId: '',
    name: '',
    facultyName: '',
    teacherName: '',
    isDeleted: '0',
  });
  const noticeFormParams = reactive({
    id: '',
    message: '',
  });

  const params = ref({
    pageSize: 5,
  });

  const recordGlobal = reactive({
    courseId: '',
    studentId: '',
    grade: '',
  });

  const actionColumn = reactive({
    width: 180,
    title: '操作',
    key: 'action',
    align: 'center',
    fixed: 'right',
    render(record) {
      return h(TableAction as any, {
        style: 'button',
        actions: [
          {
            label: '课程学生',
            type: 'info',
            onClick: handleQueryStudent.bind(null, record),
            // handleQueryStudent.bind(null, record.id),
            // 根据业务控制是否显示 isShow 和 auth 是并且关系
            ifShow: () => {
              return true;
            },
            // 根据权限控制是否显示: 有权限，会显示，支持多个
          },
          {
            label: '发送课堂通知',
            type: 'success',
            onClick: handleNoticing.bind(null, record),
            // handleQueryStudent.bind(null, record.id),
            // 根据业务控制是否显示 isShow 和 auth 是并且关系
            ifShow: () => {
              return true;
            },
            // 根据权限控制是否显示: 有权限，会显示，支持多个
          },
        ],
        select: (key) => {
          window['$message'].info(`您点击了，${key} 按钮`);
        },
      });
    },
  });

  const actionStudentColumn = reactive({
    width: 40,
    title: '操作',
    key: 'action',
    align: 'center',
    fixed: 'right',
    render(record) {
      return h(TableAction as any, {
        style: 'button',
        actions: [
          {
            label: '评分',
            type: 'info',
            onClick: handleGrading.bind(null, record),
            // 根据业务控制是否显示 isShow 和 auth 是并且关系
            ifShow: () => {
              return true;
            },
            // 根据权限控制是否显示: 有权限，会显示，支持多个
          },
        ],
        select: (key) => {
          window['$message'].info(`您点击了，${key} 按钮`);
        },
      });
    },
  });

  const [register, {}] = useForm({
    gridProps: { cols: '1 s:1 m:2 l:3 xl:4 2xl:4' },
    labelWidth: 80,
    schemas,
  });

  // function addTable() {
  //   showModal.value = true;
  // }

  const loadDataTable = async (res) => {
    return await getTableList(token, { ...formParams, ...params.value, ...res });
  };

  const loadStudentsTable = async (res) => {
    return await getStudentsTableListDemo(token, { ...recordGlobal, ...params.value, ...res });
  };

  function onCheckedRow(rowKeys) {
    console.log(rowKeys);
  }

  function reloadTable() {
    actionRef.value.reload();
  }

  function handleQueryStudent(record: Recordable) {
    recordGlobal.courseId = record.id;
    console.log('点击了查学生', record);
    showTableModal.value = true;
    // getStudentsTableListDemo(record);
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

  // function handleEdit(record: Recordable) {
  //   console.log('点击了编辑', record);
  //   router.push({ name: 'basic-info', params: { id: record.id } });
  // }

  function handleGrading(record: Recordable) {
    console.log('点击了评分', record);
    // recordGlobal.courseId = record.courseId;
    recordGlobal.studentId = record.id;
    recordGlobal.grade = record.grade;
    showGradeModal.value = true;
  }

  function handleNoticing(record: Recordable) {
    noticeFormParams.id = record.id;
    showNoticeModal.value = true;
  }

  function noticeSubmit() {
    showGradeModal.value = false;
    NoticeDemo(token, { ...noticeFormParams });
  }

  function gradingSubmit() {
    showGradeModal.value = false;
    gradingList(token, { ...recordGlobal });
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
