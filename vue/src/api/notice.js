import request from '@/utils/request'

/**
 * 前台 - 获取公告列表
 */
export function getNoticeList() {
  return request({ url: '/notice/list', method: 'get' })
}

/**
 * 前台 - 获取轮播图
 */
export function getBanners() {
  return request({ url: '/notice/banners', method: 'get' })
}

/**
 * 前台 - 公告详情
 */
export function getNoticeDetail(id) {
  return request({ url: `/notice/${id}`, method: 'get' })
}

/**
 * 后台 - 分页查询
 */
export function getNoticePage(params) {
  return request({ url: '/notice/admin/page', method: 'get', params })
}

/**
 * 后台 - 新增
 */
export function addNotice(data) {
  return request({ url: '/notice/admin', method: 'post', data })
}

/**
 * 后台 - 更新
 */
export function updateNotice(data) {
  return request({ url: '/notice/admin', method: 'put', data })
}

/**
 * 后台 - 删除
 */
export function deleteNotice(id) {
  return request({ url: `/notice/admin/${id}`, method: 'delete' })
}
