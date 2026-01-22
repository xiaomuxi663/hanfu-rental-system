import request from '@/utils/request'

/**
 * 上传文件
 */
export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/file/upload',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

/**
 * 获取上传URL（用于el-upload组件）
 */
export const uploadUrl = '/api/file/upload'
