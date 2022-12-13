<template>
  <n-card :bordered="false" class="proCard">
    <BasicTable
      :columns="columnsStudents"
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
            <n-icon>
              <TableExport />
            </n-icon>
          </template>
          导出学生信息
        </n-button>
      </template>
    </BasicTable>
  </n-card>
</template>

<script lang="ts" setup>
  import { h, reactive, ref } from 'vue';
  import { BasicTable } from '@/components/Table';
  import {
    rolestudentdemo,
    addstudentdemo,
    exportstudentdemo,
    addstudentphoto,
  } from '@/api/table/studentListDemo';
  import { columns } from './columns';
  import { TableExport, UserPlus } from '@vicons/tabler';
  import { getStudentsTableListDemo } from "@/api/table/courselistDemo";

  // async function submit(event) {
  //   event.preventDefault(); //取消默认行为
  //   let fileinfo = this.file;
  //   let forms = {
  //     name: 'file',
  //     file: fileinfo,
  //   };
  // }
  const actionRef = ref();

  const formParams = reactive({
    name: '',
    studentId: '',
    telephone: '',
    faculty: '',
    joinYear: '',
    isDeleted: '',
  });

  const loadDataTable = async (res) => {
    const data = getStudentsTableListDemo({ ...formParams, ...params.value, ...res });
    return data;
  };

  function handleQueryStudent(record: Recordable) {
    // console.log('点击了查学生', record);
    showTableModal.value = true;
    return getStudentsTableListDemo(record);
  }

  function onCheckedRow(rowKeys) {
    console.log(rowKeys);
  }

  function reloadTable() {
    actionRef.value.reload();
  }
</script>

<style lang="less" scoped></style>
