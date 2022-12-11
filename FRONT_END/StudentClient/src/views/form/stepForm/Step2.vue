<template>
  <div>
    <n-result status="success" title="修改成功" description="请重新登录" class="step-result">
      <template #footer>
        <div class="flex justify-center">
          <n-button type="primary" @click="finish" class="mr-4">完成</n-button>
        </div>
      </template>
    </n-result>
  </div>
</template>

<script lang="ts" setup>
  import { defineEmits } from 'vue';
  import { storage } from '@/utils/Storage';
  import { PageEnum } from '@/enums/pageEnum';

  const emit = defineEmits(['finish', 'prevStep']);

  function finish() {
    storage.clear();
    window.location.href = PageEnum.BASE_LOGIN;
  }
</script>

<style lang="less" scoped>
  .step-result {
    max-width: 560px;
    margin: 40px auto 0;

    ::v-deep(.n-result-content) {
      background-color: #fafafa;
      padding: 24px 40px;
    }

    .information {
      line-height: 22px;

      .ant-row:not(:last-child) {
        margin-bottom: 24px;
      }
    }

    .money {
      font-family: 'Helvetica Neue', sans-serif;
      font-weight: 500;
      font-size: 20px;
      line-height: 14px;
    }
  }
</style>
