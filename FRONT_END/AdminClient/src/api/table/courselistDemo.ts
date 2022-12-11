import { http } from '@/utils/http/axios';
import { BasicResponseModel } from '@/api/system/user';

export function getTableListDemo(params) {
  return http.request({
    url: 'http://yemaoluo.top:7000/api/admin/course',
    method: 'GET',
    params,
  });
}
export function getTeacherList(params) {
  return http.request({
    url: 'http://yemaoluo.top:7000/api/admin/teacher',
    method: 'GET',
    params,
  });
}
export function getAllStudentList(params) {
  return http.request({
    url: 'http://yemaoluo.top:7000/api/admin/student',
    method: 'GET',
    params,
  });
}
export function getStudentsTableListDemo(params) {
  return http.request({
    url: 'http://yemaoluo.top:7000/api/admin/course/student',
    method: 'GET',
    params,
  });
}

export function addcoursedemo(data) {
  return http.request<BasicResponseModel>(
    {
      url: 'http://yemaoluo.top:7000/api/admin/course',
      method: 'POST',
      data,
    },
    {
      isTransformResponse: false,
    }
  );
}
export function addCourseStudent(data) {
  return http.request<BasicResponseModel>(
    {
      url: 'http://yemaoluo.top:7000/api/admin/course/student',
      method: 'POST',
      data,
    },
    {
      isTransformResponse: false,
    }
  );
}
export function disableTableListDemo(params) {
  return http.request<BasicResponseModel>(
    {
      url: 'http://yemaoluo.top:7000/api/admin/course/',
      method: 'DELETE',
      params,
    },
    {
      isTransformResponse: false,
    }
  );
}

export function enableTableListDemo(data) {
  return http.request<BasicResponseModel>(
    {
      url: 'http://yemaoluo.top:7000/api/admin/course/' + data,
      method: 'put',
    },
    {
      isTransformResponse: false,
    }
  );
}
export function delTableListDemo(params) {
  return http.request<BasicResponseModel>(
    {
      url: 'http://yemaoluo.top:7000/api/admin/course/physical/',
      method: 'DELETE',
      params,
    },
    {
      isTransformResponse: false,
    }
  );
}
export function DeleteCourseStudent(id) {
  return http.request({
    url: 'http://yemaoluo.top:7000/api/admin/course/student/physical/' + id,
    method: 'DELETE',
  });
}

export function modifyTableListDemo(params) {
  return http.request({
    url: 'http://yemaoluo.top:7000/api/admin/course',
    method: 'PATCH',
    params,
  });
}
export function exportcoursedemo(params) {
  return http.request(
    {
      url: 'http://yemaoluo.top:7000/api/admin/course/excel',
      responseType: 'blob',
      method: 'GET',
      params,
    },
    {
      isTransformResponse: false,
    }
  );
}
export function exportcoursestudentdemo(params) {
  return http.request(
    {
      url: 'http://yemaoluo.top:7000/api/admin/course/excel/student',
      responseType: 'blob',
      method: 'GET',
      params,
    },
    {
      isTransformResponse: false,
    }
  );
}
