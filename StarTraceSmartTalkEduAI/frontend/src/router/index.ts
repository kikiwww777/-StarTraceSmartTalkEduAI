import { createRouter, createWebHistory, type RouteRecordRaw } from "vue-router";

const routes: RouteRecordRaw[] = [
    {
        path: "/",
        name: "home",
        component: () => import("../views/HomeView.vue")
    },
    {
        path: "/courses",
        name: "courses",
        component: () => import("../views/CoursesView.vue")
    },
    {
        path: "/courses/:id",
        name: "courseDetail",
        component: () => import("../views/CourseDetailView.vue")
    },
    {
        path: "/class/:id",
        name: "classDetail",
        component: () => import("../views/ClassDetailView.vue")
    },
    {
        path: "/workspace",
        name: "workspace",
        component: () => import("../views/WorkspaceView.vue")
    },
    {
        path: "/workspace/:experimentId",
        name: "workflowWorkspace",
        component: () => import("../views/WorkflowWorkspaceView.vue")
    },
    {
        path: "/templates",
        name: "templates",
        component: () => import("../views/TemplatesView.vue")
    },
    {
        path: "/templates/:id/workspace",
        name: "templateWorkspace",
        component: () => import("../views/TemplateWorkspaceView.vue")
    },
    {
        path: "/sim/workbench",
        name: "simWorkbench",
        component: () => import("../views/SimWorkbenchView.vue")
    },
    {
        path: "/edu/workbench",
        name: "eduWorkbench",
        component: () => import("../views/EduWorkbenchView.vue")
    },
    {
        path: "/knowledge",
        name: "knowledge",
        component: () => import("../views/KnowledgeView.vue")
    },
    {
        path: "/reports",
        name: "reports",
        component: () => import("../views/ReportsView.vue")
    },
    {
        path: "/settings",
        name: "settings",
        component: () => import("../views/SettingsView.vue")
    },
    {
        path: "/login",
        name: "login",
        component: () => import("../views/LoginView.vue")
    },
    {
        path: "/register",
        name: "register",
        component: () => import("../views/RegisterView.vue")
    }
];

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes
});

export default router;
