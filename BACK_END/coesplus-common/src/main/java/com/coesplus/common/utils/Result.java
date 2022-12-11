package com.coesplus.common.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 返回结果实体类
 *
 * @author LuoYemao
 * @since 2022/9/28 8:20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {

    private int code;

    private String message;

    private Object result;

    public static Result ok(String message, Object object) {
        return new Result(200, message, object);
    }

    public static Result ok(Object object) {
        return new Result(200, "操作成功！", object);
    }

    public static Result ok() {
        return new Result(200, "操作成功！", null);
    }

    public static Result ok(int code, Object object) {
        return new Result(code, "操作成功！", object);
    }

    public static Result ok(int code) {
        return new Result(code, "操作成功！", null);
    }

    public static Result error(String message) {
        return new Result(400, message, null);
    }

    public static Result error(int code, String message) {
        return new Result(code, message, null);
    }

    public static Result error(int code, Object result) {
        return new Result(code, null, result);
    }

    public static Result error() {
        return new Result(400, "操作失败！", null);
    }

    public static Result error(int code) {
        return new Result(code, "操作失败！", null);
    }
}
