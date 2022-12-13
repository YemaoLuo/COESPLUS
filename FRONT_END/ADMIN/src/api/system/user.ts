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
//     url: '/admin_info',
//     method: 'get',
//   });
// }

/**
 * @description: 用户登录
 */
export function login(data) {
  return http.request<BasicResponseModel>(
    {
      url: 'http://yemaoluo.top:7000/api/admin/account',
      method: 'POST',
      data,
    },
    {
      isTransformResponse: false,
    }
  );
}

/**
 * @description: 用户修改密码
 */
export function changePassword(token, params) {
  return http.request(
    {
      url: `http://yemaoluo.top:7000/api/admin/account?verifyToken=${token}`,
      method: 'PATCH',
      params,
    },
    {
      isTransformResponse: false,
    }
  );
}

//重置密码邮件
export function resetPassword() {
  return http.request(
    {
      url: `http://yemaoluo.top:7000/api/admin/system/activate/0836260e0b68b768dcbf6123bf0eac8d?role=franz`,
      method: 'get',
    },
    {
      isTransformResponse: false,
    }
  );
}

//小骆的新奇想法
export function changePassword2(token, params) {
  return http.request(
    {
      url: `http://yemaoluo.top:7000/api/admin/account?verifyToken=` + token,
      method: 'PATCH',
      params,
    },
    {
      isTransformResponse: false,
    }
  );
}

/**
 * @description: 用户登出
 */
export function logout() {
  return http.request({
    url: 'http://yemaoluo.top:7000/api/admin/account',
    method: 'DELETE',
  });
}
