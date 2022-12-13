<template>
  <!--  <div class="console">-->
  <div>
    <n-carousel
      effect="card"
      prev-slide-style="transform: translateX(-150%) translateZ(-800px);"
      next-slide-style="transform: translateX(50%) translateZ(-800px);"
      style="height: 300px"
      :show-dots="true"
      draggable
    >
      <n-carousel-item :style="{ width: '90%' }">
        <img
          class="carousel-img"
          src="https://www.must.edu.mo/images/newui/article1112183732.jpg"
        />
      </n-carousel-item>
      <n-carousel-item :style="{ width: '90%' }">
        <img
          class="carousel-img"
          src="https://www.must.edu.mo/images/newui/article0605190635.jpg"
        />
      </n-carousel-item>
      <n-carousel-item :style="{ width: '90%' }">
        <img
          class="carousel-img"
          src="https://www.must.edu.mo/images/newui/sgs-prospectus-2023.jpg"
        />
      </n-carousel-item>
      <n-carousel-item :style="{ width: '90%' }">
        <img
          class="carousel-img"
          src="https://www.must.edu.mo/images/newui/article0606155018.jpg"
        />
      </n-carousel-item>
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

  onMounted(async () => {
    // const data = await getConsoleInfo();
    // visits.value = data.visits;
    // saleroom.value = data.saleroom;
    // orderLarge.value = data.orderLarge;
    // volume.value = data.volume;
    // loading.value = false;
  });
</script>

<style scoped>
  .carousel-img {
    margin: 0 auto;
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
</style>
