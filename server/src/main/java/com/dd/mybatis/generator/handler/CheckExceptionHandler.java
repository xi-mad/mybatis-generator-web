package com.dd.mybatis.generator.handler;

import com.dd.mybatis.generator.common.CommonResponse;
import com.dd.mybatis.generator.exception.CheckException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
public class CheckExceptionHandler {

    @ExceptionHandler(CheckException.class)
    public CommonResponse<?> checkExceptionHandler(CheckException e) {
        return CommonResponse.fail(e.getMessage());
    }
}
