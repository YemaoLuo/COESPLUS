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
        <n-button type="primary" @click="addTable">
          <template #icon>
            <n-icon>
              <PlusOutlined />
            </n-icon>
          </template>
          添加课程
        </n-button>
        <div>&nbsp</div>
        <n-button type="primary" @click="exportTable">
          <template #icon>
            <n-icon>
              <FileExcelOutlined />
            </n-icon>
          </template>
          导出课程信息
        </n-button>
      </template>

      <template #toolbar>
        <n-button type="primary" @click="handleEdit">编辑表格</n-button>
      </template>
    </BasicTable>
    <!--      ------------------------------------------添加课程列表--------------------------------------------------------->
    <n-space>
      <n-modal v-model:show="showTeacherTable" preset="card" transform-origin="center">
        <n-Form>
          <n-form-item label="名称" path="name">
            <n-input placeholder="请输入老师名称" v-model:value="TeacherformParams.name" />
          </n-form-item>
          <n-button type="info" @click="handleSubmit">查找老师</n-button>
        </n-Form>
        <BasicTable
          :columns="columnsTeacher"
          :request="loadCourseTeacherTable"
          :row-key="(row) => row.id"
          ref="actionRef"
          :actionColumn="actionCourseTeacherColumn"
          @update:checked-row-keys="onCheckedRow"
          :scroll-x="1090"
        />
        <n-modal v-model:show="showModal" :show-icon="false" preset="dialog" title="添加课程">
          <n-form
            :model="addformParams"
            :rules="rules"
            ref="formRef"
            label-placement="left"
            :label-width="80"
            class="py-4"
          >
            <n-form-item label="名称" path="name">
              <n-input placeholder="请输入课程名称" v-model:value="addformParams.name" />
            </n-form-item>
            <n-form-item label="学院名称" path="facultyId">
              <n-select
                v-model:value="addformParams.facultyId"
                placeholder="请输入学院名称"
                :options="facultyList"
              />
            </n-form-item>
            <n-form-item label="课程编号" path="courseId">
              <n-input placeholder="请输入课程编号" v-model:value="addformParams.courseId" />
            </n-form-item>
            <n-form-item label="教室" path="classroom">
              <n-input placeholder="请输入教室号" v-model:value="addformParams.classroom" />
            </n-form-item>
            <n-form-item label="开始时间" path="startTime">
              <n-time-picker v-model:value="addformParams.startTime" format="HH:mm " />
            </n-form-item>
            <n-form-item label="结束时间" path="endTime">
              <n-time-picker v-model:value="addformParams.endTime" format="HH:mm" />
            </n-form-item>
            <n-form-item label="day" path="day">
              <n-select
                placeholder="请输入上课日期"
                v-model:value="addformParams.day"
                :options="dayList"
                multiple
              />
            </n-form-item>
            <n-form-item label="座位数" path="seat">
              <n-input placeholder="请输入座位数" v-model:value="addformParams.seat" />
            </n-form-item>
          </n-form>

          <template #action>
            <n-space>
              <n-button @click="() => (showModal = false)">取消</n-button>
              <n-button type="info" :loading="formBtnLoading" @click="confirmForm">确定</n-button>
            </n-space>
          </template>
        </n-modal>
      </n-modal>
    </n-space>
    <!--    ------------------------------------------------------查看学生列表-------------------------------------------->
    <n-space>
      <n-modal v-model:show="showTableModal" preset="card" transform-origin="center">
        <BasicTable
          :columns="columnsStudents"
          :request="loadStudentsTable"
          :row-key="(row) => row.id"
          ref="actionRefStudent"
          :action-column="actionDeleteCourseStudentColumn"
          @update:checked-row-keys="onCheckedRow"
          :scroll-x="1090"
        >
          <template #tableTitle>
            <n-button type="primary" @click="addStudentTable">
              <template #icon>
                <n-icon>
                  <PlusOutlined />
                </n-icon>
              </template>
              添加课程学生
            </n-button>
            <div>&nbsp</div>
            <n-button type="primary" @click="exportstudentTable">
              <template #icon>
                <n-icon>
                  <FileExcelOutlined />
                </n-icon>
              </template>
              导出课程学生信息
            </n-button>
          </template>
        </BasicTable>
        <n-space>
          <n-modal v-model:show="showStudentTable" preset="card" transform-origin="center">
            <n-Form>
              <n-form-item label="学生名字" path="name">
                <n-input placeholder="请输入学生名字" v-model:value="StudentformParams.name" />
              </n-form-item>
              <n-button type="info" @click="handleSubmit">查找学生</n-button>
            </n-Form>
            <BasicTable
              :columns="columnsStudents"
              :request="loadAllStudentTable"
              :row-key="(row) => row.id"
              ref="actionRef"
              :actionColumn="actionCourseStudentColumn"
              @update:checked-row-keys="onCheckedRow"
              :scroll-x="1090"
            />
          </n-modal>
        </n-space>

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
    <!--  -------------------------------------------------------  导出课程列表---------------->
    <n-modal v-model:show="showExportModal" :show-icon="false" preset="dialog" title="导出课程信息">
      <n-form
        :model="formParams"
        ref="formRef"
        label-placement="left"
        :label-width="80"
        class="py-4"
      >
        <n-form-item label="名称" path="name">
          <n-input placeholder="请输入课程名称" v-model:value="formParams.name" />
        </n-form-item>
        <n-form-item label="禁用状态" path="isDeleted">
          <n-select
            v-model:value="formParams.isDeleted"
            placeholder="请选择禁用状态"
            :options="isDeletedList"
          />
        </n-form-item>
        <n-form-item label="课程编号" path="courseId">
          <n-input placeholder="请输入课程编号 " v-model:value="formParams.courseId" />
        </n-form-item>
        <n-form-item label="学院名称" path="facultyName">
          <n-select
            v-model:value="formParams.facultyName"
            placeholder="请输入学院名称"
            :options="facultyList"
          />
        </n-form-item>
        <n-form-item label="老师名字" path="teacherName">
          <n-input placeholder="请输入老师名字 " v-model:value="formParams.teacherName" />
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
import { h, reactive, ref, unref } from "vue";
  import { SelectOption, useDialog, useMessage } from 'naive-ui';
  import { BasicTable, TableAction } from '@/components/Table';
  import { BasicForm, FormSchema, useForm } from '@/components/Form/index';
  import {
    addcoursedemo,
    delTableListDemo,
    disableTableListDemo,
    enableTableListDemo,
    exportcoursedemo,
    exportcoursestudentdemo,
    getStudentsTableListDemo,
    getTableListDemo,
    getTeacherList,
    getAllStudentList,
    addCourseStudent,
    DeleteCourseStudent,
  } from '@/api/table/courselistDemo';
  // import {
  //   delTableListDemo as delFaculty,
  //   disableTableListDemo as disableFaculty,
  // } from '@/api/table/facultyListDemo';
  import { columns } from './columns';
  import { columnsStudents } from './columnsStudents';
  import { columnsTeacher } from './columnsTeacher';
  //import { TableExport, UserPlus } from '@vicons/tabler';
  import { PlusOutlined, FileExcelOutlined } from '@vicons/antd';
import { useRoute, useRouter } from "vue-router";
  import { getFacultyList } from '@/api/table/teacherlistDemo';
import { useDataSource } from "@/components/Table/src/hooks/useDataSource";
  function addStudentTable() {
    showStudentTable.value = true;
  }
  const addformParams = reactive({
    name: '',
    facultyId: '',
    teacherId: '',
    courseId: '',
    classroom: '',
    startTime: null,
    endTime: null,
    day: '',
    seat: '50',
    seatChosen: '',
  });

  const exportstudentformParams = reactive({
    courseId: '',
    isDeleted: '',
  });
  let facultyList = ref<SelectOption[]>([]);
  const facultyParams = reactive({
    name: '',
  });
  const dayList = [
    {
      label: '星期一',
      value: 'Monday',
    },
    {
      label: '星期二',
      value: 'Tuesday',
    },
    {
      label: '星期三',
      value: 'Wednesday',
    },
    {
      label: '星期四',
      value: 'Thursday',
    },
    {
      label: '星期五',
      value: 'Friday',
    },
    {
      label: '星期六',
      value: 'Saturday',
    },
    {
      label: '星期日',
      value: 'Sunday',
    },
  ];
  // function confirmExportstudentForm(e) {
  //   e.preventDefault();
  //   formBtnLoading.value = true;
  //   formRef.value.validate(async (errors) => {
  //     if (!errors) {
  //       exportcoursestudentdemo({ ...exportstudentformParams, ...params.value, e }).then((res) => {
  //         //构造blob对象，type是文件类型，详情可以参阅blob文件类型;
  //         let blob = new Blob([res], { type: 'application/vnd.ms-excel' });
  //         let url = window.URL.createObjectURL(blob); //生成下载链接
  //         const link = document.createElement('a'); //创建超链接a用于文件下载
  //         link.href = url; //赋值下载路径
  //         // link.target = '_blank'; //打开新窗口下载，不设置则为在本窗口下载
  //         link.download = `CourseStudentlist.xlsx`; //下载的文件名称（不设置就会随机生成）
  //         link.click(); //点击超链接触发下载
  //         URL.revokeObjectURL(url); //释放URL
  //         showExportModal.value = false;
  //         message.success('下载完成');
  //       });
  //     } else {
  //       message.error('请填写完整信息');
  //     }
  //     formBtnLoading.value = false;
  //   });
  // }
  const rules = {
    name: [
      {
        required: true,
        trigger: ['blur', 'input'],
        message: '请输入名称',
      },
    ],
    facultyIdId: [
      {
        required: true,
        trigger: ['blur', 'input'],
        message: '请选择学院',
      },
    ],
    teacherId: [
      {
        required: true,
        trigger: ['blur', 'input'],
        message: '请输入教师编号',
      },
    ],
    courseIdId: [
      {
        required: true,
        trigger: ['blur', 'input'],
        message: '请输入课程编号',
      },
    ],
    classroom: [
      {
        required: true,
        trigger: ['blur', 'input'],
        message: '请输入课室号',
      },
      {
        pattern: /[A-Z]\d(0|1)\d/,
        message: '请输入正确格式的课室号',
        trigger: ['blur', 'input'],
      },
    ],
  };
  function confirmForm(e) {
    e.preventDefault();
    formBtnLoading.value = true;
    formRef.value.validate(async (errors) => {
      if (!errors) {
        const response = await addcoursedemo({ ...addformParams, ...params.value });
        if (response.code == 200) {
          console.log('添加成功');
        }
        if (response.code == 400) {
          message.error(response.message);
        }
        showModal.value = false;
        showTeacherTable.value = false;
        reloadTable();
        message.toString();
        setTimeout(() => {
          showModal.value = false;
          showTeacherTable.value = false;
          router.push({ path: '/redirect' + unref(route).fullPath });
        });
      } else {
        message.error('请填写完整信息');
      }
      formBtnLoading.value = false;
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

  const dialog = useDialog();
  const router = useRouter();
  const route = useRoute();
  const formRef: any = ref(null);
  const message = useMessage();
  const actionRef = ref();
  const actionRefStudent = ref();

  const showModal = ref(false);
  const showTeacherTable = ref(false);
  const showStudentTable = ref(false);
  const showTableModal = ref(false);
  const showExportModal = ref(false);
  // const showExportstudentModal = ref(false);
  const formBtnLoading = ref(false);
  const isDeletedList = [
    {
      label: '禁用中',
      value: '1',
    },
    {
      label: '启用中',
      value: '0',
    },
  ];
  const formParams = reactive({
    courseId: '',
    name: '',
    facultyName: '',
    teacherName: '',
    isDeleted: '',
  });

  const recordGlobal = reactive({
    courseId: '',
  });
  const params = ref({
    pageSize: 10,
  });
  const actionColumn = reactive({
    width: 240,
    title: '操作',
    key: 'action',
    fixed: 'right',
    align: 'center',
    render(record) {
      return h(TableAction as any, {
        style: 'button',
        actions: [
          {
            label: '已选学生',
            type: 'info',
            onClick: handleQueryStudent.bind(null, record),
            ifShow: () => {
              return true;
            },
          },
          {
            label: '禁用',
            type: 'warning',
            //icon: 'ic:outline-delete-outline',
            onClick: handleDisabled.bind(null, record),
            ifShow: () => {
              if (record.isDeleted == '1') return false;
              else return true;
            },
          },
          {
            label: '启用',
            type: 'success',
            onClick: handleEnabled.bind(null, record),
            ifShow: () => {
              if (record.isDeleted == '0') return false;
              else return true;
            },
          },
          {
            label: '删除',
            type: 'error',
            onClick: handleDelete.bind(null, record),
            ifShow: () => {
              return true;
            },
          },
        ],
        select: (key) => {
          // message.info(`您点击了，${key} 按钮`);
        },
      });
    },
  });
  const actionCourseTeacherColumn = reactive({
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
            label: '给该老师添加课程',
            type: 'info',
            onClick: handleAddTeacherCourse.bind(null, record),
            // handleQueryStudent.bind(null, record.id),
            // 根据业务控制是否显示 isShow 和 auth 是并且关系
            ifShow: () => {
              return true;
            },
          },
        ],
        select: (key) => {
          // message.info(`您点击了，${key} 按钮`);
        },
      });
    },
  });
  const actionCourseStudentColumn = reactive({
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
            label: '添加该学生',
            type: 'info',
            onClick: handleAddStudentCourse.bind(null, record),
            // handleQueryStudent.bind(null, record.id),
            // 根据业务控制是否显示 isShow 和 auth 是并且关系
            ifShow: () => {
              return true;
            },
          },
        ],
        select: (key) => {
          // message.info(`您点击了，${key} 按钮`);
        },
      });
    },
  });
  const actionDeleteCourseStudentColumn = reactive({
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
            label: '删除该学生',
            type: 'error',
            onClick: handleDeleteStudent.bind(null, record),
            // handleQueryStudent.bind(null, record.id),
            // 根据业务控制是否显示 isShow 和 auth 是并且关系
            ifShow: () => {
              return true;
            },
          },
        ],
        select: (key) => {
          // message.info(`您点击了，${key} 按钮`);
        },
      });
    },
  });
  const [register, {}] = useForm({
    gridProps: { cols: '1 s:1 m:2 l:3 xl:4 2xl:4' },
    labelWidth: 80,
    schemas,
  });
  // const [Teacherregister, {}] = useForm({
  //   gridProps: { cols: '1 s:1 m:2 l:3 xl:4 2xl:4' },
  //   labelWidth: 80,
  //   Teacherschemas,
  // });
  async function addTable() {
    //showModal.value = true;
    showTeacherTable.value = true;
    const response = await getFacultyList({ ...facultyParams, ...params.value });
    console.log(response.result);
    facultyList.value = response.result;
  }
  function exportTable() {
    exportcoursedemo({ ...formParams, ...params.value }).then((res) => {
      //构造blob对象，type是文件类型，详情可以参阅blob文件类型;
      let blob = new Blob([res], { type: 'application/vnd.ms-excel' }); //我是下载zip压缩包
      let url = window.URL.createObjectURL(blob); //生成下载链接
      const link = document.createElement('a'); //创建超链接a用于文件下载
      link.href = url; //赋值下载路径
      // link.target = '_blank'; //打开新窗口下载，不设置则为在本窗口下载
      link.download = `courselist.xlsx`; //下载的文件名称（不设置就会随机生成）
      link.click(); //点击超链接触发下载
      URL.revokeObjectURL(url); //释放URL
      showExportModal.value = false;
      message.success('下载完成');
    });
  }
  function exportstudentTable() {
    //showExportstudentModal.value = true;
    exportstudentformParams.courseId = recordGlobal.courseId;
    exportcoursestudentdemo({ ...exportstudentformParams, ...params.value }).then((res) => {
      //构造blob对象，type是文件类型，详情可以参阅blob文件类型;
      let blob = new Blob([res], { type: 'application/vnd.ms-excel' });
      let url = window.URL.createObjectURL(blob); //生成下载链接
      const link = document.createElement('a'); //创建超链接a用于文件下载
      link.href = url; //赋值下载路径
      // link.target = '_blank'; //打开新窗口下载，不设置则为在本窗口下载
      link.download = `CourseStudentlist.xlsx`; //下载的文件名称（不设置就会随机生成）
      link.click(); //点击超链接触发下载
      URL.revokeObjectURL(url); //释放URL
      showExportModal.value = false;
      message.success('下载完成');
    });
  }
  const loadDataTable = async (res) => {
    return await getTableListDemo({ ...formParams, ...params.value, ...res });
  };
  const TeacherformParams = reactive({
    name: '',
    sex: '',
    teacherId: '',
    telephone: '',
    facultyId: '',
    joinYear: '',
    isDeleted: '',
  });
  const loadCourseTeacherTable = async (res) => {
    return await getTeacherList({ ...TeacherformParams, ...params.value, ...res });
  };
  const StudentformParams = reactive({
    name: '',
    studentId: '',
    telephone: '',
    faculty: '',
    joinYear: '',
    isDeleted: '',
  });
  const loadAllStudentTable = async (res) => {
    return await getAllStudentList({ ...StudentformParams, ...params.value, ...res });
  };
  const loadStudentsTable = async (res) => {
    return await getStudentsTableListDemo({ ...recordGlobal, ...params.value, ...res });
  };

  function onCheckedRow(rowKeys) {
    console.log(rowKeys);
  }

  function reloadTable() {
    actionRef.value.reload();
  }

  function reloadStudentTable() {
    actionRefStudent.value.reload();
  }

  // async function handleDisabled(record: Recordable) {
  //   console.log('点击了禁用', record);
  //   const response = await disableTableListDemo(record);
  //   //disableFaculty(record);
  //   if (response.code == 200) {
  //     message.info('已禁用学生');
  //   }
  //   if (response.code == 400) {
  //     message.error(response.result);
  //   }
  //   reloadTable();
  // }
  function handleDisabled(record) {
    console.log(record);
    dialog.info({
      title: '警告',
      content: `是否禁用课程 “${record.name}”`,
      positiveText: '确定',
      negativeText: '取消',
      onPositiveClick: async () => {
        const response = await disableTableListDemo(record.id + '?force=false');
        if (response.code == 400) {
          console.log('400');
          dialog.warning({
            title: '警告',
            content: `学院关联老师和学生，是否强制禁用课程 “${record.name}”`,
            positiveText: '确定',
            negativeText: '取消',
            onPositiveClick: async () => {
              const responseForce = await disableTableListDemo(record.id + '?force=true');
              if (responseForce.code == 200) {
                message.success('已强制禁用课程');
              }
              setTimeout(() => {
                reloadTable();
              });
            },
            onNegativeClick: () => {},
          });
        }
        reloadTable();
      },
      onNegativeClick: () => {},
    });
  }

  async function handleEnabled(record: Recordable) {
    console.log('点击了启用', record);
    const response = await enableTableListDemo(record.id);
    if (response.code == 200) {
      message.info('已启用课程');
    }
    if (response.code == 400) {
      message.error(response.result);
    }
    reloadTable();
  }

  function handleEdit(record: Recordable) {
    // console.log('点击了编辑', record);
    router.push({ name: 'basic-info-course', params: { id: record.id } });
  }

  function handleQueryStudent(record: Recordable) {
    recordGlobal.courseId = record.id;
    // console.log('点击了查学生', record);
    showTableModal.value = true;
    // getStudentsTableListDemo(record);
  }
  function handleAddTeacherCourse(record: Recordable) {
    addformParams.teacherId = record.id;
    console.log('给该编号' + record + '的老师添加课程');
    showModal.value = true;
    // getStudentsTableListDemo(record);
  }
  const addCourseStudentformParams = reactive({
    courseId: '',
    studentId: '',
  });
  async function handleAddStudentCourse(Studentrecord: Recordable) {
    addCourseStudentformParams.studentId = Studentrecord.id;
    addCourseStudentformParams.courseId = recordGlobal.courseId;
    //console.log('给该编号' + Studentrecord.id + '的老师添加课程');
    const response = await addCourseStudent({ ...addCourseStudentformParams, ...params.value });
    if (response.code == 200) {
      message.success('添加学生成功');
    }
    message.toString();
    setTimeout(() => {
      reloadStudentTable();
    });
    showStudentTable.value = false;
    // getStudentsTableListDemo(record);
  }
  // function handleDelete(record) {
  //   console.log(record);
  //   dialog.info({
  //     title: '警告',
  //     content: `是否删除课程 “${record.name}”`,
  //     positiveText: '确定',
  //     negativeText: '取消',
  //     onPositiveClick: () => {
  //       delTableListDemo(record.id);
  //       reloadTable();
  //       message.success('删除成功');
  //     },
  //     onNegativeClick: () => {},
  //   });
  // }
  function handleDelete(record) {
    console.log(record);
    dialog.info({
      title: '警告',
      content: `是否删除课程 “${record.name}”`,
      positiveText: '确定',
      negativeText: '取消',
      onPositiveClick: async () => {
        const response = await delTableListDemo(record.id + '?force=false');
        if (response.code == 400) {
          console.log('400');
          dialog.warning({
            title: '警告',
            content: `学院关联老师和学生，是否强制删除课程 “${record.name}”`,
            positiveText: '确定',
            negativeText: '取消',
            onPositiveClick: async () => {
              const responseForce = await delTableListDemo(record.id + '?force=true');
              if (responseForce.code == 200) {
                message.success('已强制删除课程');
              } else if (responseForce.code == 400) {
                message.error(responseForce.message.toString());
              }
              setTimeout(() => {
                reloadTable();
              });
            },
            onNegativeClick: () => {},
          });
        }
        reloadTable();
      },
      onNegativeClick: () => {},
    });
  }
  // const deleteCourseStudentformParams = reactive({
  //   deletedId: '',
  // });
  function handleDeleteStudent(record) {
    console.log(record);
    dialog.info({
      title: '警告',
      content: `是否删除该学生 “${record.name}”`,
      positiveText: '确定',
      negativeText: '取消',
      onPositiveClick: async () => {
        // deleteCourseStudentformParams.studentId = record.id;
        //deleteCourseStudentformParams.courseId = recordGlobal.courseId;
        const response = await DeleteCourseStudent(record.id);
        if (response.code == 400) {
          message.error(response.message);
        }
        if (response.code == 200) {
          message.success('删除成功');
        }
        reloadStudentTable();
      },
      onNegativeClick: () => {},
    });
    // showTableModal.value = false;
    // reloadStudentTable();
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
