import request from './request'

/**
 * 获取题目列表
 * @param {Object} params - 查询参数 {current, pageSize, title, difficulty}
 * @returns {Promise}
 */
export function getQuestionList(params) {
  return request({
    url: '/question/list',
    method: 'get',
    params
  })
}

/**
 * 获取题目详情
 * @param {number} id - 题目ID
 * @returns {Promise}
 */
export function getQuestionDetail(id) {
  return request({
    url: `/question/${id}`,
    method: 'get'
  })
}

/**
 * 添加题目（管理员）
 * @param {Object} data - 题目数据
 * @returns {Promise}
 */
export function addQuestion(data) {
  return request({
    url: '/question/add',
    method: 'post',
    data
  })
}

/**
 * 更新题目（管理员）
 * @param {Object} data - 题目数据
 * @returns {Promise}
 */
export function updateQuestion(data) {
  return request({
    url: '/question/update',
    method: 'put',
    data
  })
}

/**
 * 删除题目（管理员）
 * @param {number} id - 题目ID
 * @returns {Promise}
 */
export function deleteQuestion(id) {
  return request({
    url: `/question/${id}`,
    method: 'delete'
  })
} 