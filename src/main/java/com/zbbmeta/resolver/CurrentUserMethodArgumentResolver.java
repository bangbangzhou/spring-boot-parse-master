package com.zbbmeta.resolver;

import com.zbbmeta.entity.Tutorial;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.Parameter;

/**
 * @author springboot葵花宝典
 * @description: TODO
 */
public class CurrentUserMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        //如果Controller的方法参数类型为Tutorial，则返回true
        return parameter.getParameterType().equals(Tutorial.class);


    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        System.out.println("参数解析器...");
        Tutorial tutorial = new Tutorial();
        tutorial.setPublished(1);
        tutorial.setTitle("test");
        tutorial.setDescription("test");
        return tutorial;
    }
}
