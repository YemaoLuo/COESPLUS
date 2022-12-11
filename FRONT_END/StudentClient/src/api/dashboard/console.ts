import { http } from '@/utils/http/axios';
import { BasicResponseModel } from '@/api/system/user';

//获取主控台信息
// export function getConsoleInfo() {
//   return http.request({
//     url: '/dashboard/console',
//     method: 'get',
//   });
// }
export function getStudentGrade() {
  return http.request<BasicResponseModel>(
    {
      url: 'http://yemaoluo.top:7000/api/coes/student/dashboard/grade',
      method: 'get',
    },
    {
      isTransformResponse: false,
    }
  );
}
export function getTeacherGrade() {
  return http.request<BasicResponseModel>(
    {
      url: 'http://yemaoluo.top:7000/api/coes/teacher/dashboard/grade',
      method: 'get',
    },
    {
      isTransformResponse: false,
    }
  );
}
