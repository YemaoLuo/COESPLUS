import { http } from '@/utils/http/axios';
//import { BasicResponseModel } from '@/api/system/user';

export function getTableListDemo(params) {
  return http.request({
    url: 'http://yemaoluo.top:7000/api/admin/teacherComment',
    method: 'GET',
    params,
  });
}
export function deleteTableListDemo(params) {
  return http.request({
    url: 'http://yemaoluo.top:7000/api/admin/teacherComment/physical/',
    method: 'DELETE',
    params,
  });
}
