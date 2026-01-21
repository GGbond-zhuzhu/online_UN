<template>
  <section class="carousel-section">
    <div
      v-for="(slide, index) in slides"
      :key="index"
      class="carousel-slide"
      :class="{ active: currentIndex === index }"
      :style="{ backgroundImage: `url(${slide.image})` }"
    >
      <div class="slide-content">
        <h2 class="slide-title">{{ slide.title }}</h2>
        <p class="slide-text">{{ slide.text }}</p>
        <a v-if="slide.buttonText" :href="slide.buttonLink" class="btn" style="margin-top: 20px;">
          {{ slide.buttonText }}
        </a>
      </div>
    </div>
    <button class="carousel-arrow prev" @click="prevSlide">
      <i class="fas fa-chevron-left"></i>
    </button>
    <button class="carousel-arrow next" @click="nextSlide">
      <i class="fas fa-chevron-right"></i>
    </button>
    <div class="carousel-indicators">
      <div
        v-for="(slide, index) in slides"
        :key="index"
        class="indicator"
        :class="{ active: currentIndex === index }"
        @click="goToSlide(index)"
      ></div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

// 轮播图数据接口
export interface CarouselSlide {
  title: string
  text: string
  image: string
  buttonText?: string
  buttonLink?: string
}

// Props
interface Props {
  slides: CarouselSlide[]
  autoplay?: boolean
  interval?: number
}

const props = withDefaults(defineProps<Props>(), {
  autoplay: true,
  interval: 5000
})

// 当前索引
const currentIndex = ref(0)
// 自动播放定时器
let autoplayTimer: number | null = null

// 下一张
const nextSlide = () => {
  currentIndex.value = (currentIndex.value + 1) % props.slides.length
  resetAutoplay()
}

// 上一张
const prevSlide = () => {
  currentIndex.value = (currentIndex.value - 1 + props.slides.length) % props.slides.length
  resetAutoplay()
}

// 跳转到指定幻灯片
const goToSlide = (index: number) => {
  currentIndex.value = index
  resetAutoplay()
}

// 重置自动播放
const resetAutoplay = () => {
  if (props.autoplay) {
    if (autoplayTimer) {
      clearInterval(autoplayTimer)
    }
    autoplayTimer = window.setInterval(nextSlide, props.interval)
  }
}

// 组件挂载
onMounted(() => {
  if (props.autoplay) {
    autoplayTimer = window.setInterval(nextSlide, props.interval)
  }
})

// 组件卸载
onUnmounted(() => {
  if (autoplayTimer) {
    clearInterval(autoplayTimer)
  }
})
</script>

<style scoped>
.carousel-section {
  position: relative;
  height: 400px;
  border-radius: 16px;
  overflow: hidden;
  margin-bottom: 40px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.carousel-slide {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  transition: opacity 1s ease-in-out;
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  text-align: center;
}

.carousel-slide.active {
  opacity: 1;
}

.slide-content {
  background: rgba(0, 0, 0, 0.5);
  padding: 30px;
  border-radius: 10px;
  max-width: 80%;
}

.slide-title {
  font-size: 36px;
  margin-bottom: 15px;
}

.slide-text {
  font-size: 18px;
}

.carousel-arrow {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(255, 255, 255, 0.7);
  border: none;
  width: 50px;
  height: 50px;
  border-radius: 50%;
  font-size: 20px;
  cursor: pointer;
  z-index: 10;
  transition: all 0.3s;
  color: #333;
}

.carousel-arrow:hover {
  background: rgba(255, 255, 255, 0.9);
}

.carousel-arrow.prev {
  left: 20px;
}

.carousel-arrow.next {
  right: 20px;
}

.carousel-indicators {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 10px;
}

.indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: all 0.3s;
}

.indicator.active {
  background: white;
  transform: scale(1.2);
}

.btn {
  display: inline-block;
  padding: 12px 30px;
  background: #d81b60;
  color: white;
  border: none;
  border-radius: 25px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  text-decoration: none;
}

.btn:hover {
  background: #c2185b;
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(216, 27, 96, 0.3);
}

.btn:active {
  transform: scale(0.95);
  box-shadow: 0 2px 8px rgba(216, 27, 96, 0.2);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .carousel-section {
    height: 300px;
  }

  .slide-title {
    font-size: 24px;
  }

  .slide-text {
    font-size: 16px;
  }
}
</style>
