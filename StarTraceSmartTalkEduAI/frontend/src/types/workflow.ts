export type WorkflowNodeType = 'start' | 'llm' | 'api' | 'condition' | 'variable';

export interface WorkflowPortSchema {
  type: string;
  description?: string;
}

export interface WorkflowNodeIO {
  inputSchema?: Record<string, WorkflowPortSchema>;
  outputSchema?: Record<string, WorkflowPortSchema>;
}

export interface WorkflowNodeConfig {
  inputFormat?: any;
  model?: string;
  systemPrompt?: string;
  temperature?: number;
  maxTokens?: number;
  tools?: any[];
  responseFormat?: any;
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE';
  url?: string;
  headers?: Record<string, string>;
  body?: string;
  expression?: string;
  assignments?: { name: string; expr: string }[];
  [key: string]: any;
}

export interface WorkflowNode extends WorkflowNodeIO {
  id: string;
  type: WorkflowNodeType;
  label: string;
  position: { x: number; y: number };
  config: WorkflowNodeConfig;
}

export interface WorkflowEdge {
  id: string;
  source: string;
  target: string;
  sourceHandle?: string;
  targetHandle?: string;
  conditionLabel?: string;
}

export interface WorkflowVariables {
  [name: string]: any;
}

export interface WorkflowDSL {
  id?: string;
  name: string;
  description?: string;
  nodes: WorkflowNode[];
  edges: WorkflowEdge[];
  variables: WorkflowVariables;
  version: number;
  updatedAt?: string;
}


