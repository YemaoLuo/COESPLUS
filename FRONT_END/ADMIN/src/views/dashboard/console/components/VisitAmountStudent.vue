<template>
  <div ref="chartRef" :style="{ height, width }"></div>
</template>
<script lang="ts">
  import { defineComponent, onMounted, ref, Ref } from 'vue';
  import { useECharts } from '@/hooks/web/useECharts';
  import { basicProps } from './props';
  import { VisitAmountStudent } from '@/api/dashboard/console';

  export default defineComponent({
    props: basicProps,
    setup() {
      const chartRef = ref<HTMLDivElement | null>(null);
      const { setOptions } = useECharts(chartRef as Ref<HTMLDivElement>);

      onMounted(async () => {
        const data = await VisitAmountStudent();
        Facultyname = Object.keys(data.result);
        FacultyNunmber = Object.values(data.result);
        loading.value = false;
        setOptions({
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              lineStyle: {
                width: 1,
                color: '#019680',
              },
            },
          },
          grid: { left: '1%', right: '1%', top: '2  %', bottom: 0, containLabel: true },
          xAxis: {
            type: 'category',
            data: Facultyname,
          },
          yAxis: {
            type: 'value',
            max: 4000,
            splitNumber: 4,
          },
          series: [
            {
              data: FacultyNunmber,
              type: 'bar',
              barMaxWidth: 80,
            },
          ],
        });
      });
      return { chartRef };
    },
  });
  const loading = ref(true);
  let Facultyname = ref<any>({});
  let FacultyNunmber = ref<any>({});
</script>
