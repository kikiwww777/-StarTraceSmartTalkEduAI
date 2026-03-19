<template>
  <div class="template-workspace-page fade-in">
    <!-- Header -->
    <div class="page-header">
      <div class="header-left">
        <h1 class="page-title">
          {{ template?.title || '教学工作台' }}
        </h1>
        <p class="page-subtitle">
          基于所选教学模板的一站式 AI 互动工作台。
        </p>
      </div>
      <div class="header-right" v-if="template">
        <div class="tpl-meta-chip">
          <span class="chip-label">{{ categoryLabel }}</span>
          <span class="chip-divider">·</span>
          <span class="chip-stat">{{ template.usageCount || 0 }} 次实验</span>
          <span class="chip-divider">·</span>
          <span class="chip-stat">评分 {{ template.rating?.toFixed(1) || '—' }}</span>
        </div>
      </div>
    </div>

    <!-- Loading / Error -->
    <div v-if="loading" class="center-box">
      <div class="loading-spinner"></div>
      <p>正在加载模板信息...</p>
    </div>
    <div v-else-if="error" class="center-box error">
      <p class="error-message">{{ error }}</p>
    </div>

    <!-- Content -->
    <div v-else-if="template" class="workspace-layout">
      <!-- 左侧：模板基本信息 -->
      <section class="info-panel">
        <div class="info-card">
          <div class="info-header">
            <div
              class="tpl-icon-box"
              :style="{ backgroundColor: (template.color || '#2563eb') + '10' }"
            >
              <div
                v-html="template.icon || ''"
                class="svg-wrapper"
                :style="{ color: template.color || '#2563eb' }"
              ></div>
            </div>
            <div class="info-title-block">
              <h2>{{ template.title }}</h2>
              <p class="info-desc">
                {{ template.description || '该模板暂无详细描述，可直接在右侧与 AI 进行互动，探索适用场景。' }}
              </p>
            </div>
          </div>
          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">教学场景</span>
              <span class="info-value">{{ categoryLabel }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">累计使用</span>
              <span class="info-value">{{ template.usageCount || 0 }} 次</span>
            </div>
            <div class="info-item">
              <span class="info-label">综合评分</span>
              <span class="info-value">{{ template.rating?.toFixed(1) || '—' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">最近更新</span>
              <span class="info-value">
                {{ template.updatedAt ? formatDate(template.updatedAt) : '—' }}
              </span>
            </div>
          </div>
        </div>
      </section>

      <!-- 右侧：AI 问答工作台 -->
      <section class="chat-panel">
        <div class="chat-card">
          <div class="chat-header">
            <div class="chat-title">
              <h2>AI 教学助手</h2>
              <p>围绕当前模板，提问教学设计、课堂活动或实验步骤相关问题。</p>
            </div>
          </div>

          <div class="chat-body" ref="chatBodyRef">
            <div class="chat-messages">
              <div
                v-for="(msg, index) in renderedMessages"
                :key="index"
                class="chat-message-row"
                :class="msg.role"
              >
                <div class="avatar" v-if="msg.role === 'assistant'">AI</div>
                <div class="avatar user" v-else>我</div>
                <div class="bubble">
                  <template v-if="msg.role === 'assistant' && msg.parsed">
                    <div class="json-block">
                      <template v-if="msg.parsed.kind === 'object'">
                        <div
                          v-for="item in msg.parsed.entries"
                          :key="item.key"
                          class="json-item"
                        >
                          <div class="json-key">{{ item.key }}</div>
                          <template v-if="formatJsonValue(item.value).type === 'list'">
                            <ul class="json-list">
                              <li v-for="(li, i) in formatJsonValue(item.value).items" :key="i">
                                {{ li }}
                              </li>
                            </ul>
                          </template>
                          <template v-else-if="formatJsonValue(item.value).type === 'object'">
                            <div class="json-nested">
                              <div
                                v-for="child in formatJsonValue(item.value).entries"
                                :key="child.key"
                                class="json-item nested"
                              >
                                <div class="json-key">{{ child.key }}</div>
                                <template v-if="formatJsonValue(child.value).type === 'list'">
                                  <ul class="json-list">
                                    <li v-for="(li, i) in formatJsonValue(child.value).items" :key="i">
                                      {{ li }}
                                    </li>
                                  </ul>
                                </template>
                                <template v-else>
                                  <p class="json-value">{{ formatJsonValue(child.value).text }}</p>
                                </template>
                              </div>
                            </div>
                          </template>
                          <template v-else>
                            <p class="json-value">{{ formatJsonValue(item.value).text }}</p>
                          </template>
                        </div>
                      </template>

                      <template v-else>
                        <ul class="json-list">
                          <li v-for="(li, i) in msg.parsed.items" :key="i">
                            {{ typeof li === 'string' ? li : JSON.stringify(li, null, 2) }}
                          </li>
                        </ul>
                      </template>
                    </div>
                  </template>
                  <p v-else>{{ msg.content }}</p>
                </div>
              </div>
              <div v-if="isThinking" class="chat-message-row assistant thinking">
                <div class="avatar">AI</div>
                <div class="bubble">
                  <span class="dot"></span>
                  <span class="dot"></span>
                  <span class="dot"></span>
                </div>
              </div>
            </div>
          </div>

          <div class="chat-input-area">
            <textarea
              v-model="input"
              class="chat-input"
              rows="3"
              :placeholder="promptPlaceholder"
              @keydown.enter.exact.prevent="handleSend"
            ></textarea>
            <div class="chat-actions">
              <span class="hint">回车发送，Shift + 回车换行</span>
              <button
                class="btn-send"
                :disabled="!input.trim() || isThinking"
                @click="handleSend"
              >
                发送
              </button>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, computed, nextTick } from 'vue';
import { useRoute } from 'vue-router';
import { templateApi, type Template } from '@/api/template';

const route = useRoute();
const template = ref<Template | null>(null);
const loading = ref(false);
const error = ref<string | null>(null);

interface ChatMessage {
  role: 'user' | 'assistant';
  content: string;
}

const messages = ref<ChatMessage[]>([]);
const input = ref('');
const isThinking = ref(false);
const chatBodyRef = ref<HTMLElement | null>(null);

// 说明：执行工作流已改为走后端代理接口，由后端统一携带 internal-jwt / X-API-Key

const promptPlaceholder = computed(() => {
  if (template.value?.prompt) {
    return template.value.prompt;
  }
  return '请输入你的教学问题，例如：请帮我根据本模板设计一个实验步骤...';
});

const categoryLabel = computed(() => {
  if (!template.value) return '教学模板';
  if (template.value.categoryInfo?.label) return template.value.categoryInfo.label;
  return template.value.category || '教学模板';
});

const scrollToBottom = async () => {
  await nextTick();
  if (chatBodyRef.value) {
    chatBodyRef.value.scrollTop = chatBodyRef.value.scrollHeight;
  }
};

const callSimWorkflow = async (question: string): Promise<string> => {
  const workflowId = template.value?.workflowId;
  if (!workflowId) {
    throw new Error('当前模板未配置工作流 ID');
  }

  const payload = {
    conversationId: `template-${template.value?.id ?? 'default'}`,
    input: template.value?.prompt
      ? `${template.value.prompt}\n\n用户问题：${question}`
      : question,
  };

  // 通过后端代理执行（后端会固定返回 workflowId，并负责携带 internal-jwt / X-API-Key）
  const response = await fetch(`/api/sim/workflows/${workflowId}/execute`, {
    method: 'POST',
    credentials: 'include',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
  });

  const wrapped = await response.json().catch(() => null);
  if (!response.ok || !wrapped) {
    const msg = wrapped?.message || wrapped?.error || `请求失败 (${response.status})`;
    throw new Error(msg);
  }

  // 后端返回：Result.success({ workflowId, simResponse })
  let data: any = wrapped?.data?.simResponse ?? wrapped?.simResponse ?? '';
  if (typeof data === 'string') {
    // simResponse 可能是 JSON 字符串
    try {
      data = JSON.parse(data);
    } catch {
      // 非 JSON 就当纯文本
      return data;
    }
  }

  // 根据 Sim 返回结构提取文本
  const content = data?.output?.content ?? data?.output ?? data?.result ?? '';

  // 如果 content 是对象，直接 String 会变成 "[object Object]"，这里优先 JSON 序列化
  if (content && typeof content === 'object') {
    try {
      return JSON.stringify(content);
    } catch {
      return String(content);
    }
  }

  return String(content ?? '');
};

type JsonRenderable =
  | { kind: 'object'; entries: Array<{ key: string; value: unknown }> }
  | { kind: 'array'; items: unknown[] };

const tryParseJsonContent = (raw: string): JsonRenderable | null => {
  if (!raw) return null;
  const trimmed = raw.trim();
  // 仅在看起来像 JSON 时才尝试解析，避免误判普通文本
  if (!(trimmed.startsWith('{') || trimmed.startsWith('['))) return null;

  try {
    const parsed = JSON.parse(trimmed) as unknown;
    if (Array.isArray(parsed)) {
      return { kind: 'array', items: parsed };
    }
    if (parsed && typeof parsed === 'object') {
      return {
        kind: 'object',
        entries: Object.entries(parsed as Record<string, unknown>).map(([key, value]) => ({ key, value })),
      };
    }
    return null;
  } catch {
    return null;
  }
};

// 在模板里会多次调用 formatJsonValue(...)，TS 很难在不同调用间做窄化。
// 这里用可选字段的结构化类型，避免模板侧访问 items/entries/text 时的类型报错。
type FormattedJsonValue = {
  type: 'text' | 'list' | 'object';
  text?: string;
  items?: string[];
  entries?: Array<{ key: string; value: unknown }>;
};

const formatJsonValue = (value: unknown): FormattedJsonValue => {
  if (Array.isArray(value)) {
    return { type: 'list', items: value.map((v) => (typeof v === 'string' ? v : JSON.stringify(v, null, 2))) };
  }
  if (value && typeof value === 'object') {
    return { type: 'object', entries: Object.entries(value as Record<string, unknown>).map(([key, v]) => ({ key, value: v })) };
  }
  return { type: 'text', text: String(value ?? '') };
};

type RenderedChatMessage = ChatMessage & { parsed: JsonRenderable | null };

const normalizeParsed = (parsed: JsonRenderable): JsonRenderable => {
  // 常见结构：{ "response": { ...真正内容... } } —— 自动展开一层
  if (parsed.kind === 'object') {
    const entries = parsed.entries;
    if (entries.length === 1 && entries[0]?.key === 'response') {
      const v = entries[0].value;
      if (v && typeof v === 'object' && !Array.isArray(v)) {
        return {
          kind: 'object',
          entries: Object.entries(v as Record<string, unknown>).map(([key, value]) => ({ key, value })),
        };
      }
    }
  }
  return parsed;
};

const renderedMessages = computed<RenderedChatMessage[]>(() =>
  messages.value.map((m) => ({
    ...m,
    parsed:
      m.role === 'assistant'
        ? (() => {
            const p = tryParseJsonContent(m.content);
            return p ? normalizeParsed(p) : null;
          })()
        : null,
  })),
);

const handleSend = async () => {
  const text = input.value.trim();
  if (!text || isThinking.value) return;

  messages.value.push({ role: 'user', content: text });
  input.value = '';
  await scrollToBottom();

  isThinking.value = true;

  try {
    const reply = await callSimWorkflow(text);
    messages.value.push({ role: 'assistant', content: reply });

    // 本次成功调用 SIM 视为一次使用，增加模板使用次数
    if (template.value?.id) {
      try {
        await templateApi.incrementUsageCount(template.value.id);
        // 前端本地也同步加一，避免重新拉取
        if (typeof template.value.usageCount === 'number') {
          template.value.usageCount += 1;
        }
      } catch (err) {
        console.error('更新模板使用次数失败:', err);
      }
    }
  } catch (e: any) {
    console.error('调用 SIM 工作流失败:', e);
    messages.value.push({
      role: 'assistant',
      content: '调用智能体服务失败，请稍后重试。',
    });
  } finally {
    isThinking.value = false;
    await scrollToBottom();
  }
};

const formatDate = (value: unknown) => {
  let d: Date | null = null;

  if (Array.isArray(value) && value.length >= 3) {
    const [year, month, day, hour = 0, minute = 0, second = 0] = value as number[];
    d = new Date(year, month - 1, day, hour, minute, second);
  } else if (typeof value === 'string') {
    d = new Date(value);
  }

  if (!d || Number.isNaN(d.getTime())) {
    return String(value);
  }

  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  const h = String(d.getHours()).padStart(2, '0');
  const min = String(d.getMinutes()).padStart(2, '0');
  const s = String(d.getSeconds()).padStart(2, '0');

  return `${y}-${m}-${day} ${h}:${min}:${s}`;
};

const loadTemplate = async () => {
  const id = route.params.id as string | undefined;
  if (!id) {
    error.value = '缺少模板 ID，无法加载工作台。';
    return;
  }
  loading.value = true;
  error.value = null;
  try {
    const result = await templateApi.getTemplateById(id);
    if (result.code === 200 && result.data) {
      template.value = result.data;
    } else {
      error.value = result.message || '加载模板信息失败';
    }
  } catch (err: any) {
    error.value = err?.message || '网络错误，无法加载模板信息。';
    console.error('加载模板详情出错:', err);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadTemplate();
});
</script>

<style scoped>
.template-workspace-page {
  padding-bottom: 40px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 24px;
}

.page-title {
  font-size: 1.8rem;
  font-weight: 800;
  color: #1e293b;
  margin-bottom: 6px;
}

.page-subtitle {
  color: #64748b;
  font-size: 0.95rem;
}

.tpl-meta-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 8px 14px;
  border-radius: 999px;
  background: #eff6ff;
  color: #1d4ed8;
  font-size: 0.8rem;
  font-weight: 600;
}

.chip-label {
  padding-right: 4px;
}

.chip-divider {
  color: #93c5fd;
}

.chip-stat {
  color: #1d4ed8;
}

.center-box {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 260px;
  color: #64748b;
  gap: 12px;
}

.center-box.error .error-message {
  color: #dc2626;
}

.loading-spinner {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: 4px solid #e5edff;
  border-top-color: #2563eb;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.workspace-layout {
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) minmax(0, 1.2fr);
  gap: 24px;
  align-items: flex-start;
}

@media (max-width: 960px) {
  .workspace-layout {
    grid-template-columns: minmax(0, 1fr);
  }
}

/* Info panel */
.info-panel {
  width: 100%;
}

.info-card {
  background: white;
  border-radius: 20px;
  border: 1px solid #e2e8f0;
  padding: 24px;
}

.info-header {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.tpl-icon-box {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.svg-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
}

.info-title-block h2 {
  font-size: 1.2rem;
  font-weight: 800;
  color: #111827;
  margin-bottom: 6px;
}

.info-desc {
  font-size: 0.9rem;
  color: #6b7280;
  line-height: 1.7;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
  margin-top: 8px;
}

@media (max-width: 640px) {
  .info-grid {
    grid-template-columns: minmax(0, 1fr);
  }
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-label {
  font-size: 0.75rem;
  color: #9ca3af;
  text-transform: uppercase;
  letter-spacing: 0.06em;
}

.info-value {
  font-size: 0.95rem;
  color: #111827;
  font-weight: 600;
}

/* Chat panel */
.chat-panel {
  width: 100%;
}

.chat-card {
  background: white;
  border-radius: 20px;
  border: 1px solid #e2e8f0;
  padding: 20px 20px 16px;
  display: flex;
  flex-direction: column;
  height: 520px;
}

.chat-header {
  padding-bottom: 12px;
  border-bottom: 1px solid #f1f5f9;
  margin-bottom: 12px;
}

.chat-title h2 {
  font-size: 1.1rem;
  font-weight: 800;
  color: #111827;
  margin-bottom: 4px;
}

.chat-title p {
  font-size: 0.85rem;
  color: #6b7280;
}

.chat-body {
  flex: 1;
  overflow-y: auto;
  padding: 4px 2px;
}

.chat-empty {
  font-size: 0.85rem;
  color: #6b7280;
}

.chat-empty ul {
  margin-top: 8px;
  padding-left: 18px;
}

.chat-empty li {
  margin-bottom: 4px;
}

.chat-messages {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.chat-message-row {
  display: flex;
  gap: 10px;
  align-items: flex-start;
}

.chat-message-row.assistant {
  flex-direction: row;
}

.chat-message-row.user {
  flex-direction: row-reverse;
}

.avatar {
  width: 28px;
  height: 28px;
  border-radius: 999px;
  background: #eff6ff;
  color: #1d4ed8;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
  font-weight: 700;
  flex-shrink: 0;
}

.avatar.user {
  background: #e5e7eb;
  color: #111827;
}

.bubble {
  max-width: 80%;
  padding: 10px 12px;
  border-radius: 14px;
  font-size: 0.9rem;
  line-height: 1.6;
}

.bubble p {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-word;
}

.json-block {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.json-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.json-nested {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding-left: 12px;
  border-left: 2px solid rgba(148, 163, 184, 0.35);
}

.json-item.nested .json-key {
  font-weight: 700;
  font-size: 0.82rem;
}

.json-key {
  font-weight: 800;
  font-size: 0.85rem;
  opacity: 0.9;
}

.json-value {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-word;
}

.json-list {
  margin: 0;
  padding-left: 18px;
}

.json-list li {
  margin: 4px 0;
  white-space: pre-wrap;
  word-break: break-word;
}

.assistant .bubble {
  background: #f9fafb;
  color: #111827;
}

.user .bubble {
  background: #2563eb;
  color: white;
}

.chat-message-row.thinking .bubble {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.dot {
  width: 6px;
  height: 6px;
  border-radius: 999px;
  background: #9ca3af;
  animation: blink 1.2s infinite ease-in-out;
}

.dot:nth-child(2) {
  animation-delay: 0.15s;
}

.dot:nth-child(3) {
  animation-delay: 0.3s;
}

@keyframes blink {
  0%,
  80%,
  100% {
    opacity: 0.3;
  }
  40% {
    opacity: 1;
  }
}

.chat-input-area {
  border-top: 1px solid #f1f5f9;
  padding-top: 8px;
  margin-top: 4px;
}

.chat-input {
  width: 100%;
  resize: none;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  padding: 8px 10px;
  font-size: 0.9rem;
  outline: none;
  transition: border-color 0.15s, box-shadow 0.15s;
}

.chat-input:focus {
  border-color: #2563eb;
  box-shadow: 0 0 0 2px rgba(37, 99, 235, 0.12);
}

.chat-actions {
  margin-top: 6px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.hint {
  font-size: 0.75rem;
  color: #9ca3af;
}

.btn-send {
  padding: 6px 16px;
  border-radius: 999px;
  border: none;
  background: #2563eb;
  color: white;
  font-size: 0.85rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.15s, transform 0.1s;
}

.btn-send:disabled {
  background: #cbd5f5;
  cursor: not-allowed;
}

.btn-send:not(:disabled):hover {
  background: #1d4ed8;
}

.btn-send:not(:disabled):active {
  transform: translateY(1px);
}
</style>


