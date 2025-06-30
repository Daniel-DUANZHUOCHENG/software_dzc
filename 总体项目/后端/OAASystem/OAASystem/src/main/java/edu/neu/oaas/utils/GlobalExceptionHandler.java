package edu.neu.oaas.utils;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MyException.class)
    public Map<String, Object> handleMyException(MyException e) {
        Map<String, Object> map = new HashMap<>();
        map.put("isOk", false);
        map.put("msg", e.getMsg());
        return map;
    }

    @ExceptionHandler(Exception.class)
    public Map<String, Object> handleException(Exception e) {
        Map<String, Object> map = new HashMap<>();
        map.put("isOk", false);
        map.put("msg", "系统异常，请联系管理员！");
        e.printStackTrace();
        return map;
    }
}
