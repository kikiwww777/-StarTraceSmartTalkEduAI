import { http } from "./http";

// ===== 类型定义
export interface Result<T> {
  code: number;
  message: string;
  data: T;
}

export interface TemplateCategory {
  id: string;
  label: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface Template {
  id: string;
  title: string;
  category: string;
  description?: string;
  prompt?: string;
  workflowId?: string;
  usageCount: number;
  rating: number;
  color: string;
  icon?: string;
  createdAt?: string;
  updatedAt?: string;
  categoryInfo?: TemplateCategory;
}

// ===== API 调用函数

/**
 * 模板分类 API
 */
export const templateCategoryApi = {
  /**
   * 获取所有分类
   */
  getAllCategories: async (): Promise<Result<TemplateCategory[]>> => {
    const response = await http.get<Result<TemplateCategory[]>>(
      `/template-category/list`
    );
    return response.data;
  },

  /**
   * 根据ID获取分类
   */
  getCategoryById: async (id: string): Promise<Result<TemplateCategory>> => {
    const response = await http.get<Result<TemplateCategory>>(
      `/template-category/${id}`
    );
    return response.data;
  },

  /**
   * 创建分类
   */
  createCategory: async (category: TemplateCategory): Promise<Result<TemplateCategory>> => {
    const response = await http.post<Result<TemplateCategory>>(
      `/template-category/create`,
      category
    );
    return response.data;
  },

  /**
   * 更新分类
   */
  updateCategory: async (category: TemplateCategory): Promise<Result<TemplateCategory>> => {
    const response = await http.put<Result<TemplateCategory>>(
      `/template-category/update`,
      category
    );
    return response.data;
  },

  /**
   * 删除分类
   */
  deleteCategory: async (id: string): Promise<Result<void>> => {
    const response = await http.delete<Result<void>>(
      `/template-category/${id}`
    );
    return response.data;
  },
};

/**
 * 教学模板 API
 */
export const templateApi = {
  /**
   * 获取所有模板
   */
  getAllTemplates: async (): Promise<Result<Template[]>> => {
    const response = await http.get<Result<Template[]>>(
      `/templates/all`
    );
    return response.data;
  },

  /**
   * 根据分类和关键词查询模板
   */
  getTemplates: async (
    category?: string,
    keyword?: string
  ): Promise<Result<Template[]>> => {
    const params: Record<string, string> = {};
    if (category) params.category = category;
    if (keyword) params.keyword = keyword;

    const response = await http.get<Result<Template[]>>(
      `/templates/list`,
      { params }
    );
    return response.data;
  },

  /**
   * 根据ID获取模板详情
   */
  getTemplateById: async (id: string): Promise<Result<Template>> => {
    const response = await http.get<Result<Template>>(
      `/templates/${id}`
    );
    return response.data;
  },

  /**
   * 按评分降序获取模板
   */
  getTemplatesByRating: async (limit?: number): Promise<Result<Template[]>> => {
    const params = limit ? { limit: limit.toString() } : {};
    const response = await http.get<Result<Template[]>>(
      `/templates/top-rating`,
      { params }
    );
    return response.data;
  },

  /**
   * 按使用次数降序获取模板
   */
  getTemplatesByUsage: async (limit?: number): Promise<Result<Template[]>> => {
    const params = limit ? { limit: limit.toString() } : {};
    const response = await http.get<Result<Template[]>>(
      `/templates/top-usage`,
      { params }
    );
    return response.data;
  },

  /**
   * 创建模板
   */
  createTemplate: async (template: Template): Promise<Result<Template>> => {
    const response = await http.post<Result<Template>>(
      `/templates/create`,
      template
    );
    return response.data;
  },

  /**
   * 更新模板
   */
  updateTemplate: async (template: Template): Promise<Result<Template>> => {
    const response = await http.put<Result<Template>>(
      `/templates/update`,
      template
    );
    return response.data;
  },

  /**
   * 删除模板
   */
  deleteTemplate: async (id: string): Promise<Result<void>> => {
    const response = await http.delete<Result<void>>(
      `/templates/${id}`
    );
    return response.data;
  },

  /**
   * 增加使用次数
   */
  incrementUsageCount: async (id: string): Promise<Result<void>> => {
    const response = await http.post<Result<void>>(
      `/templates/${id}/use`
    );
    return response.data;
  },

  /**
   * 更新评分
   */
  updateRating: async (id: string, rating: number): Promise<Result<void>> => {
    const response = await http.put<Result<void>>(
      `/templates/${id}/rating`,
      null,
      { params: { rating: rating.toString() } }
    );
    return response.data;
  },
};




