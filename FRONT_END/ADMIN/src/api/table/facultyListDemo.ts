import { http } from '@/utils/http/axios';
import { BasicResponseModel } from '@/api/system/user';

export function getTableListDemo(params) {
  return http.request({
    url: 'http://yemaoluo.top:7000/api/admin/faculty',
    method: 'GET',
    params,
  });
}
export function addfacultydemo(data) {
  return http.request<BasicResponseModel>(
    {
      url: 'http://yemaoluo.top:7000/api/admin/faculty',
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
      url: 'http://yemaoluo.top:7000/api/admin/faculty/',
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
      url: 'http://yemaoluo.top:7000/api/admin/faculty/' + data,
      method: 'PUT',
    },
    {
      isTransformResponse: false,
    }
  );
}

export function delTableListDemo(params) {
  return http.request(
    {
      url: 'http://yemaoluo.top:7000/api/admin/faculty/physical/',
      method: 'DELETE',
      params,
    },
    {
      isTransformResponse: false,
    }
  );
}

export function modifyTableListDemo(params) {
  return http.request({
    url: 'http://yemaoluo.top:7000/api/admin/faculty',
    method: 'PATCH',
    params,
  });
}
export function exportfacultydemo(params) {
  return http.request(
    {
      url: 'http://yemaoluo.top:7000/api/admin/faculty/excel',
      responseType: 'blob',
      method: 'GET',
      params,
    },
    {
      isTransformResponse: false,
    }
  );
}
