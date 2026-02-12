import { createRouter, createWebHistory } from 'vue-router'

import Home from '../views/Home.vue'

const routes = [
    {
        path: '/',
        name: 'home',
        component: Home
    },
    {
        path: '/login',
        name: 'login',
        component: () => import('../views/Login.vue')
    },
    {
        path: '/cree-compte',
        name: 'cree-compte',
        component: () => import('../views/CreeCompte.vue')
    },
    {
        path: '/admin',
        name: 'admin-layout',
        component: () => import('../views/AdminDashboard.vue'),
        meta: { requiresAdmin: true },
        children: [
            {
                path: 'dashboard',
                name: 'admin-dashboard',
                component: () => import('../components/AdminOverview.vue'),
                meta: { requiresAdmin: true }
            },
            {
                path: 'dashboard/overview',
                name: 'admin-overview',
                component: () => import('../components/AdminOverview.vue'),
                meta: { requiresAdmin: true }
            },
            {
                path: 'dashboard/users',
                name: 'admin-users',
                component: () => import('../components/UsersManagement.vue'),
                meta: { requiresAdmin: true }
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// Navigation guard to check admin access
router.beforeEach((to, from, next) => {
    if (to.meta.requiresAdmin) {
        const token = localStorage.getItem('jwt_token');
        const user = JSON.parse(localStorage.getItem('user') || '{}');
        
        if (!token || !user.id || user.role !== 'admin') {
            // Redirect to login if no valid JWT or not admin
            next('/login');
        } else {
            next();
        }
    } else {
        next();
    }
});

export default router