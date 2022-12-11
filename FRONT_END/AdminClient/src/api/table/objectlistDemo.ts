import { http } from '@/utils/http/axios';
//import { BasicResponseModel } from '@/api/system/user';

export function getTableListDemo() {
  return http.request({
    url: 'http://yemaoluo.top:7000/api/admin/file/detail',
    method: 'GET',
  });
}
export function deleteTableListDemo() {
  return http.request({
    url: 'http://yemaoluo.top:7000/api/admin/file/picCache',
    method: 'DELETE',
  });
}
