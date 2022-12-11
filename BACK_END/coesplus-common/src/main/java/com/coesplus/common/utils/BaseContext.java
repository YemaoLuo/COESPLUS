package com.coesplus.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于ThreadLocal实现用户id的共享
 *
 * @author LuoYemao
 * @since 2022/8/24 13:22
 */
@Slf4j
public class BaseContext {

    private static ThreadLocal<Map<String, Object>> threadLocalMap = new ThreadLocal<>();
    public static void setValue(String key, Object value) {
        try {
            log.info("存入ThreadLocalMap({}, {})", key, value.toString());
            Map<String, Object> map = threadLocalMap.get();
            if (ObjectUtils.isEmpty(map)) {
                map = new HashMap<>(6);
            }
            map.put(key, value);
            threadLocalMap.set(map);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public static Object getValue(String key) {
        try {
            log.info("获取ThreadLocalMap({})", key);
            return threadLocalMap.get().get(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public static Boolean removeValue(String key) {
        try {
            log.info("删除ThreadLocalMap({})", key);
            threadLocalMap.get().remove(key);
            return true;
        } catch (Exception e) {
            log.info("获取ThreadLocalMap({}, {})", key, "NULL");
            return false;
        }
    }
}
