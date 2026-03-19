package com.eduai.Controller;

import com.eduai.Service.SimMonitoringService;
import com.eduai.pojo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * Sim Webhook 接收控制器
 * 用于接收 Sim 执行完成通知，并将数据写入 sim_execution_logs / sim_webhook_logs / student_token_usage
 */
@RestController
@RequestMapping("/api/edu/webhook")
public class SimWebhookController {

    @Value("${sim.webhook.secret:}")
    private String webhookSecret;

    @Autowired
    private SimMonitoringService simMonitoringService;

    /**
     * 接收 Sim 执行完成 Webhook
     * <p>
     * Sim 侧（本仓库的 Sim）发送的头为：
     * - sim-signature: t={timestamp},v1={hex(hmac_sha256(secret, "{timestamp}.{body}"))}
     *
     * @param signatureHeader HMAC 签名头
     * @param rawBody         原始请求体
     * @return 统一 Result
     */
    @PostMapping("/sim-execution")
    public ResponseEntity<Result<Void>> handleExecutionWebhook(
            @RequestHeader(name = "sim-signature", required = false) String signatureHeader,
            @RequestHeader(name = "X-Sim-Signature", required = false) String legacySignatureHeader,
            @RequestBody String rawBody) {

        if (webhookSecret == null || webhookSecret.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Result.error("sim.webhook.secret 未配置"));
        }

        // 优先使用 Sim 默认头 sim-signature；兼容 legacy 的 X-Sim-Signature（纯 hex）
        String effectiveSignature = (signatureHeader != null && !signatureHeader.isEmpty())
                ? signatureHeader
                : legacySignatureHeader;

        if (effectiveSignature == null || effectiveSignature.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Result.error("缺少签名请求头（sim-signature / X-Sim-Signature）"));
        }

        if (!verifySignature(rawBody, effectiveSignature, webhookSecret)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Result.error("Webhook 签名校验失败"));
        }

        simMonitoringService.handleExecutionWebhook(rawBody, effectiveSignature);
        return ResponseEntity.ok(Result.success("ok"));
    }

    /**
     * 使用 HMAC-SHA256 校验签名。
     *
     * 支持两种格式：
     * 1) sim-signature（推荐，Sim 默认格式）：
     *    sim-signature: t={timestamp},v1={hex(hmac_sha256(secret, "{timestamp}.{body}"))}
     * 2) legacy 纯 hex：
     *    signature = hex(HMAC_SHA256(secret, rawBody))
     */
    private boolean verifySignature(String rawBody, String signatureHeader, String secret) {
        try {
            String header = signatureHeader.trim();

            // 处理 sim-signature 格式：t=...,v1=...
            if (header.contains("t=") && header.contains("v1=")) {
                String ts = null;
                String v1 = null;
                String[] parts = header.split(",");
                for (String p : parts) {
                    String part = p.trim();
                    if (part.startsWith("t=")) {
                        ts = part.substring(2).trim();
                    } else if (part.startsWith("v1=")) {
                        v1 = part.substring(3).trim();
                    }
                }
                if (ts == null || ts.isEmpty() || v1 == null || v1.isEmpty()) {
                    return false;
                }

                String base = ts + "." + rawBody;
                Mac mac = Mac.getInstance("HmacSHA256");
                mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
                byte[] digest = mac.doFinal(base.getBytes(StandardCharsets.UTF_8));
                String expected = bytesToHex(digest);
                return expected.equalsIgnoreCase(v1);
            }

            // legacy：纯 hex(HMAC_SHA256(secret, body))
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = mac.doFinal(rawBody.getBytes(StandardCharsets.UTF_8));
            String expected = bytesToHex(digest);
            return expected.equalsIgnoreCase(header);
        } catch (Exception e) {
            return false;
        }
    }

    private static String bytesToHex(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        char[] hex = "0123456789abcdef".toCharArray();
        char[] out = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            out[i * 2] = hex[v >>> 4];
            out[i * 2 + 1] = hex[v & 0x0F];
        }
        return new String(out);
    }
}


