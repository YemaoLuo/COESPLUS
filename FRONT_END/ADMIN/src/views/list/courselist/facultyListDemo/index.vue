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
          添加学院
        </n-button>
        <div>&nbsp</div>
        <n-button type="primary" @click="exportTable">
          <template #icon>
            <n-icon>
              <FileExcelOutlined />
            </n-icon>
          </template>
          导出学院信息
        </n-button>
      </template>

      <template #toolbar>
        <n-button type="primary" @click="handleEdit">编辑表格</n-button>
      </template>
    </BasicTable>

    <n-modal v-model:show="showModal" :show-icon="false" preset="dialog" title="添加学院">
      <n-form
        :model="addformParams"
        :rules="rules"
        ref="formRef"
        label-placement="left"
        :label-width="80"
        class="py-4"
      >
        <n-form-item label="名称" path="name">
          <n-input placeholder="请输入学院名称" v-model:value="addformParams.name" />
        </n-form-item>
      </n-form>

      <template #action>
        <n-space>
          <n-button @click="() => (showModal = false)">取消</n-button>
          <n-button type="info" :loading="formBtnLoading" @click="confirmForm">确定</n-button>
        </n-space>
      </template>
    </n-modal>
    <n-modal v-model:show="showExportModal" :show-icon="false" preset="dialog" title="导出学院信息">
      <n-form
        :model="exportformParams"
        ref="formRef"
        label-placement="left"
        :label-width="80"
        class="py-4"
      >
        <n-form-item label="名称" path="name">
          <n-input placeholder="请输入学院名称" v-model:value="exportformParams.name" />
        </n-form-item>
        <n-form-item label="禁用状态" path="isDeleted">
          <n-select
            v-model:value="exportformParams.isDeleted"
            placeholder="请选择禁用状态"
            :options="isDeletedList"
          />
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
  import { useDialog, useMessage } from 'naive-ui';
  import { BasicTable, TableAction } from '@/components/Table';
  import { BasicForm, FormSchema, useForm } from '@/components/Form/index';
  import { addfacultydemo } from '@/api/table/facultyListDemo';
  import {
    getTableListDemo,
    delTableListDemo,
    disableTableListDemo,
    enableTableListDemo,
    exportfacultydemo,
  } from '@/api/table/facultyListDemo';
  // import {
  //   delTableListDemo as delCourse,
  //   disableTableListDemo as disableCourse,
  // } from '@/api/table/courselistDemo';
  import { columns } from './columns';
  import { PlusOutlined, FileExcelOutlined } from '@vicons/antd';
  //mport { TableExport, UserPlus } from '@vicons/tabler';
  import { useRouter } from 'vue-router';

  const addformParams = reactive({
    name: '',
  });
  const rules = {
    name: [
      {
        required: true,
        trigger: ['blur', 'input'],
        message: '请输入名称',
      },
    ],
  };
  function confirmForm(e) {
    e.preventDefault();
    formBtnLoading.value = true;
    formRef.value.validate(async (errors) => {
      if (!errors) {
        const response = await addfacultydemo({ ...addformParams, ...params.value, e });
        const { code, message } = response;
        if (code != 200) {
          console.log('新建失败');
          if (code == 400) {
            message.toString();
          }
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
  const schemas: FormSchema[] = [
    {
      field: 'courseId',
      component: 'NInput',
      label: '学院ID',
      componentProps: {
        placeholder: '请输入该学院id编号',
        onInput: (e: any) => {
          formParams.courseId = e;
        },
      },
    },
    {
      field: 'name',
      component: 'NInput',
      label: '学院名称',
      componentProps: {
        placeholder: '请输入学院名',
        onInput: (e: any) => {
          formParams.name = e;
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
  const showExportModal = ref(false);
  const showModal = ref(false);
  const formBtnLoading = ref(false);
  const formParams = reactive({
    name: '',
    courseId: '',
    courseName: '',
    isDeleted: '',
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
            label: '禁用',
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

  function addTable() {
    showModal.value = true;
  }
  function exportTable() {
    exportfacultydemo({ ...formParams, ...params.value }).then((res) => {
      //构造blob对象，type是文件类型，详情可以参阅blob文件类型;
      let blob = new Blob([res], { type: 'application/vnd.ms-excel' }); //我是下载zip压缩包
      let url = window.URL.createObjectURL(blob); //生成下载链接
      const link = document.createElement('a'); //创建超链接a用于文件下载
      link.href = url; //赋值下载路径
      // link.target = '_blank'; //打开新窗口下载，不设置则为在本窗口下载
      link.download = `facultylist.xlsx`; //下载的文件名称（不设置就会随机生成）
      link.click(); //点击超链接触发下载
      URL.revokeObjectURL(url); //释放URL
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

  // async function handleDisabled(record: Recordable) {
  //   console.log('点击了禁用', record);
  //   const response = await disableTableListDemo(record);
  //   if (response.code == 200) {
  //     message.success('禁用成功');
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
      content: `是否禁用学院 “${record.name}”`,
      positiveText: '确定',
      negativeText: '取消',
      onPositiveClick: async () => {
        const response = await disableTableListDemo(record.id + '?force=false');
        if (response.code == 400) {
          console.log('400');
          dialog.warning({
            title: '警告',
            content: `学院关联老师和学生，是否强制禁用学院 “${record.name}”`,
            positiveText: '确定',
            negativeText: '取消',
            onPositiveClick: async () => {
              const responseForce = await disableTableListDemo(record.id + '?force=true');
              if (responseForce.code == 200) {
                message.success('已强制禁用学院');
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
      message.success('启用成功');
    }
    if (response.code == 400) {
      message.error(response.result);
    }
    reloadTable();
  }

  function handleEdit(record: Recordable) {
    console.log('点击了编辑', record);
    router.push({ name: 'basic-info-faculty', params: { id: record.id } });
  }

  function handleDelete(record) {
    console.log(record);
    dialog.info({
      title: '警告',
      content: `是否删除学院 “${record.name}”`,
      positiveText: '确定',
      negativeText: '取消',
      onPositiveClick: async () => {
        const response = await delTableListDemo(record.id + '?force=false');
        if (response.code == 400) {
          console.log('400');
          dialog.warning({
            title: '警告',
            content: `学院关联老师和学生，是否强制删除学院 “${record.name}”`,
            positiveText: '确定',
            negativeText: '取消',
            onPositiveClick: async () => {
              const responseForce = await delTableListDemo(record.id + '?force=true');
              if (responseForce.code == 200) {
                message.success('已强制删除学院');
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

  function handleSubmit(values: Recordable) {
    console.log(values);
    reloadTable();
  }

  function handleReset(values: Recordable) {
    formParams.courseId = '';
    formParams.name = '';
    formParams.courseName = '';
    formParams.isDeleted = '';
    reloadTable();
    console.log(values);
  }
</script>

<style lang="less" scoped></style>
