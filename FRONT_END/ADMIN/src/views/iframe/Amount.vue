<template>
  <div ref="chartRef" :style="{ height, width }"></div>
</template>
<script lang="ts">
  import { defineComponent, onMounted, ref, Ref } from 'vue';
  import { useECharts } from '@/hooks/web/useECharts';
  import { basicProps } from './props';
  import { getObjectInfo } from '@/api/dashboard/console';
  // import { type } from 'os';

  export default defineComponent({
    props: basicProps,
    setup() {
      const chartRef = ref<HTMLDivElement | null>(null);
      const { setOptions } = useECharts(chartRef as Ref<HTMLDivElement>);

      onMounted(async () => {
        const data = await getObjectInfo();
        const ObjectTotal = data.result.totalAmount;
        console.log(ObjectTotal);
        // SubjectNunmber.value = data.result.count;
        // console.log(Subjectname.value);
        // loading.value = false;
        setOptions({
          title: {
            text: '占用数量：' + ObjectTotal,
            // subtext: ObjectTotal,
            left: 'left',
          },
          series: [
            {
              data: [
                { name: 'jpg', value: 11 },
                { name: 'jpeg', value: 2 },
                { name: 'png', value: 2 },
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
