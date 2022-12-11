<template>
  <div class="mt-4">
    <NRow :gutter="24">
      <NCol :span="24">
        <n-card content-style="padding: 0;" :bordered="false">
          <n-tabs type="line" size="large" :tabs-padding="20" pane-style="padding: 20px;">
            <n-tab-pane name="激活老师数">
              <VisitAmountTeacher />
            </n-tab-pane>
            <n-tab-pane name="激活学生数">
              <VisitAmountStudent />
            </n-tab-pane>
            <n-tab-pane name="邮件发送记录">
              <VisitAmountEmail />
            </n-tab-pane>
          </n-tabs>
        </n-card>
      </NCol>
    </NRow>
  </div>
</template>
<script lang="ts">
  import { defineComponent } from 'vue';
  import VisitAmountTeacher from '@/views/dashboard/console/components/VisitAmountTeacher.vue';
  import VisitAmountStudent from '@/views/dashboard/console/components/VisitAmountStudent.vue';
  import VisitAmountEmail from '@/views/dashboard/console/components/VisitAmountEmail.vue';

  export default defineComponent({
    components: { VisitAmountStudent, VisitAmountTeacher, VisitAmountEmail },
    setup() {
      return {};
    },
  });
</script>
