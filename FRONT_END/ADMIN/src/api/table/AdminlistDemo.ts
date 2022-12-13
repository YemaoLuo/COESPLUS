import { http } from '@/utils/http/axios';
import { BasicResponseModel } from '@/api/system/user';

export function getTableListDemo(params) {
  return http.request({
    url: 'http://yemaoluo.top:7000/api/admin/administrator',
    method: 'GET',
    params,
  });
}

export function addAdmindemo(data) {
  return http.request<BasicResponseModel>(
    {
      url: 'http://yemaoluo.top:7000/api/admin/administrator',
      method: 'POST',
      data,
    },
    {
      isTransformResponse: false,
    }
  );
}

export function roleadmindemo(params, id) {
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
