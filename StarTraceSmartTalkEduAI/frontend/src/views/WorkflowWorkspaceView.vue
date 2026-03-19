<template>
  <div class="wf-page">
    <header class="wf-header">
      <div class="wf-header-left">
        <input
          v-model="dsl.name"
          class="wf-name-input"
          placeholder="工作流名称"
          @input="markDirtyAndAutosave"
        />
        <span class="wf-desc">{{ dsl.description || '自定义工作流工作台' }}</span>
      </div>
      <div class="wf-header-right">
        <button class="btn" @click="handleImport">导入 JSON</button>
        <button class="btn" @click="handleExport">导出 JSON</button>
        <button class="btn" :disabled="false" @click="undo">撤销</button>
        <button class="btn" :disabled="false" @click="redo">重做</button>
        <button class="btn" :disabled="saving" @click="saveWorkflow">
          {{ saving ? '保存中…' : '保存' }}
        </button>
        <button class="btn primary" :disabled="running" @click="runWorkflow">
          {{ running ? '运行中…' : '运行' }}
        </button>
      </div>
    </header>

    <main class="wf-main">
      <aside class="wf-sidebar">
        <h3 class="wf-sidebar-title">节点库</h3>
        <div class="node-palette">
          <div class="palette-item start" @mousedown.prevent="addNodeFromPalette('start')">
            <div class="pi-title">Start</div>
            <div class="pi-desc">统一入口（输入 Schema）</div>
          </div>
          <div class="palette-item llm" @mousedown.prevent="addNodeFromPalette('llm')">
            <div class="pi-title">LLM</div>
            <div class="pi-desc">大模型推理（系统提示 / 模型）</div>
          </div>
          <div class="palette-item api" @mousedown.prevent="addNodeFromPalette('api')">
            <div class="pi-title">API</div>
            <div class="pi-desc">HTTP 请求（URL / Headers / Body）</div>
          </div>
          <div class="palette-item condition" @mousedown.prevent="addNodeFromPalette('condition')">
            <div class="pi-title">Condition</div>
            <div class="pi-desc">条件判断（表达式）</div>
          </div>
          <div class="palette-item variable" @mousedown.prevent="addNodeFromPalette('variable')">
            <div class="pi-title">Variable</div>
            <div class="pi-desc">变量赋值 / 处理</div>
          </div>
        </div>
      </aside>

      <section class="wf-canvas" @dragover.prevent @drop="onDrop">
        <VueFlow
          v-model:nodes="flowNodes"
          v-model:edges="flowEdges"
          :node-types="nodeTypes"
          class="workflow-flow"
          :fit-view-on-init="true"
          :max-zoom="2"
          :min-zoom="0.2"
          @connect="onConnect"
          @nodes-change="onNodesChange"
          @edges-change="onEdgesChange"
          @node-click="onNodeClick"
        >
          <Background :gap="20" pattern-color="#e5e7eb" />
          <Controls />
        </VueFlow>
      </section>

      <aside class="wf-inspector">
        <div v-if="selectedNode">
          <h3 class="inspector-title">{{ selectedNode.label }} 节点配置</h3>
          <div class="inspector-inner">
            <div class="field">
              <label>显示名称</label>
              <input
                v-model="localNodeLabel"
                @change="updateNodeLabel"
                class="input"
                placeholder="节点名称"
              />
            </div>

            <template v-if="selectedNode.type === 'start'">
              <div class="section-title">Start 配置</div>
              <p class="section-tip">
                对齐 Sim 的 Start Trigger：使用 inputFormat 定义输入字段结构。
              </p>
              <label class="field-label">Input Format JSON</label>
              <textarea
                v-model="startInputFormatJson"
                class="json-editor"
                rows="10"
                spellcheck="false"
              />
              <button class="btn small" @click="applyStartInputFormat">应用 Input Format</button>
            </template>

            <template v-else-if="selectedNode.type === 'llm'">
              <div class="section-title">LLM 配置</div>
              <div class="field">
                <label>模型</label>
                <input
                  v-model="llmConfig.model"
                  class="input"
                  placeholder="如 claude-3-5-sonnet / gpt-4.1"
                  @change="updateLlmConfig"
                />
              </div>
              <div class="field">
                <label>System Prompt</label>
                <textarea
                  v-model="llmConfig.systemPrompt"
                  rows="4"
                  class="input"
                  @change="updateLlmConfig"
                />
              </div>
              <div class="field row">
                <div>
                  <label>Temperature</label>
                  <input
                    type="number"
                    step="0.1"
                    min="0"
                    max="2"
                    v-model.number="llmConfig.temperature"
                    class="input small"
                    @change="updateLlmConfig"
                  />
                </div>
                <div>
                  <label>Max Tokens</label>
                  <input
                    type="number"
                    min="1"
                    v-model.number="llmConfig.maxTokens"
                    class="input small"
                    @change="updateLlmConfig"
                  />
                </div>
              </div>
              <label class="field-label">Response Format JSON Schema（可选）</label>
              <textarea
                v-model="llmResponseFormatJson"
                rows="6"
                class="json-editor"
                spellcheck="false"
              />
              <button class="btn small" @click="applyLlmResponseFormat">应用 Response Format</button>
            </template>

            <template v-else-if="selectedNode.type === 'api'">
              <div class="section-title">API 配置</div>
              <div class="field row">
                <div>
                  <label>方法</label>
                  <select v-model="apiConfig.method" class="input small" @change="updateApiConfig">
                    <option value="GET">GET</option>
                    <option value="POST">POST</option>
                    <option value="PUT">PUT</option>
                    <option value="DELETE">DELETE</option>
                  </select>
                </div>
              </div>
              <div class="field">
                <label>URL</label>
                <input
                  v-model="apiConfig.url"
                  class="input"
                  placeholder="https://api.example.com/..."
                  @change="updateApiConfig"
                />
              </div>
              <label class="field-label">Headers（JSON）</label>
              <textarea
                v-model="apiHeadersJson"
                rows="4"
                class="json-editor"
                spellcheck="false"
              />
              <label class="field-label">Body</label>
              <textarea
                v-model="apiConfig.body"
                rows="5"
                class="json-editor"
                spellcheck="false"
                @change="updateApiConfig"
              />
              <button class="btn small" @click="applyApiHeaders">应用 Headers</button>
            </template>

            <template v-else-if="selectedNode.type === 'condition'">
              <div class="section-title">条件配置</div>
              <p class="section-tip">表达式示例：&lt;score&gt; &gt;= 60</p>
              <div class="field">
                <label>表达式</label>
                <input
                  v-model="conditionConfig.expression"
                  class="input"
                  @change="updateConditionConfig"
                />
              </div>
            </template>

            <template v-else-if="selectedNode.type === 'variable'">
              <div class="section-title">变量处理</div>
              <p class="section-tip">支持简单赋值，如 result = &lt;agent1.output&gt;</p>
              <div v-for="(a, idx) in variableConfig.assignments" :key="idx" class="field row">
                <input
                  v-model="a.name"
                  class="input small"
                  placeholder="变量名"
                  @change="updateVariableConfig"
                />
                <input
                  v-model="a.expr"
                  class="input small"
                  placeholder="表达式，如 <agent1.result>"
                  @change="updateVariableConfig"
                />
                <button class="btn small danger" @click="removeAssignment(idx)">删</button>
              </div>
              <button class="btn small" @click="addAssignment">新增变量</button>
            </template>

            <div class="section-title">高级：完整配置 JSON</div>
            <textarea
              v-model="nodeConfigJson"
              rows="10"
              class="json-editor"
              spellcheck="false"
            />
            <button class="btn small" @click="applyNodeConfigJson">应用 JSON</button>

            <button class="btn danger mt" @click="deleteNode">删除该节点</button>
          </div>
        </div>
        <div v-else class="inspector-empty">
          选择一个节点以编辑参数（Start / LLM / API / Condition / Variable）
        </div>
      </aside>
    </main>

    <transition name="slide-up">
      <section v-if="executionId" class="wf-exec-panel">
        <header class="exec-header">
          <div>
            <span class="exec-title">执行结果</span>
            <span class="exec-sub">Execution ID: {{ executionId }}</span>
          </div>
          <button class="btn small" @click="closeExecution">关闭</button>
        </header>
        <div class="exec-body">
          <div class="exec-logs">
            <div
              v-for="(log, idx) in logs"
              :key="idx"
              class="log-line"
              :class="['level-' + log.level.toLowerCase()]"
            >
              <span class="ts">{{ log.ts }}</span>
              <span class="node">[{{ log.nodeId }}]</span>
              <span class="msg">{{ log.message }}</span>
            </div>
          </div>
          <div class="exec-output" v-if="lastExecutionOutput">
            <h4>最终输出</h4>
            <pre>{{ prettyOutput }}</pre>
          </div>
        </div>
      </section>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';
import {
  VueFlow,
  Background,
  Controls,
  type Node,
  type Edge,
  type Connection,
  type NodeTypesObject,
} from '@vue-flow/core';

import { useWorkflowStore } from '@/stores/workflowStore';
import type { WorkflowEdge, WorkflowNode } from '@/types/workflow';
import { workflowApi } from '@/api/workflow';

const StartNode = {
  template: `<div class="vf-node vf-start">
    <div class="title">Start</div>
    <div class="sub">统一入口</div>
  </div>`,
};
const LlmNode = {
  template: `<div class="vf-node vf-llm">
    <div class="title">LLM</div>
    <div class="sub">大模型推理</div>
  </div>`,
};
const ApiNode = {
  template: `<div class="vf-node vf-api">
    <div class="title">API</div>
    <div class="sub">HTTP 请求</div>
  </div>`,
};
const ConditionNode = {
  template: `<div class="vf-node vf-condition">
    <div class="title">Condition</div>
    <div class="sub">条件判断</div>
  </div>`,
};
const VariableNode = {
  template: `<div class="vf-node vf-variable">
    <div class="title">Variable</div>
    <div class="sub">变量处理</div>
  </div>`,
};

const nodeTypes: NodeTypesObject = {
  start: StartNode,
  llm: LlmNode,
  api: ApiNode,
  condition: ConditionNode,
  variable: VariableNode,
};

const route = useRoute();
const experimentId = computed(() => route.params.experimentId as string);

const store = useWorkflowStore();
const dsl = store.dsl;
const selectedNode = store.selectedNode;
const logs = store.logs;
const executionId = store.executionId;
const lastExecutionOutput = store.lastExecutionOutput;

const saving = ref(false);
const running = ref(false);

const flowNodes = computed<Node[]>({
  get() {
    return dsl.nodes.map((n) => ({
      id: n.id,
      type: n.type,
      position: n.position,
      data: { label: n.label },
    }));
  },
  set(val) {
    val.forEach((vn) => {
      store.updateNode(vn.id, {
        position: { ...vn.position },
      } as any);
    });
  },
});

const flowEdges = computed<Edge[]>({
  get() {
    return dsl.edges.map((e) => ({
      id: e.id,
      source: e.source,
      target: e.target,
      sourceHandle: e.sourceHandle,
      targetHandle: e.targetHandle,
      label: e.conditionLabel,
      animated: true,
    }));
  },
  set() {
    // no-op
  },
});

const localNodeLabel = ref('');
const nodeConfigJson = ref('');
const startInputFormatJson = ref('');
const llmConfig = ref<{ model?: string; systemPrompt?: string; temperature?: number; maxTokens?: number }>({});
const llmResponseFormatJson = ref('');
const apiConfig = ref<{ method: 'GET' | 'POST' | 'PUT' | 'DELETE'; url?: string; body?: string }>({
  method: 'POST',
});
const apiHeadersJson = ref('{}');
const conditionConfig = ref<{ expression?: string }>({});
const variableConfig = ref<{ assignments: { name: string; expr: string }[] }>({ assignments: [] });

const prettyOutput = computed(() => {
  try {
    return JSON.stringify(lastExecutionOutput.value, null, 2);
  } catch {
    return String(lastExecutionOutput.value ?? '');
  }
});

function refreshInspector() {
  const node = selectedNode.value;
  if (!node) return;
  localNodeLabel.value = node.label;
  nodeConfigJson.value = JSON.stringify(node.config || {}, null, 2);

  if (node.type === 'start') {
    startInputFormatJson.value = JSON.stringify(node.config.inputFormat || {}, null, 2);
  } else if (node.type === 'llm') {
    llmConfig.value = {
      model: node.config.model,
      systemPrompt: node.config.systemPrompt,
      temperature: node.config.temperature ?? 0.3,
      maxTokens: node.config.maxTokens ?? 1024,
    };
    llmResponseFormatJson.value = JSON.stringify(node.config.responseFormat || {}, null, 2);
  } else if (node.type === 'api') {
    apiConfig.value = {
      method: (node.config.method as any) || 'POST',
      url: node.config.url,
      body: node.config.body,
    };
    apiHeadersJson.value = JSON.stringify(node.config.headers || {}, null, 2);
  } else if (node.type === 'condition') {
    conditionConfig.value = { expression: node.config.expression };
  } else if (node.type === 'variable') {
    variableConfig.value = { assignments: node.config.assignments || [] };
  }
}

function onConnect(connection: Connection) {
  const id = `e-${connection.source}-${connection.target}-${Date.now()}`;
  store.addEdge({
    id,
    source: connection.source!,
    target: connection.target!,
    sourceHandle: connection.sourceHandle,
    targetHandle: connection.targetHandle,
  } as WorkflowEdge);
}

function onNodesChange() {
  // handled by flowNodes setter
}

function onEdgesChange() {
  // optional fine-grained edge handling
}

function onNodeClick(_: any, node: Node) {
  store.setSelection(node.id);
  refreshInspector();
}

function addNodeFromPalette(type: WorkflowNode['type']) {
  store.addNode(type, { x: 150, y: 100 });
}

function onDrop() {
  // drag from palette can be implemented via dataTransfer if需要
}

async function saveWorkflow() {
  saving.value = true;
  try {
    if (!store.eduWorkflowId) {
      const res = await workflowApi.create(dsl);
      if (res.code === 200) {
        store.eduWorkflowId = res.data.eduWorkflowId;
        alert('创建成功');
      } else {
        alert(res.message || '创建失败');
      }
    } else {
      const res = await workflowApi.update(store.eduWorkflowId, dsl);
      if (res.code === 200) {
        alert('保存成功');
      } else {
        alert(res.message || '保存失败');
      }
    }
  } catch (e: any) {
    console.error(e);
    alert(e?.message || '保存异常');
  } finally {
    saving.value = false;
  }
}

async function runWorkflow() {
  if (!store.eduWorkflowId) {
    await saveWorkflow();
    if (!store.eduWorkflowId) return;
  }
  running.value = true;
  store.resetExecution();
  try {
    const res = await workflowApi.run(store.eduWorkflowId!, {});
    if (res.code === 200) {
      store.setExecution(res.data.executionId);
      pollLogs(res.data.executionId);
    } else {
      alert(res.message || '运行失败');
    }
  } catch (e: any) {
    console.error(e);
    alert(e?.message || '运行异常');
  } finally {
    running.value = false;
  }
}

let logsTimer: number | null = null;

async function pollLogs(execId: string) {
  if (logsTimer) window.clearInterval(logsTimer);
  logsTimer = window.setInterval(async () => {
    try {
      const res = await workflowApi.logs(execId);
      if (res.code !== 200 || !res.data) return;
      store.appendLogs(res.data.logs);
      store.lastExecutionStatus = res.data.status;
      store.lastExecutionOutput = res.data.output;
      if (res.data.status === 'SUCCESS' || res.data.status === 'FAILED') {
        if (logsTimer) window.clearInterval(logsTimer);
        logsTimer = null;
      }
    } catch (e) {
      console.error('poll logs error', e);
    }
  }, 1500) as unknown as number;
}

function closeExecution() {
  if (logsTimer) window.clearInterval(logsTimer);
  logsTimer = null;
  store.resetExecution();
}

function updateNodeLabel() {
  if (!selectedNode.value) return;
  store.updateNode(selectedNode.value.id, { label: localNodeLabel.value } as any);
}

function applyNodeConfigJson() {
  if (!selectedNode.value) return;
  try {
    const obj = JSON.parse(nodeConfigJson.value);
    store.updateNode(selectedNode.value.id, { config: obj } as any);
    refreshInspector();
  } catch {
    alert('配置 JSON 解析失败');
  }
}

function applyStartInputFormat() {
  if (!selectedNode.value) return;
  try {
    const obj = JSON.parse(startInputFormatJson.value);
    store.updateNode(selectedNode.value.id, {
      config: { ...selectedNode.value.config, inputFormat: obj },
    } as any);
  } catch {
    alert('Input Format JSON 解析失败');
  }
}

function updateLlmConfig() {
  if (!selectedNode.value) return;
  store.updateNode(selectedNode.value.id, {
    config: { ...selectedNode.value.config, ...llmConfig.value },
  } as any);
}

function applyLlmResponseFormat() {
  if (!selectedNode.value) return;
  try {
    const obj = JSON.parse(llmResponseFormatJson.value);
    store.updateNode(selectedNode.value.id, {
      config: { ...selectedNode.value.config, responseFormat: obj },
    } as any);
  } catch {
    alert('Response Format JSON 解析失败');
  }
}

function updateApiConfig() {
  if (!selectedNode.value) return;
  store.updateNode(selectedNode.value.id, {
    config: { ...selectedNode.value.config, ...apiConfig.value },
  } as any);
}

function applyApiHeaders() {
  if (!selectedNode.value) return;
  try {
    const obj = JSON.parse(apiHeadersJson.value);
    store.updateNode(selectedNode.value.id, {
      config: { ...selectedNode.value.config, headers: obj },
    } as any);
  } catch {
    alert('Headers JSON 解析失败');
  }
}

function updateConditionConfig() {
  if (!selectedNode.value) return;
  store.updateNode(selectedNode.value.id, {
    config: { ...selectedNode.value.config, ...conditionConfig.value },
  } as any);
}

function addAssignment() {
  variableConfig.value.assignments.push({ name: '', expr: '' });
  updateVariableConfig();
}

function removeAssignment(idx: number) {
  variableConfig.value.assignments.splice(idx, 1);
  updateVariableConfig();
}

function updateVariableConfig() {
  if (!selectedNode.value) return;
  store.updateNode(selectedNode.value.id, {
    config: { ...selectedNode.value.config, ...variableConfig.value },
  } as any);
}

function deleteNode() {
  if (!selectedNode.value) return;
  if (!window.confirm('确定删除该节点？')) return;
  store.removeNode(selectedNode.value.id);
}

function handleExport() {
  const json = store.exportJson();
  navigator.clipboard.writeText(json).catch(() => {});
  alert('DSL JSON 已复制到剪贴板');
}

function handleImport() {
  const json = window.prompt('粘贴 DSL JSON：');
  if (!json) return;
  try {
    store.importJson(json);
  } catch (e: any) {
    alert('JSON 解析失败：' + (e?.message || ''));
  }
}

function undo() {
  store.undo();
}

function redo() {
  store.redo();
}

function markDirtyAndAutosave() {
  store.markDirty();
  store.scheduleAutoSave(saveWorkflow, 2000);
}

onMounted(() => {
  const id = experimentId.value;
  console.log('打开工作流工作台 experimentId = ', id);
});

onBeforeUnmount(() => {
  if (logsTimer) window.clearInterval(logsTimer);
});
</script>

<style scoped>
.wf-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #f8fafc;
}

.wf-header {
  height: 56px;
  padding: 0 16px;
  border-bottom: 1px solid #e2e8f0;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.wf-header-left {
  display: flex;
  flex-direction: column;
}
.wf-name-input {
  border: none;
  font-size: 16px;
  font-weight: 700;
  outline: none;
  background: transparent;
}
.wf-desc {
  font-size: 12px;
  color: #64748b;
}

.wf-header-right {
  display: flex;
  gap: 8px;
}

.btn {
  height: 32px;
  padding: 0 10px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  background: #fff;
  font-size: 13px;
  cursor: pointer;
}
.btn.primary {
  background: #2563eb;
  color: #fff;
  border-color: #1d4ed8;
}
.btn.small {
  height: 28px;
  font-size: 12px;
  padding: 0 8px;
}
.btn.danger {
  border-color: #fecaca;
  color: #b91c1c;
}

.wf-main {
  flex: 1;
  display: grid;
  grid-template-columns: 260px minmax(0, 1fr) 340px;
  min-height: 0;
}

.wf-sidebar {
  border-right: 1px solid #e2e8f0;
  background: #fff;
  padding: 12px;
}
.wf-sidebar-title {
  font-size: 14px;
  font-weight: 700;
  margin-bottom: 8px;
}
.node-palette {
  display: grid;
  gap: 8px;
}
.palette-item {
  padding: 8px;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  background: #f9fafb;
  cursor: grab;
}
.palette-item .pi-title {
  font-size: 13px;
  font-weight: 700;
}
.palette-item .pi-desc {
  font-size: 11px;
  color: #64748b;
}
.palette-item.start {
  border-left: 3px solid #10b981;
}
.palette-item.llm {
  border-left: 3px solid #2563eb;
}
.palette-item.api {
  border-left: 3px solid #f97316;
}
.palette-item.condition {
  border-left: 3px solid #eab308;
}
.palette-item.variable {
  border-left: 3px solid #8b5cf6;
}

.wf-canvas {
  position: relative;
  min-height: 0;
}
.workflow-flow {
  width: 100%;
  height: 100%;
}
.vf-node {
  padding: 10px;
  border-radius: 12px;
  box-shadow: 0 6px 10px rgba(15, 23, 42, 0.08);
  background: #fff;
  border: 1px solid #e2e8f0;
}
.vf-node .title {
  font-size: 13px;
  font-weight: 800;
}
.vf-node .sub {
  font-size: 11px;
  color: #64748b;
}
.vf-start {
  border-left: 4px solid #10b981;
}
.vf-llm {
  border-left: 4px solid #2563eb;
}
.vf-api {
  border-left: 4px solid #f97316;
}
.vf-condition {
  border-left: 4px solid #eab308;
}
.vf-variable {
  border-left: 4px solid #8b5cf6;
}

.wf-inspector {
  border-left: 1px solid #e2e8f0;
  background: #fff;
  padding: 12px;
  overflow: auto;
}
.inspector-title {
  font-size: 14px;
  font-weight: 800;
  margin-bottom: 8px;
}
.inspector-inner {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.field {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.field.row {
  flex-direction: row;
  gap: 6px;
  align-items: center;
}
.field-label {
  font-size: 12px;
  color: #64748b;
  margin-top: 6px;
}
.section-title {
  margin-top: 4px;
  font-size: 13px;
  font-weight: 700;
}
.section-tip {
  font-size: 11px;
  color: #64748b;
}
.input {
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  padding: 6px 8px;
  font-size: 12px;
}
.input.small {
  width: 120px;
}
.json-editor {
  width: 100%;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New',
    monospace;
  font-size: 12px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  padding: 6px 8px;
  background: #f9fafb;
}
.mt {
  margin-top: 10px;
}
.inspector-empty {
  font-size: 12px;
  color: #94a3b8;
}

.wf-exec-panel {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  height: 260px;
  border-top: 1px solid #e2e8f0;
  background: #020617;
  color: #e5e7eb;
  display: flex;
  flex-direction: column;
}
.exec-header {
  height: 40px;
  padding: 0 12px;
  border-bottom: 1px solid #1e293b;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.exec-title {
  font-size: 13px;
  font-weight: 700;
}
.exec-sub {
  font-size: 11px;
  margin-left: 8px;
  color: #64748b;
}
.exec-body {
  display: flex;
  flex: 1;
  overflow: hidden;
}
.exec-logs {
  flex: 2;
  padding: 8px;
  overflow: auto;
  font-size: 11px;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New',
    monospace;
}
.exec-output {
  flex: 1;
  border-left: 1px solid #1e293b;
  padding: 8px;
  overflow: auto;
  font-size: 11px;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New',
    monospace;
}
.log-line {
  display: flex;
  gap: 6px;
  margin-bottom: 2px;
}
.log-line .ts {
  color: #64748b;
}
.log-line .node {
  color: #38bdf8;
}
.log-line .msg {
  color: #e5e7eb;
}
.log-line.level-error .msg {
  color: #fecaca;
}
.log-line.level-warn .msg {
  color: #facc15;
}

.slide-up-enter-active,
.slide-up-leave-active {
  transition: transform 0.2s ease, opacity 0.2s ease;
}
.slide-up-enter-from,
.slide-up-leave-to {
  transform: translateY(100%);
  opacity: 0;
}
</style>

[the very large Vue component content from the design, omitted here for brevity in this patch placeholder]

