import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
})

export const rawMaterialApi = {
  findAll: () => api.get('/raw-materials'),
  findById: (id) => api.get(`/raw-materials/${id}`),
  create: (data) => api.post('/raw-materials', data),
  update: (id, data) => api.put(`/raw-materials/${id}`, data),
  remove: (id) => api.delete(`/raw-materials/${id}`),
}

export const productApi = {
  findAll: () => api.get('/products'),
  findById: (id) => api.get(`/products/${id}`),
  create: (data) => api.post('/products', data),
  update: (id, data) => api.put(`/products/${id}`, data),
  remove: (id) => api.delete(`/products/${id}`),
}

export const productionApi = {
  getSuggestion: () => api.get('/production/suggestion'),
}
