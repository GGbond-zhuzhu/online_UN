/**
 * Web 端 Excel/表格导出工具函数
 * 说明：这里不依赖第三方 Excel 库，而是通过生成 UTF-8 CSV（兼容 Excel 打开）来完成简单导出需求。
 * 适合二手列表、兼职列表、校园卡流水等“表格数据”的快速导出。
 */

// 定义一列的数据结构（泛型 T 表示每一行的数据类型）
export interface ExcelColumn<T> { // ExcelColumn 用于描述一列应如何从数据行中取值
  key: keyof T | string // key：从数据对象中读取的字段名（也可以是自定义字符串）
  title: string // title：导出表头中显示的列标题
  formatter?: (row: T) => string | number | null | undefined // formatter：可选的格式化函数，用来自定义展示内容
}

// 将任意值安全地转换为字符串，避免出现 undefined/null 直接输出
function toCellValue(value: unknown): string { // 工具函数：把单元格原始值转换为字符串
  if (value === null || value === undefined) return '' // 如果值为空，返回空字符串
  if (typeof value === 'number') return String(value) // 如果是数字，直接转为字符串
  if (typeof value === 'boolean') return value ? '是' : '否' // 布尔值转换为中文“是/否”
  // 其他类型统一转为字符串
  return String(value) // 使用 String 函数保证安全转换
}

// 将单元格内容转换为 CSV 中安全的格式（处理逗号、引号、换行符等）
function escapeCsvCell(text: string): string { // 工具函数：按 CSV 规范转义单元格内容
  if (text.includes('"') || text.includes(',') || text.includes('\n') || text.includes('\r')) { // 如果包含特殊字符
    return '"' + text.replace(/"/g, '""') + '"' // 使用双引号包裹，并把内部的双引号替换为两个双引号
  }
  return text // 普通文本直接返回
}

// 导出一组数据为 CSV（Excel 兼容），并触发浏览器下载
export function exportToExcel<T>(options: { // 主导出函数：将数组数据导出为 CSV 文件
  filename: string // 导出文件名（不需要带扩展名）
  columns: ExcelColumn<T>[] // 列配置数组，决定表头和每列如何取值
  data: T[] // 真正要导出的数据行数组
}): void {
  const { filename, columns, data } = options // 从入参中解构出文件名、列配置和数据

  // 1. 生成表头行
  const headerRow = columns.map((col) => escapeCsvCell(col.title)) // 使用列标题并做 CSV 转义

  // 2. 生成数据行
  const bodyRows = data.map((row) => { // 遍历每一行数据
    const cells = columns.map((col) => { // 针对每一列生成该行的单元格内容
      if (col.formatter) { // 如果配置了 formatter，则优先使用格式化后的值
        const formatted = col.formatter(row) // 调用格式化函数
        return escapeCsvCell(toCellValue(formatted)) // 转为字符串后再做 CSV 转义
      }
      // 未配置 formatter，则按 key 从 row 中取值
      const raw = (row as any)[col.key as string] // 使用 any 访问动态 key
      return escapeCsvCell(toCellValue(raw)) // 转为字符串并转义
    })
    return cells.join(',') // 将当前行的所有单元格用逗号拼接
  })

  // 3. 拼接完整 CSV 文本，添加 UTF-8 BOM 以避免中文乱码
  const csvContent = '\uFEFF' + [headerRow.join(','), ...bodyRows].join('\n') // 在最前面加上 BOM，并用换行符分隔行

  // 4. 创建 Blob 对象并触发浏览器下载
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' }) // 使用 CSV MIME 类型创建二进制数据
  const url = URL.createObjectURL(blob) // 生成临时下载链接

  const link = document.createElement('a') // 创建一个隐藏的 a 标签用于触发下载
  link.href = url // 设置 a 标签的地址为刚刚生成的 blob 链接
  link.download = `${filename}.csv` // 设置下载的文件名，自动补充 .csv 后缀
  document.body.appendChild(link) // 将 a 标签临时插入到页面中
  link.click() // 触发一次点击事件，启动下载
  document.body.removeChild(link) // 下载触发后立即移除 a 标签
  URL.revokeObjectURL(url) // 释放临时链接占用的内存
}

// 从 HTMLTableElement 直接导出为 CSV（适合已经渲染好的表格）
export function exportTableElementToExcel(table: HTMLTableElement, filename: string): void { // 提供从原生表格导出的简化方法
  const rows: string[] = [] // 用于存放每一行的 CSV 文本

  const rowElements = Array.from(table.rows) // 将 HTMLCollection 转为数组，方便遍历
  rowElements.forEach((tr) => { // 遍历每一行 <tr>
    const cells = Array.from(tr.cells).map((cell) => escapeCsvCell(cell.innerText.trim())) // 取出每个单元格的文本并转义
    rows.push(cells.join(',')) // 将单元格合并为一行 CSV 文本
  })

  const csvContent = '\uFEFF' + rows.join('\n') // 与上面相同，添加 BOM 防止中文乱码
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' }) // 创建 CSV Blob
  const url = URL.createObjectURL(blob) // 生成临时链接

  const link = document.createElement('a') // 创建 a 标签
  link.href = url // 绑定链接地址
  link.download = `${filename}.csv` // 指定下载文件名
  document.body.appendChild(link) // 将 a 标签添加到文档中
  link.click() // 触发点击下载
  document.body.removeChild(link) // 下载完成后移除 a 标签
  URL.revokeObjectURL(url) // 释放临时链接
}
