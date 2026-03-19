<template>
  <div class="node agent">
    <div class="hdr">
      <div class="dot"></div>
      <div class="title">Agent</div>
      <div class="tag">SIM: Agent</div>
    </div>
    <div class="body">
      <div class="row">
        <span class="k">Agent</span>
        <span class="v">{{ agentName }}</span>
      </div>
      <div class="row hint">
        <span class="k">提示</span>
        <span class="v">后续可扩展 prompt/tools</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";
import type { NodeProps } from "@vue-flow/core";

const props = defineProps<NodeProps>();
const agentName = computed(() => String((props.data as any)?.agentName ?? "默认智能体"));
</script>

<style scoped>
.node {
  width: 260px;
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
  background: rgba(37, 99, 235, 0.08);
}
.dot {
  width: 10px;
  height: 10px;
  border-radius: 999px;
  background: #2563eb;
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
  border: 1px solid rgba(37, 99, 235, 0.25);
  background: rgba(255, 255, 255, 0.7);
}
.body {
  padding: 10px;
}
.row {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  font-size: 12px;
  line-height: 1.3;
}
.row + .row {
  margin-top: 8px;
}
.k {
  color: rgba(15, 23, 42, 0.7);
  white-space: nowrap;
}
.v {
  font-weight: 800;
  color: rgba(15, 23, 42, 0.85);
  text-align: right;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.row.hint .v {
  font-weight: 700;
  color: rgba(15, 23, 42, 0.65);
}
</style>


