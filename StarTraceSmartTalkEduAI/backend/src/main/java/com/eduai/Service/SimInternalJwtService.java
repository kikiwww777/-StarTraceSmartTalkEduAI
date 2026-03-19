package com.eduai.Service;

/**
 * 生成 Sim Internal JWT（与 Sim 源码 apps/sim/lib/auth/internal.ts 的逻辑保持一致）
 */
public interface SimInternalJwtService {

    /**
     * 生成 Internal JWT（HS256），默认 5 分钟过期
     *
     * @param simUserId Sim user.id（UUID 字符串）
     */
    String generate(String simUserId);
}


