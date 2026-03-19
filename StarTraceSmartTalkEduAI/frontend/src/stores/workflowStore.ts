import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import type { WorkflowDSL, WorkflowNode, WorkflowEdge } from '@/types/workflow';
import { nanoid } from 'nanoid';

interface HistoryEntry {
  dsl: WorkflowDSL;
}

export const useWorkflowStore = defineStore('workflow', () => {
  const dsl = ref<WorkflowDSL>({
    id: undefined,
    name: '新建工作流',
    description: '',
    nodes: [],
    edges: [],
    variables: {},
    version: 1,
  });

  const eduWorkflowId = ref<string | null>(null);
  const simWorkflowId = ref<string | null>(null);

  const history = ref<HistoryEntry[]>([]);
  const future = ref<HistoryEntry[]>([]);

  const dirty = ref(false);
  const autoSaveTimer = ref<number | null>(null);

  const selectedNodeId = ref<string | null>(null);
  const selectedNode = computed<WorkflowNode | null>(() => {
    return dsl.value.nodes.find((n) => n.id === selectedNodeId.value) || null;
  });

  const executionId = ref<string | null>(null);
  const lastExecutionStatus = ref<string | null>(null);
  const lastExecutionOutput = ref<any>(null);
  const logs = ref<
    Array<{ nodeId: string; message: string; level: 'INFO' | 'WARN' | 'ERROR'; ts: string }>
  >([]);

  function pushHistory() {
    history.value.push({ dsl: JSON.parse(JSON.stringify(dsl.value)) });
    if (history.value.length > 50) history.value.shift();
    future.value = [];
  }

  function undo() {
    const last = history.value.pop();
    if (!last) return;
    future.value.push({ dsl: JSON.parse(JSON.stringify(dsl.value)) });
    dsl.value = JSON.parse(JSON.stringify(last.dsl));
  }

  function redo() {
    const next = future.value.pop();
    if (!next) return;
    history.value.push({ dsl: JSON.parse(JSON.stringify(dsl.value)) });
    dsl.value = JSON.parse(JSON.stringify(next.dsl));
  }

  function markDirty() {
    dirty.value = true;
  }

  function setDsl(newDsl: WorkflowDSL) {
    pushHistory();
    dsl.value = JSON.parse(JSON.stringify(newDsl));
    dirty.value = false;
  }

  function addNode(type: WorkflowNode['type'], position: { x: number; y: number }) {
    pushHistory();
    const id = nanoid(8);
    const labelMap: Record<string, string> = {
      start: 'Start',
      llm: 'LLM',
      api: 'API',
      condition: 'Condition',
      variable: 'Variable',
    };
    const node: WorkflowNode = {
      id,
      type,
      label: labelMap[type],
      position,
      inputSchema: {},
      outputSchema: {},
      config: {},
    };
    dsl.value.nodes.push(node);
    selectedNodeId.value = id;
    dirty.value = true;
  }

  function updateNode(id: string, partial: Partial<WorkflowNode>) {
    pushHistory();
    const idx = dsl.value.nodes.findIndex((n) => n.id === id);
    if (idx === -1) return;
    dsl.value.nodes[idx] = {
      ...dsl.value.nodes[idx],
      ...partial,
      config: {
        ...dsl.value.nodes[idx].config,
        ...(partial.config || {}),
      },
    };
    dirty.value = true;
  }

  function removeNode(id: string) {
    pushHistory();
    dsl.value.nodes = dsl.value.nodes.filter((n) => n.id !== id);
    dsl.value.edges = dsl.value.edges.filter((e) => e.source !== id && e.target !== id);
    if (selectedNodeId.value === id) selectedNodeId.value = null;
    dirty.value = true;
  }

  function addEdge(edge: WorkflowEdge) {
    pushHistory();
    dsl.value.edges.push(edge);
    dirty.value = true;
  }

  function removeEdge(id: string) {
    pushHistory();
    dsl.value.edges = dsl.value.edges.filter((e) => e.id !== id);
    dirty.value = true;
  }

  function setSelection(nodeId: string | null) {
    selectedNodeId.value = nodeId;
  }

  function importJson(json: string) {
    const obj = JSON.parse(json) as WorkflowDSL;
    setDsl(obj);
  }

  function exportJson(): string {
    return JSON.stringify(dsl.value, null, 2);
  }

  function setExecution(id: string) {
    executionId.value = id;
  }

  function appendLogs(
    newLogs: Array<{ nodeId: string; message: string; level: 'INFO' | 'WARN' | 'ERROR'; ts: string }>,
  ) {
    logs.value = [...logs.value, ...newLogs];
  }

  function resetExecution() {
    executionId.value = null;
    lastExecutionStatus.value = null;
    lastExecutionOutput.value = null;
    logs.value = [];
  }

  function scheduleAutoSave(callback: () => Promise<void>, delayMs = 2000) {
    if (autoSaveTimer.value) window.clearTimeout(autoSaveTimer.value);
    autoSaveTimer.value = window.setTimeout(async () => {
      if (!dirty.value) return;
      await callback();
      dirty.value = false;
    }, delayMs) as unknown as number;
  }

  return {
    dsl,
    eduWorkflowId,
    simWorkflowId,
    selectedNodeId,
    selectedNode,
    executionId,
    lastExecutionStatus,
    lastExecutionOutput,
    logs,
    dirty,
    setDsl,
    addNode,
    updateNode,
    removeNode,
    addEdge,
    removeEdge,
    setSelection,
    importJson,
    exportJson,
    undo,
    redo,
    markDirty,
    setExecution,
    appendLogs,
    resetExecution,
    scheduleAutoSave,
  };
});


