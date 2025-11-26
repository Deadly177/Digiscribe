<template>
  <div class="dashboard-layout">
    <!-- Sidebar -->
    <Sidebar :is-collapsed="sidebarCollapsed" @toggle="sidebarCollapsed = !sidebarCollapsed" />
    
    <!-- Main Content -->
    <div class="main-content" :class="{ 'sidebar-collapsed': sidebarCollapsed }">
      <!-- Top Navigation with User Menu -->
      <TopNavigation @toggle-sidebar="sidebarCollapsed = !sidebarCollapsed" />
      
      <!-- Dashboard Content with Router View -->
      <div class="dashboard-content">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import Sidebar from '../components/layout/Sidebar.vue'
import TopNavigation from '../components/layout/TopNavigation.vue'

export default {
  name: 'Dashboard',
  components: {
    Sidebar,
    TopNavigation
  },
  setup() {
    const sidebarCollapsed = ref(false)

    return {
      sidebarCollapsed
    }
  }
}
</script>

<style scoped>
.dashboard-layout {
  display: flex;
  min-height: 100vh;
  background: #f8fafc;
}

.main-content {
  flex: 1;
  margin-left: 280px;
  transition: margin-left 0.3s ease;
  width: calc(100% - 280px);
}

.main-content.sidebar-collapsed {
  margin-left: 80px;
  width: calc(100% - 80px);
}

.dashboard-content {
  padding: 24px;
  min-height: calc(100vh - 70px);
  width: 100%;
  max-width: none;
}

/* Responsive */
@media (max-width: 1024px) {
  .main-content {
    margin-left: 0;
    width: 100%;
  }
  
  .main-content.sidebar-collapsed {
    margin-left: 0;
    width: 100%;
  }
}
</style>