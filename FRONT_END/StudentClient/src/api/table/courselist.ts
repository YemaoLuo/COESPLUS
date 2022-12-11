import { http } from '@/utils/http/axios';

//查询已选课程
export function getTableList(token, params) {
  return http.request({
    url: 'http://yemaoluo.top:7000/api/coes/student/course',
    method: 'get',
    headers: token,
    params,
  });
}

export function getCourseList(token, params) {
  return http.request({
    url: 'http://yemaoluo.top:7000/api/coes/student/course',
    method: 'get',
    headers: token,
    params,
  });
}

export function exportcoursedemo() {
  return http.request(
    {
      url: 'http://yemaoluo.top:7000/api/coes/student/course/excel',
      responseType: 'blob',
      method: 'GET',
    },
    {
      isTransformResponse: false,
    }
  );
}
