package com.eduai.Service;

/**
 * Sim 执行监控与 Webhook 处理服务
 */
public interface SimMonitoringService {

    /**
     * 处理来自 Sim 的执行完成 Webhook
     *
     * @param rawBody   Webhook 原始请求体
     * @param signature HMAC 签名（用于日志记录）
     */
    void handleExecutionWebhook(String rawBody, String signature);
}


