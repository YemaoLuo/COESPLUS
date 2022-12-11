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
          添加学生信息
        </n-button>
        <div>&nbsp</div>
        <n-button type="primary" @click="exportTable">
          <template #icon>
            <n-icon>
              <FileExcelOutlined />
            </n-icon>
          </template>
          导出学生信息
        </n-button>
      </template>

      <template #toolbar>
        <n-button type="primary" @click="handleEdit">编辑表格</n-button>
      </template>
    </BasicTable>
    <n-modal v-model:show="showModal" :show-icon="false" preset="dialog" title="添加学生信息">
      <n-form
        :model="addformParams"
        :rules="rules"
        ref="formRef"
        label-placement="left"
        :label-width="80"
        class="py-4"
      >
        <n-form-item label="名称" path="name">
          <n-input placeholder="请输入名称" v-model:value="addformParams.name" />
        </n-form-item>
        <n-form-item label="学生号" path="studentId">
          <n-input placeholder="请输入学生号" v-model:value="addformParams.studentId" />
        </n-form-item>
        <n-form-item label="性别" path="sex">
          <n-select v-model:value="addformParams.sex" placeholder="" :options="sexList" />
        </n-form-item>
        <n-form-item label="电话号码" path="telephone">
          <n-input placeholder="请输入电话号码" v-model:value="addformParams.telephone" />
        </n-form-item>
        <n-form-item label="邮箱" path="email">
          <n-input placeholder="请输入邮箱" v-model:value="addformParams.email" />
        </n-form-item>
        <n-form-item label="学院名称" path="facultyId">
          <n-select
            v-model:value="addformParams.facultyId"
            placeholder="请输入学院名称"
            :options="facultyList"
          />
        </n-form-item>
        <n-form-item label="入学时间" path="joinYear">
          <n-input placeholder="请输入入学年份" v-model:value="addformParams.joinYear" />
        </n-form-item>
        <n-form-item label="图片" path="img">
          <input type="file" id="file" name="file" @change="getFile($event)" />
        </n-form-item>
      </n-form>

      <template #action>
        <n-space>
          <n-button @click="() => (showModal = false)">取消</n-button>
          <n-button type="info" :loading="formBtnLoading" @click="confirmForm">确定</n-button>
        </n-space>
      </template>
    </n-modal>
  </n-card>
</template>

<script lang="ts" setup>
  import { h, reactive, ref } from 'vue';
  import { SelectOption, useDialog, useMessage } from "naive-ui";
  import { BasicTable, TableAction } from '@/components/Table';
  import { BasicForm, FormSchema, useForm } from '@/components/Form/index';
  import {
    rolestudentdemo,
    addstudentdemo,
    exportstudentdemo,
    addstudentphoto,
  } from '@/api/table/studentListDemo';
  import {
    getTableListDemo,
    delTableListDemo,
    disableTableListDemo,
    enableTableListDemo,
  } from '@/api/table/studentListDemo';
  import { columns } from './columns';
  import { FileExcelOutlined, PlusOutlined } from '@vicons/antd';
  //import { TableExport, UserPlus } from '@vicons/tabler';
  import { useRouter } from 'vue-router';
  import { getFacultyList } from '@/api/table/teacherlistDemo';
  async function getFile(event) {
    this.file = event.target.files[0];
    event.preventDefault(); //取消默认行为
    let fileinfo = this.file;
    let forms = {
      name: 'file',
      file: fileinfo,
    };
    addstudentphoto(forms).then((res) => {
      //console.log(res.data.result.fileName);
      addformParams.photo = res.data.result.fileName;
    });
  }
  const sexList = [
    {
      label: '男',
      value: '1',
    },
    {
      label: '女',
      value: '0',
    },
  ];
  //新建
  const rules = {
    name: [
      {
        required: true,
        trigger: ['blur', 'input'],
        message: '请输入名称',
      },
    ],
    studentId: [
      {
        required: true,
        trigger: ['blur', 'input'],
        message: '请输入学生号',
      },
      {
        pattern: /(2209|1909|2009|2109)\d{3}[A-Z]-[A-Z]\d{3}-\d{4}/,
        trigger: ['blur', 'input'],
        message: '请输入xx09开头的学生编号，例（2009111G-I111-1111）',
      },
    ],
    telephone: [
      {
        required: true,
        trigger: ['blur', 'input'],
        message: '请输入电话号码',
      },
      {
        pattern: /(\(\+86\) \d{11})|(\(\+853\) \d{8})/,
        message: '请输入正确格式的电话号码',
        trigger: ['blur', 'input'],
      },
    ],
    email: [
      {
        required: true,
        trigger: ['blur', 'input'],
        message: '请输入邮箱',
      },
      {
        pattern: /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/,
        message: '请输入正确格式的邮箱',
        trigger: ['blur', 'input'],
      },
    ],
    facultyId: [
      {
        required: true,
        trigger: ['blur', 'input'],
        message: '请输入学院名称',
      },
    ],
    joinYear: [
      {
        required: true,
        trigger: ['blur', 'input'],
        message: '请输入学生入学年份',
      },
      {
        pattern: /^\d{4}$/,
        message: '请输入入学年份',
        trigger: ['blur', 'input'],
      },
    ],
  };
  //表单
  const schemas: FormSchema[] = [
    {
      field: 'name',
      component: 'NInput',
      label: '姓名',
      componentProps: {
        placeholder: '请输入学生姓名',
        onInput: (e: any) => {
          formParams.name = e;
        },
      },
    },
    {
      field: 'studentId',
      component: 'NInput',
      label: '学生号',
      componentProps: {
        placeholder: '请输入学生号',
        onInput: (e: any) => {
          formParams.studentId = e;
        },
      },
    },
    {
      field: 'faculty',
      component: 'NInput',
      label: '学院',
      componentProps: {
        placeholder: '请输入学生所在的学院名称',
        onInput: (e: any) => {
          formParams.faculty = e;
        },
      },
    },
    {
      field: 'joinYear',
      component: 'NInput',
      label: '入学时间',
      componentProps: {
        placeholder: '请输入学生的入学年份',
        onInput: (e: any) => {
          formParams.joinYear = e;
        },
      },
    },
    {
      field: 'telephone',
      component: 'NInput',
      label: '电话',
      componentProps: {
        placeholder: '请输入学生的电话号码（带区号）',
        onInput: (e: any) => {
          formParams.telephone = e;
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
  const formRef: any = ref(null);
  const message = useMessage();
  const actionRef = ref();
  const showModal = ref(false);
  const showExportModal = ref(false);
  const formBtnLoading = ref(false);

  const formParams = reactive({
    name: '',
    studentId: '',
    telephone: '',
    faculty: '',
    joinYear: '',
    isDeleted: '',
  });
  const addformParams = reactive({
    name: '',
    studentId: '',
    sex: '',
    telephone: '',
    email: '',
    facultyId: '',
    joinYear: '',
    photo: '',
  });
  const activeformParams = reactive({
    role: 'student',
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
            label: '禁用',
            type: 'warning',
            //icon: 'ic:outline-delete-outline',
            onClick: handleDisabled.bind(null, record.id),
            // 根据业务控制是否显示 isShow 和 auth 是并且关系
            ifShow: () => {
              if (record.isDeleted == '1') return false;
              else return true;
            },
            // 根据权限控制是否显示: 有权限，会显示，支持多个
            // auth: ['basic_list'],
          },
          {
            label: '启用',
            type: 'success',
            onClick: handleEnabled.bind(null, record),
            ifShow: () => {
              if (record.isDeleted == '0') return false;
              else return true;
            },
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
          {
            label: '重置密码',
            type: 'success',
            onClick: handleRoleActive.bind(null, record),
            // ifShow: () => {
            //   if (record.isDeleted == '0') return false;
            //   else return true;
            // },
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
    labelWidth: 80,
    schemas,
  });
  let facultyList = ref<SelectOption[]>([]);
  const facultyParams = reactive({
    name: '',
  });
  async function addTable() {
    showModal.value = true;
    const response = await getFacultyList({ ...facultyParams, ...params.value });
    //console.log(response.result.records);
    // const facultyName = response.result.records.name;
    // const facultyId = response.result.records.id;
    console.log(response.result);
    facultyList.value = response.result;
    console.log(facultyList);
  }
  function exportTable() {
    exportstudentdemo({ ...formParams, ...params.value }).then((res) => {
      //构造blob对象，type是文件类型，详情可以参阅blob文件类型;
      let blob = new Blob([res], { type: 'application/vnd.ms-excel' }); //我是下载zip压缩包
      let url = window.URL.createObjectURL(blob); //生成下载链接
      const link = document.createElement('a'); //创建超链接a用于文件下载
      link.href = url; //赋值下载路径
      // link.target = '_blank'; //打开新窗口下载，不设置则为在本窗口下载
      link.download = `studentlist.xlsx`; //下载的文件名称（不设置就会随机生成）
      link.click(); //点击超链接触发下载
      URL.revokeObjectURL(url); //释放URL
      showExportModal.value = false;
      message.success('下载完成');
    });
  }
  const loadDataTable = async (res) => {
    const data = getTableListDemo({ ...formParams, ...params.value, ...res });
    return data;
  };

  function onCheckedRow(rowKeys) {
    console.log(rowKeys);
  }

  function reloadTable() {
    actionRef.value.reload();
  }

  function confirmForm(e) {
    e.preventDefault();
    formBtnLoading.value = true;
    formRef.value.validate(async (errors) => {
      if (!errors) {
        const response = await addstudentdemo({ ...addformParams, ...params.value, e });
        if (response.code != 200) {
          console.log('新建失败');
        }
        if (response.code == 400) {
          message.error(response.message);
        }
        // console.log('新建成功');
        setTimeout(() => {
          showModal.value = false;
          reloadTable();
        });
      } else {
        message.error('请填写完整信息');
      }
      formBtnLoading.value = false;
    });
  }
  // function confirmExportForm(e) {
  //   e.preventDefault();
  //   formBtnLoading.value = true;
  //   formRef.value.validate(async (errors) => {
  //     if (!errors) {
  //       exportstudentdemo({ ...exportformParams, ...params.value, e }).then((res) => {
  //         //构造blob对象，type是文件类型，详情可以参阅blob文件类型;
  //         let blob = new Blob([res], { type: 'application/vnd.ms-excel' }); //我是下载zip压缩包
  //         let url = window.URL.createObjectURL(blob); //生成下载链接
  //         const link = document.createElement('a'); //创建超链接a用于文件下载
  //         link.href = url; //赋值下载路径
  //         // link.target = '_blank'; //打开新窗口下载，不设置则为在本窗口下载
  //         link.download = `studentlist.xlsx`; //下载的文件名称（不设置就会随机生成）
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
  function handleEdit(record: Recordable) {
    console.log('点击了编辑', record);
    router.push({ name: 'basic-info-student', params: { id: record.id } });
  }

  function handleDisabled(record: Recordable) {
    console.log('点击了禁用', record);
    disableTableListDemo(record);
    reloadTable();
    message.info('已禁用');
  }

  function handleEnabled(record: Recordable) {
    console.log('点击了启用', record);
    enableTableListDemo(record.id);
    reloadTable();
    message.info('已启用');
  }
  // function handleRoleReset(record: Recordable) {
  //   console.log('点击了重置', record);
  //   rolestudentdemo({ ...resetformParams, ...params.value });
  //   reloadTable();
  //   message.info('已重置');
  // }
  async function handleRoleActive(record: Recordable) {
    message.info('点击了重置密码', record);
    const response = await rolestudentdemo({ ...activeformParams }, record.id);
    // message.info(response);
    message.info(response.message);
    if (response.code != 200) {
      console.log('获取失败');
      reloadTable();
    }
    console.log('获取成功');
    reloadTable();
  }

  function handleDelete(record) {
    console.log(record);
    dialog.info({
      title: '警告',
      content: `是否删除学生 “${record.name}”`,
      positiveText: '确定',
      negativeText: '取消',
      onPositiveClick: () => {
        delTableListDemo(record.id);
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
    formParams.name = '';
    formParams.studentId = '';
    formParams.telephone = '';
    formParams.faculty = '';
    formParams.joinYear = '';
    formParams.isDeleted = '';
    reloadTable();
    console.log(values);
  }
</script>

<style lang="less" scoped></style>
