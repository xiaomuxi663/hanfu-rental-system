import request from '@/utils/request'

// 分类列表
export function getCategoryList() {
  return request({
    url: '/category/list',
    method: 'get'
  })
}

// 添加分类
export function addCategory(data) {
  return request({
    url: '/category/add',
    method: 'post',
    data
  })
}

// 更新分类
export function updateCategory(data) {
  return request({
    url: '/category/update',
    method: 'put',
    data
  })
}

// 删除分类
export function deleteCategory(id) {
  return request({
    url: `/category/delete/${id}`,
    method: 'delete'
  })
}
