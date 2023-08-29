package com.dd.mybatis.generator.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommonResponse<T> {

    private T data;

    private String message;

    private int code;

    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(data, "success", 0);
    }

    public static <T> CommonResponse<T> success() {
        return new CommonResponse<>(null, "success", 0);
    }

    public static <T> CommonResponse<T> fail(String message) {
        return new CommonResponse<>(null, message, -1);
    }

}
