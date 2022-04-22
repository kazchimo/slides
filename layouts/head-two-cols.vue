<script setup lang="ts">
const props = defineProps({
  class: {
    type: String,
  },
})
</script>

<template>
  <div class="slidev-layout default">
    <div class="mb-8 head" :class="props.class">
      <slot name="head"/>
    </div>
    <div class="grid-cols-2 grid">
      <div class="pr-4 col-left" :class="props.class">
        <slot name="left"/>
      </div>
      <div class="pl-4 col-right" :class="props.class">
        <slot name="right"/>
      </div>
    </div>
  </div>
</template>
