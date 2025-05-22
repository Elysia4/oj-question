import request from './request'

/**
 * 提交代码
 * @param {Object} data - 提交数据 {questionId, language, code}
 * @returns {Promise}
 */
export function submitCode(data) {
  return request({
    url: '/question_submit/submit',
    method: 'post',
    data
  })
}

/**
 * 获取提交记录列表
 * @param {Object} params - 查询参数 {current, pageSize, questionId, userId, language, status}
 * @returns {Promise}
 */
export function getSubmitList(params) {
  return request({
    url: '/question_submit/list',
    method: 'get',
    params
  })
}

/**
 * 获取提交详情
 * @param {number} id - 提交ID
 * @returns {Promise}
 */
export function getSubmitDetail(id) {
  return request({
    url: `/question_submit/${id}`,
    method: 'get'
  })
} 