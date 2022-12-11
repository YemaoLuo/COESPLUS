package com.coesplus.common.utils;

import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.Collection;

@Slf4j
public class ExcelUtils {

    @SneakyThrows
    public static void export(HttpServletResponse response, Class classForExport, Collection data, String fileName2, String password) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode(fileName2, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            // 这里需要设置不关闭流
            EasyExcel.write(response.getOutputStream(), classForExport).password(password).autoCloseStream(Boolean.FALSE).sheet(fileName2)
                    .doWrite(data);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            // 重置response
            response.reset();
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().println(JSONUtil.toJsonStr(Result.error(e.getMessage())));
        }
    }
}
