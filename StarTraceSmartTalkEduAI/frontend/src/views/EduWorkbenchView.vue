<template>
  <div class="wb">
    <header class="wb-top">
      <div class="wb-title">
        <div class="name">教学工作台</div>
        <div class="sub">拖拽节点（Start/Agent）构建流程 · 右侧 Tool 可一键加载模板</div>
      </div>

      <div class="wb-actions">
        <button class="btn" @click="fitView">适配视图</button>
        <button class="btn" @click="resetToStartAgent">加载：Start + Agent</button>
        <button class="btn primary" @click="exportJson">导出 JSON</button>
      </div>
    </header>

    <main class="wb-main">
      <section class="canvas" @dragover.prevent @drop="onDrop">
        <VueFlow
          v-model:nodes="nodes"
          v-model:edges="edges"
          :node-types="nodeTypes"
          :default-viewport="{ zoom: 1 }"
          :min-zoom="0.2"
          :max-zoom="2"
          class="flow"
          @connect="onConnect"
        >
          <Background :gap="18" pattern-color="rgba(148,163,184,0.35)" />
          <MiniMap />
          <Controls />
        </VueFlow>
      </section>

      <aside class="tool">
        <div class="tool-section">
          <div class="tool-title">Tool</div>
          <div class="tool-desc">
            这里先内置 2 个工作流模板：<b>Start</b> 和 <b>Agent</b>（语义对齐 Sim Studio 的 Start / Agent）。
          </div>
        </div>

        <div class="tool-section">
          <div class="tool-subtitle">工作流模板</div>
          <div class="tool-grid">
            <button class="card" @click="loadTemplate('start')">
              <div class="card-title">Start</div>
              <div class="card-desc">仅包含 Start 节点</div>
            </button>
            <button class="card" @click="loadTemplate('agent')">
              <div class="card-title">Agent</div>
              <div class="card-desc">仅包含 Agent 节点</div>
            </button>
            <button class="card" @click="loadTemplate('start-agent')">
              <div class="card-title">Start → Agent</div>
              <div class="card-desc">默认：Start 连到 Agent</div>
            </button>
          </div>
        </div>

        <div class="tool-section">
          <div class="tool-subtitle">节点库（拖拽到画布）</div>
          <div class="palette">
            <div class="palette-item start" draggable="true" @dragstart="onDragStart($event, 'start')">
              <div class="pi-title">Start</div>
              <div class="pi-desc">流程起点</div>
            </div>
            <div class="palette-item agent" draggable="true" @dragstart="onDragStart($event, 'agent')">
              <div class="pi-title">Agent</div>
              <div class="pi-desc">智能体执行</div>
            </div>
          </div>
          <div class="tool-hint">提示：拖拽节点进画布后，可拖动位置、拖拽连线手柄进行连线。</div>
        </div>

        <div class="tool-section">
          <div class="tool-subtitle">JSON 输出</div>
          <textarea class="json" readonly :value="exportedJson" />
        </div>
      </aside>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from "vue";
import { VueFlow, useVueFlow, type Connection, type Edge, type Node, type NodeTypesObject } from "@vue-flow/core";
import { Background } from "@vue-flow/background";
import { Controls } from "@vue-flow/controls";
import { MiniMap } from "@vue-flow/minimap";

import StartNode from "../workbench/nodes/StartNode.vue";
import AgentNode from "../workbench/nodes/AgentNode.vue";

type NodeKind = "start" | "agent";

const nodeTypes: NodeTypesObject = {
  start: StartNode,
  agent: AgentNode,
};

const nodes = ref<Node[]>([]);
const edges = ref<Edge[]>([]);

const { addEdges, fitView: fitViewInternal, project } = useVueFlow();

const fitView = async () => {
  try {
    await fitViewInternal({ padding: 0.2, duration: 250 });
  } catch {
    // ignore
  }
};

const nextId = (() => {
  let i = 1;
  return () => String(i++);
})();

const createNode = (kind: NodeKind, position: { x: number; y: number }): Node => {
  const id = nextId();
  if (kind === "start") {
    return {
      id,
      type: "start",
      position,
      data: {
        label: "Start",
        simKind: "start",
      },
    };
  }
  return {
    id,
    type: "agent",
    position,
    data: {
      label: "Agent",
      simKind: "agent",
      // 可扩展：与 Sim Studio 对齐的 agent 配置
      agentName: "默认智能体",
      systemPrompt: "",
    },
  };
};

const loadTemplate = async (tpl: "start" | "agent" | "start-agent") => {
  nodes.value = [];
  edges.value = [];

  if (tpl === "start") {
    nodes.value = [createNode("start", { x: 80, y: 120 })];
  } else if (tpl === "agent") {
    nodes.value = [createNode("agent", { x: 120, y: 120 })];
  } else {
    const n1 = createNode("start", { x: 60, y: 140 });
    const n2 = createNode("agent", { x: 340, y: 140 });
    nodes.value = [n1, n2];
    edges.value = [
      {
        id: `e-${n1.id}-${n2.id}`,
        source: n1.id,
        target: n2.id,
        type: "smoothstep",
      },
    ];
  }

  await fitView();
};

const resetToStartAgent = () => loadTemplate("start-agent");

const onConnect = (connection: Connection) => {
  addEdges([
    {
      ...connection,
      id: `e-${connection.source}-${connection.target}-${Date.now()}`,
      type: "smoothstep",
    },
  ]);
};

const onDragStart = (e: DragEvent, kind: NodeKind) => {
  e.dataTransfer?.setData("application/eduai-node-kind", kind);
  e.dataTransfer?.setData("text/plain", kind);
  e.dataTransfer!.effectAllowed = "move";
};

const onDrop = (e: DragEvent) => {
  const kind = (e.dataTransfer?.getData("application/eduai-node-kind") ||
    e.dataTransfer?.getData("text/plain")) as NodeKind;
  if (kind !== "start" && kind !== "agent") return;

  const bounds = (e.currentTarget as HTMLElement).getBoundingClientRect();
  const position = project({
    x: e.clientX - bounds.left,
    y: e.clientY - bounds.top,
  });

  nodes.value = [...nodes.value, createNode(kind, position)];
};

const exportedJson = computed(() => {
  const payload = {
    version: 1,
    // 对齐语义：Start/Agent
    nodes: nodes.value.map((n) => ({
      id: n.id,
      kind: (n.data as any)?.simKind ?? n.type,
      position: n.position,
      data: n.data,
    })),
    edges: edges.value.map((e) => ({
      id: e.id,
      source: e.source,
      target: e.target,
    })),
  };
  return JSON.stringify(payload, null, 2);
});

const exportJson = async () => {
  try {
    await navigator.clipboard.writeText(exportedJson.value);
    alert("已复制 JSON 到剪贴板");
  } catch {
    // ignore
  }
};

// 默认先加载 Start → Agent
resetToStartAgent();
</script>

<style scoped>
.wb {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--bg-app);
  color: var(--text-main);
}

.wb-top {
  height: 60px;
  padding: 0 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid var(--border-subtle);
  background: #fff;
}
.wb-title .name {
  font-weight: 800;
  font-size: 16px;
}
.wb-title .sub {
  margin-top: 2px;
  font-size: 12px;
  color: var(--text-secondary);
}
.wb-actions {
  display: flex;
  gap: 10px;
}
.btn {
  height: 34px;
  padding: 0 12px;
  border-radius: 10px;
  border: 1px solid var(--border-subtle);
  background: #fff;
  cursor: pointer;
}
.btn.primary {
  background: var(--primary);
  color: #fff;
  border-color: rgba(37, 99, 235, 0.4);
}

.wb-main {
  flex: 1;
  min-height: 0;
  display: grid;
  grid-template-columns: 1fr 360px;
}
.canvas {
  min-height: 0;
  background: linear-gradient(180deg, #fff, #f8fafc);
}
.flow {
  width: 100%;
  height: 100%;
}

.tool {
  border-left: 1px solid var(--border-subtle);
  background: #fff;
  min-height: 0;
  overflow: auto;
  padding: 14px;
}
.tool-section + .tool-section {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px dashed var(--border-subtle);
}
.tool-title {
  font-weight: 900;
  font-size: 14px;
}
.tool-desc {
  margin-top: 6px;
  font-size: 12px;
  color: var(--text-secondary);
  line-height: 1.5;
}
.tool-subtitle {
  font-weight: 800;
  font-size: 13px;
  margin-bottom: 10px;
}
.tool-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}
.card {
  border: 1px solid var(--border-subtle);
  border-radius: 12px;
  padding: 10px;
  background: #fff;
  cursor: pointer;
  text-align: left;
}
.card-title {
  font-weight: 900;
  font-size: 13px;
}
.card-desc {
  margin-top: 4px;
  font-size: 12px;
  color: var(--text-secondary);
}

.palette {
  display: grid;
  gap: 10px;
}
.palette-item {
  border-radius: 12px;
  border: 1px solid var(--border-subtle);
  padding: 10px;
  background: #fff;
  cursor: grab;
  user-select: none;
}
.palette-item:active {
  cursor: grabbing;
}
.palette-item.start {
  border-left: 4px solid #10b981;
}
.palette-item.agent {
  border-left: 4px solid #2563eb;
}
.pi-title {
  font-weight: 900;
  font-size: 13px;
}
.pi-desc {
  margin-top: 4px;
  font-size: 12px;
  color: var(--text-secondary);
}
.tool-hint {
  margin-top: 10px;
  font-size: 12px;
  color: var(--text-secondary);
  line-height: 1.5;
}

.json {
  width: 100%;
  min-height: 220px;
  resize: vertical;
  border: 1px solid var(--border-subtle);
  border-radius: 12px;
  padding: 10px;
  font-family: var(--font-mono);
  font-size: 12px;
  line-height: 1.4;
  background: #f8fafc;
}
</style>


