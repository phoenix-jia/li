package com.example.demo.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ControllerExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @ExceptionHandler(Exception.class)
    public ModelAndView exceptionHandler(HttpServletRequest request,Exception e) throws Exception {
        logger.error("Request URL :{} ,Exception :{} ",request.getRequestURL(),e);

        //如果异常添加了注解，那么就按照注解进行显示，这里重写了404页面NotFoundException
        if((AnnotationUtils.findAnnotation(e.getClass(), ResponseStatus.class)!=null)){
            throw e;
        }

        ModelAndView mv= new ModelAndView();
        mv.addObject("url" ,request.getRequestURL());
        mv.addObject("exception" , e);
        mv.setViewName("error/error");
        return mv;
    }
}
