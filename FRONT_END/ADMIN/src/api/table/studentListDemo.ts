import { http } from '@/utils/http/axios';
import { BasicResponseModel } from '@/api/system/user';

export function getTableListDemo(params) {
  return http.request({
    url: 'http://yemaoluo.top:7000/api/admin/student',
    method: 'GET',
    params,
  });
}

export function disableTableListDemo(params) {
  return http.request<BasicResponseModel>(
    {
      url: 'http://yemaoluo.top:7000/api/admin/student/',
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
      url: 'http://yemaoluo.top:7000/api/admin/student/' + data,
      method: 'PUT',
    },
    {
      isTransformResponse: false,
    }
  );
}

export function delTableListDemo(params) {
  return http.request<BasicResponseModel>(
    {
      url: 'http://yemaoluo.top:7000/api/admin/student/physical/',
      method: 'DELETE',
      params,
    },
    {
      isTransformResponse: false,
    }
  );
}
export function addstudentdemo(data) {
  return http.request<BasicResponseModel>(
    {
      url: 'http://yemaoluo.top:7000/api/admin/student',
      method: 'POST',
      data,
    },
    {
      isTransformResponse: false,
    }
  );
}
export function rolestudentdemo(params, id) {
  return http.request<BasicResponseModel>(
    {
      url: 'http://yemaoluo.top:7000/api/admin/system/activate/' + id,
      method: 'GET',
      params,
    },
    {
      isTransformResponse: false,
    }
  );
}
export function modifyTableListDemo(params) {
  return http.request({
    url: 'http://yemaoluo.top:7000/api/admin/student',
    method: 'PATCH',
    params,
  });
}

export function exportstudentdemo(params) {
  return http.request(
    {
      url: 'http://yemaoluo.top:7000/api/admin/student/excel',
      responseType: 'blob',
      method: 'GET',
      params,
    },
    {
      isTransformResponse: false,
    }
  );
}
export function addstudentphoto(params) {
  return http.uploadFile(
    {
      url: 'http://yemaoluo.top:7000/api/admin/file',
      method: 'POST',
    },
    params
  );
}
