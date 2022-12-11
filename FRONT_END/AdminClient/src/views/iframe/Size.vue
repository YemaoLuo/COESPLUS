<template>
  <div ref="chartRef" :style="{ height, width }"></div>
</template>
<script lang="ts">
  import { defineComponent, onMounted, ref, Ref } from 'vue';
  import { useECharts } from '@/hooks/web/useECharts';
  import { basicProps } from './props';
  // import { getEmailAmountInfo } from '@/api/dashboard/console';
  // import { type } from 'os';
  import { getObjectInfo } from '@/api/dashboard/console';

  export default defineComponent({
    props: basicProps,
    setup() {
      const chartRef = ref<HTMLDivElement | null>(null);
      const { setOptions } = useECharts(chartRef as Ref<HTMLDivElement>);

      onMounted(async () => {
        const data = await getObjectInfo();
        const ObjectTotal = data.result.totalSize;
        setOptions({
          title: {
            text: '占用空间：' + ObjectTotal + 'KB',
            left: 'left',
          },
          series: [
            {
              data: [
                { name: 'jpg', value: 3958 },
                { name: 'png', value: 275 },
                { name: 'jpeg', value: 72 },
              ],
              type: 'pie',
              // radius: ['50%', '80%'],
            },
          ],
        });
      });
      return { chartRef };
    },
  });
  // const loading = ref(true);
  // const Subjectname = ref<any>({});
  // const SubjectNunmber = ref<any>({});
</script>
