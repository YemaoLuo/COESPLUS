import { http } from '@/utils/http/axios';

export interface BasicResponseModel<T = any> {
  code: number;
  message: string;
  result: T;
}

export interface BasicPageParams {
  pageNumber: number;
  pageSize: number;
  total: number;
}

/**
 * @description: 获取用户信息
 */
// export function getUserInfo() {
//   return http.request({
//     url: 'http://yemaoluo.top:7000/api/coes/account',
//     method: 'get',
//   });
// }

/**
 * @description: 用户登录
 */
export function login(params) {
  return http.request<BasicResponseModel>(
    {
      url: 'http://yemaoluo.top:7000/api/coes/account',
      method: 'POST',
      params,
    },
    {
      isTransformResponse: false,
    }
  );
}

/**
 * @description: 用户修改密码
 */
export function changePassword(token, data) {
  return http.request(
    {
      url: `http://yemaoluo.top:7000/api/coes/student/account/password`,
      method: 'PATCH',
      headers: token,
      data,
    },
    {
      isTransformResponse: false,
    }
  );
}

/**
 * @description: 用户登出
 */
export function logout(params) {
  return http.request({
    url: '/login/logout',
    method: 'POST',
    params,
  });
}
