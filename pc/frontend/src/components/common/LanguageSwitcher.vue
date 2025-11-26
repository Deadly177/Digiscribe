<template>
  <div class="language-switcher" :class="{ compact }">
    <button
      type="button"
      class="lang-btn"
      :class="{ active: locale === 'en' }"
      @click="changeLanguage('en')"
    >
      EN
    </button>
    <button
      type="button"
      class="lang-btn"
      :class="{ active: locale === 'zh' }"
      @click="changeLanguage('zh')"
    >
      中文
    </button>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useI18n } from '@/i18n'

const props = defineProps({
  compact: {
    type: Boolean,
    default: false
  }
})

const { locale: localeRef, setLocale } = useI18n()
const locale = computed(() => localeRef.value)

const changeLanguage = (lang) => {
  setLocale(lang)
}
</script>

<style scoped>
.language-switcher {
  display: inline-flex;
  padding: 4px;
  border-radius: 999px;
  background: rgba(15, 23, 42, 0.08);
  gap: 4px;
}

.language-switcher.compact {
  padding: 2px;
  background: rgba(15, 23, 42, 0.12);
}

.lang-btn {
  border: none;
  background: transparent;
  color: #0f172a;
  font-size: 12px;
  font-weight: 600;
  padding: 4px 10px;
  border-radius: 999px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.lang-btn.active {
  background: white;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.1);
}

.compact .lang-btn {
  font-size: 11px;
  padding: 3px 8px;
}
</style>
