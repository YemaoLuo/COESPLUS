import { http } from '@/utils/http/axios';
import {BasicResponseModel} from "@/api/system/user";

//查询可选课程
export function getTableList(params) {
  return http.request({
    url: 'http://yemaoluo.top:7000/api/coes/student/semester',
    method: 'get',
    params,
  });
}

export function courseAction(params) {
  return http.request<BasicResponseModel>({
    url: 'http://yemaoluo.top:7000/api/coes/student/semester/course',
    method: 'post',
    params,
  });
}

export function checkEnrollingState() {
  return http.request({
    url: 'http://yemaoluo.top:7000/api/coes/student/semester/valid',
    method: 'GET',
    // headers: token,
  });
}

export function checkEnrollingState2() {
  return http.request({
    url: 'http://yemaoluo.top:7000/api/coes/student/semester/valid',
    method: 'GET',
  });
}
