<template>
  <!--  <div class="console">-->
  <div style="margin: 50px">
    <n-carousel>
      <img
        class="carousel-img"
        src="https://www.must.edu.mo/images/newui/article0606155018.jpg"
        alt=""
      />
      <img
        class="carousel-img"
        src="https://www.must.edu.mo/images/newui/sgs-prospectus-2023.jpg"
        alt=""
      />
      <img
        class="carousel-img"
        src="https://www.must.edu.mo/images/newui/article0605190635.jpg"
        alt=""
      />
      <img
        class="carousel-img"
        src="https://www.must.edu.mo/images/newui/article1112183732.jpg"
        alt=""
      />
    </n-carousel>
  </div>
  <!--访问量 | 流量趋势-->
  <VisiTab />
  <!--  </div>-->
</template>
<script lang="ts" setup>
  import { ref, onMounted } from 'vue';
  // import { getConsoleInfo } from '@/api/dashboard/console';
  import VisiTab from './components/VisiTab.vue';
  // import { CountTo } from '@/components/CountTo/index';
  // import {
  //   CaretUpOutlined,
  //   CaretDownOutlined,
  //   UsergroupAddOutlined,
  //   BarChartOutlined,
  //   ShoppingCartOutlined,
  //   AccountBookOutlined,
  //   CreditCardOutlined,
  //   MailOutlined,
  //   TagsOutlined,
  //   SettingOutlined,
  // } from '@vicons/antd';

  // const loading = ref(true);
  // const visits = ref<any>({});
  // const saleroom = ref<any>({});
  // const orderLarge = ref<any>({});
  // const volume = ref({});

  // 图标列表
  // const iconList = [
  //   {
  //     icon: UsergroupAddOutlined,
  //     size: '32',
  //     title: '用户',
  //     color: '#69c0ff',
  //     eventObject: {
  //       click: () => {},
  //     },
  //   },
  //   {
  //     icon: BarChartOutlined,
  //     size: '32',
  //     title: '分析',
  //     color: '#69c0ff',
  //     eventObject: {
  //       click: () => {},
  //     },
  //   },
  //   {
  //     icon: ShoppingCartOutlined,
  //     size: '32',
  //     title: '商品',
  //     color: '#ff9c6e',
  //     eventObject: {
  //       click: () => {},
  //     },
  //   },
  //   {
  //     icon: AccountBookOutlined,
  //     size: '32',
  //     title: '订单',
  //     color: '#b37feb',
  //     eventObject: {
  //       click: () => {},
  //     },
  //   },
  //   {
  //     icon: CreditCardOutlined,
  //     size: '32',
  //     title: '票据',
  //     color: '#ffd666',
  //     eventObject: {
  //       click: () => {},
  //     },
  //   },
  //   {
  //     icon: MailOutlined,
  //     size: '32',
  //     title: '消息',
  //     color: '#5cdbd3',
  //     eventObject: {
  //       click: () => {},
  //     },
  //   },
  //   {
  //     icon: TagsOutlined,
  //     size: '32',
  //     title: '标签',
  //     color: '#ff85c0',
  //     eventObject: {
  //       click: () => {},
  //     },
  //   },
  //   {
  //     icon: SettingOutlined,
  //     size: '32',
  //     title: '配置',
  //     color: '#ffc069',
  //     eventObject: {
  //       click: () => {},
  //     },
  //   },
  // ];

  onMounted(async () => {
    // const data = await getConsoleInfo();
    // visits.value = data.visits;
    // saleroom.value = data.saleroom;
    // orderLarge.value = data.orderLarge;
    // volume.value = data.volume;
    // loading.value = false;
  });
</script>

<style lang="less" scoped></style>
