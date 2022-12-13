<template>
  <n-select
    v-model:value="value"
    filterable
    placeholder="搜索歌曲"
    :options="options"
    :loading="loading"
    clearable
    remote
    @search="handleSearch"
  />
</template>

<script>
  export default {
    name: 'FacultyOption',
    setup () {
      const loadingRef = ref(false)
      const optionsRef = ref<SelectOption[]>([])

      return {
        value: ref(null),
        loading: loadingRef,
        options: optionsRef,
        handleSearch: (query: string) => {
          if (!query.length) {
            optionsRef.value = []
            return
          }
          loadingRef.value = true
          window.setTimeout(() => {
            optionsRef.value = options.filter(
              (item) => ~item.label.indexOf(query)
            )
            loadingRef.value = false
          }, 1000)
        }
      }
    }
  };
</script>

<style scoped></style>
