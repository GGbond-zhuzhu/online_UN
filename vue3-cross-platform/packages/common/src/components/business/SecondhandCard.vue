<template>
  <!-- 二手商品卡片外层容器，整体可点击跳转到详情页 -->
  <div class="secondhand-card" @click="handleClick">
    <!-- 左侧商品图片区域，如果没有图片则显示占位图标 -->
    <div class="card-image">
      <img v-if="coverImage" :src="coverImage" :alt="goods.title" />
      <div v-else class="image-placeholder">
        <i class="fas fa-box-open"></i>
      </div>
      <!-- 右上角成色标签，例如“全新”、“9成新”等 -->
      <span class="condition-tag" v-if="conditionLabel">{{ conditionLabel }}</span>
    </div>

    <!-- 右侧商品信息区域 -->
    <div class="card-content">
      <!-- 标题与价格行 -->
      <div class="card-header-row">
        <h3 class="card-title">{{ goods.title }}</h3>
        <div class="price-area">
          <span class="price-current">¥{{ goods.price.toFixed(2) }}</span>
          <span v-if="goods.originalPrice" class="price-original">¥{{ goods.originalPrice?.toFixed(2) }}</span>
        </div>
      </div>

      <!-- 描述摘要 -->
      <p class="card-desc">{{ shortDescription }}</p>

      <!-- 底部元信息：校区、发布时间、浏览量等 -->
      <div class="card-meta-row">
        <div class="meta-left">
          <span class="meta-item">
            <i class="fas fa-map-marker-alt"></i>
            {{ goods.schoolName || '未填写校区' }}
          </span>
          <span class="meta-item" v-if="goods.publishTime">
            <i class="fas fa-clock"></i>
            {{ goods.publishTime }}
          </span>
        </div>
        <div class="meta-right">
          <span class="meta-item">
            <i class="fas fa-eye"></i>
            {{ goods.viewCount ?? 0 }} 浏览
          </span>
          <span class="meta-item" v-if="goods.favoriteCount !== undefined">
            <i class="fas fa-heart"></i>
            {{ goods.favoriteCount }} 收藏
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// 引入计算属性工具，用于根据原始数据生成展示字段
import { computed } from 'vue' // 从 Vue 中导入 computed，创建派生数据

// 从二手接口模块中引入类型，保证与后端字段保持一致
import type { SecondhandGoods } from '../../api/secondhand' // 引入 SecondhandGoods 类型定义

// 定义组件接收的属性
const props = defineProps<{ // 使用 defineProps 声明输入属性
  goods: SecondhandGoods // goods：当前卡片要展示的一条二手商品数据
}>()

// 定义组件对外发出的事件
const emit = defineEmits<{ // 使用 defineEmits 声明事件类型
  (e: 'click', id: number): void // click 事件：当用户点击整张卡片时触发，并携带商品 id
}>()

// 计算封面图片地址：优先使用 images，其次使用 imageUrls 数组中的第一张
const coverImage = computed(() => { // coverImage：商品封面图片地址
  if (props.goods.images && props.goods.images.length > 0) { // 如果存在 images 数组且有值
    return props.goods.images[0] // 使用 images 数组中的第一张图片
  }
  if (props.goods.imageUrls && props.goods.imageUrls.length > 0) { // 如果存在 imageUrls 数组且有值
    return props.goods.imageUrls[0] // 使用 imageUrls 数组中的第一张图片
  }
  return '' // 如果都没有图片，则返回空字符串，交由模板显示占位图标
})

// 根据商品类别和描述生成一个简短摘要，避免在卡片中展示过长文本
const shortDescription = computed(() => { // shortDescription：用于在卡片中展示的简短描述
  const desc = props.goods.description || '卖家很忙，还没有写描述~' // 如果没有描述则使用默认文案
  return desc.length > 40 ? desc.slice(0, 40) + '…' : desc // 超过 40 个字符则截断并添加省略号
})

// 将状态字段转换为更友好的中文标签
const conditionLabel = computed(() => { // conditionLabel：商品成色标签
  const status = props.goods.status // 读取商品状态字段
  const map: Record<string, string> = { // 定义状态到中文文案的映射表
    NEW: '全新', // NEW 表示全新
    NINE: '9成新', // NINE 表示 9 成新
    EIGHT: '8成新', // EIGHT 表示 8 成新
    SEVEN: '7成新', // SEVEN 表示 7 成新
    OLD: '6成及以下' // OLD 表示较旧
  }
  return map[status] || '' // 如果能映射到中文标签则返回，否则返回空字符串
})

// 处理卡片点击事件，向外部抛出 click 事件
const handleClick = () => { // handleClick：卡片点击事件处理函数
  emit('click', props.goods.id) // 向父组件抛出 click 事件，并传递商品 id
}
</script>

<style scoped>
/* 卡片外层容器样式 */
.secondhand-card {
  display: flex; /* 使用 flex 布局，将图片和内容横向排列 */
  gap: 12px; /* 图片与内容之间保留一定间距 */
  padding: 14px; /* 整体内边距，保证卡片内容不过于紧凑 */
  border-radius: 12px; /* 使用圆角与全站卡片风格保持一致 */
  background-color: #ffffff; /* 使用白色背景突出卡片 */
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06); /* 轻微阴影增强层次感 */
  cursor: pointer; /* 鼠标移入时显示可点击手型 */
  transition: transform 0.15s ease, box-shadow 0.15s ease; /* 添加悬停动效 */
}

/* 悬停状态：略微上移并增强阴影 */
.secondhand-card:hover {
  transform: translateY(-2px); /* 上移 2 像素，产生浮起效果 */
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.12); /* 阴影加深以强调悬停 */
}

/* 图片区域样式 */
.card-image {
  position: relative; /* 为成色标签的绝对定位提供参考 */
  width: 110px; /* 固定图片宽度，保证列表对齐 */
  height: 110px; /* 固定图片高度，构成方形缩略图 */
  border-radius: 10px; /* 为图片区域添加圆角 */
  overflow: hidden; /* 超出部分裁剪，避免图片溢出 */
  background-color: #f5f5f5; /* 默认灰色背景，避免图片加载前出现空白 */
  flex-shrink: 0; /* 防止在窄屏下被压缩 */
}

/* 实际图片样式 */
.card-image img {
  width: 100%; /* 图片宽度铺满容器 */
  height: 100%; /* 图片高度铺满容器 */
  object-fit: cover; /* 保持图片比例，超出部分裁剪 */
  display: block; /* 去除默认的行内元素间隙 */
}

/* 无图片时的占位图样式 */
.image-placeholder {
  width: 100%; /* 占位容器宽度占满图片区域 */
  height: 100%; /* 占位容器高度占满图片区域 */
  display: flex; /* 使用 flex 让图标居中 */
  align-items: center; /* 垂直居中图标 */
  justify-content: center; /* 水平居中图标 */
  color: #cccccc; /* 使用浅灰色图标 */
  font-size: 32px; /* 图标尺寸较大，提示明显 */
}

/* 成色标签样式 */
.condition-tag {
  position: absolute; /* 绝对定位到图片右上角 */
  top: 6px; /* 距离顶部 6 像素 */
  right: 6px; /* 距离右侧 6 像素 */
  padding: 2px 8px; /* 内边距让标签更易读 */
  border-radius: 999px; /* 使用胶囊形状圆角 */
  background-color: rgba(216, 27, 96, 0.9); /* 使用主色半透明背景 */
  color: #ffffff; /* 标签文字使用白色 */
  font-size: 11px; /* 标签文字较小但清晰可读 */
}

/* 右侧内容区域样式 */
.card-content {
  flex: 1; /* 占据除图片外的剩余宽度 */
  display: flex; /* 使用 flex 列布局 */
  flex-direction: column; /* 垂直排列标题、描述与底部信息 */
  gap: 6px; /* 各行之间保持适当间距 */
}

/* 标题与价格所在的第一行 */
.card-header-row {
  display: flex; /* 使用 flex 布局让标题和价格在一行内 */
  justify-content: space-between; /* 标题靠左，价格靠右 */
  align-items: flex-start; /* 顶部对齐以防多行标题导致错位 */
  gap: 8px; /* 标题和价格区域之间保持间距 */
}

/* 商品标题样式 */
.card-title {
  font-size: 15px; /* 标题字体大小中等偏大 */
  font-weight: 600; /* 使用半粗体提升标题重要性 */
  color: #333333; /* 使用深色字体增强可读性 */
  margin: 0; /* 去掉默认外边距 */
}

/* 价格区域样式 */
.price-area {
  display: flex; /* 使用 flex 将当前价和原价放在一行 */
  flex-direction: column; /* 竖直排列当前价和原价，保证窄屏下也易读 */
  align-items: flex-end; /* 文本靠右对齐，与卡片右边界对齐 */
}

/* 当前售价样式 */
.price-current {
  font-size: 16px; /* 当前价格稍大以吸引注意 */
  font-weight: 700; /* 使用粗体强调价格 */
  color: #d81b60; /* 使用品牌主色表示关键价格信息 */
}

/* 原价样式：显示删除线表示已优惠 */
.price-original {
  font-size: 12px; /* 原价字体较小，降低视觉权重 */
  color: #999999; /* 使用浅灰色弱化原价 */
  text-decoration: line-through; /* 使用删除线表示不再有效 */
}

/* 描述文本样式 */
.card-desc {
  font-size: 13px; /* 描述使用中等字号，便于阅读 */
  color: #666666; /* 使用中灰色文字 */
  margin: 0; /* 去掉默认外边距 */
}

/* 底部元信息行：校区、时间、浏览量等 */
.card-meta-row {
  display: flex; /* 使用 flex 将左右两块信息分布排列 */
  justify-content: space-between; /* 左右两部分贴近两侧 */
  align-items: center; /* 垂直方向居中对齐 */
  gap: 8px; /* 左右两块之间留出间距 */
}

/* 左侧元信息容器：校区和发布时间 */
.meta-left {
  display: flex; /* 使用 flex 横向排列多条元信息 */
  flex-wrap: wrap; /* 在窄屏下允许换行 */
  gap: 6px; /* 每条信息之间的间距 */
}

/* 右侧元信息容器：浏览量和收藏数 */
.meta-right {
  display: flex; /* 使用 flex 横向排列 */
  flex-wrap: wrap; /* 允许在窄屏下换行 */
  gap: 6px; /* 每条信息之间的间距 */
  justify-content: flex-end; /* 靠右对齐，贴合价格区域 */
}

/* 单条元信息样式 */
.meta-item {
  font-size: 12px; /* 使用较小字号显示辅助信息 */
  color: #999999; /* 使用浅灰色弱化显示 */
  display: inline-flex; /* 使用 inline-flex 方便图标和文字对齐 */
  align-items: center; /* 垂直方向居中图标和文字 */
  gap: 4px; /* 图标和文字之间保留水平间距 */
}

/* 元信息前的小图标样式 */
.meta-item i {
  font-size: 11px; /* 图标略小于文字，避免喧宾夺主 */
}

/* 小屏幕适配：在窄屏下调整布局 */
@media (max-width: 600px) {
  .secondhand-card {
    padding: 10px; /* 减小卡片内边距，增加可用空间 */
  }

  .card-image {
    width: 90px; /* 缩小图片尺寸以适应窄屏 */
    height: 90px; /* 高度同步缩小 */
  }

  .card-title {
    font-size: 14px; /* 标题字号略微缩小 */
  }

  .card-desc {
    font-size: 12px; /* 描述文字也略微缩小 */
  }
}
</style>
