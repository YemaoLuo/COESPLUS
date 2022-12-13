<template>
  <n-card :bordered="false" class="proCard">
    <n-button type="error" @click="handleDelete">删除头像图片缓存</n-button>
  </n-card>
  <div>
    <Amount />
    <Size />
  </div>
</template>

<script lang="ts" setup>
  // import { defineComponent } from 'vue';
  // import Amount from '@/views/iframe/Amount.vue';
  // import Size from '@/views/iframe/Size.vue';
  // import VisiTab from '@/views/iframe/Size.vue';
  import Amount from '@/views/iframe/Amount.vue';
  import Size from '@/views/iframe/Size.vue';
  import { useDialog, useMessage } from 'naive-ui';
  import { DeleteObjectInfo } from '@/api/dashboard/console';
  // export default defineComponent({
  //   components: { Amount, Size},
  //   setup() {
  //     return {};
  //   },
  // });

  // const loadDataTable = async () => {
  //   const response = await getTableListDemo();
  //   console.log(response.separateDetail);
  //   return response.separateDetail;
  // };
  //
  // function onCheckedRow(rowKeys) {
  //   console.log(rowKeys);
  // }
  //
  // function reloadTable() {
  //   actionRef.value.reload();
  // }
  //
  const dialog = useDialog();
  const message = useMessage();
  function handleDelete() {
    console.log('删除头像缓存');
    dialog.warning({
      title: '警告',
      content: `是否删除头像缓存 `,
      positiveText: '确定',
      negativeText: '取消',
      onPositiveClick: async () => {
        const response = await DeleteObjectInfo();
        if (response.code == 200) {
          message.success('删除成功');
        }
        if (response.code == 400) {
          message.error(response.message);
        }
      },
      onNegativeClick: () => {},
    });
  }
</script>

<style lang="less" scoped></style>
