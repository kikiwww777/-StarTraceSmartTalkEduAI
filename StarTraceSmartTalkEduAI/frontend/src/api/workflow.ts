import { http } from './http';
import type { WorkflowDSL } from '@/types/workflow';

export interface Result<T> {
  code: number;
  message: string;
  data: T;
}

export const workflowApi = {
  create: async (dsl: WorkflowDSL): Promise<Result<{ eduWorkflowId: string }>> => {
    const res = await http.post<Result<{ eduWorkflowId: string }>>('/workflow/create', dsl);
    return res.data;
  },

  update: async (eduWorkflowId: string, dsl: WorkflowDSL): Promise<Result<void>> => {
    const res = await http.post<Result<void>>('/workflow/update', {
      eduWorkflowId,
      workflow: dsl,
    });
    return res.data;
  },

  run: async (
    eduWorkflowId: string,
    input: Record<string, any> = {},
  ): Promise<Result<{ executionId: string }>> => {
    const res = await http.post<Result<{ executionId: string }>>('/workflow/run', {
      eduWorkflowId,
      input,
    });
    return res.data;
  },

  logs: async (
    executionId: string,
  ): Promise<
    Result<{
      status: 'PENDING' | 'RUNNING' | 'SUCCESS' | 'FAILED';
      logs: Array<{ nodeId: string; message: string; level: 'INFO' | 'WARN' | 'ERROR'; ts: string }>;
      output?: any;
    }>
  > => {
    const res = await http.get<
      Result<{
        status: 'PENDING' | 'RUNNING' | 'SUCCESS' | 'FAILED';
        logs: Array<{ nodeId: string; message: string; level: 'INFO' | 'WARN' | 'ERROR'; ts: string }>;
        output?: any;
      }>
    >('/workflow/logs', {
      params: { executionId },
    });
    return res.data;
  },
};


