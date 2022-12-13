<template>
  <n-card :bordered="false" class="proCard">
    <div class="result-box">
      <n-result title="选课">
        <div class="result-box-extra" v-show="countDown.second.length > 1">
          <div class="local-time">
            <div class="result-box-extra2">距离选课开启还有 </div>
            <div class="time">
              <n-countdown
                :render="renderCountdown"
                :duration="countDown.totalTime * 1000"
                :active="active"
              />
            </div>
          </div>
        </div>
        <template #footer>
          <div class="flex justify-center mb-4">
            <n-space align="center">
              <n-button type="info" @click="enrollReady">进入选课</n-button>
            </n-space>
          </div>
        </template>
      </n-result>
    </div>
  </n-card>
</template>
<script lang="ts" setup>
  import { computed, reactive, ref } from 'vue';
  import { CountdownProps, useMessage, useThemeVars } from 'naive-ui';
  import { useRouter } from 'vue-router';
  import { checkEnrollingState } from '@/api/table/enrollinglist';
  import { useTime } from '@/hooks/useTime';
  import { clearInterval } from 'timers';
  import router from '@/router';
  import { padStart } from 'lodash-es';

  const message = useMessage();
  const themeVars = useThemeVars();
  const countDown = reactive({
    hour: '',
    day: '',
    minute: '',
    second: '',
    totalTime: 0,
  });
  const formValue = reactive({
    semeterId: '',
  });

  // const timer = setInterval(() => {
  //   countDown.second = (Number(countDown.second) - 1).toString();
  // }, 1000);

  const getTableHeaderColor = computed(() => {
    return themeVars.value.tableHeaderColor;
  });

  const renderCountdown: CountdownProps['render'] = ({ hours, minutes, seconds }) => {
    return `${countDown.day}天${String(hours).padStart(2, '0')}时${String(minutes).padStart(
      2,
      '0'
    )}分${String(seconds).padStart(2, '0')}秒`;
  };

  async function enrollReady() {
    // clearInterval(timer);
    const response = await checkEnrollingState({});
    console.log(response);
    if (response.code == 200) {
      message.success('请稍后，即将进入选课界面…');
      router.push({ name: 'enrolling-list', params: { semeterId: response.result } });
    } else if (response.code == 500) {
      message.info('未到选课时间!');
      countDown.hour = response.result.hour;
      countDown.day = Number(response.result.day).toString();
      countDown.minute = response.result.minute;
      countDown.second = response.result.second;
      countDown.totalTime =
        Number(countDown.hour) * 3600 + Number(countDown.minute) * 60 + Number(countDown.second);
      // message.error(
      //   '选课时段将于' +
      //     response.result.day +
      //     '日' +
      //     response.result.hour +
      //     '时' +
      //     response.result.minute +
      //     '分' +
      //     response.result.second +
      //     '秒之后开启'
      // );
    } else message.error(response.message.toString());
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

    &-extra2 {
      padding: 24px 40px;
      text-align: center;
      font-size: 24px;
      text-align: center;
      //color: #071a7c;
      background: v-bind(getTableHeaderColor);
      border-radius: 4px;
    }
  }
</style>
