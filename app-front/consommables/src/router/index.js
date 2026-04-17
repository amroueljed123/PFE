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

    // ───── USER ROUTES (Standard Utilisateur) ─────
    {
        path: '/user',
        name: 'user-layout',
        component: () => import('../views/UserDashboard.vue'),
        meta: { requiresRole: 'user' }
    },

    // ───── ADMIN ROUTES ─────
    {
        path: '/admin',
        name: 'admin-layout',
        component: () => import('../views/AdminDashboard.vue'),
        meta: { requiresRole: 'admin' },
        children: [
            {
                path: '',
                name: 'admin-home',
                redirect: '/admin/dashboard/overview'
            },
            {
                path: 'dashboard/overview',
                name: 'admin-overview',
                component: () => import('../components/AdminOverview.vue'),
                meta: { requiresRole: 'admin' }
            },
            {
                path: 'dashboard/users',
                name: 'admin-users',
                component: () => import('../components/UsersManagement.vue'),
                meta: { requiresRole: 'admin' }
            },
            {
                path: 'dashboard/articles',
                name: 'admin-articles-dashboard',
                component: () => import('../views/ArticlesDashboard.vue'),
                meta: { requiresRole: 'admin' }
            },
            {
                path: 'catalogue',
                name: 'admin-catalogue',
                component: () => import('../views/Catalogue.vue'),
                meta: { requiresRole: 'admin' }
            },
            {
                path: 'articles/custom/:categoryName',
                name: 'admin-custom-articles',
                component: () => import('../views/CustomArticleManagement.vue'),
                props: true,
                meta: { requiresRole: 'admin' }
            },
            // Informatique
            {
                path: 'articles/ordinateurs',
                name: 'admin-ordinateurs',
                component: () => import('../views/OrdinateursManagement.vue'),
                meta: { requiresRole: 'admin' }
            },
            {
                path: 'articles/moniteurs',
                name: 'admin-moniteurs',
                component: () => import('../views/MontiteursManagement.vue'),
                meta: { requiresRole: 'admin' }
            },
            {
                path: 'articles/peripheriques',
                name: 'admin-peripheriques',
                component: () => import('../views/PeripheriquesManagement.vue'),
                meta: { requiresRole: 'admin' }
            },
            {
                path: 'articles/telephones',
                name: 'admin-telephones',
                component: () => import('../views/TelephonesManagement.vue'),
                meta: { requiresRole: 'admin' }
            },
            {
                path: 'articles/imprimantes',
                name: 'admin-imprimantes',
                component: () => import('../views/ImprimantesManagement.vue'),
                meta: { requiresRole: 'admin' }
            },
            {
                path: 'articles/cartouches',
                name: 'admin-cartouches',
                component: () => import('../views/CartouchesManagement.vue'),
                meta: { requiresRole: 'admin' }
            },
            {
                path: 'articles/consommables',
                name: 'admin-consommables',
                component: () => import('../views/ConsommablesManagement.vue'),
                meta: { requiresRole: 'admin' }
            },
            // Mobilier
            {
                path: 'articles/bureaux',
                name: 'admin-bureaux',
                component: () => import('../views/BureauxManagement.vue'),
                meta: { requiresRole: 'admin' }
            },
            {
                path: 'articles/chaises',
                name: 'admin-chaises',
                component: () => import('../views/ChaisesManagement.vue'),
                meta: { requiresRole: 'admin' }
            },
            {
                path: 'articles/meubles',
                name: 'admin-meubles',
                component: () => import('../views/MeublesManagement.vue'),
                meta: { requiresRole: 'admin' }
            },
            {
                path: 'articles/etageres',
                name: 'admin-etageres',
                component: () => import('../views/EtageresManagement.vue'),
                meta: { requiresRole: 'admin' }
            },
            // Supply Chain
            {
                path: 'supply-chain/suppliers',
                name: 'admin-suppliers',
                component: () => import('../views/SupplierManagement.vue'),
                meta: { requiresRole: 'admin' }
            },
            {
                path: 'supply-chain/purchase-orders',
                name: 'admin-purchase-orders',
                component: () => import('../views/PurchaseOrderManagement.vue'),
                meta: { requiresRole: 'admin' }
            },
            // Inventory
            {
                path: 'inventory/stock',
                name: 'admin-stock',
                component: () => import('../views/ConsommablesManagement.vue'),
                meta: { requiresRole: 'admin' }
            },
            // Vehicles & Equipment
            {
                path: 'vehicles/management',
                name: 'admin-vehicles',
                component: () => import('../views/VehicleManagement.vue'),
                meta: { requiresRole: 'admin' }
            },
            // Analytics & Monitoring
            {
                path: 'analytics/anomalies',
                name: 'admin-anomalies',
                component: () => import('../views/AnomalyDetectionDashboard.vue'),
                meta: { requiresRole: 'admin' }
            },
            {
                path: 'analytics/optimization',
                name: 'admin-optimization',
                component: () => import('../views/OptimizationDashboard.vue'),
                meta: { requiresRole: 'admin' }
            },
            {
                path: 'analytics/audit',
                name: 'admin-audit',
                component: () => import('../views/AuditDashboard.vue'),
                meta: { requiresRole: 'admin' }
            },
            // Finance & Documents
            {
                path: 'finance/invoices',
                name: 'admin-invoices',
                component: () => import('../views/InvoiceManagement.vue'),
                meta: { requiresRole: 'admin' }
            },
            // Operations
            {
                path: 'operations/inventory',
                name: 'admin-inventory',
                component: () => import('../views/InventoryManagement.vue'),
                meta: { requiresRole: 'admin' }
            },
            {
                path: 'operations/demandes-validation',
                name: 'admin-demandes-validation',
                component: () => import('../views/DemandesValidation.vue'),
                meta: { requiresRole: 'admin' }
            },
            {
                path: 'operations/maintenance',
                name: 'admin-maintenance',
                component: () => import('../views/MaintenanceManagement.vue'),
                meta: { requiresRole: 'admin' }
            },
            {
                path: 'operations/mouvements',
                name: 'admin-mouvements',
                component: () => import('../views/MouvementsStock.vue'),
                meta: { requiresRole: 'admin' }
            }
        ]
    },

    // ───── GESTIONNAIRE DE STOCK ROUTES ─────
    {
        path: '/gestionnaire',
        name: 'gestionnaire-layout',
        component: () => import('../views/GestionnaireDashboard.vue'),
        meta: { requiresRole: 'gestionnaire' },
        children: [
            {
                path: '',
                redirect: '/gestionnaire/overview'
            },
            {
                path: 'overview',
                name: 'gestionnaire-overview',
                component: () => import('../components/GestionnaireOverview.vue'),
                meta: { requiresRole: 'gestionnaire' }
            },
            {
                path: 'stock/articles',
                name: 'gestionnaire-articles',
                component: () => import('../views/ArticlesDashboard.vue'),
                meta: { requiresRole: 'gestionnaire' }
            },
            {
                path: 'stock/catalogue',
                name: 'gestionnaire-catalogue',
                component: () => import('../views/Catalogue.vue'),
                meta: { requiresRole: 'gestionnaire' }
            },
            {
                path: 'stock/inventory',
                name: 'gestionnaire-inventory',
                component: () => import('../views/InventoryManagement.vue'),
                meta: { requiresRole: 'gestionnaire' }
            },
            {
                path: 'stock/demandes-traitement',
                name: 'gestionnaire-demandes-traitement',
                component: () => import('../views/DemandesTraitement.vue'),
                meta: { requiresRole: 'gestionnaire' }
            },
            {
                path: 'stock/mouvements',
                name: 'gestionnaire-mouvements',
                component: () => import('../views/MouvementsStock.vue'),
                meta: { requiresRole: 'gestionnaire' }
            },
            {
                path: 'documents/invoices',
                name: 'gestionnaire-invoices',
                component: () => import('../views/InvoiceManagement.vue'),
                meta: { requiresRole: 'gestionnaire' }
            },
            // Category article pages (reused)
            {
                path: 'articles/consommables',
                name: 'gestionnaire-consommables',
                component: () => import('../views/ConsommablesManagement.vue'),
                meta: { requiresRole: 'gestionnaire' }
            },
            {
                path: 'articles/cartouches',
                name: 'gestionnaire-cartouches',
                component: () => import('../views/CartouchesManagement.vue'),
                meta: { requiresRole: 'gestionnaire' }
            },
            {
                path: 'articles/meubles',
                name: 'gestionnaire-meubles',
                component: () => import('../views/MeublesManagement.vue'),
                meta: { requiresRole: 'gestionnaire' }
            },
            {
                path: 'articles/bureaux',
                name: 'gestionnaire-bureaux',
                component: () => import('../views/BureauxManagement.vue'),
                meta: { requiresRole: 'gestionnaire' }
            },
            {
                path: 'articles/chaises',
                name: 'gestionnaire-chaises',
                component: () => import('../views/ChaisesManagement.vue'),
                meta: { requiresRole: 'gestionnaire' }
            },
            {
                path: 'articles/etageres',
                name: 'gestionnaire-etageres',
                component: () => import('../views/EtageresManagement.vue'),
                meta: { requiresRole: 'gestionnaire' }
            },
            {
                path: 'articles/imprimantes',
                name: 'gestionnaire-imprimantes',
                component: () => import('../views/ImprimantesManagement.vue'),
                meta: { requiresRole: 'gestionnaire' }
            },
            {
                path: 'articles/ordinateurs',
                name: 'gestionnaire-ordinateurs',
                component: () => import('../views/OrdinateursManagement.vue'),
                meta: { requiresRole: 'gestionnaire' }
            },
            {
                path: 'articles/moniteurs',
                name: 'gestionnaire-moniteurs',
                component: () => import('../views/MontiteursManagement.vue'),
                meta: { requiresRole: 'gestionnaire' }
            },
            {
                path: 'articles/peripheriques',
                name: 'gestionnaire-peripheriques',
                component: () => import('../views/PeripheriquesManagement.vue'),
                meta: { requiresRole: 'gestionnaire' }
            },
            {
                path: 'articles/telephones',
                name: 'gestionnaire-telephones',
                component: () => import('../views/TelephonesManagement.vue'),
                meta: { requiresRole: 'gestionnaire' }
            },
            {
                path: 'articles/custom/:categoryName',
                name: 'gestionnaire-custom-articles',
                component: () => import('../views/CustomArticleManagement.vue'),
                props: true,
                meta: { requiresRole: 'gestionnaire' }
            }
        ]
    },

    // ───── RESPONSABLE OPERATIONEL ROUTES ─────
    {
        path: '/responsable',
        name: 'responsable-layout',
        component: () => import('../views/ResponsableDashboard.vue'),
        meta: { requiresRole: 'responsable' },
        children: [
            {
                path: '',
                redirect: '/responsable/overview'
            },
            {
                path: 'overview',
                name: 'responsable-overview',
                component: () => import('../components/ResponsableOverview.vue'),
                meta: { requiresRole: 'responsable' }
            },
            {
                path: 'supply/purchase-orders',
                name: 'responsable-purchase-orders',
                component: () => import('../views/PurchaseOrderManagement.vue'),
                meta: { requiresRole: 'responsable' }
            },
            {
                path: 'supply/suppliers',
                name: 'responsable-suppliers',
                component: () => import('../views/SupplierManagement.vue'),
                meta: { requiresRole: 'responsable' }
            },
            {
                path: 'analytics/anomalies',
                name: 'responsable-anomalies',
                component: () => import('../views/AnomalyDetectionDashboard.vue'),
                meta: { requiresRole: 'responsable' }
            },
            {
                path: 'analytics/optimization',
                name: 'responsable-optimization',
                component: () => import('../views/OptimizationDashboard.vue'),
                meta: { requiresRole: 'responsable' }
            }
        ]
    },

    // ───── DIRECTEUR ROUTES ─────
    {
        path: '/directeur',
        name: 'directeur-layout',
        component: () => import('../views/DirecteurDashboard.vue'),
        meta: { requiresRole: 'directeur' },
        children: [
            {
                path: '',
                redirect: '/directeur/overview'
            },
            {
                path: 'overview',
                name: 'directeur-overview',
                component: () => import('../components/DirecteurOverview.vue'),
                meta: { requiresRole: 'directeur' }
            },
            {
                path: 'stats/articles',
                name: 'directeur-articles',
                component: () => import('../views/ArticlesDashboard.vue'),
                meta: { requiresRole: 'directeur' }
            },
            {
                path: 'reports/optimization',
                name: 'directeur-optimization',
                component: () => import('../views/OptimizationDashboard.vue'),
                meta: { requiresRole: 'directeur' }
            },
            {
                path: 'reports/anomalies',
                name: 'directeur-anomalies',
                component: () => import('../views/AnomalyDetectionDashboard.vue'),
                meta: { requiresRole: 'directeur' }
            },
            {
                path: 'supervision/audit',
                name: 'directeur-audit',
                component: () => import('../views/AuditDashboard.vue'),
                meta: { requiresRole: 'directeur' }
            },
            {
                path: 'supervision/demandes-validation',
                name: 'directeur-demandes-validation',
                component: () => import('../views/DemandesValidation.vue'),
                meta: { requiresRole: 'directeur' }
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// Navigation guard to check role-based access
router.beforeEach((to, from, next) => {
    if (to.meta.requiresRole) {
        const token = localStorage.getItem('jwt_token')
        const user = JSON.parse(localStorage.getItem('user') || '{}')
        const requiredRole = to.meta.requiresRole

        if (!token || !user.id) {
            next('/login')
        } else if (user.role !== requiredRole) {
            // Redirect to the correct dashboard based on their role
            const roleRoutes = {
                admin: '/admin/dashboard/overview',
                gestionnaire: '/gestionnaire/overview',
                responsable: '/responsable/overview',
                directeur: '/directeur/overview',
                user: '/user'
            }
            const correctRoute = roleRoutes[user.role]
            if (correctRoute && !from.path.startsWith('/' + user.role)) {
                next(correctRoute)
            } else {
                next('/login')
            }
        } else {
            next()
        }
    } else {
        next()
    }
})

export default router
