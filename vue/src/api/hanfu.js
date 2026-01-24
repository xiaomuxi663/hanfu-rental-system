import request from '@/utils/request'

// 后台分页查询
export function getHanfuPage(params) {
  return request({
    url: '/hanfu/page',
    method: 'get',
    params
  })
}

// 前台汉服列表
export function getHanfuList(params) {
  return request({
    url: '/hanfu/list',
    method: 'get',
    params
  })
}

// 汉服详情
export function getHanfuDetail(id) {
  return request({
    url: `/hanfu/${id}`,
    method: 'get'
  })
}

// 添加汉服
export function addHanfu(data) {
  return request({
    url: '/hanfu/add',
    method: 'post',
    data
  })
}

// 更新汉服
export function updateHanfu(data) {
  return request({
    url: '/hanfu/update',
    method: 'put',
    data
  })
}

// 删除汉服
export function deleteHanfu(id) {
  return request({
    url: `/hanfu/delete/${id}`,
    method: 'delete'
  })
}

// 上下架
export function updatePublish(id, isPublish) {
  return request({
    url: `/hanfu/publish/${id}`,
    method: 'put',
    params: { isPublish }
  })
}

// SKU分页
export function getSkuPage(params) {
  return request({
    url: '/sku/page',
    method: 'get',
    params
  })
}

// 获取指定SPU的SKU列表
export function getSkuBySpuId(spuId) {
  return request({
    url: `/sku/list/${spuId}`,
    method: 'get'
  })
}

// 添加SKU
export function addSku(data) {
  return request({
    url: '/sku/add',
    method: 'post',
    data
  })
}

// 更新SKU
export function updateSku(data) {
  return request({
    url: '/sku/update',
    method: 'put',
    data
  })
}

// 删除SKU
export function deleteSku(id) {
  return request({
    url: `/sku/delete/${id}`,
    method: 'delete'
  })
}

// 更新SKU状态
export function updateSkuStatus(id, status) {
  return request({
    url: `/sku/status/${id}`,
    method: 'put',
    params: { status }
  })
}
