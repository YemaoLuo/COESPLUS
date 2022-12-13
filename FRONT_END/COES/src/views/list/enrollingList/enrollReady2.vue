<template>
  <n-card :bordered="false" class="proCard">
    <div class="result-box">
      <n-result title="选课">
        <div class="result-box-extra">
          <div class="local-time">
            <div class="time">{{ countDown.hour }}:{{ countDown.minute }}</div>
            <div :content="checkEnrolling"></div>
          </div>
        </div>
        <template #footer>
          <div class="flex justify-center mb-4">
            <n-space align="center">
              <n-button type="info" @click="query">进入选课</n-button>
            </n-space>
          </div>
        </template>
      </n-result>
    </div>
  </n-card>
</template>
<script lang="ts" setup>
  import { computed, reactive } from 'vue';
  import { useMessage, useThemeVars } from 'naive-ui';
  import { useRouter } from 'vue-router';
  import { checkEnrollingState } from '@/api/table/enrollinglist';
  import { storage } from '@/utils/Storage';
  import { ACCESS_TOKEN } from '@/store/mutation-types';

  const router = useRouter();
  const message = useMessage();
  const themeVars = useThemeVars();
  const token = storage.get(ACCESS_TOKEN);
  const countDown = reactive({
    hour: '',
    day: '',
    minute: '',
    second: '',
  });
  const formValue = reactive({
    semeterId: '',
  });

  const getTableHeaderColor = computed(() => {
    return themeVars.value.tableHeaderColor;
  });

  let checkEnrolling = await checkEnrollingState({});
  if (checkEnrolling.code == 200) message.success('请稍后，即将进入选课界面…');
  else if (checkEnrolling.code == 500) {
    countDown.hour = checkEnrolling.result.hour;
    countDown.day = checkEnrolling.result.day;
    countDown.minute = checkEnrolling.result.minute;
    countDown.second = checkEnrolling.result.second;
  } else message.error(checkEnrolling.message.toString());

  function query() {
    router.push({ name: 'enrolling-list', params: { semeterId: formValue.semeterId } });
  }
</script>
<style lang="less" scoped>
  .result-box {
    width: 72%;
    margin: 0 auto;
    text-align: center;
    padding-top: 5px;

    &-extra {
      padding: 24px 40px;
      text-align: center;
      font-size: 42px;
      //color: #071a7c;
      background: v-bind(getTableHeaderColor);
      border-radius: 4px;
    }
  }
</style>
