import request from '@/utils/request'

/**
 * 获取所有配置
 */
export function getConfigList() {
  return request({ url: '/config/list', method: 'get' })
}

/**
 * 添加配置
 */
export function addConfig(data) {
  return request({ url: '/config', method: 'post', data })
}

/**
 * 更新配置
 */
export function updateConfig(data) {
  return request({ url: '/config', method: 'put', data })
}

/**
 * 删除配置
 */
export function deleteConfig(id) {
  return request({ url: `/config/${id}`, method: 'delete' })
}
