<template>
  <div class="node start">
    <div class="hdr">
      <div class="dot"></div>
      <div class="title">Start</div>
      <div class="tag">SIM: Start</div>
    </div>
    <div class="body">
      <div class="row">
        <span class="k">说明</span>
        <span class="v">流程入口节点</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { NodeProps } from "@vue-flow/core";

defineProps<NodeProps>();
</script>

<style scoped>
.node {
  width: 220px;
  border-radius: 14px;
  border: 1px solid rgba(226, 232, 240, 1);
  background: #ffffff;
  box-shadow: 0 4px 10px rgba(15, 23, 42, 0.06);
  overflow: hidden;
}
.hdr {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 10px;
  border-bottom: 1px solid rgba(226, 232, 240, 1);
  background: rgba(16, 185, 129, 0.08);
}
.dot {
  width: 10px;
  height: 10px;
  border-radius: 999px;
  background: #10b981;
}
.title {
  font-weight: 900;
  font-size: 13px;
  color: #0f172a;
}
.tag {
  margin-left: auto;
  font-size: 11px;
  color: rgba(15, 23, 42, 0.6);
  padding: 3px 8px;
  border-radius: 999px;
  border: 1px solid rgba(16, 185, 129, 0.25);
  background: rgba(255, 255, 255, 0.7);
}
.body {
  padding: 10px;
}
.row {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
}
.k {
  color: rgba(15, 23, 42, 0.7);
}
.v {
  font-weight: 700;
  color: rgba(15, 23, 42, 0.85);
}
</style>


