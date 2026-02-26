<script setup></script>

<template>
  <div id="app-wrapper">
    <nav class="navbar">
      <div class="navbar-brand">⚙️ Gestão de Produção</div>
      <div class="navbar-links">
        <RouterLink to="/raw-materials">Matérias-Primas</RouterLink>
        <RouterLink to="/products">Produtos</RouterLink>
        <RouterLink to="/production">Calcular Produção</RouterLink>
      </div>
    </nav>
    <main class="main-content">
      <RouterView />
    </main>
  </div>
</template>

<style>
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

body {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background-color: #f0f2f5;
  color: #333;
}

#app-wrapper {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.navbar {
  background-color: #1a1a2e;
  color: white;
  padding: 0 2rem;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.navbar-brand {
  font-size: 1.2rem;
  font-weight: 700;
  letter-spacing: 0.5px;
}

.navbar-links {
  display: flex;
  gap: 1.5rem;
}

.navbar-links a {
  color: #ccc;
  text-decoration: none;
  font-size: 0.95rem;
  padding: 0.3rem 0.6rem;
  border-radius: 4px;
  transition: background 0.2s, color 0.2s;
}

.navbar-links a:hover,
.navbar-links a.router-link-active {
  background-color: #16213e;
  color: #fff;
}

.main-content {
  flex: 1;
  padding: 2rem;
  max-width: 1100px;
  width: 100%;
  margin: 0 auto;
}
</style>
