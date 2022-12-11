<template>
  <n-card :bordered="false" class="proCard">

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
          新增管理员
        </n-button>
      </template>
    </BasicTable>

    <n-modal v-model:show="showModal" :show-icon="false" preset="dialog" title="添加管理员">
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
        <n-form-item label="电话号码" path="telephone">
          <n-input placeholder="请输入电话号码" v-model:value="addformParams.telephone" />
        </n-form-item>
        <n-form-item label="邮箱" path="email">
          <n-input placeholder="请输入邮箱" v-model:value="addformParams.email" />
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
  import { reactive, ref } from 'vue';
  import { useMessage } from 'naive-ui';
  import { BasicTable } from '@/components/Table';
  // import { FormSchema, useForm } from '@/components/Form/index';
  import { PlusOutlined } from '@vicons/antd';
  // import { addteacherdemo, addteacherphoto } from '@/api/table/teacherListDemo';

  import { addAdmindemo, getTableListDemo } from '@/api/table/AdminListDemo';
  import { columns } from './columns';

  async function addTable() {
    showModal.value = true;
  }

  //新建
  const rules = {
    name: [
      {
        required: true,
        trigger: ['blur', 'input'],
        message: '请输入名称',
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
  };
  /////////////////////////////////////////////////////////////////////新增列表验证

  const formRef: any = ref(null);
  const message = useMessage();
  const actionRef = ref();

  const showModal = ref(false);
  // const showExportModal = ref(false);
  const formBtnLoading = ref(false);
  const addformParams = reactive({
    name: '',
    telephone: '',
    email: '',
  });
  const params = ref({
    pageSize: 10,
    currentPage: 1,
  });

  const loadDataTable = async (res) => {
    return getTableListDemo({ ...params.value, ...res });
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
        const response = await addAdmindemo({ ...addformParams, ...params.value, e });
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
</script>

<style lang="less" scoped></style>
