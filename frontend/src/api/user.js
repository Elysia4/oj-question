import request from './request'

/**
 * 检查用户名是否可用
 * @param {String} username - 用户名
 * @returns {Promise}
 */
export function checkUsername(username) {
  return request({
    url: '/user/check-username',
    method: 'get',
    params: { username }
  })
}

/**
 * 检查邮箱是否可用
 * @param {String} email - 邮箱
 * @returns {Promise}
 */
export function checkEmail(email) {
  return request({
    url: '/user/check-email',
    method: 'get',
    params: { email }
  })
}

/**
 * 用户注册
 * @param {Object} data - 包含用户名、密码、确认密码和邮箱的对象
 * @returns {Promise}
 */
export function register(data) {
  return request({
    url: '/user/register',
    method: 'post',
    data
  })
}

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