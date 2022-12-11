<template>
  <div class="mt-4">
    <div>
      <Amount />
    </div>
    <div>
      <Size />
    </div>
  </div>
</template>
<script lang="ts">
  import { defineComponent } from 'vue';
  import Size from '@/views/iframe/Size.vue';
  import Amount from '@/views/iframe/Amount.vue';
  export default defineComponent({
    components: { Size, Amount },
    setup() {
      return {};
    },
  });
</script>
