import dayjs from 'dayjs'

/** 将后端 ISO 时间格式化为 YYYY-MM-DD HH:mm:ss */
export function formatDateTime(value?: string | null) {
  if (!value) return '-'
  const d = dayjs(value)
  return d.isValid() ? d.format('YYYY-MM-DD HH:mm:ss') : value
}
