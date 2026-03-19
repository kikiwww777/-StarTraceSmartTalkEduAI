<template>
  <div class="sim-workbench">
    <!-- Top toolbar -->
    <header class="toolbar">
      <div class="toolbar-left">
        <div class="brand">
          <span class="brand-title">SIM 工作台</span>
          <span v-if="workflowId" class="brand-sub">Workflow: {{ workflowId }}</span>
        </div>
      </div>

      <div class="toolbar-center">
        <button class="tb-btn" @click="reloadIframe" :disabled="!iframeSrc">刷新</button>
        <button class="tb-btn primary" @click="runWorkflow" :disabled="!workflowId || running">
          {{ running ? "运行中..." : "运行" }}
        </button>
        <button class="tb-btn" @click="toggleResultPanel">
          {{ showResultPanel ? "隐藏结果" : "查看结果(JSON)" }}
        </button>
      </div>

      <div class="toolbar-right">
        <button class="tb-btn" @click="copyLink" :disabled="!iframeSrc">复制链接</button>
        <button class="tb-btn" @click="openInNewTab" :disabled="!iframeSrc">新窗口打开</button>
        <button class="tb-btn" @click="toggleFullscreen" :disabled="!iframeSrc">
          {{ isFullscreen ? "退出全屏" : "全屏" }}
        </button>
      </div>
    </header>

    <!-- Main content -->
    <main class="content">
      <section class="stage" :class="{ 'with-panel': showResultPanel }">
        <div v-if="!iframeSrc" class="empty">
          <div class="empty-title">未提供 Sim 工作台链接</div>
          <div class="empty-desc">
            请从“立即实验”进入，或检查后端 `/api/sim/prepare-lab` 是否返回 `simUrl`。
          </div>
        </div>

        <iframe
          v-else
          ref="iframeRef"
          :key="iframeKey"
          :src="iframeSrc"
          class="sim-iframe"
          frameborder="0"
          allow="clipboard-read; clipboard-write"
          title="SIM Studio Workbench"
        ></iframe>
      </section>

      <aside v-if="showResultPanel" class="panel">
        <div class="panel-header">
          <div class="panel-title">运行结果(JSON)</div>
          <div class="panel-actions">
            <button class="tb-btn small" @click="clearResult" :disabled="!lastResultRaw">清空</button>
            <button class="tb-btn small" @click="copyResult" :disabled="!lastResultRaw">复制</button>
          </div>
        </div>

        <div class="panel-body">
          <div v-if="lastError" class="panel-error">{{ lastError }}</div>

          <div v-if="!lastResultRaw && !lastError" class="panel-hint">
            点击上方“运行”后，这里会展示后端返回的 `data.simResult`（结构化 JSON）。
          </div>

          <pre v-else class="json-view">{{ prettyResult }}</pre>
        </div>
      </aside>
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onBeforeUnmount, ref } from "vue";
import { useRoute } from "vue-router";

const route = useRoute();

const workflowId = computed(() => String(route.query.workflowId ?? "").trim());
const iframeSrc = computed(() => String(route.query.simUrl ?? "").trim());

const iframeRef = ref<HTMLIFrameElement | null>(null);
const iframeKey = ref(0);

const showResultPanel = ref(true);
const running = ref(false);
const lastResultRaw = ref<any>(null);
const lastError = ref<string>("");

const isFullscreen = ref(false);

const prettyResult = computed(() => {
  if (!lastResultRaw.value) return "";
  try {
    if (typeof lastResultRaw.value === "string") return lastResultRaw.value;
    return JSON.stringify(lastResultRaw.value, null, 2);
  } catch {
    return String(lastResultRaw.value);
  }
});

const reloadIframe = () => {
  iframeKey.value += 1;
};

const toggleResultPanel = () => {
  showResultPanel.value = !showResultPanel.value;
};

const clearResult = () => {
  lastError.value = "";
  lastResultRaw.value = null;
};

const copyResult = async () => {
  try {
    await navigator.clipboard.writeText(prettyResult.value || "");
  } catch (e) {
    console.error("copyResult failed:", e);
  }
};

const copyLink = async () => {
  try {
    await navigator.clipboard.writeText(iframeSrc.value || "");
  } catch (e) {
    console.error("copyLink failed:", e);
  }
};

const openInNewTab = () => {
  const url = iframeSrc.value;
  if (!url) return;
  window.open(url, "_blank", "noopener,noreferrer");
};

const toggleFullscreen = async () => {
  const el = document.documentElement;
  try {
    if (!document.fullscreenElement) {
      await el.requestFullscreen();
      isFullscreen.value = true;
    } else {
      await document.exitFullscreen();
      isFullscreen.value = false;
    }
  } catch (e) {
    console.error("toggleFullscreen failed:", e);
  }
};

const onFullscreenChange = () => {
  isFullscreen.value = !!document.fullscreenElement;
};

/**
 * 运行工作流（通过后端代理），并期望拿到结构化 JSON：data.simResult
 */
const runWorkflow = async () => {
  if (!workflowId.value) {
    lastError.value = "缺少 workflowId";
    return;
  }
  running.value = true;
  lastError.value = "";
  try {
    const resp = await fetch(`/api/sim/workflows/${encodeURIComponent(workflowId.value)}/execute`, {
      method: "POST",
      credentials: "include",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({}),
    });

    const wrapped = await resp.json().catch(() => null);
    if (!wrapped) {
      throw new Error("后端未返回 JSON");
    }
    if (wrapped.code !== 200) {
      // 后端已保证 HTTP 200，但业务 code 可能非 200
      const msg = wrapped.message || "执行失败";
      lastError.value = msg;
    }

    // 优先 simResult（结构化 JSON），其次 simResponse（字符串）
    lastResultRaw.value = wrapped?.data?.simResult ?? wrapped?.data?.simResponse ?? wrapped?.data ?? wrapped;
    // 自动展开结果面板
    showResultPanel.value = true;
  } catch (e: any) {
    console.error("runWorkflow failed:", e);
    lastError.value = e?.message ?? "执行失败";
  } finally {
    running.value = false;
  }
};

onMounted(() => {
  document.addEventListener("fullscreenchange", onFullscreenChange);
});

onBeforeUnmount(() => {
  document.removeEventListener("fullscreenchange", onFullscreenChange);
});
</script>

<style scoped>
.sim-workbench {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #0b1220;
  color: #e5e7eb;
}

.toolbar {
  height: 56px;
  padding: 0 14px;
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  align-items: center;
  gap: 12px;
  background: rgba(15, 23, 42, 0.9);
  border-bottom: 1px solid rgba(148, 163, 184, 0.16);
  backdrop-filter: blur(10px);
}

.brand {
  display: flex;
  flex-direction: column;
  line-height: 1.1;
}
.brand-title {
  font-weight: 700;
  font-size: 14px;
  letter-spacing: 0.2px;
}
.brand-sub {
  font-size: 12px;
  color: rgba(226, 232, 240, 0.7);
  margin-top: 2px;
}

.toolbar-center,
.toolbar-right {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}
.toolbar-right {
  justify-content: flex-end;
}

.tb-btn {
  height: 34px;
  padding: 0 12px;
  border-radius: 10px;
  border: 1px solid rgba(148, 163, 184, 0.18);
  background: rgba(2, 6, 23, 0.3);
  color: rgba(226, 232, 240, 0.92);
  cursor: pointer;
  transition: transform 0.06s ease, background 0.15s ease, border-color 0.15s ease;
  font-size: 13px;
}
.tb-btn:hover:not(:disabled) {
  background: rgba(30, 41, 59, 0.35);
  border-color: rgba(148, 163, 184, 0.3);
}
.tb-btn:active:not(:disabled) {
  transform: translateY(1px);
}
.tb-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
.tb-btn.primary {
  background: rgba(37, 99, 235, 0.22);
  border-color: rgba(37, 99, 235, 0.35);
}
.tb-btn.primary:hover:not(:disabled) {
  background: rgba(37, 99, 235, 0.3);
}
.tb-btn.small {
  height: 30px;
  padding: 0 10px;
  border-radius: 9px;
  font-size: 12px;
}

.content {
  flex: 1;
  display: grid;
  grid-template-columns: 1fr auto;
  min-height: 0;
}

.stage {
  position: relative;
  min-height: 0;
}

.sim-iframe {
  width: 100%;
  height: 100%;
  border: 0;
  background: #0b1220;
}

.empty {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-direction: column;
  padding: 32px;
  text-align: center;
  color: rgba(226, 232, 240, 0.8);
}
.empty-title {
  font-size: 16px;
  font-weight: 700;
  margin-bottom: 8px;
}
.empty-desc {
  font-size: 13px;
  max-width: 560px;
  line-height: 1.5;
  color: rgba(226, 232, 240, 0.65);
}

.panel {
  width: 420px;
  border-left: 1px solid rgba(148, 163, 184, 0.16);
  background: rgba(15, 23, 42, 0.6);
  display: flex;
  flex-direction: column;
  min-height: 0;
}
.panel-header {
  height: 48px;
  padding: 0 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid rgba(148, 163, 184, 0.14);
}
.panel-title {
  font-weight: 700;
  font-size: 13px;
}
.panel-actions {
  display: flex;
  gap: 8px;
}
.panel-body {
  padding: 12px;
  overflow: auto;
  min-height: 0;
}
.panel-error {
  padding: 10px 12px;
  border: 1px solid rgba(239, 68, 68, 0.3);
  background: rgba(239, 68, 68, 0.08);
  color: rgba(254, 226, 226, 0.92);
  border-radius: 10px;
  margin-bottom: 10px;
  font-size: 12px;
  line-height: 1.4;
}
.panel-hint {
  font-size: 12px;
  color: rgba(226, 232, 240, 0.65);
  line-height: 1.5;
}
.json-view {
  white-space: pre-wrap;
  word-break: break-word;
  margin: 0;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace;
  font-size: 12px;
  line-height: 1.5;
  color: rgba(226, 232, 240, 0.92);
}
</style>


