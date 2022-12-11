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
          新增学期信息
        </n-button>
      </template>
    </BasicTable>
    <!-------------------------------------------课程设置----------------------------------------------------->
    <n-space>
      <n-modal v-model:show="showTableModal" preset="card" transform-origin="center">
        <BasicTable
          :columns="columnsCourse"
          :request="loadSemesterCourseTable"
          :row-key="(row) => row.id"
          ref="actionRef"
          :actionColumn="actionCourseColumn"
          @update:checked-row-keys="onCheckedRow"
          :scroll-x="1090"
        >
          <template #tableTitle>
            <n-button type="primary" @click="addCourse">
              <template #icon>
                <n-icon>
                  <PlusOutlined />
                </n-icon>
              </template>
              新增学期课程
            </n-button>
          </template>
        </BasicTable>
        <n-modal v-model:show="showCourseModal" :show-icon="false" preset="card" title="添加课程">
          <BasicTable
            :columns="columnsAllCourse"
            :request="loadAllCourseTable"
            :row-key="(row) => row.id"
            ref="actionRef"
            :actionColumn="actionCourseChooseColumn"
            @update:checked-row-keys="onCheckedRow"
            :scroll-x="90"
          />

        </n-modal>
      </n-modal>
    </n-space>
    <!----------------------------------------学分显示-------------------------------------------------------------->
    <n-space>
      <n-modal v-model:show="showCreditTable" preset="card" transform-origin="center">
        <BasicTable
          :columns="columnsCredit"
          :request="loadSemesterCreditTable"
          :row-key="(row) => row.id"
          ref="actionRef"
          :actionColumn="actionCreditColumn"
          @update:checked-row-keys="onCheckedRow"
          :scroll-x="1090"
        >
          <template #tableTitle>
            <n-button type="primary" @click="addCredit">
              <template #icon>
                <n-icon>
                  <PlusOutlined />
                </n-icon>
              </template>
              新增学分限制
            </n-button>
          </template>
        </BasicTable>
        <n-modal
          v-model:show="showCreditModal"
          :show-icon="false"
          preset="dialog"
          title="添加学分限制"
        >
          <n-form
            :model="addCreditParams"
            :rules="rules"
            ref="CreditformRef"
            label-placement="left"
            :label-width="80"
            class="py-4"
          >
            <n-form-item label="学院" path="facultyName">
              <n-select
                v-model:value="addCreditParams.facultyId"
                placeholder="请输入学院名称"
                :options="facultyList"
              />
            </n-form-item>
            <n-form-item label="学分限制" path="creditLimit">
              <n-input placeholder="请输入学分限制" v-model:value="addCreditParams.creditLimit" />
            </n-form-item>
          </n-form>

          <template #action>
            <n-space>
              <n-button @click="() => (showCreditModal = false)">取消</n-button>
              <n-button type="info" :loading="formBtnLoading" @click="confirmAddCredit"
                >确定</n-button
              >
            </n-space>
          </template>
        </n-modal>
      </n-modal>
    </n-space>
    <!---------------------------------------------------------------    抢课添加------------------------------------>
    <n-modal v-model:show="showModal" :show-icon="false" preset="dialog" title="添加抢课时段">
      <n-form
        :model="addformParams"
        :rules="rules"
        ref="formRef"
        label-placement="left"
        :label-width="80"
        class="py-4"
      >
        <n-form-item label="学期" path="name">
          <n-input placeholder="请输入学期" v-model:value="addformParams.semester" />
        </n-form-item>
        <n-form-item label="入学时间" path="name">
          <n-input placeholder="请输入入学时间" v-model:value="addformParams.joinYear" />
        </n-form-item>
        <n-form-item label="抢课开始" path="StartTime">
          <n-date-picker
            v-model:formatted-value="addformParams.startTime"
            value-format="yyyy-MM-dd HH:mm"
            type="datetime"
          />
        </n-form-item>
        <n-form-item label="抢课结束" path="EndTime">
          <n-date-picker
            v-model:formatted-value="addformParams.endTime"
            value-format="yyyy-MM-dd HH:mm"
            type="datetime"
          />
        </n-form-item>
      </n-form>

      <template #action>
        <n-space>
          <n-button @click="() => (showModal = false)">取消</n-button>
          <n-button type="info" :loading="CreditBtnLoading" @click="confirmForm">确定</n-button>
        </n-space>
      </template>
    </n-modal>
  </n-card>
</template>

<script lang="ts" setup>
  import { h, reactive, ref } from 'vue';
  import { SelectOption, useDialog, useMessage } from 'naive-ui';
  import { BasicTable, TableAction } from '@/components/Table';
  import { BasicForm } from '@/components/Form/index';
  import {
    addSemester,
    getSemesterCourse,
    getTableListDemo,
    DeleteSemester,
    addSemesterCourse,
    DeleteSemesterCourse,
    getSemesterCredit,
    addSemesterCredit,
    DeleteSemesterCredit,
    ChangeSemesterState,
    getAllCourse,
  } from '@/api/table/semesterlistDemo';
  import { columns } from './columns';
  import { columnsCourse, columnsAllCourse } from './columnsCourse';
  import { columnsCredit } from './columnsCredit';
  //import { TableExport, UserPlus } from '@vicons/tabler';
  import { PlusOutlined } from '@vicons/antd';
  import {
    disableTableListDemo,
    enableTableListDemo,
    getFacultyList,
  } from '@/api/table/teacherlistDemo';

  const addCourseParams = reactive({
    courseId: '',
    facultyId: '',
  });
  const addCreditParams = reactive({
    facultyId: '',
    creditLimit: '',
  });
  const addformParams = reactive({
    semester: '',
    joinYear: '',
    startTime: null,
    endTime: null,
  });
  const ChangeStateParams = reactive({
    state: '',
  });
  const rules = {
    Semester: [
      {
        required: true,
        trigger: ['blur', 'input'],
        message: '请输入学期',
      },
    ],
    joinYear: [
      {
        required: true,
        trigger: ['blur', 'input'],
        message: '请输入学生的入学年份',
      },
    ],
    startTime: [
      {
        required: true,
        trigger: ['blur', 'input'],
        message: '请输入结束时间',
      },
    ],
    endTime: [
      {
        required: true,
        trigger: ['blur', 'input'],
        message: '请输入结束时间',
      },
    ],
  };
  function confirmForm(e) {
    e.preventDefault();
    formBtnLoading.value = true;
    formRef.value.validate(async (errors) => {
      if (!errors) {
        const response = await addSemester({ ...addformParams, ...params.value, e });
        // const { code, message } = response;
        if (response.code != 200) {
          console.log('新建失败');
        }
        if (response.code == 400) {
          message.error(response.message);
        }
        setTimeout(() => {
          showModal.value = false;
          reloadTable();
        });
      } else {
        message.error('请填写完整信息');
      }
      formBtnLoading.value = false;
      reloadTable();
    });
  }
  async function confirmAddCredit(e) {
    e.preventDefault();
    CreditBtnLoading.value = true;
    const response = await addSemesterCredit({
      ...addCreditParams,
      ...recordGlobal,
    });
    if (response.code == 400) {
      message.error(response.message);
    }
    message.toString();
    setTimeout(() => {
      showCreditModal.value = false;
      reloadTable();
    });
    showCreditModal.value = false;
  }
  const dialog = useDialog();
  //const router = useRouter();
  const formRef: any = ref(null);
  const message = useMessage();
  const actionRef = ref();
  //const CourseformRef: any = ref(null);
  const CreditformRef: any = ref(null);
  const showModal = ref(false);
  //const semesterState=ref();
  const showCourseModal = ref(false);
  const showCreditModal = ref(false);
  const showTableModal = ref(false);
  const showCreditTable = ref(false);
  const formBtnLoading = ref(false);
  // const CourseBtnLoading = ref(false);
  const CreditBtnLoading = ref(false);
  const formParams = reactive({
    semester: '',
    joinYear: '',
    isDeleted: '',
  });

  const recordGlobal = reactive({
    semesterId: '',
  });

  const params = ref({
    pageSize: 10,
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
            label: '结束抢课',
            type: 'warning',
            //icon: 'ic:outline-delete-outline',
            onClick: handleDisabled.bind(null, record),
            // 根据业务控制是否显示 isShow 和 auth 是并且关系
            ifShow: () => {
              if (record.isDeleted == '1') return false;
              else return true;
            },
            // 根据权限控制是否显示: 有权限，会显示，支持多个
            // auth: ['basic_list'],
          },
          {
            label: '开始抢课',
            type: 'success',
            onClick: handleEnabled.bind(null, record),
            ifShow: () => {
              if (record.isDeleted == '0') return false;
              else return true;
            },
            // auth: ['basic_list'],
          },
          {
            label: '学期课程',
            color: '#9999CC',
            onClick: handleQueryCourse.bind(null, record),
            // handleQueryStudent.bind(null, record.id),
            // 根据业务控制是否显示 isShow 和 auth 是并且关系
            ifShow: () => {
              return true;
            },
            // 根据权限控制是否显示: 有权限，会显示，支持多个
            // auth: ['basic_list'],
          },
          {
            label: '学期学分',
            color: '#9999CC',
            onClick: handleQueryCredit.bind(null, record),
            // handleQueryStudent.bind(null, record.id),
            // 根据业务控制是否显示 isShow 和 auth 是并且关系
            ifShow: () => {
              return true;
            },
            // 根据权限控制是否显示: 有权限，会显示，支持多个
            // auth: ['basic_list'],
          },
          {
            label: '删除',
            type: 'error',
            onClick: handleDelete.bind(null, record),
            ifShow: () => {
              return true;
            },
            // auth: ['basic_list'],
          },
        ],
        select: (key) => {
          message.info(`您点击了，${key} 按钮`);
        },
      });
    },
  });
  async function handleDisabled(record: Recordable) {
    ChangeStateParams.state = '1';
    const id = record.id;
    console.log('点击了结束抢课', record);
    console.log(id);
    const response = await ChangeSemesterState(id, { ...ChangeStateParams });
    if (response.code == 200) {
      message.info('已结束抢课');
      reloadTable();
    }
    if (response.code == 400) {
      message.error(response.message);
      reloadTable();
    }
    reloadTable();
  }

  async function handleEnabled(record: Recordable) {
    ChangeStateParams.state = '0';
    const id = record.id;
    console.log('点击了开始抢课', record);
    console.log(record.state);
    const response = await ChangeSemesterState(id, { ...ChangeStateParams });
    if (response.code == 200) {
      message.info('已开始抢课');
      reloadTable();
    }
    if (response.code == 400) {
      message.error(response.message);
      reloadTable();
    }
    reloadTable();
  }
  const actionCourseColumn = reactive({
    width: 160,
    title: '操作',
    key: 'action',
    fixed: 'right',
    align: 'center',
    render(Courserecord) {
      return h(TableAction as any, {
        style: 'button',
        actions: [
          {
            label: '删除',
            type: 'error',
            onClick: handleCourseDelete.bind(null, Courserecord),
            ifShow: () => {
              return true;
            },
            // auth: ['basic_list'],
          },
        ],
        select: (key) => {
          message.info(`您点击了，${key} 按钮`);
        },
      });
    },
  });
  const actionCourseChooseColumn = reactive({
    width: 160,
    title: '操作',
    key: 'action',
    fixed: 'right',
    align: 'center',
    render(CourseChooserecord) {
      return h(TableAction as any, {
        style: 'button',
        actions: [
          {
            label: '添加该课程到选课列表中',
            type: 'info',
            onClick: handleCourseChoose.bind(null, CourseChooserecord),
            ifShow: () => {
              return true;
            },
            // auth: ['basic_list'],
          },
        ],
        select: (key) => {
          message.info(`您点击了，${key} 按钮`);
        },
      });
    },
  });
  const actionCreditColumn = reactive({
    width: 160,
    title: '操作',
    key: 'action',
    fixed: 'right',
    align: 'center',
    render(Creditrecord) {
      return h(TableAction as any, {
        style: 'button',
        actions: [
          {
            label: '删除',
            type: 'error',
            onClick: handleCreditDelete.bind(null, Creditrecord),
            ifShow: () => {
              return true;
            },
            // auth: ['basic_list'],
          },
        ],
        select: (key) => {
          message.info(`您点击了，${key} 按钮`);
        },
      });
    },
  });
  function handleDelete(record) {
    console.log(record);
    dialog.info({
      title: '警告',
      content: `是否删除学期 “${record.semester}”`,
      positiveText: '确定',
      negativeText: '取消',
      onPositiveClick: () => {
        DeleteSemester(record.id);
        message.success('删除成功');
        reloadTable();
      },
      onNegativeClick: () => {},
    });
    reloadTable();
  }
  function handleCourseDelete(record) {
    console.log(record);
    dialog.info({
      title: '警告',
      content: `是否删除课程 “${record.courseName}”`,
      positiveText: '确定',
      negativeText: '取消',
      onPositiveClick: () => {
        DeleteSemesterCourse(record.id);
        reloadTable();
        message.success('删除成功');
      },
      onNegativeClick: () => {},
    });
  }
  function handleCreditDelete(record) {
    console.log(record);
    dialog.info({
      title: '警告',
      content: `是否删除 “${record.facultyName}”的学分限制记录`,
      positiveText: '确定',
      negativeText: '取消',
      onPositiveClick: () => {
        DeleteSemesterCredit(record.id);
        reloadTable();
        message.success('删除成功');
      },
      onNegativeClick: () => {},
    });
  }
  function addTable() {
    showModal.value = true;
  }
  function addCourse() {
    showCourseModal.value = true;
  }
  function addCredit() {
    showCreditModal.value = true;
  }
  let facultyList = ref<SelectOption[]>([]);
  const facultyParams = reactive({
    name: '',
  });
  const loadDataTable = async (res) => {
    //showModal.value = true;
    const response = await getFacultyList({ ...facultyParams, ...params.value });
    console.log(response.result);
    facultyList.value = response.result;
    return await getTableListDemo({ ...formParams, ...params.value, ...res });
  };

  const loadSemesterCourseTable = async (res) => {
    return await getSemesterCourse({ ...recordGlobal, ...params.value, ...res });
  };
  const CourseformParams = reactive({
    name: '',
    facultyName: '',
    isDeleted: '',
    teacherName: '',
    courseId: '',
  });
  const loadAllCourseTable = async (res) => {
    return await getAllCourse({ ...CourseformParams, ...params.value, ...res });
  };
  const loadSemesterCreditTable = async (res) => {
    return await getSemesterCredit({ ...recordGlobal, ...params.value, ...res });
  };
  function onCheckedRow(rowKeys) {
    console.log(rowKeys);
  }

  function reloadTable() {
    actionRef.value.reload();
  }

  // function handleEdit(record: Recordable) {
  //   console.log('点击了编辑', record);
  //   router.push({ name: 'basic-info-course', params: { id: record.id } });
  // }

  function handleQueryCourse(record: Recordable) {
    recordGlobal.semesterId = record.id;
    console.log('点击了查课程', record);
    showTableModal.value = true;
    // getStudentsTableListDemo(record);
  }
  async function handleCourseChoose(record: Recordable) {
    addCourseParams.courseId = record.id;
    addCourseParams.facultyId = record.facultyId;
    const response = await addSemesterCourse({
      ...addCourseParams,
      ...recordGlobal,
    });
    if (response.code != 200) {
      console.log('新建失败');
    }
    if (response.code == 400) {
      message.toString();
    }
    message.toString();
    setTimeout(() => {
      showModal.value = false;
      reloadTable();
    });
    reloadTable();
    showCourseModal.value = false;
    // console.log('点击了查课程', record);
    // showTableModal.value = true;
    // // getStudentsTableListDemo(record);
  }
  function handleQueryCredit(record: Recordable) {
    recordGlobal.semesterId = record.id;
    console.log(record.id);
    console.log('点击了查学分', record);
    showCreditTable.value = true;
    //getStudentsTableListDemo(record);
  }

  function handleSubmit(values: Recordable) {
    console.log(values);
    reloadTable();
  }
</script>

<style lang="less" scoped></style>
