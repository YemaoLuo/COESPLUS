<template>
  <n-card :bordered="false" class="proCard">
    <n-watermark
      content="luowei"
      cross
      fullscreen
      :font-size="16"
      :line-height="16"
      :width="384"
      :height="384"
      :x-offset="12"
      :y-offset="60"
      :rotate="-15"
    />
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
          新增老师
        </n-button>
        <div>&nbsp</div>
        <n-button type="primary" @click="exportTable">
          <template #icon>
            <n-icon>
              <FileExcelOutlined />
            </n-icon>
          </template>
          导出老师信息
        </n-button>
      </template>

      <template #toolbar>
        <n-button type="primary" @click="handleEdit">编辑表格</n-button>
      </template>
    </BasicTable>

    <n-modal v-model:show="showModal" :show-icon="false" preset="dialog" title="添加教师">
      <n-form
        :model="addformParams"
        ref="formRef"
        :rules="rules"
        label-placement="left"
        :label-width="80"
        class="py-4"
      >
        <n-form-item label="名称" path="name">
          <n-input placeholder="请输入名称" v-model:value="addformParams.name" />
        </n-form-item>
        <n-form-item label="性别" path="sex">
          <n-select v-model:value="addformParams.sex" placeholder="请选择性别" :options="sexList" />
        </n-form-item>
        <n-form-item label="教师编号" path="teacherId">
          <n-input placeholder="请输入教师编号" v-model:value="addformParams.teacherId" />
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
        <n-form-item label="入职时间" path="joinYear">
          <n-input placeholder="请输入入职年份" v-model:value="addformParams.joinYear" />
        </n-form-item>
        <!--        <n-form-item label="图片" path="img">-->
        <!--          <input color="#CCFFFF" type="file" id="file" name="file" @change="getFile($event)" />-->
        <!--        </n-form-item>-->
        <n-form-item label="教师照片" path="img">
          <BasicUpload
            action="http://yemaoluo.top:7000/api/admin/file"
            name="file"
            :width="100"
            :height="100"
            max="1"
            :headers="{ token: token }"
            :on-change="finish"
          />
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
  import { useDialog, useMessage } from 'naive-ui';
  import { BasicTable, TableAction } from '@/components/Table';
  import { BasicForm, FormSchema, useForm } from '@/components/Form/index';
  import { FileExcelOutlined, PlusOutlined } from '@vicons/antd';
  import { addteacherdemo } from '@/api/table/teacherListDemo';
  import { SelectOption } from 'naive-ui';
  function finish({ event: Event }) {
    var json = new String(Event.target.response);
    //("code":200,"me5sage":"操作成功! ","reult":("filelame":"7e45Bad3be4a18bef6fddd4615e016783284419.jp","ur":"http:/yemapluo.top:9 index.ve
    json = json.substring(52, json.length);
    var fileName = '';
    for (let i = 0; i < json.length; i++) {
      if (json.charAt(i) == '"') {
        break;
      } else {
        fileName += json.charAt(i);
      }
    }
    console.log(json);
    // console.log(url);
    addformParams.photo = fileName;
    console.log(typeof fileName);
  }

  import {
    getTableListDemo,
    disableTableListDemo,
    enableTableListDemo,
    delTableListDemo,
    exportteacherdemo,
    getFacultyList,
  } from '@/api/table/teacherListDemo';
  import { columns } from './columns';
  let facultyList = ref<SelectOption[]>([]);
  const facultyParams = reactive({
    name: '',
  });
  const token = storage.get(ACCESS_TOKEN);
  async function addTable() {
    showModal.value = true;
    const response = await getFacultyList({ ...facultyParams, ...params.value });
    console.log(response.result);
    facultyList.value = response.result;
    console.log(facultyList);
  }

  import { useRouter } from 'vue-router';
  import { rolestudentdemo } from '@/api/table/studentListDemo';
  import { storage } from '@/utils/Storage';
  import { ACCESS_TOKEN } from '@/store/mutation-types';

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
    teacherId: [
      {
        required: true,
        trigger: ['blur', 'input'],
        message: '请输入教师编号',
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
    facultyName: [
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
        message: '请输入教师入职年份',
      },
      {
        pattern: /^\d{4}$/,
        message: '请输入入职年份',
        trigger: ['blur', 'input'],
      },
    ],
  };
  /////////////////////////////////////////////////////////////////////新增列表验证
  //-------------------------------------------------------------------------------
  const schemas: FormSchema[] = [
    {
      field: 'name',
      component: 'NInput',
      label: '姓名',
      componentProps: {
        placeholder: '请输入姓名',
        onInput: (e: any) => {
          formParams.name = e;
        },
      },
    },
    {
      field: 'teacherId',
      component: 'NInput',
      label: '教师编号',
      componentProps: {
        placeholder: '请输入教师编号',
        onInput: (e: any) => {
          formParams.teacherId = e;
        },
      },
    },
    {
      field: 'faculty',
      component: 'NInput',
      label: '学院',
      componentProps: {
        placeholder: '请输入学院名称',
        onInput: (e: any) => {
          formParams.facultyId = e;
        },
      },
    },
    {
      field: 'joinYear',
      component: 'NInput',
      label: '入职年份',
      componentProps: {
        placeholder: '请输入入职年份',
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
        placeholder: '请输入教师的电话号码（带区号）',
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
        onInput: (e: any) => {
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
    sex: '',
    teacherId: '',
    telephone: '',
    facultyId: '',
    joinYear: '',
    isDeleted: '',
  });
  const addformParams = reactive({
    name: '',
    sex: '',
    teacherId: '',
    telephone: '',
    email: '',
    facultyId: '',
    joinYear: '',
    photo: '',
  });
  const params = ref({
    pageSize: 10,
    currentPage: 1,
  });
  const activeformParams = reactive({
    role: 'teacher',
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
            onClick: handleDisabled.bind(null, record),
            // 根据业务控制是否显示 isShow 和 auth 是并且关系
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
            // auth: ['basic_list'],
          },
        ],
        select: () => {
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

  function exportTable() {
    exportteacherdemo({ ...formParams, ...params.value }).then((res) => {
      //构造blob对象，type是文件类型，详情可以参阅blob文件类型;
      let blob = new Blob([res], { type: 'application/vnd.ms-excel' }); //我是下载zip压缩包
      let url = window.URL.createObjectURL(blob); //生成下载链接
      const link = document.createElement('a'); //创建超链接a用于文件下载
      link.href = url; //赋值下载路径
      // link.target = '_blank'; //打开新窗口下载，不设置则为在本窗口下载
      link.download = `teacherlist.xlsx`; //下载的文件名称（不设置就会随机生成）
      link.click(); //点击超链接触发下载
      URL.revokeObjectURL(url); //释放URL
      showExportModal.value = false;
      message.success('下载完成');
    });
  }
  const loadDataTable = async (res) => {
    return getTableListDemo({ ...formParams, ...params.value, ...res });
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
    console.log(addformParams.photo);
    formRef.value.validate(async (errors) => {
      if (!errors) {
        const response = await addteacherdemo({ ...addformParams, ...params.value, e });
        if (response.code == 200) {
          message.success('新建成功');
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
  }

  async function handleDisabled(record: Recordable) {
    // console.log('点击了禁用', record);
    const response = await disableTableListDemo(record.id);
    if (response.code == 200) {
      message.success('禁用成功');
    }
    if (response.code == 400) {
      message.error(response.result);
    }
    reloadTable();
  }

  async function handleEnabled(record: Recordable) {
    // console.log('点击了启用', record);
    const response = await enableTableListDemo(record.id);
    if (response.code == 200) {
      message.success('启用成功');
    }
    if (response.code == 400) {
      message.error(response.result);
    }
    reloadTable();
  }

  function handleEdit(record: Recordable) {
    // console.log('点击了编辑', record);
    router.push({ name: 'basic-info-teacher', params: { id: record.id } });
  }

  function handleDelete(record) {
    console.log(record);
    dialog.info({
      title: '警告',
      content: `是否删除老师 “${record.name}”`,
      positiveText: '确定',
      negativeText: '取消',
      onPositiveClick: async () => {
        const response = await delTableListDemo(record.id + '?force=false');
        if (response.code == 400) {
          console.log('400');
          dialog.warning({
            title: '警告',
            content: `老师关联已有课程，是否强制删除老师 “${record.name}”`,
            positiveText: '确定',
            negativeText: '取消',
            onPositiveClick: async () => {
              const responseForce = await delTableListDemo(record.id + '?force=true');
              if (responseForce.code == 200) {
                message.success('已强制删除老师');
              }
            },
            onNegativeClick: () => {},
          });
        }
        reloadTable();
      },
      onNegativeClick: () => {},
    });
  }
  async function handleRoleActive(record: Recordable) {
    message.info('已发送重置密码邮件', record);
    const response = await rolestudentdemo({ ...activeformParams }, record.id);
    if (response.code != 200) {
      console.log('获取失败');
      reloadTable();
    }
    console.log('获取成功');
    reloadTable();
  }

  function handleSubmit(values: Recordable) {
    console.log(values);
    reloadTable();
  }

  function handleReset(values: Recordable) {
    formParams.name = '';
    formParams.teacherId = '';
    formParams.telephone = '';
    formParams.facultyId = '';
    formParams.joinYear = '';
    formParams.isDeleted = '';
    reloadTable();
    console.log(values);
  }
</script>

<style lang="less" scoped></style>
