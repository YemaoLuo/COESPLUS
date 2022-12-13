import { http } from '@/utils/http/axios';

/**
 * @description: 角色列表
 */
export function getRoleList() {
  return http.request({
    url: 'http://yemaoluo.top:7000/api/admin/file/picCache',
    method: 'delete',
  });
}
