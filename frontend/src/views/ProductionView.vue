<script setup>
import { ref } from 'vue'
import { productionApi } from '../api/api.js'

const suggestion = ref(null)
const loading = ref(false)
const errorMsg = ref('')

async function calculate() {
  loading.value = true
  errorMsg.value = ''
  suggestion.value = null
  try {
    const res = await productionApi.getSuggestion()
    suggestion.value = res.data
  } catch {
    errorMsg.value = 'Erro ao calcular sugestão de produção.'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div>
    <div class="page-header">
      <h1 class="page-title">Cálculo de Produção</h1>
      <button class="btn btn-primary" @click="calculate" :disabled="loading">
        {{ loading ? 'Calculando...' : '▶ Calcular Sugestão' }}
      </button>
    </div>

    <div class="info-box">
      <strong>Como funciona:</strong> O sistema analisa o estoque atual e sugere quais produtos fabricar
      para obter o <strong>maior valor total de venda</strong>, priorizando os produtos de maior valor em caso de conflito.
    </div>

    <div v-if="errorMsg" class="alert-danger">{{ errorMsg }}</div>

    <div v-if="suggestion">
      <div v-if="suggestion.items.length === 0" class="card empty-state">
        <p>⚠️ Não é possível fabricar nenhum produto com o estoque atual.</p>
      </div>

      <div v-else>
        <div class="card">
          <table class="table">
            <thead>
              <tr>
                <th>#</th>
                <th>Código</th>
                <th>Produto</th>
                <th>Qtd. a Fabricar</th>
                <th>Valor Unitário</th>
                <th>Valor Total</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(item, index) in suggestion.items" :key="item.productId">
                <td class="rank">{{ index + 1 }}º</td>
                <td><span class="badge">{{ item.productCode }}</span></td>
                <td>{{ item.productName }}</td>
                <td class="quantity">{{ item.quantity }} un.</td>
                <td>R$ {{ Number(item.unitPrice).toFixed(2) }}</td>
                <td class="total-value">R$ {{ Number(item.totalValue).toFixed(2) }}</td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="grand-total-card">
          <span class="grand-total-label">💰 Valor Total de Venda</span>
          <span class="grand-total-value">R$ {{ Number(suggestion.grandTotal).toFixed(2) }}</span>
        </div>
      </div>
    </div>

    <div v-if="!suggestion && !loading" class="placeholder-card">
      <p>Clique em <strong>"Calcular Sugestão"</strong> para ver a recomendação de produção com base no estoque atual.</p>
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
.info-box {
  background: #e8eaf6;
  color: #283593;
  border-left: 4px solid #3949ab;
  padding: 0.85rem 1rem;
  border-radius: 6px;
  margin-bottom: 1.5rem;
  font-size: 0.9rem;
  line-height: 1.5;
}
.card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  overflow: hidden;
  margin-bottom: 1rem;
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
.table tr:last-child td { border-bottom: none; }
.table tr:hover td { background-color: #f8f9ff; }
.rank { color: #888; font-weight: 600; }
.badge {
  background: #e8eaf6;
  color: #3949ab;
  padding: 0.2rem 0.5rem;
  border-radius: 4px;
  font-size: 0.85rem;
  font-weight: 600;
}
.quantity { font-weight: 700; }
.total-value {
  font-weight: 700;
  color: #2e7d32;
}
.grand-total-card {
  background: #1a1a2e;
  color: white;
  border-radius: 8px;
  padding: 1.25rem 1.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 0.5rem;
}
.grand-total-label {
  font-size: 1rem;
  font-weight: 600;
}
.grand-total-value {
  font-size: 1.6rem;
  font-weight: 800;
  color: #69f0ae;
}
.placeholder-card {
  background: white;
  border-radius: 8px;
  padding: 2.5rem;
  text-align: center;
  color: #888;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  font-size: 0.95rem;
  line-height: 1.6;
}
.empty-state {
  padding: 2rem;
  text-align: center;
  color: #e65100;
  font-size: 1rem;
}
.alert-danger {
  background: #fdecea;
  color: #c62828;
  padding: 0.75rem 1rem;
  border-radius: 6px;
  margin-bottom: 1rem;
}
.btn {
  padding: 0.55rem 1.4rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.95rem;
  font-weight: 600;
  transition: opacity 0.2s;
}
.btn:hover { opacity: 0.85; }
.btn:disabled { opacity: 0.6; cursor: not-allowed; }
.btn-primary { background: #3949ab; color: white; }
</style>
