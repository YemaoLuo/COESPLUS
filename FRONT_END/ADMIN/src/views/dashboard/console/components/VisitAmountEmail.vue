<template>
  <div ref="chartRef" :style="{ height, width }"></div>
</template>
<script lang="ts">
  import { defineComponent, onMounted, ref, Ref } from 'vue';
  import { useECharts } from '@/hooks/web/useECharts';
  import { basicProps } from './props';
  // import { getEmailAmountInfo } from '@/api/dashboard/console';
  // import { type } from 'os';

  export default defineComponent({
    props: basicProps,
    setup() {
      const chartRef = ref<HTMLDivElement | null>(null);
      const { setOptions } = useECharts(chartRef as Ref<HTMLDivElement>);

      onMounted(async () => {
        // const data = await getEmailAmountInfo();
        // Subjectname.value = Object.values(data.result.subject);
        // SubjectNunmber.value = data.result.count;
        // console.log(Subjectname.value);
        // loading.value = false;
        setOptions({
          // tooltip: {
          //   trigger: 'axis',
          //   axisPointer: {
          //     lineStyle: {
          //       width: 1,
          //       color: '#019680',
          //     },
          //   },
          // },
          series: [
            {
              data: [
                { name: '导出学生信息表通知', value: 55 },
                { name: '导出学院信息表通知', value: 4 },
                { name: '导出教师信息表通知', value: 2 },
                { name: '导出课程信息表通知', value: 8 },
                { name: '导出课程学生信息表通知', value: 33 },
                { name: '登录通知', value: 137 },
                { name: '登录预警', value: 57 },
                { name: '账户创建', value: 8 },
                { name: '重置密码', value: 34 },
              ],
              type: 'pie',
              radius: ['50%', '80%'],
            },
          ],
        });
      });
      return { chartRef };
    },
  });
  const loading = ref(true);
  const Subjectname = ref<any>({});
  const SubjectNunmber = ref<any>({});
</script>
