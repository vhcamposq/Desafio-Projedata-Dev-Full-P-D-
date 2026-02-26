<script setup>
import { ref, watch } from 'vue'
import { rawMaterialApi } from '../api/api.js'

const props = defineProps({
  initialData: { type: Object, default: null },
})
const emit = defineEmits(['save', 'cancel'])

const form = ref({ code: '', name: '', stockQuantity: '' })
const errorMsg = ref('')
const saving = ref(false)

watch(
  () => props.initialData,
  (val) => {
    form.value = val ? { ...val } : { code: '', name: '', stockQuantity: '' }
  },
  { immediate: true },
)

async function handleSubmit() {
  saving.value = true
  errorMsg.value = ''
  try {
    const payload = { ...form.value, stockQuantity: Number(form.value.stockQuantity) }
    if (props.initialData?.id) {
      await rawMaterialApi.update(props.initialData.id, payload)
    } else {
      await rawMaterialApi.create(payload)
    }
    emit('save')
  } catch (err) {
    errorMsg.value = err.response?.data?.error || 'Erro ao salvar matéria-prima.'
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div class="form-card">
    <h2 class="form-title">{{ initialData ? 'Editar' : 'Nova' }} Matéria-Prima</h2>
    <div v-if="errorMsg" class="alert-danger">{{ errorMsg }}</div>
    <form @submit.prevent="handleSubmit" class="form-grid">
      <div class="form-group">
        <label>Código</label>
        <input v-model="form.code" required placeholder="Ex: RM-001" />
      </div>
      <div class="form-group">
        <label>Nome</label>
        <input v-model="form.name" required placeholder="Ex: Farinha de Trigo" />
      </div>
      <div class="form-group">
        <label>Quantidade em Estoque</label>
        <input v-model="form.stockQuantity" type="number" min="0" step="0.01" required placeholder="Ex: 500" />
      </div>
      <div class="form-actions">
        <button type="button" class="btn btn-secondary" @click="emit('cancel')">Cancelar</button>
        <button type="submit" class="btn btn-primary" :disabled="saving">
          {{ saving ? 'Salvando...' : 'Salvar' }}
        </button>
      </div>
    </form>
  </div>
</template>

<style scoped>
.form-card {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  margin-bottom: 1.5rem;
}
.form-title {
  font-size: 1.1rem;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 1rem;
}
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr auto;
  gap: 1rem;
  align-items: end;
}
.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
}
.form-group label {
  font-size: 0.85rem;
  font-weight: 600;
  color: #555;
}
.form-group input {
  padding: 0.5rem 0.75rem;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 0.9rem;
  outline: none;
  transition: border-color 0.2s;
}
.form-group input:focus {
  border-color: #3949ab;
}
.form-actions {
  display: flex;
  gap: 0.5rem;
}
.alert-danger {
  background: #fdecea;
  color: #c62828;
  padding: 0.6rem 0.8rem;
  border-radius: 6px;
  margin-bottom: 0.75rem;
  font-size: 0.9rem;
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
.btn:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-primary { background: #3949ab; color: white; }
.btn-secondary { background: #607d8b; color: white; }
</style>
