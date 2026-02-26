<script setup>
import { ref, watch, onMounted } from 'vue'
import { productApi, rawMaterialApi } from '../api/api.js'

const props = defineProps({
  initialData: { type: Object, default: null },
})
const emit = defineEmits(['save', 'cancel'])

const form = ref({ code: '', name: '', price: '', ingredients: [] })
const rawMaterials = ref([])
const errorMsg = ref('')
const saving = ref(false)

watch(
  () => props.initialData,
  (val) => {
    if (val) {
      form.value = {
        code: val.code,
        name: val.name,
        price: val.price,
        ingredients: val.ingredients.map((i) => ({
          rawMaterialId: i.rawMaterialId,
          quantityRequired: i.quantityRequired,
        })),
      }
    } else {
      form.value = { code: '', name: '', price: '', ingredients: [] }
    }
  },
  { immediate: true },
)

function addIngredient() {
  form.value.ingredients.push({ rawMaterialId: '', quantityRequired: '' })
}

function removeIngredient(index) {
  form.value.ingredients.splice(index, 1)
}

async function handleSubmit() {
  saving.value = true
  errorMsg.value = ''
  try {
    const payload = {
      code: form.value.code,
      name: form.value.name,
      price: Number(form.value.price),
      ingredients: form.value.ingredients.map((i) => ({
        rawMaterialId: Number(i.rawMaterialId),
        quantityRequired: Number(i.quantityRequired),
      })),
    }
    if (props.initialData?.id) {
      await productApi.update(props.initialData.id, payload)
    } else {
      await productApi.create(payload)
    }
    emit('save')
  } catch (err) {
    errorMsg.value = err.response?.data?.error || 'Erro ao salvar produto.'
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  const res = await rawMaterialApi.findAll()
  rawMaterials.value = res.data
})
</script>

<template>
  <div class="form-card">
    <h2 class="form-title">{{ initialData ? 'Editar' : 'Novo' }} Produto</h2>
    <div v-if="errorMsg" class="alert-danger">{{ errorMsg }}</div>
    <form @submit.prevent="handleSubmit">
      <div class="form-row">
        <div class="form-group">
          <label>Código</label>
          <input v-model="form.code" required placeholder="Ex: P-001" />
        </div>
        <div class="form-group">
          <label>Nome</label>
          <input v-model="form.name" required placeholder="Ex: Pão de Forma" />
        </div>
        <div class="form-group">
          <label>Valor (R$)</label>
          <input v-model="form.price" type="number" min="0.01" step="0.01" required placeholder="Ex: 12.50" />
        </div>
      </div>

      <div class="ingredients-section">
        <div class="ingredients-header">
          <span class="ingredients-title">Composição (Ingredientes)</span>
          <button type="button" class="btn btn-sm btn-add" @click="addIngredient">+ Adicionar</button>
        </div>

        <div v-if="form.ingredients.length === 0" class="empty-ingredients">
          Nenhum ingrediente adicionado.
        </div>

        <div v-for="(ing, index) in form.ingredients" :key="index" class="ingredient-row">
          <div class="form-group">
            <label>Matéria-Prima</label>
            <select v-model="ing.rawMaterialId" required>
              <option value="" disabled>Selecione...</option>
              <option v-for="rm in rawMaterials" :key="rm.id" :value="rm.id">
                {{ rm.name }} ({{ rm.code }})
              </option>
            </select>
          </div>
          <div class="form-group">
            <label>Quantidade Necessária</label>
            <input v-model="ing.quantityRequired" type="number" min="0.01" step="0.01" required placeholder="Ex: 200" />
          </div>
          <button type="button" class="btn btn-sm btn-danger remove-btn" @click="removeIngredient(index)">✕</button>
        </div>
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
.form-row {
  display: grid;
  grid-template-columns: 1fr 2fr 1fr;
  gap: 1rem;
  margin-bottom: 1.25rem;
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
.form-group input,
.form-group select {
  padding: 0.5rem 0.75rem;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 0.9rem;
  outline: none;
  transition: border-color 0.2s;
}
.form-group input:focus,
.form-group select:focus {
  border-color: #3949ab;
}
.ingredients-section {
  background: #f8f9ff;
  border: 1px solid #e8eaf6;
  border-radius: 8px;
  padding: 1rem;
  margin-bottom: 1.25rem;
}
.ingredients-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
}
.ingredients-title {
  font-size: 0.9rem;
  font-weight: 700;
  color: #3949ab;
}
.ingredient-row {
  display: grid;
  grid-template-columns: 2fr 1fr auto;
  gap: 0.75rem;
  align-items: end;
  margin-bottom: 0.5rem;
}
.remove-btn {
  align-self: flex-end;
  margin-bottom: 0;
}
.empty-ingredients {
  color: #aaa;
  font-size: 0.85rem;
  text-align: center;
  padding: 0.5rem;
}
.form-actions {
  display: flex;
  justify-content: flex-end;
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
.btn-danger { background: #e53935; color: white; }
.btn-add { background: #2e7d32; color: white; }
.btn-sm { padding: 0.3rem 0.8rem; font-size: 0.82rem; }
</style>
