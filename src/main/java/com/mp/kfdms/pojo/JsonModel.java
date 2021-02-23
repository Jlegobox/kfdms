package com.mp.kfdms.pojo;

import org.springframework.http.HttpStatus;

import java.util.LinkedHashMap;

/**
 * 通用json返回模板
 * @Author J
 * @Email jlc_game123@163.com
 * @Date 2021/2/23
 * @Time 19:33
 */
public class JsonModel {
    private Object data;

    private LinkedHashMap<String,Object> dataMap = new LinkedHashMap<String, Object>();
    private String code;
    private String message;

    private HttpStatus status;

    public JsonModel(){
        this.status = HttpStatus.OK;
    }

    public JsonModel put(String key, Object value){
        this.dataMap.put(key, value);
        return this;
    }

    public Object get(String key){
        return this.dataMap.get(key);
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public LinkedHashMap<String, Object> getDataMap() {
        return dataMap;
    }

    public void setDataMap(LinkedHashMap<String, Object> dataMap) {
        this.dataMap = dataMap;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}
