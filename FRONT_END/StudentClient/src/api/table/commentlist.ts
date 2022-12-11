import { http } from '@/utils/http/axios';
import { BasicResponseModel } from '@/api/system/user';
//获取table
export function getTableList(params) {
  return http.request({
    url: 'http://yemaoluo.top:7000/api/coes/student/teacherComment',
    method: 'get',
    params,
  });
}
export function addComment(data) {
  return http.request<BasicResponseModel>(
    {
      url: 'http://yemaoluo.top:7000/api/coes/student/teacherComment',
      method: 'POST',
      data,
    },
    {
      isTransformResponse: false,
    }
  );
}
export function getOwnTableList(params) {
  return http.request({
    url: 'http://yemaoluo.top:7000/api/coes/teacher/teacherComment',
    method: 'get',
    params,
  });
}
