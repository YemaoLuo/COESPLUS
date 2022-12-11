package com.coesplus.admin;

import com.coesplus.admin.vo.CourseVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class NormalTest {

    @Test
    public void testDistinct() {
        List<String> list1 = new ArrayList<>();
        list1.add("1");
        list1.add("2");
        list1.add("3");
        list1.add("4");
        List<String> list2 = new ArrayList<>();
        list2.add("3");
        list2.add("1");
        list2.add("1");
        list2.add("8");
        list1.addAll(list2);
        list1 = list1.stream().distinct().collect(Collectors.toList());
        log.info(list1.toString());
    }

    @Test
    public void testSplit() {
        String fileName = "1231231298fdajf.png";
        log.info(fileName.split("\\.")[1]);
    }

    @Test
    public void testNull() {
        CourseVo c = new CourseVo();
        c.setId("123");
        c.setDay(null);
        c.getDay();
    }

    @Test
    public void testHashMap() {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("test", map.getOrDefault("test", 0) + 1);
        log.info(map.get("test").toString());
        map.put("test", map.getOrDefault("test", 0) + 1);
        log.info(map.get("test").toString());
    }

    @Test
    public void testHashMapContain() {
        long time1 = System.currentTimeMillis();
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (int i = 0; i < 1000000; i++) {
            if (map.containsKey("test")) {
                map.put("test", map.get("test") + 1);
            } else {
                map.put("test", 0);
            }
        }
        log.info(map.get("test").toString() + "::" + (System.currentTimeMillis() - time1));

        long time2 = System.currentTimeMillis();
        Map<String, Integer> map2 = new HashMap<String, Integer>();
        for (int i = 0; i < 1000000; i++) {
            map2.put("test", map2.getOrDefault("test", 0) + 1);
        }
        log.info(map.get("test").toString() + "::" + (System.currentTimeMillis() - time2));
    }


}
