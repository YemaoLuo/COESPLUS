<template>
  <div class="console">
    <!--数据卡片-->
    <n-grid cols="1 s:2 m:3 l:4 xl:4 2xl:4" responsive="screen" :x-gap="12" :y-gap="8">
      <n-grid-item>
        <NCard
          title="管理员登录次数排名"
          :segmented="{ content: true, footer: true }"
          size="small"
          :bordered="false"
        >
          <template #header-extra>
            <n-tag type="success">Admin</n-tag>
          </template>
          <div class="py-1 px-1 flex justify-between">
            <ol>
              <li v-for="item in LoginAdmin" :key="item.id">
                {{ item.name }} &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp{{ item.count }}次
              </li>
            </ol>
          </div>
        </NCard>
      </n-grid-item>
      <n-grid-item>
        <NCard
          title="老师登录次数排名"
          :segmented="{ content: true, footer: true }"
          size="small"
          :bordered="false"
        >
          <template #header-extra>
            <n-tag type="success">Teacher</n-tag>
          </template>
          <div class="py-1 px-1 flex justify-between">
            <ol>
              <li v-for="item in LoginTeacher" :key="item.id">
                {{ item.name }} &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp{{ item.count }}次
              </li>
            </ol>
          </div>
        </NCard>
      </n-grid-item>
      <n-grid-item>
        <NCard
          title="学生登录次数排名"
          :segmented="{ content: true, footer: true }"
          size="small"
          :bordered="false"
        >
          <template #header-extra>
            <n-tag type="success">Student</n-tag>
          </template>
          <div class="py-1 px-1 flex justify-between">
            <ol>
              <li v-for="item in LoginStudent" :key="item.id">
                {{ item.name }} &nbsp&nbsp&nbsp&nbsp&nbsp&nbsp{{ item.count }}次
              </li>
            </ol>
          </div>
        </NCard>
      </n-grid-item>
      <n-grid-item>
        <NCard
          title="访问量"
          :segmented="{ content: true, footer: true }"
          size="small"
          :bordered="false"
        >
          <template #header-extra>
            <n-tag type="success">PageView</n-tag>
          </template>
          <div class="py-1 px-1 flex justify-between">
            <n-skeleton v-if="loading" :width="100" size="medium" />
            <CountTo
              v-else
              prefix="在线管理员数："
              :startVal="0"
              :endVal="VisitAdmin"
              class="text-xl"
            />
          </div>
          <div class="py-1 px-1 flex justify-between">
            <n-skeleton v-if="loading" :width="100" size="medium" />
            <CountTo
              v-else
              prefix="在线老师数："
              :startVal="1"
              :endVal="VisitTeacher"
              class="text-xl"
            />
          </div>
          <div class="py-1 px-1 flex justify-between">
            <n-skeleton v-if="loading" :width="100" size="medium" />
            <CountTo
              v-else
              prefix="在线学生数："
              :startVal="1"
              :endVal="VisitStudent"
              class="text-xl"
            />
          </div>
          <template #footer>
            <div class="flex justify-between">
              <n-skeleton v-if="loading" text :repeat="2" />
              <template v-else>
                <div class="text-sn"> 总访问量： </div>
                <div class="text-sn">
                  <CountTo :startVal="0" :endVal="AllVisit" />
                </div>
              </template>
            </div>
          </template>
        </NCard>
      </n-grid-item>
    </n-grid>
    <!--访问量 | 流量趋势-->
    <VisiTab />
  </div>
</template>
<script lang="ts" setup>
  import { ref, onMounted } from 'vue';
  import { getConsoleVisitInfo, getConsoleLoginInfo } from '@/api/dashboard/console';
  import VisiTab from './components/VisiTab.vue';
  import { CountTo } from '@/components/CountTo/index';
  import { CaretUpOutlined, CaretDownOutlined } from '@vicons/antd';

  const loading = ref(true);
  const VisitTeacher = ref<any>({});
  const VisitAdmin = ref<any>({});
  const VisitStudent = ref<any>({});
  const AllVisit = ref<any>({});
  const LoginAdmin = ref<any>({});
  const LoginTeacher = ref<any>({});
  const LoginStudent = ref<any>({});
  onMounted(async () => {
    const visitdata = await getConsoleVisitInfo();
    VisitAdmin.value = visitdata.result.administrator;
    VisitTeacher.value = visitdata.result.teacher;
    VisitStudent.value = visitdata.result.student; //访问量数据
    AllVisit.value = VisitTeacher.value + VisitStudent.value + VisitAdmin.value;
    const temp = await getConsoleLoginInfo();
    LoginAdmin.value = temp.result.administrator;
    LoginTeacher.value = temp.result.teacher;
    LoginStudent.value = temp.result.student;
    loading.value = false;
  });
</script>

<style lang="less" scoped></style>
