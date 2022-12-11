import { http } from '@/utils/http/axios';
import { BasicResponseModel } from '@/api/system/user';

export function getTableListDemo(params) {
  return http.request({
    url: 'http://yemaoluo.top:7000/api/admin/teacher',
    method: 'GET',
    params,
  });
}
export function getFacultyList(params) {
  return http.request(
    {
      url: 'http://yemaoluo.top:7000/api/admin/faculty/select',
      method: 'GET',
      params,
    },
    {
      isTransformResponse: false,
    }
  );
}
export function addteacherdemo(data) {
  return http.request<BasicResponseModel>(
    {
      url: 'http://yemaoluo.top:7000/api/admin/teacher',
      method: 'POST',
      data,
    },
    {
      isTransformResponse: false,
    }
  );
}

export function disableTableListDemo(params) {
  return http.request({
    url: 'http://yemaoluo.top:7000/api/admin/teacher/',
    method: 'DELETE',
    params,
  });
}

export function enableTableListDemo(data) {
  return http.request({
    url: 'http://yemaoluo.top:7000/api/admin/teacher/' + data,
    method: 'PUT',
  });
}

export function delTableListDemo(params) {
  return http.request({
    url: 'http://yemaoluo.top:7000/api/admin/teacher/physical/',
    method: 'DELETE',
    params,
  });
}

export function modifyTableListDemo(params) {
  return http.request({
    url: 'http://yemaoluo.top:7000/api/admin/teacher',
    method: 'PATCH',
    params,
  });
}
export function exportteacherdemo(params) {
  return http.request(
    {
      url: 'http://yemaoluo.top:7000/api/admin/teacher/excel',
      responseType: 'blob',
      method: 'GET',
      params,
    },
    {
      isTransformResponse: false,
    }
  );
}
export function addteacherphoto(params) {
  return http.uploadFile(
    {
      url: 'http://yemaoluo.top:7000/api/admin/file',
      method: 'POST',
      // headers: { 'content-type': 'application/x-www-form-urlencoded' },
    },
    params
  );
}
