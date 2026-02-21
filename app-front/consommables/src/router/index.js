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
            },
            {
                path: 'dashboard/articles',
                name: 'admin-articles-dashboard',
                component: () => import('../views/ArticlesDashboard.vue'),
                meta: { requiresAdmin: true }
            },
            {
                path: 'articles/consommables',
                name: 'admin-consommables',
                component: () => import('../views/ConsommablesManagement.vue'),
                meta: { requiresAdmin: true }
            },
            {
                path: 'articles/cartouches',
                name: 'admin-cartouches',
                component: () => import('../views/CartouchesManagement.vue'),
                meta: { requiresAdmin: true }
            },
            {
                path: 'articles/meubles',
                name: 'admin-meubles',
                component: () => import('../views/MeublesManagement.vue'),
                meta: { requiresAdmin: true }
            },
            {
                path: 'articles/bureaux',
                name: 'admin-bureaux',
                component: () => import('../views/BureauxManagement.vue'),
                meta: { requiresAdmin: true }
            },
            {
                path: 'articles/chaises',
                name: 'admin-chaises',
                component: () => import('../views/ChaisesManagement.vue'),
                meta: { requiresAdmin: true }
            },
            {
                path: 'articles/etageres',
                name: 'admin-etageres',
                component: () => import('../views/EtageresManagement.vue'),
                meta: { requiresAdmin: true }
            },
            {
                path: 'articles/imprimantes',
                name: 'admin-imprimantes',
                component: () => import('../views/ImprimantesManagement.vue'),
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