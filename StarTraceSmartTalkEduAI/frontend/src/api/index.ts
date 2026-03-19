import axios from 'axios'

const api = axios.create({
    baseURL: '/api',
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json'
    }
})

// 请求拦截器：添加用户ID
api.interceptors.request.use(config => {
    // 临时方案：模拟用户ID
    config.headers['X-User-Id'] = '11111111-1111-1111-1111-111111111111'
    return config
})

// 响应拦截器
api.interceptors.response.use(
    response => response.data,
    error => {
        console.error('API Error:', error)
        return Promise.reject(error)
    }
)

export default api

// 课程相关 API
export const courseApi = {
    // 获取课程列表
    getList: () => api.get('/course'),

    // 获取课程详情
    getDetail: (id: string) => api.get(`/course/${id}`),

    // 创建课程
    create: (data: { name: string; description?: string }) => api.post('/course', data),

    // 更新课程
    update: (id: string, data: { name?: string; description?: string }) => api.put(`/course/${id}`, data),

    // 删除课程
    delete: (id: string) => api.delete(`/course/${id}`)
}

// 成员相关 API
export const memberApi = {
    // 获取成员列表
    getList: (courseId: string) => api.get(`/course/${courseId}/members`),

    // 添加成员
    add: (courseId: string, data: { userId: string; role: string }) =>
        api.post(`/course/${courseId}/members`, data),

    // 更新角色
    updateRole: (courseId: string, userId: string, role: string) =>
        api.put(`/course/${courseId}/members/${userId}`, { role }),

    // 移除成员
    remove: (courseId: string, userId: string) =>
        api.delete(`/course/${courseId}/members/${userId}`)
}
