package com.google.core.security.processer;

import org.springframework.web.context.request.ServletWebRequest;

/**
 * 验证码接口类
 * @author iris
 */
public interface ValidateCodeProcessor {

    /**
     * 创建验证码
     *
     * @param request 请求
     * @throws Exception 异常
     */
    void create(ServletWebRequest request) throws Exception;

    /**
     * 验证验证码
     *
     * @param request 请求
     */
    void validate(ServletWebRequest request);

}
