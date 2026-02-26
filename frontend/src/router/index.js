import { createRouter, createWebHistory } from 'vue-router'
import RawMaterialsView from '../views/RawMaterialsView.vue'
import ProductsView from '../views/ProductsView.vue'
import ProductionView from '../views/ProductionView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/raw-materials' },
    { path: '/raw-materials', name: 'raw-materials', component: RawMaterialsView },
    { path: '/products', name: 'products', component: ProductsView },
    { path: '/production', name: 'production', component: ProductionView },
  ],
})

export default router
