<template>
  <div>
    <div class="n-layout-page-header"> </div>
    <n-card :bordered="false" class="mt-4 proCard">
      <n-space vertical class="steps" justify="center">
        <n-steps :current="currentTab" :status="currentStatus">
          <n-step title="输入新密码" />
          <n-step title="修改完成！" />
        </n-steps>
        <step1 v-if="currentTab === 1" @nextStep="nextStep" />
        <step2 v-if="currentTab === 2" @finish="() => {}" />
      </n-space>
    </n-card>
  </div>
</template>

<script setup>
  import { ref } from 'vue';
  import step1 from './Step1.vue';
  import step2 from './Step2.vue';

  const currentTab = ref(1);
  const currentStatus = ref('process');

  function nextStep() {
    if (currentTab.value < 2) {
      currentTab.value += 1;
    }
  }
</script>

<style lang="less" scoped>
  .steps {
    max-width: 750px;
    margin: 16px auto;
  }
</style>
