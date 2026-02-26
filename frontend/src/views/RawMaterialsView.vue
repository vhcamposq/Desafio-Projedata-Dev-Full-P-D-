<script setup>
import { ref, onMounted } from 'vue'
import { rawMaterialApi } from '../api/api.js'
import RawMaterialForm from '../components/RawMaterialForm.vue'

const rawMaterials = ref([])
const loading = ref(false)
const errorMsg = ref('')
const showForm = ref(false)
const editingItem = ref(null)

async function loadAll() {
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await rawMaterialApi.findAll()
    rawMaterials.value = res.data
  } catch {
    errorMsg.value = 'Erro ao carregar matérias-primas.'
  } finally {
    loading.value = false
  }
}

function openCreate() {
  editingItem.value = null
  showForm.value = true
}

function openEdit(item) {
  editingItem.value = { ...item }
  showForm.value = true
}

async function handleDelete(id) {
  if (!confirm('Deseja excluir esta matéria-prima?')) return
  try {
    await rawMaterialApi.remove(id)
    await loadAll()
  } catch {
    errorMsg.value = 'Erro ao excluir matéria-prima.'
  }
}

async function handleSave() {
  showForm.value = false
  await loadAll()
}

onMounted(loadAll)
</script>

<template>
  <div>
    <div class="page-header">
      <h1 class="page-title">Matérias-Primas</h1>
      <button class="btn btn-primary" @click="openCreate">+ Nova Matéria-Prima</button>
    </div>

    <div v-if="errorMsg" class="alert alert-danger">{{ errorMsg }}</div>
    <div v-if="loading" class="loading">Carregando...</div>

    <RawMaterialForm
      v-if="showForm"
      :initial-data="editingItem"
      @save="handleSave"
      @cancel="showForm = false"
    />

    <div class="card" v-if="!loading">
      <table class="table" v-if="rawMaterials.length">
        <thead>
          <tr>
            <th>Código</th>
            <th>Nome</th>
            <th>Estoque</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="rm in rawMaterials" :key="rm.id">
            <td><span class="badge">{{ rm.code }}</span></td>
            <td>{{ rm.name }}</td>
            <td>{{ rm.stockQuantity }}</td>
            <td class="actions">
              <button class="btn btn-sm btn-secondary" @click="openEdit(rm)">Editar</button>
              <button class="btn btn-sm btn-danger" @click="handleDelete(rm.id)">Excluir</button>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-else class="empty-state">Nenhuma matéria-prima cadastrada.</p>
    </div>
  </div>
</template>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}
.page-title {
  font-size: 1.6rem;
  font-weight: 700;
  color: #1a1a2e;
}
.card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  overflow: hidden;
}
.table {
  width: 100%;
  border-collapse: collapse;
}
.table th {
  background-color: #1a1a2e;
  color: white;
  padding: 0.75rem 1rem;
  text-align: left;
  font-weight: 600;
}
.table td {
  padding: 0.75rem 1rem;
  border-bottom: 1px solid #f0f2f5;
}
.table tr:last-child td {
  border-bottom: none;
}
.table tr:hover td {
  background-color: #f8f9ff;
}
.actions {
  display: flex;
  gap: 0.5rem;
}
.badge {
  background: #e8eaf6;
  color: #3949ab;
  padding: 0.2rem 0.5rem;
  border-radius: 4px;
  font-size: 0.85rem;
  font-weight: 600;
}
.empty-state {
  text-align: center;
  color: #999;
  padding: 2rem;
}
.loading {
  text-align: center;
  color: #666;
  padding: 1rem;
}
.alert-danger {
  background: #fdecea;
  color: #c62828;
  padding: 0.75rem 1rem;
  border-radius: 6px;
  margin-bottom: 1rem;
}
.btn {
  padding: 0.5rem 1.2rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 600;
  transition: opacity 0.2s;
}
.btn:hover { opacity: 0.85; }
.btn-primary { background: #3949ab; color: white; }
.btn-secondary { background: #607d8b; color: white; }
.btn-danger { background: #e53935; color: white; }
.btn-sm { padding: 0.3rem 0.8rem; font-size: 0.82rem; }
</style>
