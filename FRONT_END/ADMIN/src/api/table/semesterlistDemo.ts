import { http } from '@/utils/http/axios';
import { BasicResponseModel } from '@/api/system/user';

export function getTableListDemo(params) {
  return http.request({
    url: 'http://yemaoluo.top:7000/api/admin/semester',
    method: 'GET',
    params,
  });
}
export function getAllCourse(params) {
  return http.request({
    url: 'http://yemaoluo.top:7000/api/admin/course',
    method: 'GET',
    params,
  });
}
export function getSemesterCourse(params) {
  return http.request({
    url: 'http://yemaoluo.top:7000/api/admin/semester/course',
    method: 'GET',
    params,
  });
}
export function getSemesterCredit(params) {
  return http.request({
    url: 'http://yemaoluo.top:7000/api/admin/semester/credit',
    method: 'GET',
    params,
  });
}
export function addSemester(data) {
  return http.request<BasicResponseModel>(
    {
      url: 'http://yemaoluo.top:7000/api/admin/semester',
      method: 'POST',
      data,
    },
    {
      isTransformResponse: false,
    }
  );
}
export function addSemesterCourse(data) {
  return http.request<BasicResponseModel>(
    {
      url: 'http://yemaoluo.top:7000/api/admin/semester/course',
      method: 'POST',
      data,
    },
    {
      isTransformResponse: false,
    }
  );
}
export function addSemesterCredit(data) {
  return http.request<BasicResponseModel>(
    {
      url: 'http://yemaoluo.top:7000/api/admin/semester/credit',
      method: 'POST',
      data,
    },
    {
      isTransformResponse: false,
    }
  );
}
//
export function ChangeSemesterState(id, params) {
  return http.request<BasicResponseModel>(
    {
      url: 'http://yemaoluo.top:7000/api/admin/semester/' + id,
      method: 'GET',
      params,
    },
    {
      isTransformResponse: false,
    }
  );
}

export function DeleteSemester(params) {
  return http.request<BasicResponseModel>(
    {
      url: 'http://yemaoluo.top:7000/api/admin/semester/',
      method: 'DELETE',
      params,
    },
    {
      isTransformResponse: false,
    }
  );
}
export function DeleteSemesterCourse(params) {
  return http.request<BasicResponseModel>(
    {
      url: 'http://yemaoluo.top:7000/api/admin/semester/course/',
      method: 'DELETE',
      params,
    },
    {
      isTransformResponse: false,
    }
  );
}
export function DeleteSemesterCredit(params) {
  return http.request<BasicResponseModel>(
    {
      url: 'http://yemaoluo.top:7000/api/admin/semester/credit/',
      method: 'DELETE',
      params,
    },
    {
      isTransformResponse: false,
    }
  );
}
//
// export function modifyTableListDemo(params) {
//   return http.request({
//     url: 'http://yemaoluo.top:7000/api/admin/course',
//     method: 'PATCH',
//     params,
//   });
// }
// export function exportcoursedemo(params) {
//   return http.request(
//     {
//       url: 'http://yemaoluo.top:7000/api/admin/course/excel',
//       responseType: 'blob',
//       method: 'GET',
//       params,
//     },
//     {
//       isTransformResponse: false,
//     }
//   );
// }
// export function exportcoursestudentdemo(params) {
//   return http.request(
//     {
//       url: 'http://yemaoluo.top:7000/api/admin/course/excel/student',
//       responseType: 'blob',
//       method: 'GET',
//       params,
//     },
//     {
//       isTransformResponse: false,
//     }
//   );
// }
