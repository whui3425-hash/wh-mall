package com.gupaoedu.mall.util;

public enum RespCode {
    SUCCESS(20000, "Operation successful"),
    ERROR(50000, "Operation failed"),
    SYSTEM_ERROR(50001, "System error");

    private Integer code;
    private String message;

    RespCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    RespCode() {
    }
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
