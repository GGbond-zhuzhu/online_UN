<template>
  <div v-if="visible" class="app-modal" @keydown.esc="handleClose">
    <div class="modal-overlay" @click="handleOverlayClick"></div>

    <div class="modal-content" :style="{ width }" role="dialog" aria-modal="true">
      <div v-if="title" class="modal-header">
        <h3 class="modal-title">{{ title }}</h3>
        <button class="close-btn" type="button" @click="handleClose" aria-label="关闭">
          <i class="fas fa-times"></i>
        </button>
      </div>

      <div class="modal-body">
        <slot />
      </div>

      <div v-if="$slots.footer" class="modal-footer">
        <slot name="footer" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { withDefaults, defineProps, defineEmits } from 'vue'

interface Props {
  visible: boolean
  title?: string
  width?: string
  closeOnOverlay?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  title: '',
  width: 'min(920px, 94vw)',
  closeOnOverlay: true
})

const emit = defineEmits<{
  close: []
}>()

const handleClose = () => emit('close')

const handleOverlayClick = () => {
  if (!props.closeOnOverlay) return
  handleClose()
}
</script>

<style scoped>
.app-modal {
  position: fixed;
  inset: 0;
  z-index: 9999;
}

.modal-overlay {
  position: absolute;
  inset: 0;
  background: rgba(15, 23, 42, 0.55);
  backdrop-filter: blur(6px);
}

.modal-content {
  position: relative;
  margin: 4vh auto;
  max-height: 92vh;
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.25);
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.25);
  background: linear-gradient(135deg, #f9f0ff 0%, #e6f7ff 100%);
}

.modal-title {
  margin: 0;
  font-size: 16px;
  font-weight: 800;
  color: #111827;
}

.close-btn {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  border: 1px solid rgba(148, 163, 184, 0.35);
  background: rgba(255, 255, 255, 0.8);
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #6b7280;
  transition: transform 0.1s ease, background 0.2s ease;
}

.close-btn:hover {
  background: #fff;
}

.close-btn:active {
  transform: scale(0.98);
}

.modal-body {
  padding: 16px;
  overflow: auto;
}

.modal-footer {
  padding: 12px 16px;
  border-top: 1px solid rgba(148, 163, 184, 0.25);
  background: #fff;
}
</style>
