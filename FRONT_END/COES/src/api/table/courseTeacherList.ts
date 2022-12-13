import { http } from '@/utils/http/axios';

//查询已选课程
export function getTableList(token, params) {
  return http.request({
    url: 'http://yemaoluo.top:7000/api/coes/teacher/course',
    method: 'get',
    headers: token,
    params,
  });
}

export function getStudentsTableListDemo(token, params) {
  return http.request({
    url: 'http://yemaoluo.top:7000/api/coes/teacher/course/student',
    method: 'GET',
    headers: token,
    params,
  });
}

export function NoticeDemo(token, params) {
  return http.request({
    url: 'http://yemaoluo.top:7000/api/coes/teacher/mail/class',
    method: 'GET',
    headers: token,
    params,
  });
}

export function gradingList(token, params) {
  return http.request({
    url: 'http://yemaoluo.top:7000/api/coes/teacher/course',
    method: 'PATCH',
    headers: token,
    params,
  });
}

export function exportcourse() {
  return http.request(
    {
      url: 'http://yemaoluo.top:7000/api/coes/teacher/course/excel',
      responseType: 'blob',
      method: 'GET',
    },
    {
      isTransformResponse: false,
    }
  );
}
export function exportStudentScore(params) {
  return http.request(
    {
      url: 'http://yemaoluo.top:7000/api/coes/teacher/course/excel',
      responseType: 'blob',
      method: 'GET',
      params,
    },
    {
      isTransformResponse: false,
    }
  );
}
