import { http } from '@/utils/http/axios';
import { BasicResponseModel } from '@/api/system/user';

//获取主控台信息
// export function getConsoleInfo() {
//   return http.request({
//     url: 'http://yemaoluo.top:7000/api/admin/dashboard/login/counter',
//     method: 'GET',
//   });
// }
// export function VisitAmountStudent() {
//   return http.request({
//     url: 'http://yemaoluo.top:7000/api/admin/dashboard/student',
//     method: 'get',
//   });
// }
export function VisitAmountStudent() {
  return http.request<BasicResponseModel>(
    {
      url: 'http://yemaoluo.top:7000/api/admin/dashboard/student',
      method: 'GET',
    },
    {
      isTransformResponse: false,
    }
  );
}
export function VisitAmountTeacher() {
  return http.request<BasicResponseModel>(
    {
      url: 'http://yemaoluo.top:7000/api/admin/dashboard/teacher',
      method: 'GET',
    },
    {
      isTransformResponse: false,
    }
  );
}
export function getConsoleVisitInfo() {
  return http.request<BasicResponseModel>(
    {
      url: 'http://yemaoluo.top:7000/api/admin/dashboard/login/counter',
      method: 'GET',
    },
    {
      isTransformResponse: false,
    }
  );
}
export function getConsoleLoginInfo() {
  return http.request<BasicResponseModel>(
    {
      url: 'http://yemaoluo.top:7000/api/admin/dashboard/login/rank',
      method: 'GET',
    },
    {
      isTransformResponse: false,
    }
  );
}

export function getEmailAmountInfo() {
  return http.request<BasicResponseModel>(
    {
      url: 'http://yemaoluo.top:7000/api/admin/dashboard/email',
      method: 'GET',
    },
    {
      isTransformResponse: false,
    }
  );
}
export function getObjectInfo() {
  return http.request<BasicResponseModel>(
    {
      url: 'http://yemaoluo.top:7000/api/admin/file/detail',
      method: 'GET',
    },
    {
      isTransformResponse: false,
    }
  );
}
