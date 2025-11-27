<script setup></script>

<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

:root {
  --primary: #1af354;
  --primary-dark: #1d4ed8;
  --primary-light: #dbeafe;
  --secondary: #27bb79;
  --success: #10b981;
  --warning: #f59e0b;
  --error: #ef4444;
  --background: #f8fafc;
  --surface: #ffffff;
  --text-primary: #1e293b;
  --text-secondary: #64748b;
  --border: #e2e8f0;
  --shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1);
  --shadow-lg: 0 10px 15px -3px rgb(0 0 0 / 0.1);
}

:root[data-theme='dark'] {
  --primary: #22c55e;
  --primary-dark: #22c55e;
  --primary-light: #0f172a;
  --secondary: #0ea5e9;
  --success: #22c55e;
  --warning: #fbbf24;
  --error: #f87171;
  --background: #0b1120;
  --surface: #0f172a;
  --text-primary: #e2e8f0;
  --text-secondary: #94a3b8;
  --border: #1f2937;
  --shadow: 0 6px 16px rgb(0 0 0 / 0.35);
  --shadow-lg: 0 12px 24px rgb(0 0 0 / 0.4);
}

body {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
  background: var(--background);
  min-height: 100vh;
  color: var(--text-primary);
  line-height: 1.6;
}

#app {
  min-height: 100vh;
  width: 100%;
  display: block;
  background: var(--background);
}
</style>

<style scoped></style>
