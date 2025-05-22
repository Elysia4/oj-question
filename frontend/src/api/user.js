import request from './request'

/**
 * 用户登录
 * @param {Object} data - 包含用户名和密码的对象
 * @returns {Promise}
 */
export function login(data) {
  return request({
    url: '/user/login',
    method: 'post',
    data
  })
}

/**
 * 获取当前登录用户信息
 * @returns {Promise}
 */
export function getCurrentUser() {
  return request({
    url: '/user/current',
    method: 'get'
  })
}

/**
 * 用户登出
 * @returns {Promise}
 */
export function logout() {
  return request({
    url: '/user/logout',
    method: 'post'
  })
} 