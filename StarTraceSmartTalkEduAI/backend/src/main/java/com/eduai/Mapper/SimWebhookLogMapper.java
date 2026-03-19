package com.eduai.Mapper;

import com.eduai.pojo.entity.SimWebhookLog;

public interface SimWebhookLogMapper {

    int insert(SimWebhookLog log);

    int updateProcessed(SimWebhookLog log);
}


